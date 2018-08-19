package com.armdroid.smartpreferences;

import com.squareup.javapoet.ClassName;

import java.lang.reflect.Type;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

public class PrefClass implements Type {

    private final String simpleName;
    private final String typeName;
    private final String classPackageName;

    protected PrefClass(Element element) {
        TypeElement typeElement = (TypeElement) element;
        this.simpleName = typeElement.getSimpleName().toString();
        this.typeName = typeElement.getQualifiedName().toString();
        this.classPackageName = typeName.substring(0, typeName.lastIndexOf(simpleName) - 1);
    }

    private PrefClass(String simpleName, String typeName) {
        this.simpleName = simpleName;
        this.typeName = typeName;
        this.classPackageName = typeName.substring(0, typeName.lastIndexOf(simpleName) - 1);
    }

    public String getSimpleName() {
        return simpleName;
    }

    public String getSimpleNameLowerCase() {
        return simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
    }

    public String getClassPackageName() {
        return classPackageName;
    }

    public ClassName getClassName() {
        return ClassName.get(classPackageName, simpleName);
    }

    @Override
    public String getTypeName() {
        return typeName;
    }


    public PrefClass getGeneratedClass() {
        return new PrefClass(simpleName + "Preferences", typeName + "Preferences");
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PrefClass && ((PrefClass) obj).typeName.equals(typeName);
    }

    @Override
    public int hashCode() {
        return typeName.hashCode();
    }
}
