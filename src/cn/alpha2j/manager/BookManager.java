package cn.alpha2j.manager;

import cn.alpha2j.bean.Book;
import cn.alpha2j.utils.BookUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cn.alpha2j on 2016/11/26.
 *
 * 书籍管理
 */
public class BookManager {

    private static final String SEPARATOR = "   ";
    //所有异常都抛到调用他的主页面处理
    public static void bookManagerIndex(BufferedReader bufferedReader, PrintStream printStream) throws IOException {
        boolean isQuit = false;
        String action;
        while(!isQuit) {
            printStream.println("----------图书管理页面----------");
            printStream.println("1 显示所有图书");
            printStream.println("2 增加图书");
            printStream.println("3 删除图书");
            printStream.println("4 查找图书");
            printStream.println("其他任意键退出图书管理页面");

            action = bufferedReader.readLine().trim();

            switch(action) {
                case "1" :
                    showAllBooks(printStream);
                    break;
                case "2" :
                    addBooks(bufferedReader, printStream);
                    break;
                case "3" :
                    deleteBooks(bufferedReader, printStream);
                    break;
                case "4" :
                    findBooks(bufferedReader, printStream);
                default :
                    isQuit = true;
                    break;
            }
        }
    }

    //普通用户用
    public static void forCustomerIndex(BufferedReader bufferedReader, PrintStream printStream) throws IOException {
        boolean isQuit = false;
        String action;
        while(!isQuit) {
            printStream.println("----------图书查询页面----------");
            printStream.println("1 显示所有图书");
            printStream.println("2 查找图书");
            printStream.println("其他任意键退出图书查询页面");

            action = bufferedReader.readLine().trim();

            switch(action) {
                case "1" :
                    showAllBooks(printStream);
                    break;
                case "2" :
                    findBooks(bufferedReader, printStream);
                    break;
                default :
                    isQuit = true;
                    break;
            }
        }
    }

    //显示所有书籍
    private static void showAllBooks(PrintStream printStream) {
        printStream.println("----------显示所有书籍----------");
        List<Book> allBook = BookUtils.getAllBook();

        for (int i = 0; i < allBook.size(); i++) {
            printStream.println(i+ "  " + BookUtils.formatBook(allBook.get(i), "   "));
        }
    }

