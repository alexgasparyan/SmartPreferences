package com.armdroid.smartpreferences;

import java.util.Set;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.armdroid.smartpreferences.TypeUtils.fieldType;
import static com.armdroid.smartpreferences.TypeUtils.isSameType;
import static javax.tools.Diagnostic.Kind.ERROR;

public class FieldValidator {

    public static boolean isValid(Element element, ProcessingEnvironment processingEnvironment, Messager messager) {
        if (element.getKind() != ElementKind.FIELD) {
            error(messager, element, "Only fields can be annotated with @***Preference.");
            return false;
        }
        if (element.getEnclosingElement().getKind() != ElementKind.CLASS) {
            error(messager, element, "Fields can be annotated with @***Preference only inside of type 'class' (i.e not interface).");
            return false;
        }
        Set<Modifier> modifiers = element.getModifiers();
        if (modifiers.contains(Modifier.FINAL)) {
            error(messager, element, "Field with @***Preference annotation cannot be final.");
            return false;
        }

        Types typeUtils = processingEnvironment.getTypeUtils();
        Elements elementUtils = processingEnvironment.getElementUtils();
        VariableElement variableElement = (VariableElement) element;
        TypeMirror fieldMirror = variableElement.asType();

        Transform transform = variableElement.getAnnotation(Transform.class);
        if (transform != null) {
            TypeMirror mirror = getTypeMirror(transform);
            TypeElement transformElement = (TypeElement) typeUtils.asElement(mirror);
            if (transformElement.getKind() != ElementKind.CLASS) {
                error(messager, transformElement, "Class implementing PreferenceTransformer<From, To> should be of type 'class' (i.e. not interface).");
                return false;
            }

            if (transformElement.getModifiers().contains(Modifier.ABSTRACT)) {
                int abstractMethodCount = ElementFilter.methodsIn(transformElement.getEnclosedElements())
                        .stream()
                        .filter(method -> method.getModifiers().contains(Modifier.ABSTRACT))
                        .toArray()
                        .length;
                if (abstractMethodCount != 0) {
                    error(messager, transformElement, "Class extending PreferenceTransformer<From, To> can be abstract, but cannot have abstract methods.");
                }
            }

            boolean emptyConstructorNotFound = ElementFilter.constructorsIn(transformElement.getEnclosedElements())
                    .stream()
                    .filter(constructor -> constructor.getParameters().isEmpty())
                    .toArray()
                    .length == 0;

            if (emptyConstructorNotFound) {
                error(messager, transformElement, "Class extending PreferenceTransformer<From, To> must have empty constructor (or no constructors at all).");
                return false;
            }

        } else {
            if (element.getAnnotation(IntPreference.class) != null
                    && !isSameType(typeUtils, fieldMirror, fieldType(Integer.class, elementUtils))) {
                error(messager, element, "Annotation @IntPreference must be used with int/Integer or must have @Transform annotation as well.");
                return false;
            }
            if (element.getAnnotation(LongPreference.class) != null
                    && !isSameType(typeUtils, fieldMirror, fieldType(Long.class, elementUtils))) {
                error(messager, element, "Annotation @LongPreference must be used with long/Long or must have @Transform annotation as well.");
                return false;
            }
            if (element.getAnnotation(FloatPreference.class) != null
                    && !isSameType(typeUtils, fieldMirror, fieldType(Float.class, elementUtils))) {
                error(messager, element, "Annotation @FloatPreference must be used with float/Float or must have @Transform annotation as well.");
                return false;
            }
            if (element.getAnnotation(BooleanPreference.class) != null
                    && !isSameType(typeUtils, fieldMirror, fieldType(Boolean.class, elementUtils))) {
                error(messager, element, "Annotation @BooleanPreference must be used with boolean/Boolean or must have @Transform annotation as well.");
                return false;
            }
            if (element.getAnnotation(StringPreference.class) != null
                    && !isSameType(typeUtils, fieldMirror, fieldType(String.class, elementUtils))) {
                error(messager, element, "Annotation @StringPreference must be used with String or must have @Transform annotation as well.");
                return false;
            }
        }

        return !modifiers.contains(Modifier.PRIVATE) || MethodValidator.hasGetterAndSetter(element, messager, typeUtils);
    }

    private static TypeMirror getTypeMirror(Transform transformAnnotation) {
        try {
            transformAnnotation.using();
            return null;
        } catch (MirroredTypeException mte) {
            return mte.getTypeMirror();
        }
    }

    private static void error(Messager messager, Element element, String error) {
        messager.printMessage(ERROR, error, element);
    }
}
