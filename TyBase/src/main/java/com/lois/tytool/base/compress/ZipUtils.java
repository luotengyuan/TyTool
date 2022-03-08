package com.lois.tytool.base.compress;

import com.lois.tytool.base.exception.ZipException;
import com.lois.tytool.base.io.IOUtils;
import com.lois.tytool.base.string.StringUtils;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @Description 压缩工具类
 * @Author Luo.T.Y
 * @Date 2022/1/21 16:09
 */
public class ZipUtils {
    /**
     * zip压缩文件后缀
     */
    private static String ZIP_SUFFIX = ".zip";
    /**
     * 路径分割符
     */
    private static String SEPARATOR = "/";

    /**
     * 压缩文件
     * 压缩文件名称按待压缩文件名称加上zip后缀
     * @param filePath 待压缩文件的路径
     * @throws com.lois.tytool.base.exception.ZipException 异常
     */
    public static void zipFile(String filePath) throws com.lois.tytool.base.exception.ZipException {
        String zipFilePath = filePath + ZIP_SUFFIX;
        zipFile(filePath, zipFilePath);
    }

    /**
     * 压缩文件
     * @param filePath 待压缩文件路径
     * @param zipFilePath 压缩后的文件路径
     * @throws com.lois.tytool.base.exception.ZipException 异常
     */
    public static void zipFile(String filePath, String zipFilePath) throws com.lois.tytool.base.exception.ZipException {
        File file = new File(filePath);
        File zipFile = new File(zipFilePath);
        zipFile(file, zipFile);
    }

    /**
     * 压缩文件
     * @param filePath 待压缩文件路径
     * @param outputStream 压缩文件的输入流
     * @throws com.lois.tytool.base.exception.ZipException 异常
     */
    public static void zipFile(String filePath, OutputStream outputStream) throws com.lois.tytool.base.exception.ZipException {
        File file = new File(filePath);
        zipFile(file, outputStream);
    }

