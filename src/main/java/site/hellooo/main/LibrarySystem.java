package site.hellooo.main;

import site.hellooo.bean.Customer;
import site.hellooo.manager.BookManager;
import site.hellooo.manager.CustomerManager;
import site.hellooo.manager.SocketManager;
import site.hellooo.manager.TransactionManager;
import site.hellooo.utils.CustomerUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Created by cn.alpha2j on 2016/11/22.
 */
public class LibrarySystem {

    private Socket socket;
    private PrintStream printStream;
    private BufferedReader bufferedReader;
    private Customer currentCustomer; //登录的账号
    private boolean isLogin;

    public LibrarySystem() {
    }

    public LibrarySystem(Socket socket) {
        this.socket = socket;
        try {
            printStream = new PrintStream(socket.getOutputStream());
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void librarySystemIndex() {
        try {
            printStream.println("----------welcome to alpha's library manager system----------");
            printStream.println("1 登录");
            printStream.println("2 注册");
            printStream.println("输入其他退出");

            String action = bufferedReader.readLine().trim();

            switch (action) {
                case "1":
                    login();
                    break;
                case "2":
                    register();
                    break;
                default:
                    printStream.println("请按提示进行操作, 会话终止");
                    break;
            }

            if (isLogin) {
                showAfterLogin();
            }
        } catch (IOException e) {
            System.out.println("出异常" + socket.getInetAddress() + "已断开连接");
            SocketManager.close(socket);
        } finally {
            logout();
            SocketManager.close(socket);
        }
    }


    private void showAfterLogin() throws IOException {
        if (currentCustomer.isManager()) {
            boolean isQuit = false;
            while (!isQuit) {
                printStream.println("亲爱的管理员用户 " + currentCustomer.getUsername());
                printStream.println("请按提示进行输入(只能输入数字):");
                printStream.println("1. 图书管理");//图书增删查改
                printStream.println("2. 用户管理");//用户增删查改
                printStream.println("3. 书籍借阅情况查询");//查看书籍借阅情况
                printStream.println("其他任意按键退出系统");

                String action = bufferedReader.readLine().trim();

                switch (action) {
                    case "1":
                        BookManager.bookManagerIndex(bufferedReader, printStream);
                        break;
                    case "2":
                        CustomerManager.customerManagerIndex(bufferedReader, printStream);
                        break;
                    case "3":
                        TransactionManager.transactionManagerIndex(bufferedReader, printStream);
                        break;
                    default:
                        isQuit = true;
                        break;
                }
            }
        } else {
            boolean isQuit = false;
            while (!isQuit) {
                printStream.println("亲爱的用户 " + currentCustomer.getUsername());
                printStream.println("请按提示进行输入(只能输入数字):");
                printStream.println("1. 图书查询");
                printStream.println("2. 图书借阅");
                printStream.println("其他任意键退出");

                String action = bufferedReader.readLine().trim();

                switch (action) {
                    case "1":
                        BookManager.forCustomerIndex(bufferedReader, printStream);
                        break;
                    case "2":
                        TransactionManager.forCustomerIndex(bufferedReader, printStream, currentCustomer);
                        break;
                    default:
                        isQuit = true;
                        break;
                }
            }
        }
    }

    //登录
    private void login() throws IOException {
        boolean isSuccess = false;

        while (!isSuccess) {
            printStream.println("----------请登录----------");

            printStream.println("用户名(英文,数字,或下划线 长度: 4-20个字符):");
            String username = bufferedReader.readLine().trim();
            printStream.println("密码(英文,数字,!@#$ 长度: 4-30个字符 )");
            String password = bufferedReader.readLine().trim();

            //输入不合法不进行查询
            if (!Pattern.matches("\\w{4,20}", username) || !Pattern.matches("[\\w!@#$]{4,30}", password)) {
                printStream.println("用户名或者密码不合法, <quit>退出, 任意字符继续");

                String action = bufferedReader.readLine().trim();

                if (action.equals("<quit>")) {
                    break;
                } else {
                    continue;
                }
            }

            Customer customer = CustomerUtils.getCustomer(username, password);

            if (customer != null) {
                printStream.println("登录成功");
                currentCustomer = customer;
                isLogin = true;
                isSuccess = true;
            } else {
                printStream.println("登录失败 1重新登录 其他任意键退出系统");
                String action = bufferedReader.readLine().trim();
                if (action.equals("1")) {
                    continue;
                } else {
                    break;//任意键退出
                }
            }
        }
    }//end of login()

    //注册
    private void register() throws IOException {
        boolean isSuccess = false;

        while (!isSuccess) {
            printStream.println("----------注册页面----------");
            printStream.println("用户名(英文,数字,或下划线 长度: 4-20个字符):");
            String username = bufferedReader.readLine().trim();
            printStream.println("密码(英文,数字,!@#$ 长度: 4-30个字符 )");
            String password = bufferedReader.readLine().trim();

            //输入不合法不能注册
            if (!Pattern.matches("\\w{4,20}", username) || !Pattern.matches("[\\w!@#$]{4,30}", password)) {
                printStream.println("用户名或者密码不合法, <quit>退出, 任意字符继续");

                String action = bufferedReader.readLine().trim();

                if (action.equals("<quit>")) {
                    break;
                } else {
                    continue;
                }
            }

            if (CustomerUtils.addCustomer(username, password)) {
                isSuccess = true;
                printStream.println("注册成功, 即将跳转到登录页面");
                login();
            } else {
                printStream.println("用户名已存在或输入不合法(用户名20英文字符以内, 密码30个字符内, 不能包含 '-' )");
                printStream.println("1 重新注册, 其他任意键退出系统");
                String action = bufferedReader.readLine().trim();
                if (action.equals("1")) {
                    continue;
                } else {
                    break;
                }
            }
        }
    }//end of register method

    //退出系统
    private void logout() {
        printStream.println("退出系统....");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(socket.getInetAddress() + "  logout at " + simpleDateFormat.format(new Date()));
    }
}
