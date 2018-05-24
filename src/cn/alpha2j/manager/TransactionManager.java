package cn.alpha2j.manager;

import cn.alpha2j.bean.Book;
import cn.alpha2j.bean.Customer;
import cn.alpha2j.bean.ResultRecord;
import cn.alpha2j.bean.TransactionRecord;
import cn.alpha2j.utils.BookUtils;
import cn.alpha2j.utils.TransactionUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by cn.alpha2j on 2016/11/27.
 */
public class TransactionManager {

    //管理员调用的方法
    public static void transactionManagerIndex(BufferedReader bufferedReader, PrintStream printStream) throws IOException {
        boolean isQuit = false;
        while(!isQuit) {
            printStream.println("----------借阅记录查询(管理员用)----------");
            printStream.println("1 查看所有历史借阅记录");
            printStream.println("2 查看在借书籍(已经借出去没有还回来的书籍)");
            printStream.println("3 历史借阅书籍排序(根据借阅次数来进行排序)");
            printStream.println("其他任意键退出该页面");

            String action = bufferedReader.readLine().trim();

            switch(action) {
                case "1" :
                    showHistoryRecord(printStream);
                    break;
                case "2" :
                    showNotReturnedRecord(printStream);
                    break;
                case "3" :
                    showBookByTimes(printStream);
                    break;
                default :
                    isQuit = true;
                    break;
            }
        }

    }

    //普通用户调用的方法
    public static void forCustomerIndex(BufferedReader bufferedReader, PrintStream printStream, Customer customer) throws IOException {
        boolean isQuit = false;
        while(!isQuit) {
            printStream.println("----------图书借阅页面----------");
            printStream.println("1 查看已借书籍");
            printStream.println("2 借书");
            printStream.println("3 还书");
            printStream.println("其他任意键退出借书页面");

            String action = bufferedReader.readLine().trim();

            switch(action) {
                case "1" :
                    showBorrowedBook(printStream, customer);
                    break;
                case "2" :
                    borrowBook(bufferedReader, printStream, customer);
                    break;
                case "3" :
                    returnBook(bufferedReader, printStream, customer);
                    break;
                default :
                    isQuit = true;
                    break;
            }
        }
    }

    //显示图书馆书籍所有历史借阅情况
    public static void showHistoryRecord(PrintStream printStream) {
        showRecord(printStream, "historyRecord");
    }

    //显示所有现在还没有还回来的书籍
    public static void showNotReturnedRecord(PrintStream printStream) {
        showRecord(printStream, "record");
    }

    private static void showRecord(PrintStream printStream, String recordFile) {
        printStream.println("借书者id\t\t借书人用户名\t\tisbn\t\t书名\t\t借书时间\t\t是否归还(归还时间)");

        List<TransactionRecord> recordList = TransactionUtils.getNotReturnedRecord();//未归还的书籍
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        String borrowTime;
        String returnTime;
        long now;//现在的时间到1970年的毫秒值

        if(recordFile.equals("historyRecord")) {
            List<TransactionRecord> historyRecord = TransactionUtils.getAllHistoryRecord();
            String tip = "已归还";
            for(TransactionRecord record : historyRecord) {
                if(recordList.contains(record)) {
                    tip = "未归还";
                }

                date = new Date(Long.valueOf(record.getBorrowTime()));
                borrowTime = simpleDateFormat.format(date);

                printStream.println(record.getCustomerId() + "\t"
                        + record.getCustomerName() + "\t"
                        + record.getIsbn() + "\t"
                        + record.getBookName() + "\t"
                        + borrowTime + "\t"
                        + tip);
            }

        } else {
            for(TransactionRecord record : recordList) {
                date = new Date(Long.valueOf(record.getBorrowTime()));
                borrowTime = simpleDateFormat.format(date);

                now = new Date().getTime();
                if(now > Long.valueOf(record.getReturnTime())) {
                    returnTime = "已过期";
                } else {
                    date = new Date(Long.valueOf(record.getReturnTime()));
                    returnTime = simpleDateFormat.format(date);
                }

                printStream.println(record.getCustomerId() + "\t"
                        + record.getCustomerName() + "\t"
                        + record.getIsbn() + "\t"
                        + record.getBookName()+ "\t"
                        + borrowTime + "\t"
                        + returnTime);
            }
        }
    }

