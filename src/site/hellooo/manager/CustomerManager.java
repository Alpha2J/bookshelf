package cn.alpha2j.manager;

import cn.alpha2j.bean.Customer;
import cn.alpha2j.utils.CustomerUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by cn.alpha2j on 2016/11/26.
 */
public class CustomerManager {

    public static void customerManagerIndex(BufferedReader bufferedReader, PrintStream printStream) throws IOException {
        boolean isQuit = false;
        while (!isQuit) {
            printStream.println("----------用户管理页面----------");
            printStream.println("1 显示所有用户");
            printStream.println("2 添加用户");
            printStream.println("3 删除用户");
            printStream.println("其他任意键退出");

            String action = bufferedReader.readLine();

            switch (action) {
                case "1":
                    showAllCustomers(printStream);
                    break;
                case "2":
                    addCustomers(bufferedReader, printStream);
                    break;
                case "3":
                    deleteCustomers(bufferedReader, printStream);
                    break;
                default:
                    isQuit = true;
                    break;
            }
        }
    }//end of index method

    //显示所有用户
    private static void showAllCustomers(PrintStream printStream) {
        printStream.println("----------显示所有用户----------");
        List<Customer> allCustomer = CustomerUtils.getAllCustomer();

        for (Customer customer : allCustomer) {
            printStream.println("id: " + customer.getId() + "  用户名:" + customer.getUsername());
        }
    }

    //添加用户
    private static void addCustomers(BufferedReader bufferedReader, PrintStream printStream) throws IOException {
        boolean isQuit = false;
        while (!isQuit) {
            printStream.println("----------添加用户----------");

            printStream.println("用户名(英文,数字,或下划线 长度: 4-20个字符):");
            String username = bufferedReader.readLine();
            printStream.println("密码(英文,数字,!@#$ 长度: 4-30个字符 )");
            String password = bufferedReader.readLine();

            String action;

            if (!Pattern.matches("\\w{4,20}", username) || !Pattern.matches("[\\w!@#$]{4,30}", password)) {
                printStream.println("用户名或者密码不合法, <quit>退出, 任意字符继续");

                action = bufferedReader.readLine().trim();

                if (action.equals("<quit>")) {
                    isQuit = true;
                    continue;
                } else {
                    continue;
                }
            }

            Boolean isSuccess = CustomerUtils.addCustomer(username, password);

            if (isSuccess) {
                printStream.println("添加成功, <quit>退出, 任意键继续");
            } else {
                printStream.println("用户名已经存在, 添加失败, <quit>退出, 任意键继续");
            }

            action = bufferedReader.readLine().trim();

            if (action.equals("<quit>")) {
                isQuit = true;
            }
        }
    }

    //删除用户
    private static void deleteCustomers(BufferedReader bufferedReader, PrintStream printStream) throws IOException {
        boolean isQuit = false;
        while (!isQuit) {
            printStream.println("----------删除用户----------");

            printStream.println("请输入需要删除的用户名:");
            String username = bufferedReader.readLine().trim();

            boolean isSuccess = CustomerUtils.deleteCustomer(username);

            if (isSuccess) {
                printStream.println("删除成功, 任意键继续, <quit>退出");
            } else {
                printStream.println("删除失败, 任意键继续, <quit>退出");
            }

            String action = bufferedReader.readLine().trim();

            if (action.equals("<quit>")) {
                isQuit = true;
            }
        }
    }

}
