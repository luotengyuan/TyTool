package com.lois.tytool.basej.util;


import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Properties;

/**
 * 解析properties文件工具类
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public class PropUtils {

    /**
     * 解析properties文件
     * @param filePath
     * @return
     */
    public static Properties getProp(String filePath){
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(filePath));
            return getProp(in);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return new Properties();
    }

    /**
     * 解析properties文件
     * @param in
     * @return
     */
    public static Properties getProp(InputStream in){
        Properties props = new Properties();
        try {
            props.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return props;
    }


    /**
     * 写入properties信息
     * @param filepath
     * @param map
     */
    @SuppressWarnings("NewApi")
    public static void setProp(String filepath, Map<String, String> map){
        Properties props = getProp(filepath);
        OutputStream fos = null;
        try {
            fos = new FileOutputStream(filepath);
            map.forEach(props::setProperty);
            props.store(fos, "");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
