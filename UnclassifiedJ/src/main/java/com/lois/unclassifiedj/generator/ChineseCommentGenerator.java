package com.lois.unclassifiedj.generator;

import com.lois.tytool.basej.constant.ClassConstants;
import com.lois.tytool.basej.constant.MethodConstants;
import com.lois.tytool.basej.string.StringUtils;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.InnerClass;
import org.mybatis.generator.api.dom.java.InnerEnum;
import org.mybatis.generator.api.dom.java.JavaElement;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.config.MergeConstants;
import org.mybatis.generator.config.PropertyRegistry;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * 自定义mybatis反向生成代码注解
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public class ChineseCommentGenerator implements CommentGenerator {

    private Properties properties;
    private boolean suppressDate;
    private boolean suppressAllComments;
    private String nowTime;

    public ChineseCommentGenerator() {
        super();
        properties = new Properties();
        suppressDate = false;
        suppressAllComments = false;
        nowTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date());
    }
    @Override
    public void addJavaFileComment(CompilationUnit compilationUnit) {
        if (suppressAllComments) {
            return;
        }
        return;
    }

    @Override
    public void addComment(XmlElement xmlElement) {
        return;
    }
    @Override
    public void addRootComment(XmlElement rootElement) {
        // add no document level comments by default
        return;
    }
    @Override
    public void addConfigurationProperties(Properties properties) {
        this.properties.putAll(properties);
        suppressDate = isTrue(properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_DATE));
        suppressAllComments = isTrue(properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_ALL_COMMENTS));
    }


    private boolean isTrue(String property) {
//        if("true".equals(property)){
//            return true;
//        }
        return (Boolean.parseBoolean(property));
//        return false;
    }

    /**
     * This method adds the custom javadoc tag for. You may do nothing if you do
     * not wish to include the Javadoc tag - however, if you do not include the
     * Javadoc tag then the Java merge capability of the eclipse plugin will
     * break.
     *
     * @param javaElement the java element
     * @param markAsDoNotDelete 是否添加注释
     */
    protected void addJavadocTag(JavaElement javaElement, boolean markAsDoNotDelete) {
        javaElement.addJavaDocLine(" *");
        StringBuilder sb = new StringBuilder();
        sb.append(" * ");
        sb.append(MergeConstants.NEW_ELEMENT_TAG);
        if (markAsDoNotDelete) {
            sb.append("do_not_delete_during_merge");
        }
        String s = getDateString();
        if (s != null) {
            sb.append(' ');
            sb.append(s);
        }
        javaElement.addJavaDocLine(sb.toString());
    }

    /**
     * This method returns a formated date string to include in the Javadoc tag
     * and XML comments. You may return null if you do not want the date in
     * these documentation elements.
     *
     * @return a string representing the current timestamp, or null
     */
    protected String getDateString() {
        String result = null;
        if (!suppressDate) {
            result = nowTime;
        }
        return result;
    }
    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable) {
        if (suppressAllComments) {
            return;
        }
//        StringBuilder sb = new StringBuilder();
//        innerClass.addJavaDocLine("/**");
//        sb.append(" * ");
//        sb.append(introspectedTable.getFullyQualifiedTable());
//        sb.append(" ");
//        sb.append(getDateString());
//        innerClass.addJavaDocLine(sb.toString().replace("\n", " "));
//        innerClass.addJavaDocLine(" */");
    }
    @Override
    public void addEnumComment(InnerEnum innerEnum, IntrospectedTable introspectedTable) {
        if (suppressAllComments) {
            return;
        }
//        StringBuilder sb = new StringBuilder();
//        innerEnum.addJavaDocLine("/**");
//        sb.append(" * ");
//        sb.append(introspectedTable.getFullyQualifiedTable());
//        innerEnum.addJavaDocLine(sb.toString().replace("\n", " "));
//        innerEnum.addJavaDocLine(" */");
    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        if (suppressAllComments) {
            return;
        }
        if (StringUtils.isBlank(introspectedColumn.getRemarks())) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        field.addJavaDocLine("/**");
        sb.append(" * ");
        sb.append(introspectedColumn.getRemarks());

        field.addJavaDocLine(sb.toString().replace("\n", " "));
        field.addJavaDocLine(" */");
    }
    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable) {
        if (suppressAllComments) {
            return;
        }
        if (ClassConstants.ORDER_BY_CLAUSE.equals(field.getName())) {
            field.addJavaDocLine("/**");
            field.addJavaDocLine(" * 排序条件");
            field.addJavaDocLine(" */");
        } else if (ClassConstants.DISTINCT.equals(field.getName())) {
            field.addJavaDocLine("/**");
            field.addJavaDocLine(" * 是否distinct");
            field.addJavaDocLine(" */");
        } else if (ClassConstants.ORED_CRITERIA.equals(field.getName())) {
            field.addJavaDocLine("/**");
            field.addJavaDocLine(" * 条件集合");
            field.addJavaDocLine(" */");
        } else if (ClassConstants.SERIAL_VERSION_ID.equals(field.getName())) {
            field.addJavaDocLine("/**");
            field.addJavaDocLine(" * 序列化版本号");
            field.addJavaDocLine(" */");
        }
    }

    @Override
    public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        if (suppressAllComments) {
            return;
        }
