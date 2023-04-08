package site.hellooo.utils;

import site.hellooo.bean.Book;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cn.alpha2j on 2016/11/24.
 */
public class BookUtils {

    private static final String RECORD_FILE = "D://librarySystemFile/book.txt";//用户信息记录文件
    private static final String SEPARATOR = "---";
    //每条记录长度, 字节为单位, isbn: 13, bookName(20中文): 60, author(15中文): 45, press(10中文): 30, totalNum: 3, 4个separator: 12 总153
    private static final int RECORD_SIZE = 153;


    //增加书籍
    public static boolean addBook(Book book) {
        boolean isSuccess = false;

        RandomAccessFile randomAccessFile = null;

        try {
            randomAccessFile = FileUtils.getRandomAccessFile(BookUtils.RECORD_FILE, "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //要是抛出异常就返回false
        if (randomAccessFile == null) {
            return isSuccess;
        }

        try {
            randomAccessFile.seek(randomAccessFile.length());//指针移到最后, 在最后加书
            String recordString = formatBook(book);
            isSuccess = FileUtils.writeFixedString(recordString, BookUtils.RECORD_SIZE, randomAccessFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileUtils.releaseRandomAccessFile(randomAccessFile);
        return isSuccess;
    }

    //更新书籍记录
    public static boolean updateBookNum(String isbn, int updateNum) {
        RandomAccessFile randomAccessFile = null;

        try {
            randomAccessFile = FileUtils.getRandomAccessFile(BookUtils.RECORD_FILE, "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //假如上面抛出了异常的话那么下面这些还是会运行, 但是randomAccessFile 为null, 就全乱了
        if (randomAccessFile == null) {
            return false;
        }

        Book record = queryByIsbn(isbn, randomAccessFile);

        if (record == null) {
            FileUtils.releaseRandomAccessFile(randomAccessFile);
            return false;
        }

        int totalNum = updateNum + Integer.valueOf(record.getTotalNum());
        record.setTotalNum(String.valueOf(totalNum));
        String recordString = formatBook(record);

        //文件指针指向更新的记录开头, 覆盖这条记录
        try {
            randomAccessFile.seek(randomAccessFile.getFilePointer() - (BookUtils.RECORD_SIZE + 2));
            FileUtils.writeFixedString(recordString, BookUtils.RECORD_SIZE, randomAccessFile);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            FileUtils.releaseRandomAccessFile(randomAccessFile);
        }

        return true;
    }

    /**
     * 根据 isbn 删除书籍
     *
     * @param isbn
     * @return
     */
    public static boolean deleteBook(String isbn) {
        //不存在该书籍
        if (queryByIsbn(isbn) == null) {
            return false;
        }

        //将所有记录放到list里面, 等下删除了记录再把记录用DataOutputStream 写回文件(DataOutputStream 实现了DataOutput接口)
        List<Book> recordList = getAllBook();

        OutputStream refreshStream = null;
        DataOutputStream refreshDataOutputStream = null;
        OutputStream appendStream = null;
        DataOutputStream appendDataOutputStream = null;
        try {
            refreshStream = new FileOutputStream(BookUtils.RECORD_FILE);//第一次打开文件需要把全部东西都清掉,append默认是false的
            appendStream = new FileOutputStream(BookUtils.RECORD_FILE, true);//执行第一次后记录要加在后面
            refreshDataOutputStream = new DataOutputStream(refreshStream);
            appendDataOutputStream = new DataOutputStream(appendStream);

            boolean isFirst = true;
            for (int i = 0; i < recordList.size(); i++) {
                if (recordList.get(i).getIsbn().equals(isbn)) {
                    //recordList.remove(i);  不要remove, 不然后面的i就乱了
                    continue;
                }
                if (isFirst) {
                    //第一次要清空文件再写
                    FileUtils.writeFixedString(formatBook(recordList.get(i)), BookUtils.RECORD_SIZE, refreshDataOutputStream);

                    release(refreshDataOutputStream, refreshStream);//如果执行完这步了就先把他关了

                    isFirst = false;
                } else {
                    FileUtils.writeFixedString(formatBook(recordList.get(i)), BookUtils.RECORD_SIZE, appendDataOutputStream);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            release(refreshDataOutputStream, refreshStream);//再写一遍, 假如上面 isFirst 没有执行的话那么就没有关掉
            release(appendDataOutputStream, appendStream);  //关闭appendStream
        }

        return true;
    }

    //查询所有图书
    public static List<Book> getAllBook() {
        RandomAccessFile randomAccessFile = null;
        List<Book> bookList = new ArrayList<Book>();

        try {
            randomAccessFile = FileUtils.getRandomAccessFile(BookUtils.RECORD_FILE, "r");  //查询书籍, 只读

            String record;
            String[] splitRecord;
            while (randomAccessFile.getFilePointer() != randomAccessFile.length()) {
                record = FileUtils.readSpecificRecord(BookUtils.RECORD_SIZE, randomAccessFile).trim();
                splitRecord = record.split(BookUtils.SEPARATOR);
                bookList.add(new Book(splitRecord[0], splitRecord[1], splitRecord[2], splitRecord[3], splitRecord[4]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            FileUtils.releaseRandomAccessFile(randomAccessFile);
        }

        return bookList;
    }

    //根据isbn查询书籍是否存在, 这个方法主要是给 addBook() 用的
    private static Book queryByIsbn(String isbn, RandomAccessFile recordFile) {
        Book book = null;

        try {
            recordFile.seek(0);//从文件开头开始扫描
            String record;  //存返回的数据
            String[] splitRecord;   //split后的record
            while (recordFile.getFilePointer() != recordFile.length()) {
                record = FileUtils.readSpecificRecord(BookUtils.RECORD_SIZE, recordFile).trim();//要去掉后面空格
                splitRecord = record.split(BookUtils.SEPARATOR);
                if (splitRecord[BookField.ISBN.toValue()].equals(isbn)) {
                    book = new Book(splitRecord[0], splitRecord[1], splitRecord[2], splitRecord[3], splitRecord[4]);
                    break; //一本书只有一个isbn, 找到后就不往下扫描了
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return book;
    }

    //根据isbn查询书籍, isbn是唯一的
    public static Book queryByIsbn(String isbn) {
        RandomAccessFile randomAccessFile = null;
        Book book = null;

        try {
            randomAccessFile = FileUtils.getRandomAccessFile(BookUtils.RECORD_FILE, "r");
            book = queryByIsbn(isbn, randomAccessFile);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            FileUtils.releaseRandomAccessFile(randomAccessFile);
        }

        return book;
    }

    //根据书名查询书籍
    public static List<Book> queryByBookName(String bookName) {
        List<Book> bookList = null;

        try {
            bookList = queryForList(bookName, BookField.BOOKNAME.toValue());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bookList;
    }

    //根据作者查询书籍
    public static List<Book> queryByAuthor(String author) {
        List<Book> bookList = null;

        try {
            bookList = queryForList(author, BookField.AUTHOR.toValue());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bookList;
    }

    //根据出版社查询书籍
    public static List<Book> queryByPress(String press) {
        List<Book> bookList = null;

        try {
            bookList = queryForList(press, BookField.PRESS.toValue());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bookList;
    }

    //查询, 返回list
    private static List<Book> queryForList(String param, int field) throws IOException {
        RandomAccessFile randomAccessFile = FileUtils.getRandomAccessFile(BookUtils.RECORD_FILE, "r");
        List<Book> bookList = new ArrayList<>();

        String record;
        String[] splitRecord;

        while (randomAccessFile.getFilePointer() != randomAccessFile.length()) {
            record = FileUtils.readSpecificRecord(BookUtils.RECORD_SIZE, randomAccessFile).trim();
            splitRecord = record.split(BookUtils.SEPARATOR);

            if (splitRecord[field].equals(param)) {
                bookList.add(new Book(splitRecord[0], splitRecord[1], splitRecord[2], splitRecord[3], splitRecord[4]));
            }
        }

        FileUtils.releaseRandomAccessFile(randomAccessFile);

        return bookList;
    }

    //以String返回格式化后的book对象,如  9787121261886---数据结构(java版)(第4版)---叶核亚---电子工业出版社---3
    public static String formatBook(Book book) {
        return formatBook(book, BookUtils.SEPARATOR);
    }

    public static String formatBook(Book book, String separator) {
        StringBuilder stringBuilder = new StringBuilder();

        //格式化
        stringBuilder.append(book.getIsbn());
        stringBuilder.append(separator);
        stringBuilder.append(book.getBookName());
        stringBuilder.append(separator);
        stringBuilder.append(book.getAuthor());
        stringBuilder.append(separator);
        stringBuilder.append(book.getPress());
        stringBuilder.append(separator);
        stringBuilder.append(book.getTotalNum());

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

    enum BookField {
        ISBN(0), BOOKNAME(1), AUTHOR(2), PRESS(3);

        int value;

        private BookField(int value) {
            this.value = value;
        }

        private int toValue() {
            return value;
        }
    }
}
