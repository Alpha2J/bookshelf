package cn.alpha2j.utils;

import cn.alpha2j.bean.Book;
import cn.alpha2j.bean.Customer;
import cn.alpha2j.bean.TransactionRecord;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by cn.alpha2j on 2016/11/27.
 */
public class TransactionUtils {

    private static final String HISTORY_RECORD_FILE = "D://librarySystemFile/historyTransactionRecord.txt";//历史借阅书籍记录
    private static final String RECORD_FILE = "D://librarySystemFile/transactionRecord.txt";//现在还没还回来的书籍记录
    private static final String SEPARATOR = "---";
    //customerId: 7  customerName: 20, isbn: 13, bookName(20中文): 60,  borrowTime: 15, returnTime: 15, 5个separator: 15
    private static final int RECORD_SIZE = 145;

    //书籍是否还有库存
    public static boolean isBookLeft(String isbn) {
        Book record = BookUtils.queryByIsbn(isbn);

        if (record == null) {
            return false;
        }

        int leftNum = Integer.valueOf(record.getTotalNum());

        if (leftNum > 0) {
            return true;
        } else {
            return false;
        }
    }

    //借书, 一次只能借一本
    public static boolean borrowBook(Customer customer, Book book) {
        if (customer == null || book == null) {
            throw new NullPointerException();
        }

        String isbn = book.getIsbn();

        if (!isBookLeft(isbn)) {
            return false;  //书籍数目不够
        }

        long borrowTime = new Date().getTime();
        long temp = 3600 * 24 * 30 * 1000L;            //一个月时间
        long returnTime = borrowTime + temp;

        TransactionRecord record = new TransactionRecord(customer.getId(), customer.getUsername(), book.getIsbn(), book.getBookName(), String.valueOf(borrowTime), String.valueOf(returnTime));

        String recordString = formatRecord(record);

        RandomAccessFile recordFile = null;
        RandomAccessFile historyRecordFile = null;
        try {
            recordFile = FileUtils.getRandomAccessFile(TransactionUtils.RECORD_FILE, "rw");
            historyRecordFile = FileUtils.getRandomAccessFile(TransactionUtils.HISTORY_RECORD_FILE, "rw");
            recordFile.seek(recordFile.length());
            historyRecordFile.seek(recordFile.length());
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileUtils.writeFixedString(recordString, TransactionUtils.RECORD_SIZE, recordFile);//写入记录文件
        FileUtils.writeFixedString(recordString, TransactionUtils.RECORD_SIZE, historyRecordFile); //写入历史记录文件

        FileUtils.releaseRandomAccessFile(recordFile);
        FileUtils.releaseRandomAccessFile(historyRecordFile);

        BookUtils.updateBookNum(isbn, -1);  //要减少一本书

        return true;
    }

    //还书
    public static boolean returnBook(String customerId, String isbn) {
        boolean isSuccess = false;
        List<TransactionRecord> recordList = getNotReturnedRecord();

        OutputStream refreshStream = null;
        DataOutputStream refreshDataOutputStream = null;
        OutputStream appendStream = null;
        DataOutputStream appendDataOutputStream = null;

        try {
            refreshStream = new FileOutputStream(TransactionUtils.RECORD_FILE);//第一次打开文件需要把全部东西都清掉,append默认是false的
            refreshDataOutputStream = new DataOutputStream(refreshStream);
            appendStream = new FileOutputStream(TransactionUtils.RECORD_FILE, true);//执行第一次后记录要加在后面
            appendDataOutputStream = new DataOutputStream(appendStream);

            if (recordList.size() == 1) {
                if (recordList.get(0).getCustomerId().equals(customerId) && recordList.get(0).getIsbn().equals(isbn)) {
                    refreshStream.flush();//清空文件
                    isSuccess = true;
                } else {
                    isSuccess = FileUtils.writeFixedString(formatRecord(recordList.get(0)), TransactionUtils.RECORD_SIZE, refreshDataOutputStream);
                }
            } else {
                boolean isFirst = true;
                for (int i = 0; i < recordList.size(); i++) {
                    if (recordList.get(i).getCustomerId().equals(customerId) && recordList.get(i).getIsbn().equals(isbn)) {
                        continue;
                    }

                    if (isFirst) {
                        isSuccess = FileUtils.writeFixedString(formatRecord(recordList.get(i)), TransactionUtils.RECORD_SIZE, refreshDataOutputStream);

                        release(refreshDataOutputStream, refreshStream);

                        isFirst = false;
                    } else {
                        isSuccess = FileUtils.writeFixedString(formatRecord(recordList.get(i)), TransactionUtils.RECORD_SIZE, appendDataOutputStream);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            release(refreshDataOutputStream, refreshStream);
            release(appendDataOutputStream, appendStream);
        }

        BookUtils.updateBookNum(isbn, 1);  //要将书的库存+1

        return isSuccess;
    }

    //从历史记录文件获取所有历史借书记录
    public static List<TransactionRecord> getAllHistoryRecord() {
        return getRecord(TransactionUtils.HISTORY_RECORD_FILE);
    }

    //从在借记录文件获取还没有还回去的书籍记录
    public static List<TransactionRecord> getNotReturnedRecord() {
        return getRecord(TransactionUtils.RECORD_FILE);
    }

    private static List<TransactionRecord> getRecord(String fileName) {
        RandomAccessFile recordFile = null;
        List<TransactionRecord> recordList = new ArrayList<>();

        try {
            recordFile = FileUtils.getRandomAccessFile(fileName, "r");  //只读
            while (recordFile.getFilePointer() != recordFile.length()) {
                String record = FileUtils.readSpecificRecord(TransactionUtils.RECORD_SIZE, recordFile).trim();
                String[] splitRecord = record.split(TransactionUtils.SEPARATOR);

                recordList.add(new TransactionRecord(splitRecord[0], splitRecord[1], splitRecord[2], splitRecord[3], splitRecord[4], splitRecord[5]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            FileUtils.releaseRandomAccessFile(recordFile);
        }

        return recordList;
    }

    private static String formatRecord(TransactionRecord record) {
        return formatRecord(record, TransactionUtils.SEPARATOR);
    }

    public static String formatRecord(TransactionRecord record, String separator) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(record.getCustomerId());
        stringBuilder.append(separator);
        stringBuilder.append(record.getCustomerName());
        stringBuilder.append(separator);
        stringBuilder.append(record.getIsbn());
        stringBuilder.append(separator);
        stringBuilder.append(record.getBookName());
        stringBuilder.append(separator);
        stringBuilder.append(record.getBorrowTime());
        stringBuilder.append(separator);
        stringBuilder.append(record.getReturnTime());

        return stringBuilder.toString();
    }

    //关闭各种流
    private static void release(DataOutputStream dataOutputStream, OutputStream outputStream) {
        if (dataOutputStream != null) {
            try {
                dataOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}





















