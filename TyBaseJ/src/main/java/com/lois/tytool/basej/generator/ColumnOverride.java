package com.lois.tytool.basej.generator;

/**
 * Column
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public class ColumnOverride {
    /**
     * 表字段名
     */
    private String column;
    /**
     * java对应的属性名称
     */
    private String property;
    /**
     * java属性的类型
     */
    private String javaType;
    /**
     * jdbc类型
     */
    private String jdbcType;

    private String typeHandler;

    private String delimitedColumnName;

    private boolean generatedAlways;

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    public String getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(String jdbcType) {
        this.jdbcType = jdbcType;
    }

    public String getTypeHandler() {
        return typeHandler;
    }

    public void setTypeHandler(String typeHandler) {
        this.typeHandler = typeHandler;
    }

    public String getDelimitedColumnName() {
        return delimitedColumnName;
    }

    public void setDelimitedColumnName(String delimitedColumnName) {
        this.delimitedColumnName = delimitedColumnName;
    }

    public boolean isGeneratedAlways() {
        return generatedAlways;
    }

    public void setGeneratedAlways(boolean generatedAlways) {
        this.generatedAlways = generatedAlways;
    }
}