    //根据借阅次数进行排序显示(由少到多)
    public static void showBookByTimes(PrintStream printStream) {
        List<TransactionRecord> historyRecordList = TransactionUtils.getAllHistoryRecord();

        List<ResultRecord> resultRecordList = new ArrayList<>();//存resultRecord的list里面的resultRecord
        for (int i = 0; i < historyRecordList.size(); i++) {
            ResultRecord resultRecord = new ResultRecord(historyRecordList.get(i).getIsbn(), historyRecordList.get(i).getBookName());
            //如果resultRecordList里面没有这个resultrecord, 那么就在这个 i 的地方开始遍历, 得到相同的resultRecord的数目
            if(!historyRecordList.contains(resultRecord)) {
                int num = 1;
                for (int j = i + 1; j < historyRecordList.size(); j++) {
                    ResultRecord tempRecord = new ResultRecord(historyRecordList.get(j).getIsbn(), historyRecordList.get(j).getBookName());
                    if(tempRecord.equals(resultRecord)) {
                        num++;
                    }
                }
                ResultRecord realRecord = new ResultRecord(historyRecordList.get(i).getIsbn(), historyRecordList.get(i).getBookName(), num);
                resultRecordList.add(realRecord);
            }
        }

        //对resultRecordList按照次数进行排序, ResultRecord 里面重写的 compareTo 根据num来排序
        Collections.sort(resultRecordList);

        printStream.println("历史借阅结果(排序后)");
        printStream.println("isbn" + "\t\t\t\t" + "书名" + "\t\t\t" + "历史借阅次数");
        for(ResultRecord sortedRecord : resultRecordList) {
            printStream.println(sortedRecord.getIsbn() + "\t" + sortedRecord.getBookName() + "\t\t" + sortedRecord.getHistoryBorrowNum());
        }

    }

    //查看已经借阅的书籍
    private static void showBorrowedBook(PrintStream printStream, Customer customer) {
        printStream.println("isbn\t\t书名\t借书时间\t还书时间");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        String borrowTime;
        String returnTime;

        List<TransactionRecord> recordList = TransactionUtils.getNotReturnedRecord();
        for (TransactionRecord record : recordList) {
            if(record.getCustomerName().equals(customer.getUsername())) {
                long now = new Date().getTime();
                String isTime = " ";
                if(now > Long.valueOf(record.getReturnTime())) {
                    isTime = "已到期";
                }
                date = new Date(Long.valueOf(record.getBorrowTime()));
                borrowTime = simpleDateFormat.format(date);
                date = new Date(Long.valueOf(record.getReturnTime()));
                returnTime = simpleDateFormat.format(date);

                printStream.println(record.getIsbn()+"\t"+record.getBookName()+"\t"+borrowTime+"\t"+returnTime+"\t"+isTime);
            }
        }
    }

    //借书
    private static void borrowBook(BufferedReader bufferedReader, PrintStream printStream, Customer customer) throws IOException {
        boolean isQuit = false;
        while(!isQuit) {
            printStream.println("---------借书页面----------");
            printStream.println("请输入需要借的书籍 isbn 号码, <quit>退出");

            String isbn = bufferedReader.readLine().trim();

            if(isbn.equals("<quit>")) {
                isQuit = true;
                continue;
            } else if(!Pattern.matches("\\d{13}", isbn)) {
                printStream.println("输入的 isbn 号码不合法(必须是 13 位数字, 请重新输入");
                continue;
            }

            if(TransactionUtils.isBookLeft(isbn)) {
                Book book = BookUtils.queryByIsbn(isbn);
                TransactionUtils.borrowBook(customer, book);
                printStream.println("借书成功");
            } else {
                printStream.println("你要借的书籍不存在或者库存为零");
            }
        }

    }

    //还书
    private static void returnBook(BufferedReader buffered, PrintStream printStream, Customer customer) throws IOException {
        boolean isQuit = false;
        while(!isQuit) {
            printStream.println("----------还书页面----------");
            printStream.println("请输入你要还的书的 isbn 号码, <quit>退出");

            String isbn = buffered.readLine().trim();

            if(isbn.equals("<quit>")) {
                isQuit = true;
                continue;
            } else if(!Pattern.matches("\\d{13}", isbn)) {
                printStream.println("输入的 isbn 号码不合法(必须是 13 位数字, 请重新输入");
                continue;
            }

            if(TransactionUtils.returnBook(customer.getId(), isbn)) {
                printStream.println("还书成功");
            } else{
                printStream.println("还书失败, 未知错误");
            }
        }
    }


}
