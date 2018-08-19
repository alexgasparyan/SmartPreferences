package com.armdroid.smartpreferences;

import javax.lang.model.element.Element;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

public class TypeUtils {

    protected static boolean isSameType(Types types, Element elem1, Element elem2) {
        return types.isSameType(getBoxedIfNeeded(types, elem1.asType()), getBoxedIfNeeded(types, elem2.asType()));
    }

    protected static boolean isSameType(Types types, TypeMirror elem1, TypeMirror elem2) {
        return types.isSameType(getBoxedIfNeeded(types, elem1), getBoxedIfNeeded(types, elem2));
    }

    private static TypeMirror getBoxedIfNeeded(Types types, TypeMirror element) {
        if (element.getKind().isPrimitive()) {
            return types.boxedClass(((PrimitiveType) element)).asType();
        }
        return element;
    }
}
