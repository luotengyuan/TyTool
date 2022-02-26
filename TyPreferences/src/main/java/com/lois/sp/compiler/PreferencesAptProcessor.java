package com.lois.sp.compiler;

import com.google.auto.service.AutoService;
import com.lois.sp.annotation.PreferencesClass;
import com.lois.sp.annotation.BooleanEntity;
import com.lois.sp.annotation.FloatEntity;
import com.lois.sp.annotation.IntEntity;
import com.lois.sp.annotation.LongEntity;
import com.lois.sp.annotation.StringEntity;
import com.lois.sp.compiler.method.GetterMethod;
import com.lois.sp.compiler.method.SetterMethod;
import com.squareup.javapoet.JavaFile;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * @Description SharedPreferences 代码自动生成处理类
 * @Author Luo.T.Y
 * @Date 2022/2/21
 * @Time 20:51
 */
@AutoService(Processor.class)
public class PreferencesAptProcessor extends AbstractProcessor {
    private Filer filer;
    private Elements elementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        filer = processingEnv.getFiler();
        elementUtils = processingEnv.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<TypeElement, PreferencesAptSet> bindingMap = findAndParseTargets(roundEnv);

        for (Map.Entry<TypeElement, PreferencesAptSet> entry : bindingMap.entrySet()) {
            TypeElement typeElement = entry.getKey();
            PreferencesAptSet binding = entry.getValue();

            JavaFile javaFile = binding.brewJava();
            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                error(typeElement, "Unable to write file for type %s: %s", typeElement, e.getMessage());
            }
        }
        return false;
    }

    private Map<TypeElement, PreferencesAptSet> findAndParseTargets(RoundEnvironment env) {
        Map<TypeElement, PreferencesAptSet> builderMap = new LinkedHashMap<>();

        for (Element element : env.getElementsAnnotatedWith(PreferencesClass.class)) {
            try {
                paserAnnotationPrefs(element, builderMap);
            } catch (Exception e) {
                logParsingError(element, PreferencesClass.class, e);
            }
        }

        for (Element element : env.getElementsAnnotatedWith(StringEntity.class)) {
            try {
                parseStringEntity(element, builderMap);
            } catch (Exception e) {
                logParsingError(element, StringEntity.class, e);
            }
        }

        for (Element element : env.getElementsAnnotatedWith(BooleanEntity.class)) {
            try {
                parseBooleanEntity(element, builderMap);
            } catch (Exception e) {
                logParsingError(element, BooleanEntity.class, e);
            }
        }

        for (Element element : env.getElementsAnnotatedWith(FloatEntity.class)) {
            try {
                parseFloatEntity(element, builderMap);
            } catch (Exception e) {
                logParsingError(element, FloatEntity.class, e);
            }
        }

        for (Element element : env.getElementsAnnotatedWith(IntEntity.class)) {
            try {
                parseIntEntity(element, builderMap);
            } catch (Exception e) {
                logParsingError(element, IntEntity.class, e);
            }
        }

        for (Element element : env.getElementsAnnotatedWith(LongEntity.class)) {
            try {
                parseLongEntity(element, builderMap);
            } catch (Exception e) {
                logParsingError(element, LongEntity.class, e);
            }
        }
        return builderMap;
    }

    private void paserAnnotationPrefs(Element element, Map<TypeElement, PreferencesAptSet> builderMap) {
        if (element.getKind() != ElementKind.CLASS) {
            return;
        }
        TypeElement typeElement = (TypeElement) element;
        String spFileName = typeElement.getAnnotation(PreferencesClass.class).prefsFileName();
        String packageName = elementUtils.getPackageOf(element).getQualifiedName().toString();
        PreferencesAptSet prefsSet = getOrCreateSet(builderMap, typeElement);
        prefsSet.setJavafileName(typeElement.getSimpleName() + "_impl");
        prefsSet.setSharedPreferencesFileName(spFileName);
        prefsSet.setPackageName(packageName);
    }

    private void parseStringEntity(Element element, Map<TypeElement, PreferencesAptSet> builderMap) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        String key = element.getAnnotation(StringEntity.class).key();
        String defaultValue = element.getAnnotation(StringEntity.class).defaultValue();

        GetterMethod getterMethod = new GetterMethod(StringEntity.class.getCanonicalName(), key, PreferencesAptSet.INSTANCE_NAME, defaultValue);
        SetterMethod setterMethod = new SetterMethod(StringEntity.class.getCanonicalName(), key, PreferencesAptSet.INSTANCE_NAME);

        PreferencesAptSet prefsSet = getOrCreateSet(builderMap, enclosingElement);
        prefsSet.addMethod(getterMethod);
        prefsSet.addMethod(setterMethod);
    }

    private void parseBooleanEntity(Element element, Map<TypeElement, PreferencesAptSet> builderMap) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        String key = element.getAnnotation(BooleanEntity.class).key();
        boolean defaultValue = element.getAnnotation(BooleanEntity.class).defaultValue();

        GetterMethod getterMethod = new GetterMethod(BooleanEntity.class.getCanonicalName(), key, PreferencesAptSet.INSTANCE_NAME, String.valueOf(defaultValue));
        SetterMethod setterMethod = new SetterMethod(BooleanEntity.class.getCanonicalName(), key, PreferencesAptSet.INSTANCE_NAME);

        PreferencesAptSet prefsSet = getOrCreateSet(builderMap, enclosingElement);
        prefsSet.addMethod(getterMethod);
        prefsSet.addMethod(setterMethod);
    }

    private void parseFloatEntity(Element element, Map<TypeElement, PreferencesAptSet> builderMap) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        String key = element.getAnnotation(FloatEntity.class).key();
        float defaultValue = element.getAnnotation(FloatEntity.class).defaultValue();

        GetterMethod getterMethod = new GetterMethod(FloatEntity.class.getCanonicalName(), key, PreferencesAptSet.INSTANCE_NAME, String.valueOf(defaultValue));
        SetterMethod setterMethod = new SetterMethod(FloatEntity.class.getCanonicalName(), key, PreferencesAptSet.INSTANCE_NAME);

        PreferencesAptSet prefsSet = getOrCreateSet(builderMap, enclosingElement);
        prefsSet.addMethod(getterMethod);
        prefsSet.addMethod(setterMethod);
    }

    private void parseIntEntity(Element element, Map<TypeElement, PreferencesAptSet> builderMap) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        String key = element.getAnnotation(IntEntity.class).key();
        int defaultValue = element.getAnnotation(IntEntity.class).defaultValue();

        GetterMethod getterMethod = new GetterMethod(IntEntity.class.getCanonicalName(), key, PreferencesAptSet.INSTANCE_NAME, String.valueOf(defaultValue));
        SetterMethod setterMethod = new SetterMethod(IntEntity.class.getCanonicalName(), key, PreferencesAptSet.INSTANCE_NAME);

        PreferencesAptSet prefsSet = getOrCreateSet(builderMap, enclosingElement);
        prefsSet.addMethod(getterMethod);
        prefsSet.addMethod(setterMethod);
    }

    private void parseLongEntity(Element element, Map<TypeElement, PreferencesAptSet> builderMap) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        String key = element.getAnnotation(LongEntity.class).key();
        long defaultValue = element.getAnnotation(LongEntity.class).defaultValue();

        GetterMethod getterMethod = new GetterMethod(LongEntity.class.getCanonicalName(), key, PreferencesAptSet.INSTANCE_NAME, String.valueOf(defaultValue));
        SetterMethod setterMethod = new SetterMethod(LongEntity.class.getCanonicalName(), key, PreferencesAptSet.INSTANCE_NAME);

        PreferencesAptSet prefsSet = getOrCreateSet(builderMap, enclosingElement);
        prefsSet.addMethod(getterMethod);
        prefsSet.addMethod(setterMethod);
    }

    private PreferencesAptSet getOrCreateSet(Map<TypeElement, PreferencesAptSet> builderMap, TypeElement enclosingElement) {
        PreferencesAptSet prefsSet = builderMap.get(enclosingElement);
        if (prefsSet == null) {
            prefsSet = new PreferencesAptSet();
            builderMap.put(enclosingElement, prefsSet);
        }
        return prefsSet;
    }

    private void logParsingError(Element element, Class<? extends Annotation> annotation, Exception e) {
        StringWriter stackTrace = new StringWriter();
        e.printStackTrace(new PrintWriter(stackTrace));
        error(element, "Unable to parse @%s binding.\n\n%s", annotation.getSimpleName(), stackTrace);
    }

    private void error(Element element, String message, Object... args) {
        printMessage(Diagnostic.Kind.ERROR, element, message, args);
    }

    private void printMessage(Diagnostic.Kind kind, Element element, String message, Object[] args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }

        processingEnv.getMessager().printMessage(kind, message, element);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotataions = new LinkedHashSet<>();
        annotataions.add(PreferencesClass.class.getCanonicalName());
        annotataions.add(BooleanEntity.class.getCanonicalName());
        annotataions.add(FloatEntity.class.getCanonicalName());
        annotataions.add(IntEntity.class.getCanonicalName());
        annotataions.add(LongEntity.class.getCanonicalName());
        annotataions.add(StringEntity.class.getCanonicalName());
        return annotataions;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
