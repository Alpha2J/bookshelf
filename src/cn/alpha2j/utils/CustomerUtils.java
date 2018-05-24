package cn.alpha2j.utils;

import cn.alpha2j.bean.Customer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cn.alpha2j on 2016/11/23.
 * 1. 用户登录
 * 2. 用户注册
 * 3. 删除用户
 * 4. 更新用户信息
 *
 * 另: 解析用户文件也定义在这好了
 *
 * 注: 等完成后还要对用户名和密码长度等其他写入文件的字段合法性进行判断
 */
public class CustomerUtils {

    private static final String RECORD_FILE = "D://librarySystemFile/customer.txt";//用户信息记录文件
    private static final String SEPARATOR = "---";  //名字和密码等之间的分隔符
    private static final int RECORD_SIZE = 75; //每条记录长度, 字节为单位, id:7, username: 20, password: 30, isManager: 5, 3个separator: 9, 共71, 给75

    /**
     * 根据用户名和密码获取一个Customer对象
     *
     * @param username
     * @param password
     * @return
     */
    public static Customer getCustomer(String username, String password) {
        RandomAccessFile recordFile = null;

        try {
            recordFile = FileUtils.getRandomAccessFile(CustomerUtils.RECORD_FILE, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if(recordFile == null) {
            return null;
        }

        String record;  //存返回的数据
        String[] splitRecord;   //split后的record
        Customer customer = null;

        try {
            while(recordFile.getFilePointer() != recordFile.length()) {
                record = FileUtils.readSpecificRecord(CustomerUtils.RECORD_SIZE, recordFile).trim();//如果separator为'---'
                splitRecord = record.split(CustomerUtils.SEPARATOR);                           //则标准格式 1---admin---admin---true---[1,2,3]
                if(splitRecord[1].equals(username) && splitRecord[2].equals(password)) {
                    customer = new Customer(splitRecord[0], splitRecord[1], splitRecord[2], Boolean.valueOf(splitRecord[3]));
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            FileUtils.releaseRandomAccessFile(recordFile);
        }

        return customer;
    }

    //增加用户
    public static boolean addCustomer(String username, String password) {
        boolean isSuccess = false;

        //用户名不允许重复
        if(isCustomerExist(username)) {
            return false;
        }

        RandomAccessFile recordFile = null;

        try {
            recordFile = FileUtils.getRandomAccessFile(CustomerUtils.RECORD_FILE, "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if(recordFile == null) {
            return isSuccess;
        }

        try {
            int customerNum = getLastCustomerNum(recordFile) + 1;
            Customer customer = new Customer(String.valueOf(customerNum), username, password);

            String recordString = formatCustomer(customer);

            recordFile.seek(recordFile.length()); //把指针放到最后, 在后面添加用户

            isSuccess = FileUtils.writeFixedString(recordString, CustomerUtils.RECORD_SIZE, recordFile);//记录写进文件
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            FileUtils.releaseRandomAccessFile(recordFile);//关闭文件
        }

        return isSuccess;
    }

    //根据用户名删除用户
    public static boolean deleteCustomer(String username) {
        boolean isSuccess = false;

        //如果用户不存在, false
        if(!isCustomerExist(username)) {
            return isSuccess;
        }

        List<Customer> recordList = getAllCustomer();

        OutputStream refreshStream = null;
        OutputStream appendStream = null;
        DataOutputStream refreshDataOutputStream = null;
        DataOutputStream appendDataOutputStream = null;

        try {
            refreshStream = new FileOutputStream(CustomerUtils.RECORD_FILE);
            appendStream = new FileOutputStream(CustomerUtils.RECORD_FILE, true);
            refreshDataOutputStream = new DataOutputStream(refreshStream);
            appendDataOutputStream = new DataOutputStream(appendStream);

            boolean isFirst = true;
            for (int i = 0; i < recordList.size(); i++) {
                if(recordList.get(i).getUsername().equals(username)) {
                    continue;
                }
                if(isFirst) {
                    FileUtils.writeFixedString(formatCustomer(recordList.get(i)), CustomerUtils.RECORD_SIZE, refreshDataOutputStream);

                    release(refreshDataOutputStream, refreshStream);

                    isFirst = false;
                } else {
                    FileUtils.writeFixedString(formatCustomer(recordList.get(i)), CustomerUtils.RECORD_SIZE, appendDataOutputStream);
                }
            }

            isSuccess = true;   //文件写完了就算成功了
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            release(refreshDataOutputStream, refreshStream);
            release(appendDataOutputStream, appendStream);
        }

        return isSuccess;
    }


    //获取所有用户
    public static List<Customer> getAllCustomer() {
        RandomAccessFile recordFile = null;
        List<Customer> customerList = new ArrayList<Customer>();

        try {
            recordFile = new RandomAccessFile(CustomerUtils.RECORD_FILE, "r");

            String record;
            String[] splitRecord;
            while(recordFile.getFilePointer() != recordFile.length()) {
                record = FileUtils.readSpecificRecord(CustomerUtils.RECORD_SIZE, recordFile).trim();
                splitRecord = record.split(CustomerUtils.SEPARATOR);
                customerList.add(new Customer(splitRecord[0], splitRecord[1], splitRecord[2], Boolean.valueOf(splitRecord[3])));
            }
        } catch(IOException e) {
            e.printStackTrace();
        }

        FileUtils.releaseRandomAccessFile(recordFile);
        return customerList;
    }


    private static int getLastCustomerNum(RandomAccessFile recordFile) throws IOException {
        if(recordFile.length() == 0) {
            return 0;
        }

        recordFile.seek(recordFile.length() - (CustomerUtils.RECORD_SIZE + 2));

        String record = FileUtils.readSpecificRecord(CustomerUtils.RECORD_SIZE, recordFile).trim();
        String[] splitRecord = record.split(CustomerUtils.SEPARATOR);

        return Integer.valueOf(splitRecord[0]);
    }

    //以String返回格式化后的book对象,如  1---admin---admin---false
    private static String formatCustomer(Customer customer) {
        return formatCustomer(customer, CustomerUtils.SEPARATOR);
    }

    public static String formatCustomer(Customer customer, String separator) {
        StringBuilder stringBuilder = new StringBuilder();

        //格式化
        stringBuilder.append(customer.getId());
        stringBuilder.append(separator);
        stringBuilder.append(customer.getUsername());
        stringBuilder.append(separator);
        stringBuilder.append(customer.getPassword());
        stringBuilder.append(separator);
        stringBuilder.append(String.valueOf(customer.isManager()));

        return stringBuilder.toString();
    }

    //判断用户是否存在
    private static boolean isCustomerExist(String username) {
        RandomAccessFile recordFile = null;

        try {
            recordFile = FileUtils.getRandomAccessFile(CustomerUtils.RECORD_FILE, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if(recordFile == null) {
            return false;
        }

        String record;  //存返回的数据
        String[] splitRecord;   //split后的record
        try {
            while(recordFile.getFilePointer() != recordFile.length()) {
                record = FileUtils.readSpecificRecord(CustomerUtils.RECORD_SIZE, recordFile);//如果separator为'---'
                splitRecord = record.split(CustomerUtils.SEPARATOR);                           //则标准格式1---admin---admin---true---[1,2,3]
                if(splitRecord[1].equals(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            FileUtils.releaseRandomAccessFile(recordFile);//return 返回之前还是会执行这个
        }

        return false;
    }

    //关闭各种流
    private static void release(DataOutputStream dataOutputStream, OutputStream outputStream) {
        if(dataOutputStream != null) {
            try {
                dataOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

















