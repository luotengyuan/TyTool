package com.lois.tytool.basej.io;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @Description 数据流转换工具类
 * @Author Luo.T.Y
 * @Date 2017-09-22
 * @Time 19:45
 */
public class StreamUtils {

    /**
     * 读取流中指定长度的字节数据
     * @param inputStream 输入流
     * @param dataLength 指定长度
     * @return 字节数组
     * @throws IOException IO异常
     */
    public static byte[] read(InputStream inputStream, int dataLength) throws IOException {
        byte[] data = new byte[dataLength];
        int offset = 0;
        int len = dataLength;
        int tmpIndex;
        while (-1 != (tmpIndex = inputStream.read(data, offset, len)) ) {
            offset += tmpIndex;
            if (offset >= dataLength) {
                break;
            }
            len = dataLength - offset;
        }
        return data;
    }

    public static byte[] readInputStreamToByteArray(InputStream inStream) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1){
            outStream.write(buffer,0, len);
        }
        return outStream.toByteArray();
    }

    public static String readInputStreamToString(InputStream inStream) throws IOException{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len=inStream.read(buffer)) != -1){
            outStream.write(buffer,0,len);
        }
        try {
            String str = outStream.toString();
            return str;
        } finally {
            if (outStream != null) {
                outStream.close();
            }
        }
    }

    /**
     * 输入流转换为字符串
     *
     * @param inputStream 输入流
     * @param encoding    编码格式
     * @return 转换后的字符串
     * @throws Exception
     */
    public static String readInputStreamToString(InputStream inputStream, String encoding) throws Exception {

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, encoding));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
        }
        inputStream.close();
        return sb.toString();
    }

    public static File readInputStreamToFile(InputStream inStream , File file) throws IOException{
        @SuppressWarnings("resource")
        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int len=0;
            while ((len=inStream.read(buffer))!=-1){
                outStream.write(buffer,0,len);
            }
            return file;
        } finally {
            if (outStream != null) {
                outStream.close();
            }
        }
    }

    public static File readInputStreamToFile(InputStream inStream , String filepath , String key) throws IOException{
        File file = File.createTempFile(filepath, key);
        return readInputStreamToFile(inStream ,file);
    }

    /**
     * 输入流转byte[]
     *
     * @param inStream InputStream
     * @return Byte数组
     */
    public static final byte[] input2byte(InputStream inStream) {
        if (inStream == null)
            return null;
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        try {
            while ((rc = inStream.read(buff, 0, 100)) > 0) {
                swapStream.write(buff, 0, rc);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return swapStream.toByteArray();
    }

}
