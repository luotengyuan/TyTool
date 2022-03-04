package com.lois.unclassifiedj.generator;

import java.util.ArrayList;
import java.util.List;

/**
 * Table
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public class Table {
    /**
     * 数据库schema（用户名）
     */
    private String schema;
    /**
     * 表名称
     */
    private String tableName;
    /**
     * <p>生成 cout sql by example</p>
     * <p>默认为true</p>
     */
    private boolean enableCountByExample = true;
    /**
     * <p>生成 update sql by example</p>
     * <p>默认为true</p>
     */
    private boolean enableUpdateByExample = true;
    /**
     * <p>生成 delete sql by example</p>
     * <p>默认为true</p>
     */
    private boolean enableDeleteByExample = true;
    /**
     * <p>生成 select sql by example</p>
     * <p>默认为true</p>
     */
    private boolean enableSelectByExample = true;
    /**
     * <p>生成 select query id sql by example</p>
     * <p>默认为true</p>
     */
    private boolean selectByExampleQueryId = true;
    /**
     * <p>设置主键策略，可选：</p>
     * <ul>
     *     <li>DB2</li>
     *     <li>DB2_MF</li>
     *     <li>Derby</li>
     *     <li>HSQLDB</li>
     *     <li>Informix</li>
     *     <li>MySql</li>
     *     <li>SqlServer</li>
     *     <li>SYBASE</li>
     *     <li>JDBC</li>
     * </ul>
     */
     private String sqlStatment;
     /**
      * 主键字段
      */
     private String primaryKey;
    /**
     * 是否自增
     */
    private boolean identity = false;

    private List<ColumnOverride> columnOverrideList = new ArrayList<>();
    public Table() {
    }
    public Table(String tableName) {
        this.tableName = tableName;
    }



    /**
     * 获取 数据库schema
     * @return 数据库schema
     */
    public String getSchema() {
        return this.schema;
    }

    /**
     * 设置 数据库schema
     * @param schema 数据库schema
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    /**
     * 获取 表名称
     * @return 表名称
     */
    public String getTableName() {
        return this.tableName;
    }

    /**
     * 设置 表名称
     * @param tableName 表名称
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * <p>获取是否生成 cout sql by example</p>
     * <p>默认为true</p>
     * @return 是否生成 cout sql
     */
    public boolean isEnableCountByExample() {
        return this.enableCountByExample;
    }

    /**
     * <p>设置是否生成 cout sql by example</p>
     * <p>默认为true</p>
     * @param enableCountByExample 是否生成 cout sql
     */
    public void setEnableCountByExample(boolean enableCountByExample) {
        this.enableCountByExample = enableCountByExample;
    }

    /**
     * <p>获取是否生成 update sql by example</p>
     * <p>默认为true</p>
     * @return 是否生成 update sql
     */
    public boolean isEnableUpdateByExample() {
        return this.enableUpdateByExample;
    }

    /**
     * <p>设置是否生成 update sql by example</p>
     * <p>默认为true</p>
     * @param enableUpdateByExample 是否生成 update sql
     */
    public void setEnableUpdateByExample(boolean enableUpdateByExample) {
        this.enableUpdateByExample = enableUpdateByExample;
    }

    /**
     * <p>获取是否生成 delete sql by example</p>
     * <p>默认为true</p>
     * @return 是否生成 delete sql
     */
    public boolean isEnableDeleteByExample() {
        return this.enableDeleteByExample;
    }

    /**
     * <p>设置是否生成 delete sql by example</p>
     * <p>默认为true</p>
     * @param enableDeleteByExample 是否生成 delete sql
     */
    public void setEnableDeleteByExample(boolean enableDeleteByExample) {
        this.enableDeleteByExample = enableDeleteByExample;
    }

    /**
     * <p>获取是否生成 select sql by example</p>
     * <p>默认为true</p>
     * @return 是否生成 select sql
     */
    public boolean isEnableSelectByExample() {
        return this.enableSelectByExample;
    }

    /**
     * <p>设置是否生成 select sql by example</p>
     * <p>默认为true</p>
     * @param enableSelectByExample 是否生成 select sql
     */
    public void setEnableSelectByExample(boolean enableSelectByExample) {
        this.enableSelectByExample = enableSelectByExample;
    }

    /**
     * <p>获取是否生成 select query id sql by example</p>
     * <p>默认为true</p>
     * @return 是否生成 select query id sql
     */
    public boolean isSelectByExampleQueryId() {
        return this.selectByExampleQueryId;
    }

    /**
     * <p>设置是否生成 select query id sql by example</p>
     * <p>默认为true</p>
     * @param selectByExampleQueryId select query id sql
     */
    public void setSelectByExampleQueryId(boolean selectByExampleQueryId) {
        this.selectByExampleQueryId = selectByExampleQueryId;
    }


    /**
     * <p>设置主键策略，可选：</p>
     * <ul>
     *     <li>DB2</li>
     *     <li>DB2_MF</li>
     *     <li>Derby</li>
     *     <li>HSQLDB</li>
     *     <li>Informix</li>
     *     <li>MySql</li>
     *     <li>SqlServer</li>
     *     <li>SYBASE</li>
     *     <li>JDBC</li>
     * </ul>
     * @return sqlStatement
     */
    public String getSqlStatment() {
        return this.sqlStatment;
    }

    /**
     * <p>设置主键策略，可选：</p>
     * <ul>
     *     <li>DB2</li>
     *     <li>DB2_MF</li>
     *     <li>Derby</li>
     *     <li>HSQLDB</li>
     *     <li>Informix</li>
     *     <li>MySql</li>
     *     <li>SqlServer</li>
     *     <li>SYBASE</li>
     *     <li>JDBC</li>
     * </ul>
     * @param sqlStatment 数据库类型
     */
    public void setSqlStatment(String sqlStatment) {
        this.sqlStatment = sqlStatment;
    }

    /**
     * 获取 主键字段
     * @return 主键字段
     */
    public String getPrimaryKey() {
        return this.primaryKey;
    }

    /**
     * 设置 主键字段
     * @param primaryKey 主键
     */
    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }


    /**
     * 获取 是否自增
     * @return 是否自增
     */
    public boolean isIdentity() {
        return this.identity;
    }

    /**
     * 设置 是否自增
     * @param identity 是否自增
     */
    public void setIdentity(boolean identity) {
        this.identity = identity;
    }

    /**
     * 设置字段的属性
     * @param column 字段名
     * @param jdbcType jdbc类型
     * @return table对象
     */
    public Table setColumnOverride(String column, String jdbcType) {
        ColumnOverride columnOverride = new ColumnOverride();
        columnOverride.setColumn(column);
        columnOverride.setJdbcType(jdbcType);
        this.columnOverrideList.add(columnOverride);
        return this;
    }

    /**
     * 设置字段属性
     * @param column 字段名
     * @param jdbcType jdbc类型
     * @param javaType 实体类属性类型
     * @return table对象
     */
    public Table setColumnOverride(String column, String jdbcType, String javaType) {
        ColumnOverride columnOverride = new ColumnOverride();
        columnOverride.setColumn(column);
        columnOverride.setJdbcType(jdbcType);
        columnOverride.setJavaType(javaType);
        this.columnOverrideList.add(columnOverride);
        return this;
    }

    /**
     * 设置字段属性
     * @param columnOverride 字段属性
     * @return table对象
     */
    public Table setColumnOverride(ColumnOverride columnOverride) {
        this.columnOverrideList.add(columnOverride);
        return this;
    }

    public List<ColumnOverride> getColumnOverrideList() {
        return columnOverrideList;
    }
}
