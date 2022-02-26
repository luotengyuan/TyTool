package com.lois.tytool.basej.exception;

/**
 * cell校验异常
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public class CellCheckException extends ExcelException {
    /**
     * 行数
     */
    private int rowIndex;
    /**
     * 列数
     */
    private int cellIndex;

    public CellCheckException() { }
    public CellCheckException(String message) {
        super(message);
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public int getCellIndex() {
        return cellIndex;
    }

    public void setCellIndex(int cellIndex) {
        this.cellIndex = cellIndex;
    }
}