    /**
     * 压缩文件
     * @param file 待压缩文件对象
     * @param zipFile 压缩文件对象
     * @throws com.lois.tytool.base.exception.ZipException 异常
     */
    public static void zipFile(File file, File zipFile) throws com.lois.tytool.base.exception.ZipException {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(zipFile);
            zipFile(file, fileOutputStream);
        } catch (FileNotFoundException e) {
            throw new com.lois.tytool.base.exception.ZipException(e);
        } finally {
            com.lois.tytool.base.io.IOUtils.close(fileOutputStream);
        }
    }

    /**
     * 压缩文件
     * @param file 待压缩文件对象
     * @param outputStream 压缩文件输出流
     * @throws com.lois.tytool.base.exception.ZipException 异常
     */
    public static void zipFile(File file, OutputStream outputStream) throws com.lois.tytool.base.exception.ZipException {
        FileInputStream fis = null;
        try {
            if (!file.isFile()) {
                throw new IllegalArgumentException("压缩文件错误，路径非文件");
            }
            fis = new FileInputStream(file);
            zipFile(file.getName(), fis, outputStream);
        } catch (FileNotFoundException e) {
            throw new com.lois.tytool.base.exception.ZipException(e);
        } finally {
            com.lois.tytool.base.io.IOUtils.close(fis);
        }
    }

    /**
     * 压缩文件
     * 将输入流的数据，压缩后，传输到指定的输出流
     * @param fileName 待压缩文件名称
     * @param is 待压缩文件输入流
     * @param os 待压缩文件输出流
     * @throws com.lois.tytool.base.exception.ZipException 异常
     */
    public static void zipFile(String fileName, InputStream is, OutputStream os) throws com.lois.tytool.base.exception.ZipException {
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(os);
            ZipEntry entry = new ZipEntry(fileName);
            zos.putNextEntry(entry);
            com.lois.tytool.base.io.IOUtils.copy(is, zos);
            zos.flush();
            zos.closeEntry();
        } catch (IOException e) {
            throw new com.lois.tytool.base.exception.ZipException(e);
        } finally {
            com.lois.tytool.base.io.IOUtils.close(zos);
        }
    }

    /**
     * 压缩指定文件夹
     * 压缩后的文件按文件夹名称命名
     * @param folder 待压缩文件夹路径
     * @throws com.lois.tytool.base.exception.ZipException 异常
     */
    public static void zipFolder(String folder) throws com.lois.tytool.base.exception.ZipException {
        File file = new File(folder);
        String zipPath = file.getParent() + SEPARATOR + file.getName() + ZIP_SUFFIX;
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(zipPath);
            zipFolder(folder, fileOutputStream);
        } catch (FileNotFoundException e) {
            throw new com.lois.tytool.base.exception.ZipException(e);
        } finally {
            com.lois.tytool.base.io.IOUtils.close(fileOutputStream);
        }

    }

    /**
     * 压缩指定文件夹内容
     * 并将文件夹压缩至指定路径
     * @param folder 待压缩文件夹路径
     * @param zipFilePath 压缩文件路径
     * @throws com.lois.tytool.base.exception.ZipException 异常
     */
    public static void zipFolder(String folder, String zipFilePath) throws com.lois.tytool.base.exception.ZipException {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(zipFilePath);
            zipFolder(folder, fileOutputStream);
        } catch (FileNotFoundException e) {
            throw new com.lois.tytool.base.exception.ZipException(e);
        } finally {
            com.lois.tytool.base.io.IOUtils.close(fileOutputStream);
        }
    }

    /**
     * 压缩指定文件夹内容
     * 并将文件夹内容压缩至指定输出流
     * @param folder 待压缩文件夹路径
     * @param outputStream 指定压缩文件输出流
     * @throws com.lois.tytool.base.exception.ZipException 异常
     */
    public static void zipFolder(String folder, OutputStream outputStream) throws com.lois.tytool.base.exception.ZipException {
        File file = new File(folder);
        if (!file.isDirectory()) {
            throw new IllegalArgumentException("[" + folder + "]路径不是一个文件夹路径");
        }
        File[] files = file.listFiles();
        ZipOutputStream zos = new ZipOutputStream(outputStream);
        try {
            toZip(files, zos, file.getName());
        } finally {
            com.lois.tytool.base.io.IOUtils.close(zos);
        }
    }

    /**
     * 递归压缩文件
     * 所有的压缩文件保留文件夹相对路径结构
     * @param files 待压缩文件列表
     * @param zos zip压缩流
     * @param entryPath 压缩文件内的相对位置
     * @throws com.lois.tytool.base.exception.ZipException 异常
     */
    private static void toZip(File[] files, ZipOutputStream zos, String entryPath) throws com.lois.tytool.base.exception.ZipException {
        try {
            if (files == null || files.length == 0) {
                //列表为空时，判断是否有路径，如果有，则保留路径结构
                if (StringUtils.isBlank(entryPath)) {
                    return;
                }
                //保留路径结构
                ZipEntry entry = new ZipEntry(entryPath + SEPARATOR);
                zos.putNextEntry(entry);
                zos.closeEntry();
                return;
            }
            //遍历所有的待压缩文件，并将文件按路径结构进行压缩
            for (File file : files) {
                if (file.isDirectory()) {
                    //如果是文件夹，则继续遍历文件下的文件
                    File[] subFiles = file.listFiles();
                    String subEntryPath = null;
                    //压缩文件内部路径结构
                    if (StringUtils.isBlank(entryPath)) {
                        subEntryPath = file.getName();
                    } else {
                        subEntryPath = entryPath + SEPARATOR + file.getName();
                    }
                    //递归
                    toZip(subFiles, zos, subEntryPath);
                    continue;
                }
                if (file.isFile()) {
                    //如果是文件，则直接压缩
                    FileInputStream fis = null;
                    ZipEntry entry = null;
                    try {
                        fis = new FileInputStream(file);
                        //压缩文件内部路径结构
                        if (StringUtils.isBlank(entryPath)) {
                            entry = new ZipEntry(file.getName());
                        } else {
                            entry = new ZipEntry(entryPath + SEPARATOR + file.getName());
                        }
                        //进行压缩
                        zos.putNextEntry(entry);
                        com.lois.tytool.base.io.IOUtils.copy(fis, zos);
                        zos.closeEntry();
                        zos.flush();
                    } finally {
                        fis.close();
                    }

                }
            }
        } catch (IOException e) {
            throw new com.lois.tytool.base.exception.ZipException(e);
        }

    }


    /**
     * 解压zip文件
     * 解压路径为zip文件的同级路径
     * @param zipFilePath 待解压文件路径
     * @throws com.lois.tytool.base.exception.ZipException 异常
     */
    public static void unzipFile(String zipFilePath) throws com.lois.tytool.base.exception.ZipException {
        File file = new File(zipFilePath);
        if (!file.isFile()) {
            throw new com.lois.tytool.base.exception.ZipException("路径【" + zipFilePath + "】不是一个文件");
        }
        String unzipFilePath = file.getParent();
        unzipFile(zipFilePath, unzipFilePath);
    }

    /**
     * 解压zip文件
     * 并指定解压后的路径
     * 注：
     * 1、如果该解压路径不存在时，会自动创建。
     * 2、如果解压路径中已经存在该解压文件的文件，则会自动删除后，重新解压
     * @param zipFilePath 待解压的文件路径
     * @param unzipFolerPath 指定解压路径
     * @throws com.lois.tytool.base.exception.ZipException 异常
     */
    public static void unzipFile(String zipFilePath, String unzipFolerPath) throws com.lois.tytool.base.exception.ZipException {
        File file = new File(zipFilePath);
        if (!file.isFile()) {
            throw new com.lois.tytool.base.exception.ZipException("路径【" + zipFilePath + "】不是一个文件");
        }
        //创建解压缩文件保存的路径
        File unzipFileDir = new File(unzipFolerPath);
        if (!unzipFileDir.exists() || !unzipFileDir.isDirectory()) {
            boolean mkdirs = unzipFileDir.mkdirs();
            if (!mkdirs) {
                throw new com.lois.tytool.base.exception.ZipException("创建文件路径【" + unzipFolerPath + "】失败");
            }
        }

        //开始解压
        ZipEntry entry = null;
        ZipFile zip = null;
        try {
            zip = new ZipFile(zipFilePath);
            Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>)zip.entries();
            //循环对压缩包里的每一个文件进行解压
            while(entries.hasMoreElements()) {
                entry = entries.nextElement();
                //构建压缩包中一个文件解压后保存的文件全路径
                String entryFilePath = null;
                if (unzipFolerPath.endsWith(File.separator)) {
                    entryFilePath = unzipFolerPath + entry.getName();
                } else {
                    entryFilePath = unzipFolerPath + File.separator + entry.getName();
                }
                if (entryFilePath.endsWith(File.separator)) {
                    //这是一个文件夹，直接创建
                    File dir = new File(entryFilePath);
                    if (!dir.exists()) {
                        boolean mkdir = dir.mkdirs();
                        if (!mkdir) {
                            throw new com.lois.tytool.base.exception.ZipException("创建路径【" + entryFilePath + "】失败");
                        }
                    }
                    continue;
                }
                //构建解压后保存的文件夹路径
                int index = entryFilePath.lastIndexOf(File.separator);
                String entryDirPath = null;
                if (index != -1) {
                    entryDirPath = entryFilePath.substring(0, index);
                } else {
                    entryDirPath = "";
                }
                File entryDir = new File(entryDirPath);
                //如果文件夹路径不存在，则创建文件夹
                if (!entryDir.exists() || !entryDir.isDirectory()) {
                    boolean mkdirs = entryDir.mkdirs();
                    if (!mkdirs) {
                        throw new com.lois.tytool.base.exception.ZipException("创建路径【" + entryDirPath + "】失败");
                    }
                }

                //创建解压文件
                File entryFile = new File(entryFilePath);
                if (entryFile.exists() && !entryFile.isDirectory()) {
                    //检测文件是否允许删除，如果不允许删除，将会抛出SecurityException
                    SecurityManager securityManager = new SecurityManager();
                    securityManager.checkDelete(entryFilePath);
                    //删除已存在的目标文件
                    boolean delete = entryFile.delete();
                    if (!delete) {
                        throw new com.lois.tytool.base.exception.ZipException("删除文件【" + entryFilePath + "】失败");
                    }
                }

                if (!entryFile.getParentFile().exists()) {
                    entryFile.getParentFile().mkdirs();
                }
                if (!entryFile.exists()) {
                    entryFile.createNewFile();
                }

                //解压文件
                FileOutputStream fileOutputStream = null;
                InputStream inputStream = null;
                try {
                    fileOutputStream = new FileOutputStream(entryFilePath);
                    inputStream = zip.getInputStream(entry);
                    com.lois.tytool.base.io.IOUtils.copy(inputStream, fileOutputStream);
                    fileOutputStream.flush();
                } catch (FileNotFoundException e) {
                    throw new com.lois.tytool.base.exception.ZipException(e);
                } finally {
                    IOUtils.close(fileOutputStream);
                }
            }
        } catch (IOException e) {
            throw new ZipException(e);
        } finally {
            try {
                if (zip != null) {
                    zip.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
