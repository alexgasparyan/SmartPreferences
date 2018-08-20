package com.armdroid.smartpreferences;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class PrefField {

    private final String fieldName;
    private final String preferenceName;
    private final Object defaultValue;
    private final boolean isObservable;
    private final TypeMirror transformer;
    private final TypeMirror typeParam1;
    private final TypeMirror typeParam2;
    private final TypeMirror fieldType;
    private final String subscribeMethodName;
    private final int typeArgumentsSize;
    private final boolean isPrivate;
    private final boolean isList;

    protected PrefField(Element element,
                        Class<? extends Annotation> clazz,
                        String subscribeMethodName,
                        Types types,
                        Elements elements) {
        Annotation annotation = element.getAnnotation(clazz);
        this.fieldName = element.getSimpleName().toString();
        this.subscribeMethodName = subscribeMethodName;
        String preferenceName;
        if (annotation instanceof IntPreference) {
            preferenceName = ((IntPreference) annotation).named();
            this.defaultValue = ((IntPreference) annotation).defaultValue();
        } else if (annotation instanceof StringPreference) {
            preferenceName = ((StringPreference) annotation).named();
            this.defaultValue = ((StringPreference) annotation).defaultValue();
        } else if (annotation instanceof FloatPreference) {
            preferenceName = ((FloatPreference) annotation).named();
            this.defaultValue = ((FloatPreference) annotation).defaultValue();
        } else if (annotation instanceof LongPreference) {
            preferenceName = ((LongPreference) annotation).named();
            this.defaultValue = ((LongPreference) annotation).defaultValue();
        } else {
            preferenceName = ((BooleanPreference) annotation).named();
            this.defaultValue = ((BooleanPreference) annotation).defaultValue();
        }
        if (preferenceName.isEmpty()) {
            this.preferenceName = fieldName;
        } else {
            this.preferenceName = preferenceName;
        }

        Transform transformAnnotation = element.getAnnotation(Transform.class);
        isObservable =  element.getAnnotation(Observe.class) != null;
        transformer = transformAnnotation != null ? getTransformer(transformAnnotation) : null;
        typeParam1 = transformAnnotation != null ? getTypeParam1(transformAnnotation) : null;
        typeParam2 = transformAnnotation != null ? getTypeParam2(transformAnnotation) : null;
        fieldType = element.asType();
        isPrivate = element.getModifiers().contains(Modifier.PRIVATE);
        isList = types.isAssignable(types.erasure(fieldType), TypeUtils.fieldType(List.class, elements));

        if (transformer != null) {
            TypeElement typeElement = (TypeElement) types.asElement(transformer);
            typeArgumentsSize = typeElement == null ? 0 : typeElement.getTypeParameters().size();
        } else {
            typeArgumentsSize = 0;
        }
    }

    private TypeMirror getTransformer(Transform transformAnnotation) {
        try {
            transformAnnotation.using();
            return null;
        } catch (MirroredTypeException mte) {
            return mte.getTypeMirror();
        }
    }

    private TypeMirror getTypeParam1(Transform transformAnnotation) {
        try {
            transformAnnotation.typeParam1();
            return null;
        } catch (MirroredTypeException mte) {
            return mte.getTypeMirror();
        }
    }

    private TypeMirror getTypeParam2(Transform transformAnnotation) {
        try {
            transformAnnotation.typeParam2();
            return null;
        } catch (MirroredTypeException mte) {
            return mte.getTypeMirror();
        }
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldNameCapitalized() {
        return fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    public String getPreferenceName() {
        return preferenceName;
    }

    public Object getDefaultValue() {
        if (defaultValue instanceof Long) {
            return defaultValue + "L";
        } else if (defaultValue instanceof Float) {
            return defaultValue + "F";
        }
        return defaultValue;
    }

    public String getDefaultValueType() {
        if (defaultValue instanceof String) {
            return "$S";
        }
        return "$L";
    }

    public TypeMirror getTransformer() {
        return transformer;
    }

    public TypeMirror getFieldType() {
        return fieldType;
    }

    public boolean isObservable() {
        return isObservable;
    }

    public String getSubscribeMethodName() {
        return subscribeMethodName;
    }

    public int getTypeArgumentsSize() {
        return typeArgumentsSize;
    }

    public TypeMirror getTypeParam1() {
        return typeParam1;
    }

    public TypeMirror getTypeParam2() {
        return typeParam2;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public boolean isList() {
        return isList;
    }
}
