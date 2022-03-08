package com.lois.tytool.base.io;

import com.lois.tytool.base.debug.TyLog;
import com.lois.tytool.base.math.CalculateUtils;
import com.lois.tytool.base.string.StringUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * @Description 文件操作工具类
 * @Author Luo.T.Y
 * @Date 2022/1/21 16:09
 */
public class FileUtils {

    /**
     * 文件分割符
     */
    public final static String SEPARATOR = File.separatorChar == '\\' ? "\\\\" : File.separator;

    /**
     * 文件后缀名分割符
     */
    public final static String FILE_NAME_SUFFIX = ".";

    /**
     * linux 文件路径分割符
     */
    public final static String LINUX_SEPARATOR = "/";

    /**
     * windows 文件路径分割符
     */
    public final static String WINDOWS_SEPARATOR = "\\";
    public static final long GB = 1073741824; // 1024 * 1024 * 1024
    public static final long MB = 1048576; // 1024 * 1024
    public static final long KB = 1024;

    public static final int ICON_TYPE_ROOT = 1;
    public static final int ICON_TYPE_FOLDER = 2;
    public static final int ICON_TYPE_MP3 = 3;
    public static final int ICON_TYPE_MTV = 4;
    public static final int ICON_TYPE_JPG = 5;
    public static final int ICON_TYPE_FILE = 6;

    public static final String MTV_REG = "^.*\\.(mp4|3gp)$";
    public static final String MP3_REG = "^.*\\.(mp3|wav)$";
    public static final String JPG_REG = "^.*\\.(gif|jpg|png)$";

    private static final String FILENAME_REGIX = "^[^\\/?\"*:<>\\]{1,255}$";

    public final static String FILE_EXTENSION_SEPARATOR = "";

    /**
     * 获取文件的后缀
     * @param fileFullPath 文件完整路径
     * @return 文件后缀
     */
    public static String getFileType(String fileFullPath) {
        int index = fileFullPath.indexOf(FILE_NAME_SUFFIX);
        if (index == -1) {
            return "";
        }
        String fileType = fileFullPath.substring(index + 1);
        return fileType;
    }

    /**
     * 获取文件目录
     * @param fileFullPath 完成的文件路径
     * @return 文件所在目录
     */
    public static String getFileDirectory(String fileFullPath) {
        String separator = null;
        if (fileFullPath.contains(WINDOWS_SEPARATOR)) {
            //win 路径写法
            separator = WINDOWS_SEPARATOR;
        } else if (fileFullPath.contains(LINUX_SEPARATOR)) {
            //linux/unix 路径写法
            separator = LINUX_SEPARATOR;
        } else {
            throw new IllegalArgumentException("非法文件路径【" + fileFullPath + "】");
        }
        int index = fileFullPath.lastIndexOf(separator) + 1;
        String dir = fileFullPath.substring(0, index);
        return dir;
    }

