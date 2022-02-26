package com.lois.tytool.basej.io;

import org.dom4j.io.XMLWriter;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;

import javax.imageio.stream.ImageInputStream;

/**
 * @Description io工具类
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public class IOUtils {

    /**
     * 关闭字节输入流
     * @param in
     */
    public static void close(InputStream in){
        if(in != null){
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 关闭字节输入输出流
     * @param in
     * @param out
     */
    public static void close(InputStream in, OutputStream out){
        close(in);
        close(out);
    }

    /**
     * 关闭字符输入流
     * @param reader
     */
    public static void close(Reader reader){
        if(reader != null){
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭字符输出流
     * @param writer
     */
    public static void close(Writer writer){
        if(writer != null){
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭image输入流
     * @param iis
     */
    public static void close(ImageInputStream iis){
        if(iis != null){
            try {
                iis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 批量关闭字节输入流
     * @param ins
     */
    @SuppressWarnings("NewApi")
    public static void close(InputStream... ins){
        Arrays.asList(ins).parallelStream().forEach(IOUtils::close);
    }

    /**
     * 批量关闭字节输出流
     * @param outs
     */
    @SuppressWarnings("NewApi")
    public static void close(OutputStream... outs){
        Arrays.asList(outs).parallelStream().forEach(IOUtils::close);
    }

    /**
     * 关闭字节输出流
     * @param out
     */
    public static void close(OutputStream out){
        if(out != null){
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭XMLWriter输出流
     * @param out
     */
    public static void close(XMLWriter out){
        if(out != null){
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭所有
     * @param closeables
     */
    public static void closeAll(Closeable... closeables) {
        if (null == closeables || closeables.length <= 0) {
            return;
        }
        for (Closeable cb : closeables) {
            try {
                if (null == cb) {
                    continue;
                }
                cb.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
