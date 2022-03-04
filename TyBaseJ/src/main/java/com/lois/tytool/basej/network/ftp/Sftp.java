package com.lois.tytool.basej.network.ftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.lois.tytool.basej.debug.TyLog;
import com.lois.tytool.basej.exception.FtpException;
import com.lois.tytool.basej.io.IOUtils;
import com.lois.tytool.basej.string.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

/**
 * SFTP工具（单线程工具）
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public class Sftp {

    private static String ANONYMOUS = "anonymous";
    private static String ANONYMOUS_PASSWORD = "anonymous";
    /**
     * 会话
     */
    private Session session = null;
    /**
     * sftp连接管道
     */
    private ChannelSftp channel = null;
    /**
     * sftp默认连接端口
     */
    private int port = 22;
    /**
     * sftp默认超时时间3s
     */
    private int connectionTimeout = 3000;
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



    public Sftp(String hostname) {
        this.hostname = hostname;
    }

    public Sftp(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public Sftp(String hostname, String username, String pwd) {
        this.hostname = hostname;
        this.username = username;
        this.pwd = pwd;
    }

    public Sftp(String hostname, int port, String username, String pwd) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.pwd = pwd;
    }


    public Sftp(String hostname, int port, String username, String pwd, int connectionTimeout) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.pwd = pwd;
        this.connectionTimeout = connectionTimeout;
    }

    public void connect() throws FtpException {
        JSch jsch = new JSch();
        // 根据用户名，主机ip，端口获取一个Session对象
        try {
            session = jsch.getSession(this.username, this.hostname, this.port);
            if (StringUtils.isNotBlank(this.pwd)) {
                session.setPassword(this.pwd);
            }
            // 为Session对象设置properties
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setTimeout(this.connectionTimeout);
            session.setConfig(config);
            session.connect();
            this.channel = (ChannelSftp) session.openChannel("sftp");
            // 建立SFTP通道的连接
            channel.connect();
            TyLog.v("SFTP server login successful.");
        } catch (JSchException e) {
            throw new FtpException(e);
        }
    }

    public void close() {
        if (channel != null && channel.isConnected()) {
            channel.disconnect();
        }
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
        TyLog.v("SFTP server connection closed successfully ");
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
            channel.put(fileInputStream, savePath);
            return true;
        } catch (SftpException e) {
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
            this.channel.get(remoteFilePath, fileOutputStream);
            return true;
        } catch (SftpException e) {
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
            return this.channel.pwd();
        } catch (SftpException e) {
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
     * 获取 SFTP客户端指定目录下的所有文件信息
     * @param directory 指定远程目录
     * @return 文件列表
     * @throws FtpException FTP异常，可能是IO异常、文件路径异常等
     */
    public List<FtpFileItem> getWorkdingDirectoryListFiles(String directory) throws FtpException {
        try {
            Vector vv = channel.ls(directory);
            if (vv.size() == 0) {
                return Collections.EMPTY_LIST;
            }
            Object[] items = vv.toArray();
            List<FtpFileItem> ftpFileItems = new ArrayList<>(items.length);
            for (int i = 0; i < items.length; i++) {
                ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) items[i];
                SftpATTRS attrs = entry.getAttrs();
                Date date = new Date(attrs.getMTime() * 1000L);
                int type = attrs.isDir() ? 1 : 0;
                FtpFileItem fileItem = new FtpFileItem(attrs.getSize(), entry.getFilename(), type, directory, date);
                ftpFileItems.add(fileItem);
            }
            return ftpFileItems;
        } catch (SftpException e) {
            throw new FtpException(e);
        }
    }


    /**
     * 重命名指定目录下的文件
     * @param directory 目录
     * @param oldFileName 旧文件名称
     * @param newFileName 新文件名称
     * @throws FtpException FTP异常，可能是IO异常、文件路径异常等
     * @return 是否成功
     */
    public boolean renameFile(String directory, String oldFileName, String newFileName) throws FtpException {
        try {
            channel.cd(directory);
            channel.rename(oldFileName, newFileName);
            return true;
        } catch (SftpException e) {
            throw new FtpException(e);
        }
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
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
}
