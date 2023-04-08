package cn.alpha2j.utils;

import java.io.*;

/**
 * Created by cn.alpha2j on 2016/11/24.
 */
public class FileUtils {

    /**
     * 获取带指针的文件对象
     *
     * @param name 文件名字, 包括路径
     * @param mode
     * @return
     * @throws FileNotFoundException
     */
    public static RandomAccessFile getRandomAccessFile(String name, String mode) throws FileNotFoundException {
        return new RandomAccessFile(name, mode);
    }


    /**
     * 关闭文件
     *
     * @param randomAccessFile
     */
    public static void releaseRandomAccessFile(RandomAccessFile randomAccessFile) {
        if (randomAccessFile != null) {
            try {
                randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 将每条记录以固定长度写到文件中(utf-8)编码
     *
     * @param record
     * @param recordSize
     * @param output
     * @return
     */
    public static boolean writeFixedString(String record, int recordSize, DataOutput output) {
        boolean isSuccess = false;

        if (record == null) {
            throw new NullPointerException();
        }

        //用utf-8编码方式获取传进来的String 的byte[]
        byte[] bytes = null;
        try {
            bytes = record.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //每条记录的byte长度是传进来的参数length + 2, 不足length长的话在后面用空字符的byte填充, 后面的 2 用来存 \r\n 换行
        byte[] tempBytes = new byte[recordSize + 2];
        byte tempByte;
        for (int i = 0; i < recordSize; i++) {
            tempByte = 32;           //不够length长度的话用空字符填充, 32代表的是空字符
            if (i < bytes.length) {
                tempByte = bytes[i];
            }
            tempBytes[i] = tempByte;
        }

        tempBytes[recordSize] = 13;  // \r的int值
        tempBytes[recordSize + 1] = 10; // \n的int值
        try {
            output.write(tempBytes);
            isSuccess = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return isSuccess;
    }

    /**
     * 从文件读一条固定字节大小的记录
     *
     * @param recordSize       写入文件记录大小, 字节为单位
     * @param randomAccessFile
     * @return
     */
    public static String readSpecificRecord(int recordSize, RandomAccessFile randomAccessFile) {
//recordSize还要做判断,先不管它
        byte[] record = new byte[recordSize];

        try {
            randomAccessFile.read(record);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String recordStr = null;
        try {
            recordStr = new String(record, "utf-8");  //编码也必须要用 utf-8
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            randomAccessFile.seek(randomAccessFile.getFilePointer() + 2);  //每条记录后面都有 \r\n 换行符, 所以读完后要把指针后移两位
        } catch (IOException e) {
            e.printStackTrace();
        }

        return recordStr;
    }


}














