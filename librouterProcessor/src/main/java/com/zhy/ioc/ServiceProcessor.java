package com.zhy.ioc;

import com.google.auto.service.AutoService;

import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;


@AutoService(Processor.class)
public class  ServiceProcessor extends AbstractProcessor {
    private Messager messager;
    private Elements elementUtils;

    private Map<String, ServiceProxyInfo> mProxyMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        elementUtils = processingEnv.getElementUtils();
    }


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> supportTypes = new LinkedHashSet<>();
        supportTypes.add(Service.class.getCanonicalName());
        return supportTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        messager.printMessage(Diagnostic.Kind.NOTE, "process...");
        mProxyMap.clear();

        if (annotations.isEmpty()) {
            return false;
        }

        Set<? extends Element> elesWithBind = roundEnv.getElementsAnnotatedWith(Service.class);

        for (Element element : elesWithBind) {

            if (!(element instanceof TypeElement)) {
                return false;
            }

            //class type
            TypeElement classElement = (TypeElement) element;
            messager.printMessage(Diagnostic.Kind.NOTE, classElement.toString());
            //full class name
            String classfullName = classElement.getQualifiedName().toString();
            messager.printMessage(Diagnostic.Kind.NOTE, classfullName);

            ServiceProxyInfo proxyInfo = mProxyMap.get(classfullName);
            if (proxyInfo == null) {
                proxyInfo = new ServiceProxyInfo(elementUtils, classElement);
                mProxyMap.put(classfullName, proxyInfo);
            }

            Service bindAnnotation = element.getAnnotation(Service.class);
            String key = bindAnnotation.name();
            messager.printMessage(Diagnostic.Kind.NOTE, key);
            proxyInfo.injectVariables.put(key, (TypeElement) element);

        }

        for (String key : mProxyMap.keySet()) {
            ServiceProxyInfo proxyInfo = mProxyMap.get(key);
            try {

                messager.printMessage(Diagnostic.Kind.NOTE, proxyInfo.getProxyClassFullName());
                messager.printMessage(Diagnostic.Kind.NOTE, proxyInfo.getTypeElement().getQualifiedName());

                JavaFileObject jfo = processingEnv.getFiler().createSourceFile(
                        proxyInfo.getProxyClassFullName(),
                        proxyInfo.getTypeElement());
                Writer writer = jfo.openWriter();

                messager.printMessage(Diagnostic.Kind.NOTE, proxyInfo.generateJavaCode());

                writer.write(proxyInfo.generateJavaCode());

                messager.printMessage(Diagnostic.Kind.NOTE, jfo.toUri().toString());

                writer.flush();
                writer.close();


                messager.printMessage(Diagnostic.Kind.NOTE, "file created");

            } catch (Exception e) {
                error(proxyInfo.getTypeElement(),
                        "Unable to write injector for type %s: %s",
                        proxyInfo.getTypeElement(), e.getMessage());
            }

        }
        return true;
    }


    private void error(Element element, String message, Object... args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, message, element);
    }


}
