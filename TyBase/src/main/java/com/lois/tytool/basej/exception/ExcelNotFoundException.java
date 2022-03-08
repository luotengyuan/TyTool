package com.lois.tytool.basej.exception;

/**
 * @Description Excel未找到异常
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public class ExcelNotFoundException extends ExcelException {
    /**
     * 文件路径
     */
    private String filePath;
    public ExcelNotFoundException() { }
    public ExcelNotFoundException(String message) {
        super(message);
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
