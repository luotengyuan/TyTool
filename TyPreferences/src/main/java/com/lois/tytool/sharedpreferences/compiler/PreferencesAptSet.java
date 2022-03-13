package com.lois.tytool.sharedpreferences.compiler;

import com.lois.tytool.sharedpreferences.compiler.method.BaseMethod;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;

import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * @Description SharedPreferences 代码自动生成方法集合
 * @Author Luo.T.Y
 * @Date 2022/2/21
 * @Time 20:51
 */
public class PreferencesAptSet {
    public static final String INSTANCE_NAME = "mPreferences";

    private static final ClassName CONTEXT = ClassName.get("android.content", "Context");
    private static final ClassName SHAREDPREFERENCES = ClassName.get("android.content", "SharedPreferences");
    private static final ClassName RUNTIME_EXCEPTION = ClassName.get("java.lang", "RuntimeException");

    private String javafileName;
    private String sharedPreferencesFileName;
    private String packageName;
    private List<com.lois.tytool.sharedpreferences.compiler.method.BaseMethod> methodList;

    public PreferencesAptSet() {
        this.methodList = new ArrayList<>();
    }

    JavaFile brewJava() {
        return JavaFile.builder(packageName, createType()).addFileComment("Generated code from AptSharedPreferences. Do not modify!").build();
    }

    private TypeSpec createType() {
        TypeSpec.Builder result = TypeSpec.classBuilder(javafileName).addModifiers(PUBLIC);

        FieldSpec prefsField = FieldSpec.builder(SHAREDPREFERENCES, INSTANCE_NAME, Modifier.PRIVATE).build();
        result.addField(prefsField);

        FieldSpec instanceField = FieldSpec
                .builder(ClassName.get(packageName, javafileName), "sInstance", Modifier.PRIVATE, Modifier.STATIC, Modifier.VOLATILE).build();
        result.addField(instanceField);

        result.addMethod(createConstructor());
        result.addMethod(createGetMethod());
        result.addMethod(createInitMethod());

        for (com.lois.tytool.sharedpreferences.compiler.method.BaseMethod method : methodList) {
            result.addMethod(method.render());
        }

        return result.build();
    }

    private MethodSpec createConstructor() {
        MethodSpec.Builder builder = MethodSpec.constructorBuilder().addModifiers(PRIVATE)
                .addParameter(CONTEXT, "context")
                .addStatement("$L = context.getSharedPreferences($S,Context.MODE_PRIVATE)", INSTANCE_NAME,sharedPreferencesFileName);

        return builder.build();
    }

    private MethodSpec createInitMethod() {
        MethodSpec newInstanceMethd = MethodSpec.methodBuilder("init")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(TypeName.VOID)
                .addParameter(CONTEXT, "context")
                .addCode("if (sInstance == null) {\n")
                .addCode("     sInstance = new $T(context);\n", ClassName.get(packageName, javafileName))
                .addCode("}\n")
                .build();
        return newInstanceMethd;
    }

    private MethodSpec createGetMethod() {
        MethodSpec getMethd = MethodSpec.methodBuilder("getInstance")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ClassName.get(packageName, javafileName))
                .addCode("if (sInstance != null) { \n" + "      return sInstance; \n" + "} " + "else { \n")
                .addStatement("     throw new $T($S)", RUNTIME_EXCEPTION, "the instance has not initialized")
                .addCode("} \n")
                .build();
        return getMethd;
    }

    public void addMethod(BaseMethod method) {
        methodList.add(method);
    }

    public void setJavafileName(String javafileName) {
        this.javafileName = javafileName;
    }

    public void setSharedPreferencesFileName(String sharedPreferencesFileName) {
        this.sharedPreferencesFileName = sharedPreferencesFileName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
