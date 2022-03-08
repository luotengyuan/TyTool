package com.lois.tytool.basej.compress;

import com.lois.tytool.basej.constant.FileConstants;
import com.lois.tytool.basej.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @Description GZIP压缩工具类
 * @Author Luo.T.Y
 * @date 2018/10/12 17:47
 * @version V1.0
 */
public class GZipUtils {

    /**
     * 文件压缩（不支持文件夹）
     * @param source 待压缩文件路径
     * @param target 压缩后文件夹路径（文件必须存在）
     */
    public static void compress(String source, String target){
        compress(new File(source), target);
    }

    /**
     * 文件压缩（不支持文件夹）
     * @param source File 待压缩
     * @param target 压缩后文件夹路径（文件必须存在）
     */
    public static void compress(File source, String target){
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(source);
            File targetFile = new File(target);
            if(targetFile.isFile()){
                System.err.println("target不能是文件，否则压缩失败");
                return;
            }
            target += (source.getName() + FileConstants.GZ_FILE);
            FileOutputStream fos = new FileOutputStream(target);
            compress(fis, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(fis);
        }
    }

    /**
     * 文件解压缩
     * @param source 待压缩文件路径
     * @param target 解压后文件夹路径（文件必须存在）
     */
    public static void uncompress(String source, String target){
        uncompress(new File(source), target);
    }

    /**
     * 文件解压缩
     * @param source File 待解压缩
     * @param target 解压后文件夹路径（文件必须存在）
     */
    public static void uncompress(File source, String target){
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(source);
            target += source.getName().replace(FileConstants.GZ_FILE, "");
            FileOutputStream fos = new FileOutputStream(target);
            uncompress(fis, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(fis);
        }
    }


    /**
     * 文件流压缩
     * @param in 待压缩输入流
     * @param out 解压后输出流
     */
    private static void compress(InputStream in, OutputStream out){
        try(GZIPOutputStream gos = new GZIPOutputStream(out)) {
            int count;
            byte[] data = new byte[FileConstants.BUFFER_1024];
            while ((count = in.read(data, 0, data.length)) != -1) {
                gos.write(data, 0, count);
            }
            gos.finish();
            gos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 文件流解压缩
     * @param in 待解压输入流
     * @param out 解压后输出流
     */
    private static void uncompress(InputStream in, OutputStream out){
        try(GZIPInputStream gis = new GZIPInputStream(in)) {
            int count;
            byte[] data = new byte[FileConstants.BUFFER_1024];
            while ((count = gis.read(data, 0, FileConstants.BUFFER_1024)) != -1) {
                out.write(data, 0, count);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
