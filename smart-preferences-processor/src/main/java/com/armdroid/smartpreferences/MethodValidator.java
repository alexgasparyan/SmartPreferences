package com.armdroid.smartpreferences;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Types;

import static com.armdroid.smartpreferences.TypeUtils.isSameType;
import static javax.tools.Diagnostic.Kind.ERROR;

public class MethodValidator {

    public static String tryGetSubscribeMethodName(Element variable, Types typeUtils, Messager messager) {
        Element clazz = variable.getEnclosingElement();
        Observe observe = variable.getAnnotation(Observe.class);
        String tag = variable.getSimpleName().toString();
        if (observe != null) {
            List<ExecutableElement> methods = ElementFilter
                    .methodsIn(clazz.getEnclosedElements())
                    .stream()
                    .filter(executableElement -> {
                        Subscribe subscribe = executableElement.getAnnotation(Subscribe.class);
                        return subscribe != null && subscribe.tag().equals(tag);
                    })
                    .collect(Collectors.toList());
            if (methods.size() > 1) {
                error(messager, methods.get(0), "Found multiple methods with tag '" + tag +
                        "' and annotation @Subscribe");
                return null;
            } else if (methods.size() == 1) {
                ExecutableElement method = methods.get(0);
                if (method.getModifiers().contains(Modifier.PRIVATE)) {
                    error(messager, method, "Method with annotation @Subscribe cannot be private.");
                    return null;
                }
                if (method.getParameters().size() != 1) {
                    error(messager, method, "Method with annotation @Subscribe and with tag '" + tag + "' must have 1 parameter" +
                            " of type " + variable.asType().toString() + ".");
                    return null;
                }

                if (!isSameType(typeUtils, method.getParameters().get(0), variable)) {
                    error(messager, method, "Method with annotation @Subscribe and with tag '" + tag + "' must have 1 parameter" +
                            " of type " + variable.asType().toString() + ".");
                    return null;
                }
                return method.getSimpleName().toString();
            }
        }
        return "";
    }

    public static boolean hasGetterAndSetter(Element element, Messager messager, Types typeUtils) {
        String fieldName = element.getSimpleName().toString();
        String fieldNameCap = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        if (ElementFilter.methodsIn(element.getEnclosingElement().getEnclosedElements())
                .stream()
                .filter(method -> {
                    boolean isPrivate = method.getModifiers().contains(Modifier.PRIVATE);
                    String methodName = method.getSimpleName().toString();

                    if (methodName.equals("get" + fieldNameCap)
                            && !isPrivate
                            && method.getParameters().isEmpty()) {
                        return isSameType(typeUtils, method.getReturnType(), element.asType());
                    } else if (methodName.equals("set" + fieldNameCap)
                            && !isPrivate
                            && method.getParameters().size() == 1
                            && method.getReturnType().getKind() == TypeKind.VOID) {
                        return isSameType(typeUtils, method.getParameters().get(0).asType(), element.asType());
                    }
                    return false;
                }).toArray().length != 2) {
            error(messager, element, "Field with @***Preference annotation can be private only if enclosing class has public or protected getter and setter for that field");
            return false;
        }
        return true;
    }

    private static void error(Messager messager, Element element, String error) {
        messager.printMessage(ERROR, error, element);
    }
}
