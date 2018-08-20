package com.armdroid.smartpreferences;

import com.squareup.javapoet.JavaFile;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

public class Processor extends AbstractProcessor {

    private Messager mMessager;
    private Map<PrefClass, List<PrefField>> mClassFieldMapping;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mMessager = processingEnvironment.getMessager();
        mClassFieldMapping = new HashMap<>();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (processAnnotationsForType(roundEnvironment, IntPreference.class)
            && processAnnotationsForType(roundEnvironment, StringPreference.class)
            && processAnnotationsForType(roundEnvironment, FloatPreference.class)
            && processAnnotationsForType(roundEnvironment, LongPreference.class)
            && processAnnotationsForType(roundEnvironment, BooleanPreference.class)) {

            for(Map.Entry<PrefClass, List<PrefField>> entry : mClassFieldMapping.entrySet()) {
                try {
                    JavaFile file = PreferenceClassCreator.create(entry.getKey(), entry.getValue());
                    file.writeTo(processingEnv.getFiler());
                } catch (Exception e) {
//                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    private boolean processAnnotationsForType(RoundEnvironment roundEnvironment, Class<? extends Annotation> klass) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(klass);
        for (Element element : elements) {
            Element enclosingElement = element.getEnclosingElement();
            if (FieldValidator.isValid(element, processingEnv, mMessager)) {
                PrefClass prefClass = new PrefClass(enclosingElement);
                List<PrefField> vars = mClassFieldMapping.get(prefClass);
                if (vars == null) {
                    vars = new ArrayList<>();
                }
                String subscribeMethodName = MethodValidator.tryGetSubscribeMethodName(element, processingEnv.getTypeUtils(), mMessager);
                if (subscribeMethodName == null) {
                    return false;
                }
                vars.add(new PrefField(element, klass, subscribeMethodName, processingEnv.getTypeUtils(), processingEnv.getElementUtils()));
                mClassFieldMapping.put(prefClass, vars);
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<>(Arrays.asList(
                FloatPreference.class.getCanonicalName(),
                IntPreference.class.getCanonicalName(),
                LongPreference.class.getCanonicalName(),
                StringPreference.class.getCanonicalName(),
                BooleanPreference.class.getCanonicalName()));
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
