package com.zhy.ioc;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

public class ServiceProxyInfo {
    private String packageName;
    private String proxyClassName;
    private TypeElement typeElement;

    public Map<String, TypeElement> injectVariables = new HashMap<>();

    public static final String PROXY = "Service";

    public ServiceProxyInfo(Elements elementUtils, TypeElement classElement) {
        this.typeElement = classElement;
        PackageElement packageElement = elementUtils.getPackageOf(classElement);
        String packageName = packageElement.getQualifiedName().toString();
        //classname
        String className = ClassValidator.getClassName(classElement, packageName);
        this.packageName = packageName;
        this.proxyClassName = PROXY + "$$" + className;
    }


    public String generateJavaCode() {
        StringBuilder builder = new StringBuilder();
        builder.append("// Generated code. Do not modify!\n");
        builder.append("package ").append(packageName).append(";\n\n");
        builder.append("import com.zhy.ioc.*;\n");
        builder.append("import java.util.Map;\n");
        builder.append('\n');

        builder.append("public class ").append(proxyClassName).append(" implements ILoaded ");
        builder.append(" {\n");

         generateMethods(builder);

        builder.append('\n');

        builder.append("}\n");
        return builder.toString();

    }


    private void generateMethods(StringBuilder builder) {

        builder.append("@Override\n ");
        builder.append("public void load(Map<String, Class<?>> classMap) {\n");
        for (String key : injectVariables.keySet()) {
            TypeElement element = injectVariables.get(key);
            builder.append("classMap.put(\"" + key + "\", " + element.getQualifiedName().toString() + ".class); \n");
        }

        builder.append("\n};");

    }

    public String getProxyClassFullName() {
        return packageName + "." + proxyClassName;
    }

    public TypeElement getTypeElement() {
        return typeElement;
    }


}