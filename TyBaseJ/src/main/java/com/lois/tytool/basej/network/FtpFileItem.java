package com.lois.tytool.basej.network;

import com.lois.tytool.basej.io.FileUtils;

import java.util.Date;

/**
 * FtpFileItem
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public class FtpFileItem {
    private long size;
    private String name;
    private int type;
    private String directory;
    private Date date;

    public FtpFileItem(long size, String name, int type, String directory, Date date) {
        this.size = size;
        this.name = name;
        this.type = type;
        this.directory = directory;
        this.date = (Date) date.clone();
    }

    public boolean isDirectory() {
        return this.type == 1;
    }

    public boolean isFile() {
        return this.type == 0;
    }

    public String getFilePath() {
        if (!isFile()) {
            return null;
        }
        StringBuilder builder = new StringBuilder(3);
        builder.append(this.directory);
        builder.append(FileUtils.SEPARATOR);
        builder.append(this.name);
        return builder.toString();
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public Date getDate() {
        return (Date) date.clone();
    }

    public void setDate(Date date) {
        this.date = (Date) date.clone();
    }

    @Override
    public String toString() {
        return "FtpFileItem{" +
                "size=" + size +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", directory='" + directory + '\'' +
                ", date=" + date +
                '}';
    }
}
