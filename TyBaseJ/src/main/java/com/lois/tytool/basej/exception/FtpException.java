package com.lois.tytool.basej.exception;

/**
 * @Description FTP异常
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public class FtpException extends Exception {

    public FtpException() { }
    public FtpException(String message) {
        super(message);
    }

    public FtpException(String message, Throwable cause) {
        super(message, cause);
    }

    public FtpException(Throwable cause) {
        super(cause);
    }

}