//        topLevelClass.addJavaDocLine("/**");
//        addJavadocTag(topLevelClass, false);
//        topLevelClass.addJavaDocLine(" */");
    }

    @Override
    public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {
        if (suppressAllComments) {
            return;
        }
        getMethodComment(method);

    }


    @Override
    public void addGetterComment(Method method, IntrospectedTable introspectedTable,
                                 IntrospectedColumn introspectedColumn) {
        if (suppressAllComments) {
            return;
        }
        if (StringUtils.isBlank(introspectedColumn.getRemarks())) {
            return;
        }
        method.addJavaDocLine("/**");
        StringBuilder sb = new StringBuilder();
        sb.append(" * 获取 ");
        sb.append(introspectedColumn.getRemarks());
        method.addJavaDocLine(sb.toString().replace("\n", " "));
        sb.setLength(0);

//        //是否加入时间戳
        if(suppressDate){
            sb.append(" * @date " + nowTime);
            method.addJavaDocLine(sb.toString().replace("\n", " "));
            sb.setLength(0);
        }

        sb.append(" * @return ");
        sb.append(introspectedColumn.getRemarks());
        method.addJavaDocLine(sb.toString().replace("\n", " "));
        method.addJavaDocLine(" */");
    }

    @Override
    public void addSetterComment(Method method, IntrospectedTable introspectedTable,
                                 IntrospectedColumn introspectedColumn) {
        if (suppressAllComments) {
            return;
        }
        if (StringUtils.isBlank(introspectedColumn.getRemarks())) {
            return;
        }
        method.addJavaDocLine("/**");
        StringBuilder sb = new StringBuilder();
        sb.append(" * 设置 ");
        sb.append(introspectedColumn.getRemarks());
        method.addJavaDocLine(sb.toString().replace("\n", " "));
        sb.setLength(0);

//        //是否加入时间戳
        if(suppressDate){
            sb.append(" * @date " + nowTime);
            method.addJavaDocLine(sb.toString().replace("\n", " "));
            sb.setLength(0);
        }

        Parameter parm = method.getParameters().get(0);
        sb.append(" * @param ");
        sb.append(parm.getName());
        sb.append(" ");
        sb.append(introspectedColumn.getRemarks());
        method.addJavaDocLine(sb.toString().replace("\n", " "));
        method.addJavaDocLine(" */");
    }
    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable, boolean markAsDoNotDelete) {
        if (suppressAllComments) {
            return;
        }
//        StringBuilder sb = new StringBuilder();
//        innerClass.addJavaDocLine("/**");
//        sb.append(" * ");
//        sb.append(introspectedTable.getFullyQualifiedTable());
//        innerClass.addJavaDocLine(sb.toString().replace("\n", " "));
//        sb.setLength(0);
//        sb.append(" * @author ");
//        sb.append(systemPro.getProperty("user.name"));
//        sb.append(" ");
//        sb.append(nowTime);
//        innerClass.addJavaDocLine(" */");
    }

    public void getMethodComment(Method method) {
        String methodName = method.getName();
        List<Parameter> parameters = method.getParameters();
        if (MethodConstants.COUNT_BY_EXAMPLE.equals(methodName)) {
            addJavaDoc(method, parameters, "根据条件统计数据", null, "条件", "统计的数量");
        } else if (MethodConstants.DELETE_BY_EXAMPLE.equals(methodName)) {
            addJavaDoc(method, parameters, "根据条件删除数据", null, "条件", "删除的条数");
        } else if (MethodConstants.DELETE_BY_PRIMARY_KEY.equals(methodName)) {
            addJavaDoc(method, parameters, "根据主键删除数据", "主键", null, "删除的条数");
        } else if (MethodConstants.INSERT.equals(methodName)) {
            addJavaDoc(method, parameters, "插入一条数据", "PO对象", null, "插入的条数");
        } else if (MethodConstants.INSERT_SELECTIVE.equals(methodName)) {
            addJavaDoc(method, parameters, "根据条件插入数据（字段值为null时，该字段不插入）", "PO对象", null, "插入的条数");
        } else if (MethodConstants.SELECT_BY_EXAMPLE.equals(methodName)) {
            addJavaDoc(method, parameters, "根据条件查询数据", null, "条件", "查询的集合");
        } else if (MethodConstants.SELECT_BY_PRIMARY_KEY.equals(methodName)) {
            addJavaDoc(method, parameters, "根据主键查询数据", "主键", null, "查询的集合");
        } else if (MethodConstants.UPDATE_BY_EXAMPLE.equals(methodName)) {
            addJavaDoc(method, parameters, "根据条件更新所有字段数据", "条件", "PO对象", "更新的条数");
        } else if (MethodConstants.UPDATE_BY_EXAMPLE_SELECTIVE.equals(methodName)) {
            addJavaDoc(method, parameters, "根据条件更新字段数据（字段值为null时，该字段不更新）", "条件", "PO对象", "更新的条数");
        } else if (MethodConstants.UPDATE_BY_PRIMARY_KEY.equals(methodName)) {
            addJavaDoc(method, parameters, "根据主键更新所有字段数据", "条件", "PO对象", "更新的条数");
        } else if (MethodConstants.UPDATE_BY_PRIMARY_KEY_SELECTIVE.equals(methodName)) {
            addJavaDoc(method, parameters, "根据主键更新字段数据（字段值为null时，该字段不更新）", "条件", "PO对象", "更新的条数");
        } else if (methodName.endsWith(MethodConstants.EXAMPLE)) {
            addJavaDoc(method, parameters, "构造器", null, null, null);
        } else if (MethodConstants.SET_ORDER_BY_CLAUSE.equals(methodName)) {
            addJavaDoc(method, parameters, "设置 排序条件", "排序条件", null, null);
        } else if (MethodConstants.GET_ORDER_BY_CLAUSE.equals(methodName)) {
            addJavaDoc(method, parameters, "获取 排序条件", null, null, "排序条件");
        } else if (MethodConstants.IS_DISTINCT.equals(methodName)) {
            addJavaDoc(method, parameters, "获取 是否distinct", null, null, "是否distinct");
        } else if (MethodConstants.SET_DISTINCT.equals(methodName)) {
            addJavaDoc(method, parameters, "设置 是否distinct", "是否distinct", null, null);
        } else if (MethodConstants.GET_ORED_CRITERIA.equals(methodName)) {
            addJavaDoc(method, parameters, "获取 所有条件规则", null, null, "所有条件规则");
        }
    }

    /**
     * 为方法添加doc注释
     * @param method 方法
     * @param parameters 参数
     * @param c1 方法作用描述
     * @param c2 参数描述
     * @param c3 参数描述
     * @param c4 返回值描述
     */
    private static void addJavaDoc(Method method, List<Parameter> parameters, String c1, String c2, String c3, String c4) {
        method.addJavaDocLine("/**");
        method.addJavaDocLine(" * " + c1);
        for (Parameter parameter : parameters) {
            if (parameter.getName().endsWith("example")) {
                if (StringUtils.isNotBlank(c3)) {
                    method.addJavaDocLine(" * @param " + parameter.getName() + " " + c3);
                }
            } else {
                if (StringUtils.isNotBlank(c2)) {
                    method.addJavaDocLine(" * @param " + parameter.getName() + " " + c2);
                }
            }
        }
        if (StringUtils.isNotBlank(c4)) {
            method.addJavaDocLine(" * @return " + c4);
        }
        method.addJavaDocLine(" */");
    }

}
