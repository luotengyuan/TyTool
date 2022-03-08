package com.lois.tytool.basej.exception;

/**
 * 压缩以及解压缩异常
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public class ZipException extends Exception {

    public ZipException() { }
    public ZipException(String message) {
        super(message);
    }

    public ZipException(String message, Throwable cause) {
        super(message, cause);
    }

    public ZipException(Throwable cause) {
        super(cause);
    }

}
