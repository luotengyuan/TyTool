package com.lois.tytool.basej.generator.impl;

import com.lois.tytool.basej.generator.ColumnOverride;
import com.lois.tytool.basej.generator.Generator;
import com.lois.tytool.basej.generator.JdbcConfig;
import com.lois.tytool.basej.generator.Table;
import com.lois.tytool.basej.string.StringUtils;
import com.lois.tytool.basej.util.CollectionUtils;
import com.lois.tytool.basej.xml.XmlUtils;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MybatisGenerator
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public class MybatisGenerator implements Generator {
    private static Logger logger = LoggerFactory.getLogger(MybatisGenerator.class);
    private static String ORACLE_DB = "oracle";

    /**
     * <p>实体类是否继承Serializable序列化接口</p>
     * <p>默认为true</p>
     */
    private boolean enableSerializable = true;
    /**
     * <p>实体类是否生成toString()方法</p>
     * <p>默认为true</p>
     */
    private boolean enableToString = true;
    /**
     * <p>是否生成example文件</p>
     * <p>默认为true</p>
     */
    private boolean enableExample = true;
    /**
     * <p>是否生成 count sql by example</p>
     * <p>默认为true</p>
     */
    private boolean enableCountByExample = true;
    /**
     * <p>是否生成 update sql by example</p>
     * <p>默认为true</p>
     */
    private boolean enableUpdateByExample = true;
    /**
     * <p>是否生成 delete sql by example</p>
     * <p>默认为true</p>
     */
    private boolean enableDeleteByExample = true;
    /**
     * <p>是否生成 select sql by example</p>
     * <p>默认为true</p>
     */
    private boolean enableSelectByExample = true;
    /**
     * <p>是否生成 select query id sql by example</p>
     * <p>默认为true</p>
     */
    private boolean enableSelectByExampleQueryId = true;
    /**
     * 默认的数据库schema
     */
    private String schema;
    /**
     * 数据库配置信息
     */
    private JdbcConfig jdbcConfig;
    /**
     * <p>true时，把 JDBC DECIMAL 和 NUMERIC 类型解析为java.math.BigDecimal</p>
     * <p>false时，把 JDBC DECIMAL 和 NUMERIC 类型解析为 Integer</p>
     * <p>默认为false</p>
     */
    private boolean forceBigDecimalsAble;
     /**
     * 设置entity实体类的包路径
     */
    private String modelTargetPackage;
    /**
     * 设置entity项目路径
     */
    private String modelTargetProject;
    /**
     * <p>设置entity数据前后清理空格</p>
     * <p>默认为false</p>
     */
    private boolean trimStringsable;
    /**
     * 设置mapper映射文件的包路径
     */
    private String sqlMapTargetPackage;
    /**
     * 设置mapper映射文件项目路径
     */
    private String sqlMapTargetProject;
    /**
     * 设置mapper接口的包路径
     */
    private String clientTargetPackage;
    /**
     * 设置mapper接口项目路径
     */
    private String clientTargetProject;
    /**
     * 存放需要生成代码的表信息
     */
    private Map<String, Table> tableMap = new HashMap<String, Table>();
    /**
     * 数据表名集合
     */
    private List<String> tablesName = new ArrayList<String>();
    /**
     * 数据表名集合，包含配置信息
     */
    private List<Table> tables = new ArrayList<Table>();
    @Override
    public void setEnableSerializable(boolean enableSerializable) {
        this.enableSerializable = enableSerializable;
    }

    @Override
    public void setEnableToString(boolean enableToString) {
        this.enableToString = enableToString;
    }

    @Override
    public void setEnableExample(boolean enableExample) {
        this.enableExample = enableExample;
    }

    @Override
    public void setEnableCountByExample(boolean enableCountByExample) {
        this.enableCountByExample = enableCountByExample;
    }

    @Override
    public void setEnableUpdateByExample(boolean enableUpdateByExample) {
        this.enableUpdateByExample = enableUpdateByExample;
    }

    @Override
    public void setEnableDeleteByExample(boolean enableDeleteByExample) {
        this.enableDeleteByExample = enableDeleteByExample;
    }

    @Override
    public void setEnableSelectByExample(boolean enableSelectByExample) {
        this.enableSelectByExample = enableSelectByExample;
    }

    @Override
    public void setEnableSelectByExampleQueryId(boolean enableSelectByExampleQueryId) {
        this.enableSelectByExampleQueryId = enableSelectByExampleQueryId;
    }

    @Override
    public void setDefaultSchema(String schema) {
        this.schema = schema;
    }

    @Override
    public void setJdbcConfig(JdbcConfig jdbcConfig) {
        this.jdbcConfig = jdbcConfig;
    }

    @Override
    public void setForceBigDecimals(boolean forceBigDecimalsAble) {
        this.forceBigDecimalsAble = forceBigDecimalsAble;
    }

    @Override
    public void setModelTargetPackage(String targetPackage) {
        this.modelTargetPackage = targetPackage;
    }

    @Override
    public void setModelTargetProject(String targetProject) {
        this.modelTargetProject = targetProject;
    }

    @Override
    public void setModelTrimStrings(boolean trimStringsable) {
        this.trimStringsable = trimStringsable;
    }

    @Override
    public void setSqlMapTargetPackage(String targetPackage) {
        this.sqlMapTargetPackage = targetPackage;
    }

    @Override
    public void setSqlMapTargetProject(String targetProject) {
        this.sqlMapTargetProject = targetProject;
    }

    @Override
    public void setClientTargetPackage(String targetPackage) {
        this.clientTargetPackage = targetPackage;
    }

    @Override
    public void setClientTargetProject(String targetProject) {
        this.clientTargetProject = targetProject;
    }

    @Override
    public void addTableName(String tableName) {
        this.tablesName.add(tableName);
    }

    @Override
    public void addTablesName(List<String> tablesName) {
        this.tablesName.addAll(tablesName);
    }

    @Override
    public void addTable(Table table) {
        this.tables.add(table);
    }

    @Override
    public void addTables(List<Table> tables) {
        this.tables.addAll(tables);
    }

    @Override
    public void create() {
        //1.检查必填数据是否正确
        logger.info("开始检查配置数据...");
        checkData();
        logger.info("开始组装数据...");
        //2.组装数据
        assemblyData();;
        logger.info("开始生成配置信息");
        //3.生成配置文件
        String configXml = generateConfiguration();
        logger.debug("生成的配置信息：\n{}", configXml);
        logger.info("开始生成代码...");
        //4.生成代码
        generateCode(configXml);
        logger.info("生成代码结束.");

    }

    /**
     * 检查数据
     */
    private void checkData() {
        if (this.jdbcConfig == null) {
            throw new RuntimeException("没有配置数据库信息");
        }
        if (StringUtils.isBlank(jdbcConfig.getConnectionUrl())
        || StringUtils.isBlank(jdbcConfig.getDriverClass())
        || StringUtils.isBlank(jdbcConfig.getPassword())
        || StringUtils.isBlank(jdbcConfig.getUserId())) {
            throw new RuntimeException("数据库配置不能为空");
        }
        if (StringUtils.isBlank(this.modelTargetPackage) || StringUtils.isBlank(this.modelTargetProject)) {
            throw new RuntimeException("实体类配置信息不能为空");
        }
        if (StringUtils.isBlank(this.clientTargetPackage) || StringUtils.isBlank(this.clientTargetProject)) {
            throw new RuntimeException("mapper接口配置信息不能为空");
        }
        if (StringUtils.isBlank(this.sqlMapTargetPackage) || StringUtils.isBlank(this.sqlMapTargetProject)) {
            throw new RuntimeException("mapper Xml配置信息不能为空");
        }
        if (tables.size() < 1 && tablesName.size() < 1) {
            throw new RuntimeException("没有配置需要生成的表名称");
        }
    }

    /**
     * 组装数据
     */
    private void assemblyData() {
        //去掉tableName
        List<String> newTablesName = new ArrayList<>();
        for (String name : tablesName) {
            if (newTablesName.contains(name)) {
                continue;
            } else {
                newTablesName.add(name);
            }
        }
        //根据默认的配置信息，组装table
        for (String name : newTablesName) {
            Table table = new Table();
            table.setTableName(name);
            table.setSchema(this.schema);
            table.setEnableCountByExample(this.enableCountByExample);
            table.setEnableDeleteByExample(this.enableDeleteByExample);
            table.setEnableSelectByExample(this.enableSelectByExample);
            table.setEnableUpdateByExample(this.enableUpdateByExample);
            table.setSelectByExampleQueryId(this.enableSelectByExampleQueryId);
            tableMap.put(name, table);
        }

        //获取tables集合，如果已经存在，则更新，反之添加进待生成的集合中
        //优先级是table > tableName
        for (Table table : tables) {
            Table tmp = tableMap.get(table.getTableName());
            if (tmp != null) {
                if (!StringUtils.isBlank(table.getSchema())) {
                    tmp.setSchema(table.getSchema());
                }
                if (!table.isSelectByExampleQueryId()) {
                    tmp.setSelectByExampleQueryId(table.isSelectByExampleQueryId());
                }
                if (!table.isEnableUpdateByExample()) {
                    tmp.setEnableUpdateByExample(table.isEnableUpdateByExample());
                }
                if (!table.isEnableSelectByExample()) {
                    tmp.setEnableSelectByExample(table.isEnableSelectByExample());
                }
                if (!table.isEnableDeleteByExample()) {
                    tmp.setEnableDeleteByExample(table.isEnableDeleteByExample());
                }
                if (!table.isEnableCountByExample()) {
                    tmp.setEnableCountByExample(table.isEnableCountByExample());
                }
                if (StringUtils.isNotBlank(schema)) {
                    tmp.setSchema(table.getSchema());
                }
                tmp.setSqlStatment(table.getSqlStatment());
                tmp.setPrimaryKey(table.getPrimaryKey());
                tmp.setIdentity(table.isIdentity());
            } else {
                if (StringUtils.isBlank(table.getSchema()) && StringUtils.isNotBlank(this.schema)) {
                    table.setSchema(schema);
                }
                if (table.isEnableCountByExample()) {
                    table.setEnableCountByExample(this.enableCountByExample);
                }
                if (table.isEnableDeleteByExample()) {
                    table.setEnableDeleteByExample(this.enableDeleteByExample);
                }
                if (table.isEnableSelectByExample()) {
                    table.setEnableSelectByExample(this.enableSelectByExample);
                }
                if (table.isEnableUpdateByExample()) {
                    table.setEnableUpdateByExample(this.enableUpdateByExample);
                }
                if (table.isSelectByExampleQueryId()) {
                    table.setSelectByExampleQueryId(this.enableSelectByExampleQueryId);
                }
                tableMap.put(table.getTableName(), table);

            }
        }

    }

    /**
     * 生成配置文件
     * @return
     */
    private String generateConfiguration() {

        Document document = DocumentHelper.createDocument();
        document.addDocType("generatorConfiguration", "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN\" \"http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd", null);

        Element root = DocumentHelper.createElement("generatorConfiguration");
        document.add(root);

        Element context = root.addElement("context");
        context.addAttribute("id", "MysqlContext");
        context.addAttribute("targetRuntime", "MyBatis3");

        if (this.enableSerializable) {
            Element plugin1 = context.addElement("plugin");
            plugin1.addAttribute("type", "org.mybatis.generator.plugins.SerializablePlugin");
        }

        if (this.enableToString) {
            Element plugin2 = context.addElement("plugin");
            plugin2.addAttribute("type", "org.mybatis.generator.plugins.ToStringPlugin");
        }

        Element commentGenerator = context.addElement("commentGenerator");
        commentGenerator.addAttribute("type", "com.nantian.generator.ChineseCommentGenerator");

        createPropertyNode(commentGenerator, "property", "suppressAllComments", "false");
        createPropertyNode(commentGenerator, "property", "suppressDate", "false");
        Element jdbcConnection = createJdbcConnection(context);

        createPropertyNode(jdbcConnection, "property", "nullCatalogMeansCurrent", "true");

        if (this.jdbcConfig.getDriverClass().contains(ORACLE_DB)) {
            createPropertyNode(jdbcConnection, "property", "remarksReporting", "true");
        }

        Element javaTypeResolver = context.addElement("javaTypeResolver");
        createPropertyNode(javaTypeResolver, "property", "forceBigDecimals", String.valueOf(this.forceBigDecimalsAble).toLowerCase());


        Element javaModelGenerator = context.addElement("javaModelGenerator");
        javaModelGenerator.addAttribute("targetPackage", this.modelTargetPackage);
        javaModelGenerator.addAttribute("targetProject", this.modelTargetProject);

        createPropertyNode(javaModelGenerator, "property", "enableSubPackages", "false");
        createPropertyNode(javaModelGenerator, "property", "trimStrings", String.valueOf(this.trimStringsable).toLowerCase());

        Element sqlMapGenerator = context.addElement("sqlMapGenerator");
        sqlMapGenerator.addAttribute("targetPackage", this.sqlMapTargetPackage);
        sqlMapGenerator.addAttribute("targetProject", this.sqlMapTargetProject);

        createPropertyNode(sqlMapGenerator, "property", "enableSubPackages", "false");


        Element javaClientGenerator = context.addElement("javaClientGenerator");
        javaClientGenerator.addAttribute("type", "XMLMAPPER");
        javaClientGenerator.addAttribute("targetPackage", this.clientTargetPackage);
        javaClientGenerator.addAttribute("targetProject", this.clientTargetProject);

        createPropertyNode(javaClientGenerator, "property", "enableSubPackages", "false");
        createTableNode(context);

        String xml = XmlUtils.formatXml(document);
        return xml;
    }

    /**
     * 通用创建property节点
     * @param element 上一节点
     * @param nodeName 属性名称
     * @param name name
     * @param value vaule
     * @return 当前节点
     */
    private Element createPropertyNode(Element element, String nodeName, String name, String value) {
        Element property = element.addElement("property");
        property.addAttribute("name", name);
        property.addAttribute("value", value);
        return property;
    }

    /**
     * 创建jdbc节点
     * @param element 上一节点
     * @return jdbc节点
     */
    private Element createJdbcConnection(Element element) {
        Element jdbcConnection = element.addElement("jdbcConnection");
        jdbcConnection.addAttribute("driverClass", this.jdbcConfig.getDriverClass());
        jdbcConnection.addAttribute("connectionURL", this.jdbcConfig.getConnectionUrl());
        jdbcConnection.addAttribute("userId", this.jdbcConfig.getUserId());
        jdbcConnection.addAttribute("password", this.jdbcConfig.getPassword());
        return jdbcConnection;
    }

    /**
     * 创建table节点
     * @param context 上一节点
     */
    private void createTableNode(Element context) {
        for (Map.Entry<String, Table> entry : tableMap.entrySet()) {
            Table table = entry.getValue();
            Element tableEle = context.addElement("table");
            tableEle.addAttribute("schema", table.getSchema());
            tableEle.addAttribute("tableName", table.getTableName());
            createExampleAttribute(tableEle, table);
            if (!StringUtils.isBlank(table.getPrimaryKey()) && !StringUtils.isBlank(table.getSqlStatment())) {
                Element key = tableEle.addElement("generatedKey");
                key.addAttribute("column", table.getPrimaryKey());
                key.addAttribute("sqlStatement", table.getSqlStatment());
                key.addAttribute("identity", String.valueOf(table.isIdentity()).toLowerCase());
            }
            if (CollectionUtils.isNotEmpty(table.getColumnOverrideList())) {
                for (ColumnOverride columnOverride : table.getColumnOverrideList()) {
                    if (StringUtils.isBlank(columnOverride.getColumn())) {
                        continue;
                    }
                    Element column = tableEle.addElement("columnOverride");
                    createOverrideAttribute(column, columnOverride);
                }
            }
        }
    }

    /**
     * 添加是否需要Example属性
     * @param tableEle table节点
     * @param table table信息
     */
    private void createExampleAttribute(Element tableEle, Table table) {
        boolean isEnableCountByExample = this.enableExample && table.isEnableCountByExample();
        tableEle.addAttribute("enableCountByExample", String.valueOf(isEnableCountByExample).toLowerCase());

        boolean isEnableDeleteByExample = this.enableExample && table.isEnableDeleteByExample();
        tableEle.addAttribute("enableDeleteByExample", String.valueOf(isEnableDeleteByExample).toLowerCase());

        boolean isEnableUpdateByExample = this.enableExample && table.isEnableUpdateByExample();
        tableEle.addAttribute("enableUpdateByExample", String.valueOf(isEnableUpdateByExample).toLowerCase());

        boolean isEnableSelectByExample = this.enableExample && table.isEnableSelectByExample();
        tableEle.addAttribute("enableSelectByExample", String.valueOf(isEnableSelectByExample).toLowerCase());

        boolean isSelectByExampleQueryId = this.enableExample && table.isSelectByExampleQueryId();
        tableEle.addAttribute("selectByExampleQueryId", String.valueOf(isSelectByExampleQueryId).toLowerCase());
    }

    /**
     * 添加是否需要Override属性
     * @param column column节点
     * @param columnOverride Override信息
     */
    private void createOverrideAttribute(Element column, ColumnOverride columnOverride) {
        column.addAttribute("column", columnOverride.getColumn());
        if (StringUtils.isNotBlank(columnOverride.getProperty())) {
            column.addAttribute("property", columnOverride.getProperty());
        }
        if (StringUtils.isNotBlank(columnOverride.getJavaType())) {
            column.addAttribute("javaType", columnOverride.getJavaType());
        }
        if (StringUtils.isNotBlank(columnOverride.getJdbcType())) {
            column.addAttribute("jdbcType", columnOverride.getJdbcType());
        }
        if (StringUtils.isNotBlank(columnOverride.getTypeHandler())) {
            column.addAttribute("typeHandler", columnOverride.getTypeHandler());
        }
        if (StringUtils.isNotBlank(columnOverride.getDelimitedColumnName())) {
            column.addAttribute("delimitedColumnName", columnOverride.getDelimitedColumnName());
        }
        if (columnOverride.isGeneratedAlways()) {
            column.addAttribute("isGeneratedAlways", "true");
        }
    }

    /**
     * 生成java代码文件
     */
    private void generateCode(String configXml) {
        List<String> warnings = new ArrayList<String>();
        boolean overwrite = true;
        ConfigurationParser cp = new ConfigurationParser(warnings);

        ByteArrayInputStream tInputStringStream = new ByteArrayInputStream(StringUtils.stringToBytes(configXml));
        try {
            Configuration config = cp.parseConfiguration(tInputStringStream);
            DefaultShellCallback callback = new DefaultShellCallback(overwrite);
            MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config,
                    callback, warnings);
            myBatisGenerator.generate(null);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException("生成代码文件失败");
        }
    }

}