    /**
     * 创建新文件
     *
     * @param fileName 文件完整路径
     * @return 创建的文件对象，创建失败返回空
     */
    public static File createNewFile(String fileName) {
        File file = new File(fileName);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            TyLog.v("crashed  " + e.toString());
            return null;
        }
        return file;
    }

    /**
     * 创建新文件夹
     *
     * @param directoryName 文件夹完整路径
     * @return 创建的文件对象，创建失败返回空
     */
    public static File createNewDirectory(String directoryName) {
        File file = new File(directoryName);
        if (isDirectoryExist(directoryName)) {
            return file;
        }
        try {
            file.mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return file;
    }

    public static boolean createFolder(String filePath) {
        return createFolder(filePath, false);
    }

    public static boolean createFolder(String filePath, boolean recreate) {
        String folderName = getFolderName(filePath);
        if (folderName == null || folderName.length() == 0 || folderName.trim().length() == 0) {
            return false;
        }
        File folder = new File(folderName);
        if (folder.exists()) {
            if (recreate) {
                deleteFile(folderName);
                return folder.mkdirs();
            } else {
                return true;
            }
        } else {
            return folder.mkdirs();
        }
    }

    /**
     * 删除文件
     *
     * @param fileName : 文件名
     * @return
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        if (file == null || !file.exists() || file.isDirectory()) {
            return false;
        } else {
            return file.delete();
        }
    }

    /**
     * 删除指定文件夹下所有文件
     * @param directory
     */
    public static void deleteFileByDirectory(File directory) {
        if (directory.exists() && directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                file.delete();
            }
        }
    }

    /**
     * 文件或目录是否存在
     *
     * @param fileName : 文件名
     * @return
     */
    public static Boolean isFileOrDirectoryExist(String fileName) {
        return (new File(fileName)).exists();
    }

    /**
     * 文件是否存在
     *
     * @param fileName : 文件名
     * @return
     */
    public static Boolean isFileExist(String fileName) {
        File file = new File(fileName);
        if (file.exists() && file.isFile()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 文件夹是否存在
     *
     * @param fileName : 文件名
     * @return
     */
    public static Boolean isDirectoryExist(String fileName) {
        File file = new File(fileName);
        if (file.exists() && file.isDirectory()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取文件大小
     *
     * @param fileName : 文件名
     * @return
     */
    public static long getFileSize(String fileName) {
        long fileSize;
        if (fileName == null) {
            fileSize = 0L;
        } else {
            fileSize = (new File(fileName)).length();
        }
        return fileSize;
    }

    /**
     * 获取文件大小
     *
     * @param file : 文件描述
     * @return
     */
    public static long getFileSize(File file) {
        long fileSize;
        if (file == null) {
            fileSize = 0L;
        } else {
            fileSize = file.length();
        }
        return fileSize;
    }

    /**
     * 文件大小获取
     *
     * @param file File对象
     * @return 文件大小字符串
     */
    public static String getFileSizeStr(File file) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            int length = fis.available();
            if (length >= GB) {
                return String.format("%.2f GB", length * 1.0 / GB);
            } else if (length >= MB) {
                return String.format("%.2f MB", length * 1.0 / MB);
            } else {
                return String.format("%.2f KB", length * 1.0 / KB);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "未知";
    }

    /**
     * 获取文件的校验和
     *
     * @param fileName : 文件名
     * @return
     */
    public static int getFileCheckSum(String fileName) {

        byte[] buffer = null;
        int numOfBytes = 0, byteCount = 0, ckSum = 0;
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(fileName);
            if (fis != null) {
                numOfBytes = fis.available();
                buffer = new byte[128 * 1024];
                while (byteCount < numOfBytes) {
                    int length = fis.read(buffer);
                    // 计算数据累加和
                    byteCount += length;
                    ckSum += CalculateUtils.calCheckSum(buffer, length);
                }
                fis.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return ckSum;
    }

    /**
     * 将缓存数据写入文件
     *
     * @param file
     * @param buf    : 数据缓存
     * @param offset ：数据缓存的偏移
     * @param length ：写入数据长度
     * @return
     * @throws IOException
     */
    public static void writeFile(File file, byte[] buf, int offset, int length) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);
            fos.write(buf, offset, length);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将缓存数据写入文件
     *
     * @param fileName
     * @param buf      : 数据缓存
     * @param offset   ：数据缓存的偏移
     * @param length   ：写入数据长度
     * @return
     * @throws IOException
     */
    public static void writeFile(String fileName, byte[] buf, int offset, int length) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(fileName);
            fos.write(buf, offset, length);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将缓存数据写入文件
     *
     * @param fileName 写入文件名
     * @param writeStr : 写入数据
     */
    public static void writeFile2(String fileName, String writeStr) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(fileName);
            byte[] bytes = writeStr.getBytes();
            fos.write(bytes);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * write file
     *
     * @param filePath 文件路径
     * @param content  内容
     * @param append   is append, if true, write to the end of file, else clear
     *                 content of file and write into it
     * @return return false if content is empty, true otherwise
     * @throws RuntimeException if an error occurs while operator FileWriter
     */
    public static boolean writeFile(String filePath, String content,
                                    boolean append) {
        if (StringUtils.isEmpty(content)) {
            return false;
        }

        FileWriter fileWriter = null;
        try {
            makeDirs(filePath);
            fileWriter = new FileWriter(filePath, append);
            fileWriter.write(content);
            fileWriter.close();
            return true;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    throw new RuntimeException("IOException occurred. ", e);
                }
            }
        }
    }

    /**
     * write file
     *
     * @param filePath    文件路径
     * @param contentList 内容List
     * @param append      is append, if true, write to the end of file, else clear
     *                    content of file and write into it
     * @return return false if contentList is empty, true otherwise
     * @throws RuntimeException if an error occurs while operator FileWriter
     */
    public static boolean writeFile(String filePath, List<String> contentList,
                                    boolean append) {
        if (contentList == null || contentList.size() < 1) {
            return false;
        }

        FileWriter fileWriter = null;
        try {
            makeDirs(filePath);
            fileWriter = new FileWriter(filePath, append);
            int i = 0;
            for (String line : contentList) {
                if (i++ > 0) {
                    fileWriter.write("\r\n");
                }
                fileWriter.write(line);
            }
            fileWriter.close();
            return true;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    throw new RuntimeException("IOException occurred. ", e);
                }
            }
        }
    }

    /**
     * write file, the string will be written to the begin of the file
     *
     * @param filePath 文件路径
     * @param content  内容
     * @return 执行结果
     */
    public static boolean writeFile(String filePath, String content) {
        return writeFile(filePath, content, false);
    }

    /**
     * write file, the string list will be written to the begin of the file
     *
     * @param filePath    文件路径
     * @param contentList 内容List
     * @return 执行结果
     */
    public static boolean writeFile(String filePath, List<String> contentList) {
        return writeFile(filePath, contentList, false);
    }

    /**
     * write file, the bytes will be written to the begin of the file
     *
     * @param filePath 文件路径
     * @param stream   InputStream
     * @return 执行结果
     * @see {@link #writeFile(String, InputStream, boolean)}
     */
    public static boolean writeFile(String filePath, InputStream stream) {
        return writeFile(filePath, stream, false);
    }

    /**
     * write file
     *
     * @param filePath the file to be opened for writing.
     * @param stream   the input stream
     * @param append   if <code>true</code>, then bytes will be written to the end of
     *                 the file rather than the beginning
     * @return return true
     * @throws RuntimeException if an error occurs while operator FileOutputStream
     */
    public static boolean writeFile(String filePath, InputStream stream,
                                    boolean append) {
        return writeFile(filePath != null ? new File(filePath) : null, stream,
                append);
    }

    /**
     * write file, the bytes will be written to the begin of the file
     *
     * @param file   File对象
     * @param stream InputStream
     * @return
     * @see {@link #writeFile(File, InputStream, boolean)}
     */
    public static boolean writeFile(File file, InputStream stream) {
        return writeFile(file, stream, false);
    }

    /**
     * write file
     *
     * @param file   the file to be opened for writing.
     * @param stream the input stream
     * @param append if <code>true</code>, then bytes will be written to the end of
     *               the file rather than the beginning
     * @return return true
     * @throws RuntimeException if an error occurs while operator FileOutputStream
     */
    public static boolean writeFile(File file, InputStream stream,
                                    boolean append) {
        OutputStream o = null;
        try {
            makeDirs(file.getAbsolutePath());
            o = new FileOutputStream(file, append);
            byte data[] = new byte[1024];
            int length = -1;
            while ((length = stream.read(data)) != -1) {
                o.write(data, 0, length);
            }
            o.flush();
            return true;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFoundException occurred. ", e);
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (o != null) {
                try {
                    o.close();
                    stream.close();
                } catch (IOException e) {
                    throw new RuntimeException("IOException occurred. ", e);
                }
            }
        }
    }

    /**
     * 保存字节流数据到文件
     *
     * @param fileName
     * @param data
     */
    public static boolean writeFileByte(String fileName, byte[] data) {
        if (isFileOrDirectoryExist(fileName)) {
            createNewFile(fileName);
        }
        if (data == null || data.length == 0) {
            return false;
        }
        writeFile(fileName, data, 0, data.length);
        return true;
    }

    /**
     * 在原有文件上继续写文件, 采用GBK编码
     *
     * @param fileName 原有文件
     * @param writeStr 文件内容
     * @return
     * @throws IOException
     */
    public static void appendWriteFile(String fileName, String writeStr) {
        appendWriteFile(fileName, writeStr, "GBK");
    }

    /**
     * 在原有文件上继续写文件
     *
     * @param fileName    原有文件
     * @param writeStr    文件内容
     * @param charsetName 字符串集编码
     * @return
     * @throws IOException
     */
    public static void appendWriteFile(String fileName, String writeStr, String charsetName) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(fileName, true);
            byte[] bytes = writeStr.getBytes(charsetName);
            fos.write(bytes);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 在原有文件上继续写文件
     *
     * @param fileName   原有文件
     * @param writebytes 文件内容
     * @return
     * @throws IOException
     */
    public static boolean appendWriteFile(String fileName, byte[] writebytes) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(fileName, true);
            if (fos != null) {
                fos.write(writebytes);
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 将文件名为fileName中的数据读到buf中
     *
     * @param fileName
     * @param buf      : 外部传入的缓存
     * @param offset   ：buf的起始地址
     * @param length   ：读取长度
     * @return 读取数据字节数
     * @throws IOException
     */
    public static int readFile(String fileName, byte[] buf, int offset, int length) {
        FileInputStream fis;
        int bytes = 0;
        try {
            fis = new FileInputStream(fileName);
            bytes = fis.read(buf, offset, length);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * 将文件描述符fileName中的数据读到buf中
     *
     * @param file
     * @param buf    : 数据缓存
     * @param offset ：buf的起始地址
     * @param length ：读取长度
     * @return 读取数据字节数
     * @throws IOException
     */
    public static int readFile(File file, byte[] buf, int offset, int length) {
        FileInputStream fis;
        int bytes = 0;
        try {
            fis = new FileInputStream(file);
            bytes = fis.read(buf, offset, length);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * 将文件数据读取到缓存
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static String readFile(File file) {
        String readBuf = "";
        FileInputStream fis;
        try {
            fis = new FileInputStream(file);
            int length = fis.available();
            byte[] buffer = new byte[length];
            fis.read(buffer);
            readBuf = new String(buffer, "GBK");
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return readBuf;
    }

    /**
     * 将文件数据读取到缓存
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public static String readFile(String fileName) {
        String readBuf = "";
        FileInputStream fis;
        try {
            fis = new FileInputStream(fileName);
            int length = fis.available();
            byte[] buffer = new byte[length];
            fis.read(buffer);
            readBuf = new String(buffer, 0, length, "GBK");
            // readBuf = new String(buffer);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return readBuf;
    }

    /**
     * 读取文件中的二进制数据
     *
     * @param fileName 文件名全路径
     * @return
     */
    public static byte[] readFileByte(String fileName) {
        int fileSize = (int) getFileSize(fileName);
        if (fileSize == 0) {
            return null;
        }
        byte fileData[] = new byte[fileSize];
        readFile(fileName, fileData, 0, fileSize);
        return fileData;
    }

    /**
     * 将文件数据读取到缓存
     *
     * @param fileName 文件名
     * @param offset   读取数据的开始位置
     * @param length   读取的数据长度
     * @return 获取到的文件数据, 保存为字符串
     */
    public static String readFile(String fileName, int offset, int length) {
        int filesize;
        String readBuf = "";
        FileInputStream fis;

        try {
            fis = new FileInputStream(fileName);
            filesize = fis.available();
            if (offset + length > filesize) {
                fis.close();
                return readBuf;
            }
            byte[] buffer = new byte[filesize];
            fis.read(buffer);
            readBuf = new String(buffer, offset, length, "UTF-8");
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return readBuf;
    }

    /**
     * read file
     *
     * @param filePath    文件路径
     * @param charsetName The name of a supported {@link java.nio.charset.Charset
     *                    </code>charset<code>}
     * @return if file not exist, return null, else return content of file
     * @throws RuntimeException if an error occurs while operator BufferedReader
     */
    public static StringBuilder readFile(String filePath, String charsetName) {
        File file = new File(filePath);
        StringBuilder fileContent = new StringBuilder("");
        if (!file.isFile()) {
            return null;
        }

        BufferedReader reader = null;
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(
                    file), charsetName);
            reader = new BufferedReader(is);
            String line;
            while ((line = reader.readLine()) != null) {
                if (!fileContent.toString().equals("")) {
                    fileContent.append("\r\n");
                }
                fileContent.append(line);
            }
            reader.close();
            return fileContent;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException("IOException occurred. ", e);
                }
            }
        }
    }

    /**
     * 修改文件或目录名
     *
     * @param oldFileName
     * @param newFileName
     * @return
     */
    public static boolean renameFile(String oldFileName, String newFileName) {
        File oleFile = new File(oldFileName);
        File newFile = new File(newFileName);
        return oleFile.renameTo(newFile);
    }

    /**
     * 拷贝文件
     *
     * @param srcFile  源文件
     * @param destFile 目标文件
     * @throws IOException
     */
    public static boolean copyFile(File srcFile, File destFile) {
        if (srcFile.isDirectory() || destFile.isDirectory()) {
            return false;
        }
        FileInputStream fis;
        try {
            fis = new FileInputStream(srcFile);
            FileOutputStream fos = new FileOutputStream(destFile);
            int readLen = 0;
            byte[] buf = new byte[1024];
            while ((readLen = fis.read(buf)) != -1) {
                fos.write(buf, 0, readLen);
            }
            fos.flush();
            fos.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 拷贝文件
     *
     * @param srcFileName  源文件全路径名
     * @param destFileName 目标文件全路径名
     * @throws IOException
     */
    public static boolean copyFile(String srcFileName, String destFileName)
            throws IOException {
        File srcFile = new File(srcFileName);
        File destFile = new File(destFileName);
        return copyFile(srcFile, destFile);
    }

    /**
     * 通过流拷贝文件
     * @param in 输入文件
     * @param out 输出文件
     */
    public static boolean copyFile(InputStream in, OutputStream out) {
        try {
            byte[] b = new byte[2 * 1024 * 1024]; //2M memory
            int len = -1;
            while ((len = in.read(b)) > 0) {
                out.write(b, 0, len);
                out.flush();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(in, out);
        }
        return false;
    }

    /**
     * 快速拷贝
     * @param in 输入文件
     * @param out 输出文件
     */
    public static boolean copyFileFast(File in, File out) {
        FileChannel filein = null;
        FileChannel fileout = null;
        try {
            filein = new FileInputStream(in).getChannel();
            fileout = new FileOutputStream(out).getChannel();
            filein.transferTo(0, filein.size(), fileout);
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeAll(filein, fileout);
        }
        return false;
    }

    /**
     * 移动一个文件
     *
     * @param srcFile  源文件
     * @param destFile 目标文件
     * @return
     * @throws IOException
     */
    public static boolean moveFile(File srcFile, File destFile) {
        boolean iscopy = copyFile(srcFile, destFile);
        if (!iscopy) {
            return false;
        }
        srcFile.delete();
        return true;
    }

    /**
     * 关闭文件输入流
     *
     * @param fis
     * @return
     */
    public static void closeFileInputStream(FileInputStream fis) {
        try {
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 移动单个文件
     *
     * @param srcFileName  源文件全路径名
     * @param destFileName 目标文件全路径名
     * @return
     * @throws IOException
     */
    public static boolean moveFile(String srcFileName, String destFileName)
            throws IOException {
        File srcFile = new File(srcFileName);
        File destFile = new File(destFileName);
        return moveFile(srcFile, destFile);
    }

    /**
     * 删除空目录
     *
     * @param dirName : 目录路径
     * @return
     */
    public static boolean deleteEmptyDir(String dirName) {
        File dir = new File(dirName);
        return dir.delete();
    }

    /**
     * 删除目录及目录内的所有文件整个
     *
     * @param dir : 目录
     * @return
     */
    public static boolean deleteDirAll(File dir) {
        if (dir == null || !dir.exists() || dir.isFile()) {
            return false;
        }
        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                deleteDirAll(file);
            }
        }
        dir.delete();
        return true;
    }

    /**
     * 删除目录及目录内的所有文件
     *
     * @param dirName : 目录全路径名
     * @return
     */
    public static boolean deleteDirAll(String dirName) {
        if (dirName == null) {
            return false;
        }
        File dir = new File(dirName);
        return deleteDirAll(dir);
    }

    /**
     * 递归获取目录文件夹大小
     *
     * @param dir : 目录全路径名
     * @return 目录文件夹大小(字节)
     */
    private static long getDirSize(File dir) {
        long dirSize = 0;
        if (dir == null || !dir.exists() || dir.isFile()) {
            return 0;
        }
        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                dirSize += file.length();
            } else if (file.isDirectory()) {
                getDirSize(file);
            }
        }
        return dirSize;
    }

    /**
     * 获取整个目录文件夹大小
     *
     * @param dirName : 目录全路径名
     * @return 目录文件夹大小(字节)
     */
    public static long getDirSize(String dirName) {
        if (dirName == null) {
            return 0;
        }
        File dir = new File(dirName);
        return getDirSize(dir);
    }

    /**
     * 修改目录内所有文件属性为777(读/写/执行)
     *
     * @param dirName
     */
    public static void changeDirMod(String dirName) {
        if (dirName == null) {
            return;
        }
        File dir = new File(dirName);
        if (dir == null || !dir.exists() || dir.isFile()) {
            return;
        }
        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                changeMod("777", file.getAbsolutePath());
            } else if (file.isDirectory()) {
                changeMod("777", file.getAbsolutePath());
                changeDirMod(file.getAbsolutePath());
            }
        }
    }

    /**
     * 修改目录或文件权限级别
     *
     * @param cmdStr   : 权限类型
     * @param fileName :文件或目录全路径名称
     */
    public static void changeMod(String cmdStr, String fileName) {
        try {
            Runtime.getRuntime().exec((new StringBuilder()).append("chmod ").append(cmdStr)
                    .append(" ").append(fileName).toString()).waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取目录下所有文件, 包括子文件夹内文件, 并按文件修改时间倒叙排列
     *
     * @param path
     * @return
     */
    public static List<File> getFileListSort(String path) {
        List<File> list = getFileList(path);
        if (list != null && list.size() > 0) {
            Collections.sort(list, new Comparator<File>() {
                @Override
                public int compare(File file, File newFile) {
                    if (file.lastModified() < newFile.lastModified()) {
                        return 1;
                    } else if (file.lastModified() == newFile.lastModified()) {
                        return 0;
                    } else {
                        return -1;
                    }
                }
            });
        }
        return list;
    }

    /**
     * 获取目录下所有文件, 包括子文件夹内文件
     *
     * @param realpath
     * @return
     */
    public static List<File> getFileList(String realpath) {
        List<File> files = new ArrayList<>();
        File realFile = new File(realpath);
        if (realFile.isDirectory()) {
            File[] subfiles = realFile.listFiles();
            for (File file : subfiles) {
                if (file.isDirectory()) {
                    files.addAll(getFileList(file.getAbsolutePath()));
                } else {
                    files.add(file);
                }
            }
        }
        return files;
    }

    public static void Stream2File(InputStream is, File file) {
        byte[] b = new byte[1024];
        int len;
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
            while ((len = is.read(b)) != -1) {
                os.write(b, 0, len);
                os.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeAll(is, os);
        }
    }

    public static String getFileName2(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return filePath;
        }

        int filePosi = filePath.lastIndexOf(File.separator);
        return (filePosi == -1) ? filePath : filePath.substring(filePosi + 1);
    }

    public static String getFolderName2(String filePath) {
        if (filePath == null || filePath.length() == 0 || filePath.trim().length() == 0) {
            return filePath;
        }
        int filePos = filePath.lastIndexOf(File.separator);
        return (filePos == -1) ? "" : filePath.substring(0, filePos);
    }

    /**
     * 重命名文件和文件夹
     *
     * @param file        File对象
     * @param newFileName 新的文件名
     * @return 执行结果
     */
    public static boolean renameFile(File file, String newFileName) {
        if (newFileName.matches(FILENAME_REGIX)) {
            File newFile = null;
            if (file.isDirectory()) {
                newFile = new File(file.getParentFile(), newFileName);
            } else {
                String temp = newFileName
                        + file.getName().substring(
                        file.getName().lastIndexOf('.'));
                newFile = new File(file.getParentFile(), temp);
            }
            if (file.renameTo(newFile)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 用于递归查找文件夹下面的符合条件的文件
     *
     * @param folder 文件夹
     * @param filter 文件过滤器
     * @return 符合条件的文件List
     */
    public static List<HashMap<String, Object>> recursionFolder(File folder,
                                                                FileFilter filter) {

        List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

        // 获得文件夹下的所有目录和文件集合
        File[] files = folder.listFiles();
        /** 如果文件夹下没内容,会返回一个null **/
        // 判断适配器是否为空
        if (filter != null) {
            files = folder.listFiles(filter);
        }
        // 找到合适的文件返回
        if (files != null) {
            for (int m = 0; m < files.length; m++) {
                File file = files[m];
                if (file.isDirectory()) {
                    // 是否递归调用
                    list.addAll(recursionFolder(file, filter));

                } else {
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("file", file);
                    // 设置图标种类
                    if (file.getAbsolutePath().toLowerCase().matches(MP3_REG)) {
                        map.put("iconType", 3);
                    } else if (file.getAbsolutePath().toLowerCase()
                            .matches(MTV_REG)) {
                        map.put("iconType", 4);
                    } else if (file.getAbsolutePath().toLowerCase()
                            .matches(JPG_REG)) {
                        map.put("iconType", 5);
                    } else {
                        map.put("iconType", 6);
                    }
                    list.add(map);
                }
            }
        }
        return list;
    }

    /**
     * 示例:"^.*\\.(mp3|mp4|3gp)$"
     *
     * @param reg 目前允许取值 REG_MTV, REG_MP3, REG_JPG三种
     * @return 文件过滤器
     */
    public static FileFilter getFileFilter(final String reg, boolean isdir) {
        if (isdir) {
            return new FileFilter() {
                @Override
                public boolean accept(File pathname) {

                    return pathname.getAbsolutePath().toLowerCase()
                            .matches(reg)
                            || pathname.isDirectory();
                }
            };
        } else {
            return new FileFilter() {
                @Override
                public boolean accept(File pathname) {

                    return pathname.getAbsolutePath().toLowerCase()
                            .matches(reg)
                            && pathname.isFile();
                }
            };
        }
    }

    /**
     * read file to string list, a element of list is a line
     *
     * @param filePath    文件路径
     * @param charsetName 编码方式
     *                    The name of a supported {@link java.nio.charset.Charset
     *                    </code>charset<code>}
     * @return if file not exist, return null, else return content of file
     * @throws RuntimeException if an error occurs while operator BufferedReader
     */
    public static List<String> readFileToList(String filePath,
                                              String charsetName) {
        File file = new File(filePath);
        List<String> fileContent = new ArrayList<String>();
        if (!file.isFile()) {
            return null;
        }

        BufferedReader reader = null;
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(
                    file), charsetName);
            reader = new BufferedReader(is);
            String line = null;
            while ((line = reader.readLine()) != null) {
                fileContent.add(line);
            }
            reader.close();
            return fileContent;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException("IOException occurred. ", e);
                }
            }
        }
    }

    /**
     * get file name from path, not include suffix
     * <p/>
     * <pre>
     *      getFileNameWithoutExtension(null)               =   null
     *      getFileNameWithoutExtension("")                 =   ""
     *      getFileNameWithoutExtension("   ")              =   "   "
     *      getFileNameWithoutExtension("abc")              =   "abc"
     *      getFileNameWithoutExtension("a.mp3")            =   "a"
     *      getFileNameWithoutExtension("a.b.rmvb")         =   "a.b"
     *      getFileNameWithoutExtension("c:\\")              =   ""
     *      getFileNameWithoutExtension("c:\\a")             =   "a"
     *      getFileNameWithoutExtension("c:\\a.b")           =   "a"
     *      getFileNameWithoutExtension("c:a.txt\\a")        =   "a"
     *      getFileNameWithoutExtension("/home/admin")      =   "admin"
     *      getFileNameWithoutExtension("/home/admin/a.txt/b.mp3")  =   "b"
     * </pre>
     *
     * @param filePath 文件路径
     * @return file name from path, not include suffix
     * @see
     */
    public static String getFileNameWithoutExtension(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return filePath;
        }

        int extenPosi = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        int filePosi = filePath.lastIndexOf(File.separator);
        if (filePosi == -1) {
            return (extenPosi == -1 ? filePath : filePath.substring(0,
                    extenPosi));
        }
        if (extenPosi == -1) {
            return filePath.substring(filePosi + 1);
        }
        return (filePosi < extenPosi ? filePath.substring(filePosi + 1,
                extenPosi) : filePath.substring(filePosi + 1));
    }

    /**
     * get file name from path, include suffix
     * <p/>
     * <pre>
     *      getFileName(null)               =   null
     *      getFileName("")                 =   ""
     *      getFileName("   ")              =   "   "
     *      getFileName("a.mp3")            =   "a.mp3"
     *      getFileName("a.b.rmvb")         =   "a.b.rmvb"
     *      getFileName("abc")              =   "abc"
     *      getFileName("c:\\")              =   ""
     *      getFileName("c:\\a")             =   "a"
     *      getFileName("c:\\a.b")           =   "a.b"
     *      getFileName("c:a.txt\\a")        =   "a"
     *      getFileName("/home/admin")      =   "admin"
     *      getFileName("/home/admin/a.txt/b.mp3")  =   "b.mp3"
     * </pre>
     *
     * @param filePath 文件路径
     * @return file name from path, include suffix
     */
    public static String getFileName(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return filePath;
        }

        int filePosi = filePath.lastIndexOf(File.separator);
        return (filePosi == -1) ? filePath : filePath.substring(filePosi + 1);
    }

    /**
     * get folder name from path
     * <p/>
     * <pre>
     *      getFolderName(null)               =   null
     *      getFolderName("")                 =   ""
     *      getFolderName("   ")              =   ""
     *      getFolderName("a.mp3")            =   ""
     *      getFolderName("a.b.rmvb")         =   ""
     *      getFolderName("abc")              =   ""
     *      getFolderName("c:\\")              =   "c:"
     *      getFolderName("c:\\a")             =   "c:"
     *      getFolderName("c:\\a.b")           =   "c:"
     *      getFolderName("c:a.txt\\a")        =   "c:a.txt"
     *      getFolderName("c:a\\b\\c\\d.txt")    =   "c:a\\b\\c"
     *      getFolderName("/home/admin")      =   "/home"
     *      getFolderName("/home/admin/a.txt/b.mp3")  =   "/home/admin/a.txt"
     * </pre>
     *
     * @param filePath 文件路径
     * @return 文件夹名称
     */
    public static String getFolderName(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return filePath;
        }

        int filePosi = filePath.lastIndexOf(File.separator);
        return (filePosi == -1) ? "" : filePath.substring(0, filePosi);
    }

    /**
     * get suffix of file from path
     * <p/>
     * <pre>
     *      getFileExtension(null)               =   ""
     *      getFileExtension("")                 =   ""
     *      getFileExtension("   ")              =   "   "
     *      getFileExtension("a.mp3")            =   "mp3"
     *      getFileExtension("a.b.rmvb")         =   "rmvb"
     *      getFileExtension("abc")              =   ""
     *      getFileExtension("c:\\")              =   ""
     *      getFileExtension("c:\\a")             =   ""
     *      getFileExtension("c:\\a.b")           =   "b"
     *      getFileExtension("c:a.txt\\a")        =   ""
     *      getFileExtension("/home/admin")      =   ""
     *      getFileExtension("/home/admin/a.txt/b")  =   ""
     *      getFileExtension("/home/admin/a.txt/b.mp3")  =   "mp3"
     * </pre>
     *
     * @param filePath 文件路径
     * @return
     */
    public static String getFileExtension(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return filePath;
        }

        int extenPosi = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        int filePosi = filePath.lastIndexOf(File.separator);
        if (extenPosi == -1) {
            return "";
        }
        return (filePosi >= extenPosi) ? "" : filePath.substring(extenPosi + 1);
    }

    /**
     * Creates the directory named by the trailing filename of this file,
     * including the complete directory path required to create this directory. <br/>
     * <br/>
     * <ul>
     * <strong>Attentions:</strong>
     * <li>makeDirs("C:\\Users\\Trinea") can only create users folder</li>
     * <li>makeFolder("C:\\Users\\Trinea\\") can create Trinea folder</li>
     * </ul>
     *
     * @param filePath 文件路径
     * @return true if the necessary directories have been created or the target
     * directory already exists, false one of the directories can not be
     * created.
     * <ul>
     * <li>if {@link FileUtils#getFolderName(String)} return null,
     * return false</li>
     * <li>if target directory already exists, return true</li>
     * <li>return {@link File}</li>
     * </ul>
     */
    public static boolean makeDirs(String filePath) {
        String folderName = getFolderName(filePath);
        if (StringUtils.isEmpty(folderName)) {
            return false;
        }

        File folder = new File(folderName);
        return (folder.exists() && folder.isDirectory()) || folder
                .mkdirs();
    }

    /**
     * @param filePath 文件路径
     * @return 执行结果
     * @see #makeDirs(String)
     */
    public static boolean makeFolders(String filePath) {
        return makeDirs(filePath);
    }

    public static File getFileByPath(final String filePath) {
        return StringUtils.isEmpty(filePath) ? null : new File(filePath);
    }

    public static long getFileLastModified(final String filePath) {
        return getFileLastModified(getFileByPath(filePath));
    }

    public static long getFileLastModified(final File file) {
        if (file == null) {
            return -1;
        }
        return file.lastModified();
    }

    public static String getFileCharsetSimple(final String filePath) {
        return getFileCharsetSimple(getFileByPath(filePath));
    }

    public static String getFileCharsetSimple(final File file) {
        int p = 0;
        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(file));
            p = (is.read() << 8) + is.read();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        switch (p) {
            case 0xefbb:
                return "UTF-8";
            case 0xfffe:
                return "Unicode";
            case 0xfeff:
                return "UTF-16BE";
            default:
                return "GBK";
        }
    }

    public static boolean createOrExistsDir(final File file) {
        return createOrExistsDir(file);
    }

    public static boolean createOrExistsDir(final String dirPath) {
        return createOrExistsDir(getFileByPath(dirPath));
    }

    /**
     * 判断指定的文件是否存在，如果不存在就创建它
     *
     * @param file 指定的文件
     * @return 该文件是否存在，true 表示存在
     */
    public static boolean createOrExistsFile(final File file) {
        if (file == null) {
            return false;
        }
        if (file.exists()) {
            return file.isFile();
        }
        // 判断父目录是否是文件夹，如果不存在就创建它
        if (!createOrExistsDir(file.getParentFile())) {
            return false;
        }
        try {
            // 创建文件
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean createFileByDeleteOldFile(final String filePath) {
        return createFileByDeleteOldFile(getFileByPath(filePath));
    }

    /**
     * Create a file if it doesn't exist, otherwise delete old file before creating.
     *
     * @param file The file.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean createFileByDeleteOldFile(final File file) {
        if (file == null) {
            return false;
        }
        // file exists and unsuccessfully delete then return false
        if (file.exists() && !file.delete()) {
            return false;
        }
        if (!createOrExistsDir(file.getParentFile())) {
            return false;
        }
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