    //增加书籍
    private static void addBooks(BufferedReader bufferedReader, PrintStream printStream) throws IOException {
        boolean isQuit = false;
        String action;

        //用来判断输入中是否有 '-' 号
        Pattern pattern = Pattern.compile("-+");
        Matcher matcher = null;

        byte bytes[];//存输入的书名或作者或出版社的 utf-8 编码数组

        while(!isQuit) {
            printStream.println("----------增加书籍----------");
            printStream.println("请输入isbn(13个数字)");
            String isbn = bufferedReader.readLine().trim();
            if(!Pattern.matches("\\d{13}", isbn)) {
                printStream.println("isbn输入不合法(必须是13个数字), 任意字符继续, <quit>退出");
                action = bufferedReader.readLine().trim();
                if(action.equals("<quit>")) {
                    isQuit = true;
                    continue;
                } else {
                    continue;
                }
            }

            printStream.println("请输入书名(中文20个以内, 不能有 字符'-')");
            String bookName = bufferedReader.readLine().trim();
            bytes = bookName.getBytes("utf-8");
            matcher = pattern.matcher(bookName);
            if(bytes.length == 0 || bytes.length > 60 || matcher.find()) {
                printStream.println("书名输入不合法(中文20个以内, 不能有 字符'-'), 任意字符继续, <quit>退出");
                action = bufferedReader.readLine().trim();
                if(action.equals("<quit>")) {
                    isQuit = true;
                    continue;
                } else {
                    continue;
                }
            }

            printStream.println("请输入作者(中文15个以内, 不能有 字符'-')");
            String author = bufferedReader.readLine().trim();
            bytes = author.getBytes("utf-8");
            matcher = pattern.matcher(author);
            if(bytes.length == 0 || bytes.length > 45 || matcher.find()) {
                printStream.println("作者名输入不合法(中文15个以内, 不能有 字符'-'), 任意字符继续, <quit>退出");
                action = bufferedReader.readLine().trim();
                if(action.equals("<quit>")) {
                    isQuit = true;
                    continue;
                } else {
                    continue;
                }
            }


            printStream.println("请输入出版社(中文10个以内, 不能有 字符'-')");
            String press = bufferedReader.readLine().trim();
            bytes = press.getBytes("utf-8");
            matcher = pattern.matcher(press);
            if(bytes.length == 0 || bytes.length > 30 || matcher.find()) {
                printStream.println("出版社输入不合法(中文10个以内, 不能有 字符'-'), 任意字符继续, <quit>退出");
                action = bufferedReader.readLine().trim();
                if(action.equals("<quit>")) {
                    isQuit = true;
                    continue;
                } else {
                    continue;
                }
            }

            printStream.println("请输入书本数(3位数字以内, 正整数)");
            String totalNum = bufferedReader.readLine().trim();
            if(!Pattern.matches("\\d{1,3}", totalNum) || Integer.valueOf(totalNum) < 0) {
                printStream.println("书本数输入不合法(3位数字以内, 正整数), 任意字符继续, <quit>退出");
                action = bufferedReader.readLine().trim();
                if(action.equals("<quit>")) {
                    isQuit = true;
                    continue;
                } else {
                    continue;
                }
            }

            //如果已经存在这本书了那么更新书籍总数
            Book book = BookUtils.queryByIsbn(isbn);
            if(book != null) {
                //更新后的书籍总数
                int updateNum = Integer.valueOf(totalNum);
                int tempNum = Integer.valueOf(book.getTotalNum()) + updateNum;
                if(tempNum > 999) {
                    printStream.println("超过单种书的最大数目(999本), 添加失败");
                } else {
                    BookUtils.updateBookNum(isbn, updateNum);
                }
            } else {
                //如果书籍不存在, 在后面添加
                book = new Book(isbn, bookName, author, press, totalNum);
                BookUtils.addBook(book);
            }

            printStream.println("增加成功, 任意键继续, <quit>退出");

            action = bufferedReader.readLine().trim();

            if(action.equals("<quit>")) {
                isQuit = true;
                continue;
            }
        }
    }

    //删除图书
    private static void deleteBooks(BufferedReader bufferedReader, PrintStream printStream) throws IOException {
        boolean isQuit = false;
        String action;

        while(!isQuit) {
            printStream.println("----------删除书籍----------");
            printStream.println("请输入你要删除的书籍isbn号码");
            String isbn = bufferedReader.readLine().trim();
            if(!Pattern.matches("\\d{13}", isbn)) {
                printStream.println("isbn不合法(13位数字), 任意键继续, <quit>退出");
                action = bufferedReader.readLine().trim();
                if(action.equals("<quit>")) {
                    isQuit = true;
                    continue;
                } else {
                    continue;
                }
            }

            Book book = BookUtils.queryByIsbn(isbn);

            if(book == null) {
                printStream.println("不存在这本书, 任意键继续, <quit>退出");
                action = bufferedReader.readLine().trim();
                if(action.equals("<quit>")) {
                    isQuit = true;
                    continue;
                } else {
                    continue;
                }
            }

            boolean isSuccess = false;
            isSuccess = BookUtils.deleteBook(isbn);

            if(isSuccess) {
                printStream.println("删除成功, 任意键继续, <quit>退出");
            } else {
                printStream.println("删除失败, 任意键继续, <quit>退出");
            }

            action = bufferedReader.readLine().trim();

            if(action.equals("<quit>")) {
                isQuit = true;
                continue;
            } else {
                continue;
            }
        }
    }

