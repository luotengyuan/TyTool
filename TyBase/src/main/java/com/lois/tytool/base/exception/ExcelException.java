package com.lois.tytool.base.exception;

/**
 * @Description excel异常
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public class ExcelException extends RuntimeException {
    public ExcelException() { }
    public ExcelException(String message) {
        super(message);
    }
}
