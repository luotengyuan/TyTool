package com.lois.tytool.basej.network.ftp;

import com.lois.tytool.basej.debug.TyLog;
import com.lois.tytool.basej.exception.FtpException;
import com.lois.tytool.basej.io.FileUtils;
import com.lois.tytool.basej.io.IOUtils;
import com.lois.tytool.basej.string.StringUtils;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * FTP工具类
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public class Ftp {

    private static String ANONYMOUS = "anonymous";
    private static String ANONYMOUS_PASSWORD = "anonymous";

    /**
     * ftp客户端
     */
    private FTPClient ftpClient;

    /**
     * 连接超时时间 默认3s
     */
    private int connectionTimeout = 3000;
    /**
     * ftp连接端口 默认21
     */
    private int port = 21;
    /**
     * FTP ip/域名 地址
     */
    private String hostname;

    /**
     * FTP帐号
     */
    private String username;
    /**
     * FTP密码
     */
    private String pwd;

    /**
     * 当上传路径不存在时，是否自动创建目录。默认为false<br/>
     * 在单机模式下，开启没有问题，在集群模式下，有并发问题。
     */
    private boolean automaticDirectoryCreation = false;


    private int fileType = FTPClient.BINARY_FILE_TYPE;

    private String encoding = CharEncoding.UTF_8;


    public Ftp(String hostname) {
        this.hostname = hostname;
    }

    public Ftp(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public Ftp(String hostname, String username, String pwd) {
        this.hostname = hostname;
        this.username = username;
        this.pwd = pwd;
    }

    public Ftp(String hostname, int port, String username, String pwd) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.pwd = pwd;
    }


    public Ftp(String hostname, int port, String username, String pwd, int connectionTimeout) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.pwd = pwd;
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * 连接和登录FTP服务器
     * @throws FtpException 登录失败异常，可能帐号密码不正确
     */
    public void connect() throws FtpException {
        try {
            ftpClient = new FTPClient();
            //设置超时
            ftpClient.setConnectTimeout(this.connectionTimeout);
            //初始化ftp客户端
            ftpClient.connect(this.hostname, this.port);
            ftpClient.enterLocalPassiveMode();
            //设置文件传输类型
            ftpClient.setFileType(this.fileType);
            //设置字符编码格式
            ftpClient.setControlEncoding(this.encoding);
            boolean result = false;
            if (StringUtils.isNotBlank(this.username)) {
                //帐号密码登录
                result = ftpClient.login(this.username, this.pwd);
            } else {
                //匿名登录
                result = ftpClient.login(ANONYMOUS, ANONYMOUS_PASSWORD);
            }
            if (!result) {
                close();
                throw new FtpException("FTP server login failed.");
            }
            TyLog.v("FTP server login successful.");
        } catch (IOException e) {
            throw new FtpException(e);
        }
    }

    /**
     * 断开连接
     */
    public void close() {
        boolean closed = true;
        if (ftpClient != null && ftpClient.isConnected()) {
            try {
                //退出帐号
                ftpClient.logout();
                //断开连接
                ftpClient.disconnect();
            } catch (IOException e) {
                TyLog.e(e.getMessage(), e);
                closed = false;
            }
        }
        TyLog.v("FTP server connection closed result [{}]", closed);
    }


    /**
     * 上传文件
     * @param savePath 全路径。如/home/public/test.txt
     * @param fileInputStream 要上传的文件流
     * @return 上传结果
     * @throws FtpException ftp异常，可能有路径不存在、文件不存在、IO异常等
     */
    public boolean uploadFile(String savePath, InputStream fileInputStream) throws FtpException {
        try {
            //每次数据连接之前，ftp client告诉server开通一个端口来传输数据，即被动模式
            ftpClient.enterLocalPassiveMode();
            //文件存放目录
            String fileDir = FileUtils.getFileDirectory(savePath);
            //切换当前目录到文件存放的目录
            boolean change = ftpClient.changeWorkingDirectory(fileDir);
            if (!change) {
                //切换失败，可能不存在该目录
                if (automaticDirectoryCreation) {
                    boolean createDir = createDirectory(fileDir);
                    if (!createDir) {
                        throw new FtpException("创建目录失败");
                    }
                } else {
                    throw new FtpException("FTP 服务器不存在目录【" + fileDir + "】，上传失败");
                }
            }
            //防止中文乱码，把UTF-8格式文件路径转为ISO-8859-1
            String savePathConversion = new String(savePath.getBytes(CharEncoding.UTF_8), CharEncoding.ISO_8859_1);
            boolean upload = ftpClient.storeFile(savePathConversion, fileInputStream);
            return upload;
        } catch (Exception e) {
            close();
            throw new FtpException(e);
        }
    }


    /**
     * 上传文件
     * @param savePath 全路径。如/home/public/a.txt
     * @param filePath 要上传的文件路径
     * @throws FtpException ftp异常，可能有路径不存在、文件不存在、IO异常等
     * @return 上传结果
     */
    public boolean uploadFile(String savePath, String filePath) throws FtpException {
        File file = new File(filePath);
        FileInputStream fileInputStream = null;
        boolean upload = false;
        try {
            fileInputStream = new FileInputStream(file);
            upload = uploadFile(savePath, fileInputStream);
        } catch (FileNotFoundException e) {
            throw new FtpException(e);
        } finally {
            IOUtils.close(fileInputStream);
        }
        return upload;
    }


    /**
     * 下载FTP服务器的文件
     * @param remoteFilePath FTP服务器指定文件路径
     * @param fileOutputStream 输出到指定流
     * @return 下载是否成功
     * @throws FtpException ftp异常，可能有路径不存在、文件不存在、IO异常等
     */
    public boolean downloadFile(String remoteFilePath, OutputStream fileOutputStream) throws FtpException {
        try {
            //防止中文乱码
            String fileName = new String(remoteFilePath.getBytes(CharEncoding.UTF_8), CharEncoding.ISO_8859_1);
            return ftpClient.retrieveFile(fileName, fileOutputStream);
        } catch (IOException e) {
            throw new FtpException(e);
        }
    }

    /**
     * 下载FTP服务器的文件
     * @param remoteFilePath FTP服务器指定文件路径
     * @param filePath 本地文件路径，需要带上全路径
     * @return 是否下载成功
     * @throws FtpException FTP异常，可能是文件不存在等异常
     */
    public boolean downloadFile(String remoteFilePath, String filePath) throws FtpException {
        File file = new File(filePath);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            return downloadFile(remoteFilePath, fileOutputStream);
        } catch (FileNotFoundException e) {
            throw new FtpException(e);
        } finally {
            IOUtils.close(fileOutputStream);
        }
    }
    /**
     * 获取 FTP客户端当前操作的目录
     * @return 当前操作的文件目录
     * @throws FtpException FTP异常，可能是iO异常
     */
    public String getCurrentWorkingDirectory() throws FtpException {
        try {
            return ftpClient.printWorkingDirectory();
        } catch (IOException e) {
            throw new FtpException(e.getMessage(), e);
        }
    }

    /**
     * 获取 FTP客户端当前操作的目录下的所有文件信息
     * @return 文件列表
     * @throws FtpException FTP异常，可能是IO异常、文件路径异常等
     */
    public List<FtpFileItem> getWorkdingDirectoryListFiles() throws FtpException {
        String currentDirectory = getCurrentWorkingDirectory();
        List<FtpFileItem> ftpFileItems = getWorkdingDirectoryListFiles(currentDirectory);
        return ftpFileItems;
    }

    /**
     * 获取 FTP客户端指定目录下的所有文件信息
     * @param directory 指定远程目录
     * @return 文件列表
     * @throws FtpException FTP异常，可能是IO异常、文件路径异常等
     */
    public List<FtpFileItem> getWorkdingDirectoryListFiles(String directory) throws FtpException {
        // 转移到FTP服务器目录
        try {
            boolean change = ftpClient.changeWorkingDirectory(directory);
            if (!change) {
                throw new FtpException("FTP 切换到目录【 " + directory + " 】失败，可能FTP Server不存在该目录");
            }
            FTPFile[] fs = ftpClient.listFiles();
            if (fs.length <= 0) {
                return Collections.emptyList();
            }
            List<FtpFileItem> ftpFileItems = new ArrayList<>(fs.length);
            for (FTPFile ftpFile : fs) {
                FtpFileItem ftpFileItem = new FtpFileItem(ftpFile.getSize(), ftpFile.getName(), ftpFile.getType(), directory, ftpFile.getTimestamp().getTime());
                ftpFileItems.add(ftpFileItem);
            }
            return ftpFileItems;
        } catch (IOException e) {
            throw new FtpException(e);
        }
    }


    /**
     * 判断指定文件名称，是否在当前的工作目录中（默认根目录）
     * @param fileName 文件名称
     * @return 是否存在
     * @throws FtpException FTP异常，可能是IO异常、文件路径异常等
     */
    public boolean existFileInCurrentWorkdingPath(String fileName) throws FtpException {
        String currentPath = getCurrentWorkingDirectory();
        return existFileInWorkingPath(currentPath, fileName);
    }

    /**
     * 判断指定文件名称，是否在指定的文件夹中
     * @param workingDirectory 文件夹路径
     * @param fileName 文件名称
     * @return 是否存在
     * @throws FtpException FTP异常，可能是IO异常、文件路径异常等
     */
    public boolean existFileInWorkingPath(String workingDirectory, String fileName) throws FtpException {
        List<FtpFileItem> ftpFileItems = getWorkdingDirectoryListFiles(workingDirectory);
        for (FtpFileItem ftpFileItem : ftpFileItems) {
            if (ftpFileItem.getName().equals(fileName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 重命名指定目录下的文件
     * @param directory 目录
     * @param oldFileName 旧文件名称
     * @param newFileName 新文件名称
     * @throws FtpException FTP异常，可能是IO异常、文件路径异常等
     */
    public void renameFile(String directory, String oldFileName, String newFileName) throws FtpException {
        try {
            boolean change = ftpClient.changeWorkingDirectory(directory);
            if (!change) {
                throw new FtpException("FTP 切换到目录【 " + directory + " 】失败，可能FTP Server不存在该目录");
            }
            String oldName = new String(oldFileName.getBytes(CharEncoding.UTF_8), CharEncoding.ISO_8859_1);
            String newName = new String(newFileName.getBytes(CharEncoding.UTF_8), CharEncoding.ISO_8859_1);
            boolean rename =  ftpClient.rename(oldName, newName);
            if (!rename) {
                throw new FtpException("修改文件名称失败");
            }
        } catch (IOException e) {
            throw new FtpException(e);
        }
    }

    /**
     * 在当前操作目录上上创建一个文件夹
     * @param dir 文件夹名称，不能含有特殊字符，如 \ 、/ 、: 、* 、?、 "、 <、>...
     * @return 是否创建成功
     * @throws FtpException FTP异常，可能是IO异常、文件路径异常等
     */
    private boolean makeDirectory(String dir) throws FtpException {
        try {
            return ftpClient.makeDirectory(dir);
        } catch (Exception e) {
            throw new FtpException(e);
        }
    }

    /**
     * 递归创建远程服务器目录
     *
     * @param remote 远程服务器文件绝对路径
     * @return 目录创建是否成功
     * @throws IOException
     */
    private synchronized boolean createDirectory(String remote) throws IOException, FtpException {
        String directoryFoot = "/";
        if(!remote.endsWith(directoryFoot)) {
            remote = remote + directoryFoot;
        }
        String directory = remote;
        // 如果远程目录不存在，则递归创建远程服务器目录
        if (!directory.equalsIgnoreCase(directoryFoot) && ! ftpClient.changeWorkingDirectory(directory)) {
            int start;
            int end;
            if (directory.startsWith(directoryFoot)) {
                start = 1;
            } else {
                start = 0;
            }
            end = directory.indexOf(directoryFoot, start);
            while (true) {
                String subDirectory = new String(remote.substring(start, end).getBytes(CharEncoding.UTF_8), CharEncoding.ISO_8859_1);
                if (!ftpClient.changeWorkingDirectory(subDirectory)) {
                    if (makeDirectory(subDirectory)) {
                        ftpClient.changeWorkingDirectory(subDirectory);
                    } else {
                        TyLog.e("创建目录【{}】失败", directory);
                        return false;
                    }
                }
                start = end + 1;
                end = directory.indexOf(directoryFoot, start);
                // 检查所有目录是否创建完毕
                if (end <= start) {
                    break;
                }
            }
        }
        return true;
    }


    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setAutomaticDirectoryCreation(boolean automaticDirectoryCreation) {
        this.automaticDirectoryCreation = automaticDirectoryCreation;
    }
}