    //查找书籍, 异常通通抛出去
    private static void findBooks(BufferedReader bufferedReader, PrintStream printStream) throws IOException {
        boolean isQuit = false;
        while(!isQuit) {
            printStream.println("----------查找书籍----------");
            printStream.println("1 根据isbn查询");
            printStream.println("2 根据书名查询");
            printStream.println("3 根据作者进行查询");
            printStream.println("4 根据出版社进行查询");
            printStream.println("其他任意键退出 查找书籍页面");

            String action = bufferedReader.readLine().trim();

            switch (action) {
                case "1" :
                    findBookByIsbn(bufferedReader, printStream);
                    break;
                case "2" :
                    findBookByName(bufferedReader, printStream);
                    break;
                case "3" :
                    findBookByAuthor(bufferedReader, printStream);
                    break;
                case "4" :
                    findBookByPress(bufferedReader, printStream);
                    break;
                default:
                    isQuit = true;
                    break;
            }
        }

    }

    //根据isbn查找
    private static void findBookByIsbn(BufferedReader bufferedReader, PrintStream printStream) throws IOException {
        boolean isQuit = false;
        while(!isQuit) {
            printStream.println("----------根据isbn查找书籍----------");
            printStream.println("请输入13位的isbn号码, <quit> 退出");

            String isbn = bufferedReader.readLine().trim();

            //<quit>退出
            if(isbn.equals("<quit>")) {
                isQuit = true;
                continue;
            }

            //如果输入的不是13位的 数字, 那么continue
            if(!Pattern.matches("\\d{13}", isbn)) {
                printStream.println("输入的isbn不合法(只能是13个数字)");
                continue;
            }

            Book record = BookUtils.queryByIsbn(isbn);

            if(record == null) {
                printStream.println("没有该条记录");
                continue;
            }

            String recordString = BookUtils.formatBook(record, "   ");

            printStream.println("查询结果:");
            printStream.println(recordString);
        }
    }

    //根据书名查找
    private static void findBookByName(BufferedReader bufferedReader, PrintStream printStream) throws IOException {
        boolean isQuit = false;
        while(!isQuit) {
            printStream.println("----------根据书名查找书籍----------");
            printStream.println("请输入书名, <quit> 退出");

            String bookName = bufferedReader.readLine().trim();

            //<quit> 退出
            if(bookName.equals("<quit>")) {
                isQuit = true;
                continue;
            }

            List<Book> recordList = BookUtils.queryByBookName(bookName);

            if(recordList == null) {
                printStream.println("没有这本书");
                continue;
            }

            printStream.println("查询结果:");
            for (Book record : recordList) {
                String recordStr = BookUtils.formatBook(record, "   ");
                printStream.println(recordStr);
            }
        }
    }

    //根据作者查询
    private static void findBookByAuthor(BufferedReader bufferedReader, PrintStream printStream) throws IOException {
        boolean isQuit = false;
        while(!isQuit) {
            printStream.println("----------根据作者查找书籍----------");
            printStream.println("请输入作者名字, <quit> 退出");

            String author = bufferedReader.readLine().trim();

            //<quit> 退出
            if(author.equals("<quit>")) {
                isQuit = true;
                continue;
            }

            List<Book> recordList = BookUtils.queryByAuthor(author);

            if(recordList == null) {
                printStream.println("没有这这个作者的书");
                continue;
            }

            printStream.println("查询结果:");
            for (Book record : recordList) {
                String recordStr = BookUtils.formatBook(record, "   ");
                printStream.println(recordStr);
            }
        }
    }

    //根据出版社查询
    private static void findBookByPress(BufferedReader bufferedReader, PrintStream printStream) throws IOException {
        boolean isQuit = false;
        while(!isQuit) {
            printStream.println("----------根据出版社查找书籍----------");
            printStream.println("请输入出版社名字, <quit> 退出");

            String press = bufferedReader.readLine().trim();

            //<quit>退出
            if(press.equals("<quit>")) {
                isQuit = true;
                continue;
            }

            List<Book> recordList = BookUtils.queryByPress(press);

            if(recordList == null) {
                printStream.println("没有这个出版社的书");
                continue;
            }

            printStream.println("查询结果:");
            for (Book record : recordList) {
                String recordStr = BookUtils.formatBook(record, "   ");
                printStream.println(recordStr);
            }
        }
    }

}
