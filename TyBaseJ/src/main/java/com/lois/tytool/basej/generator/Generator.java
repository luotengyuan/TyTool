package com.lois.tytool.basej.generator;

import java.util.List;

/**
 * Generator
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public interface Generator {
    /**
     * <p>实体类(po)是否自动继承{@code Serializable}序列化接口</p>
     * <p>默认为true</p>
     * @param enableSerializable 实体类是否继承Serializable序列化接口
     */
    void setEnableSerializable(boolean enableSerializable);

    /**
     * <p>实体类(po)是否生成toString()方法</p>
     * <p>默认为true</p>
     * @param enableToString 是否生成toString方法
     */
    void setEnableToString(boolean enableToString);

    /**
     * <p>设置是否生成Example文件</p>
     * <p>默认为true</p>
     * @param enableExample 是否生成example文件
     */
    void setEnableExample(boolean enableExample);

    /**
     * <p>设置是否生成count sql by example</p>
     * <p>默认为true</p>
     * @param enableCountByExample 是否生成count sql
     */
    void setEnableCountByExample(boolean enableCountByExample);

    /**
     * <p>设置是否生成update sql by example</p>
     * <p>默认为true</p>
     * @param enableUpdateByExample 是否生成update sql
     */
    void setEnableUpdateByExample(boolean enableUpdateByExample);

    /**
     * <p>设置是否生成delete sql by example</p>
     * <p>默认为true</p>
     * @param enableDeleteByExample 是否生成delete sql
     */
    void setEnableDeleteByExample(boolean enableDeleteByExample);

    /**
     * <p>设置是否生成select sql by example</p>
     * <p>默认为true</p>
     * @param enableSelectByExample 是否生成select sql
     */
    void setEnableSelectByExample(boolean enableSelectByExample);

    /**
     * <p>设置是否生成select query id sql by example</p>
     * <p>默认为true</p>
     * @param enableSelectByExampleQueryId 是否生成select query id sql
     */
    void setEnableSelectByExampleQueryId(boolean enableSelectByExampleQueryId);

    /**
     * <p>设置数据库schema</p>
     * <p>默认为空</p>
     * @param schema 数据库schema
     */
    void setDefaultSchema(String schema);

    /**
     * 设置数据库配置信息
     * @param jdbcConfig 数据库配置
     */
    void setJdbcConfig(JdbcConfig jdbcConfig);

    /**
     * <p>true时，把 JDBC DECIMAL 和 NUMERIC 类型解析为java.math.BigDecimal</p>
     * <p>false时，把 JDBC DECIMAL 和 NUMERIC 类型解析为 Integer</p>
     * <p>默认为false</p>
     * @param forceBigDecimalsAble 是否将bigDecimal类型转成integer类型
     */
    void setForceBigDecimals(boolean forceBigDecimalsAble);

    /**
     * <p>设置entity实体类的包</p>
     * @param targetPackage 包路径
     */
    void setModelTargetPackage(String targetPackage);

    /**
     * <p>设置entity项目路径</p>
     * <p>例如：./src/main/java</p>
     * @param targetProject 生成的路径
     */
    void setModelTargetProject(String targetProject);

    /**
     * <p>设置entity数据前后清理空格</p>
     * <p>默认为false</p>
     * @param trimStringsable 是否清理字符串空格
     */
    void setModelTrimStrings(boolean trimStringsable);

    /**
     * <p>设置mapper映射文件的包</p>
     * @param targetPackage 包路径
     */
    void setSqlMapTargetPackage(String targetPackage);

    /**
     * <p>设置mapper映射文件项目路径</p>
     * <p>例如：./src/main/java</p>
     * @param targetProject 生成的路径
     */
    void setSqlMapTargetProject(String targetProject);

    /**
     * <p>设置mapper接口的包</p>
     * @param targetPackage 包路径
     */
    void setClientTargetPackage(String targetPackage);

    /**
     * <p>设置mapper接口项目路径</p>
     * <p>例如：./src/main/java</p>
     * @param targetProject 生成的路径
     */
    void setClientTargetProject(String targetProject);

    /**
     * <p>添加需要生成代码的数据库表名称</p>
     * <p>按默认配置生成</p>
     * @param tableName 数据库表名称
     */
    void addTableName(String tableName);

    /**
     * <p>添加需要生成代码的数据库表名称</p>
     * <p>按默认配置生成</p>
     * @param tablesName 数据库表名称集合
     */
    void addTablesName(List<String> tablesName);

    /**
     * <p>添加需要生成代码的数据库表配置</p>
     * @param table 数据库表配置
     */
    void addTable(Table table);

    /**
     * <p>添加需要生成代码的数据库表配置</p>
     * @param tables 数据库表配置集合
     */
    void addTables(List<Table> tables);

    /**
     * 开始生成代码
     */
    void create();

}
