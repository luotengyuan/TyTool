package com.lois.tytool.basej.jdbc.entity;

/**
 * 表中主键信息
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public class PrimaryKey {
    /**
     * 列名称
     */
    private String colName;
    /**
     * 主键的名称（可为 null）
     */
    private String pkName;
    /**
     * 主键中的序列号
     */
    private short keySeq;

    public String getColName() {
        return colName;
    }

    public void setColName(String colName) {
        this.colName = colName;
    }

    public String getPkName() {
        return pkName;
    }

    public void setPkName(String pkName) {
        this.pkName = pkName;
    }

    public short getKeySeq() {
        return keySeq;
    }

    public void setKeySeq(short keySeq) {
        this.keySeq = keySeq;
    }
}
