package com.armdroid.smartpreferences;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.List;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;

public class PreferenceClassCreator {

    private static final String targetClassFieldName = "mTargetClass";

    private static final String repoFieldName = "mPreferenceRepository";
    private static final String repoClass = "PreferenceRepository";
    private static final String repoFieldGetter = "PreferenceRepository.getInstance()";

    private static final String libPackageName = "com.armdroid.smartpreferences";
    private static int controlFlowCounter = 0;


    public static JavaFile create(PrefClass clazz, List<PrefField> vars) {
        PrefClass genClass = clazz.getGeneratedClass();

        ClassName targetClassName = clazz.getClassName();
        ClassName preferenceListenerClassName = ClassName.get("android.content.SharedPreferences", "OnSharedPreferenceChangeListener");
        ClassName preferenceRepositoryClassName = ClassName.get(libPackageName, repoClass);
        ClassName preferenceBinder = ClassName.get(libPackageName, "PreferenceBinder");

        FieldSpec targetClassField = FieldSpec.builder(
                targetClassName,
                targetClassFieldName,
                Modifier.PRIVATE)
                .build();

        FieldSpec preferenceRepositoryField = FieldSpec.builder(
                preferenceRepositoryClassName,
                repoFieldName,
                Modifier.PRIVATE)
                .build();

        TypeSpec.Builder classTypeBuilder = TypeSpec.classBuilder(genClass.getSimpleName())
                .addSuperinterface(preferenceBinder)
                .addSuperinterface(preferenceListenerClassName)
                .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                .addField(targetClassField)
                .addField(preferenceRepositoryField)
                .addMethod(getStaticReadMethod(clazz))
                .addMethod(getSaticWriteMethod(clazz))
                .addMethod(getStaticBindMethod(clazz))
                .addMethod(getStaticReadAndBindMethod(clazz))
                .addMethod(getStaticWriteAndBindMethod(clazz))
                .addMethod(getConstructor(targetClassName))
                .addMethod(getPreferenceChangeListenerMethod(vars))
                .addMethod(getReadAllMethod(vars))
                .addMethod(getWriteAllMethod(vars))
                .addMethod(getObserveChangesMethod())
                .addMethod(getStopObserveChangesMethod())
                .addMethod(getUnbindMethod())
                .addMethod(getPreferenceRepositoryMethod())
                .addMethod(getSetTypeDefaultsMethod(vars));

        for (PrefField var : vars) {
            classTypeBuilder.addMethod(getFieldReadMethod(var, clazz));
            classTypeBuilder.addMethod(getFieldWriteMethod(var, clazz));
        }

        return JavaFile.builder(genClass.getClassPackageName(), classTypeBuilder.build())
                .addFileComment("This is a generated file. DO NOT MODIFY!")
                .build();
    }

    private static MethodSpec getStaticReadMethod(PrefClass targetClass) {
        PrefClass genClass = targetClass.getGeneratedClass();
        ClassName genClassName = ClassName.get(genClass.getClassPackageName(), genClass.getSimpleName());
        return MethodSpec.methodBuilder("read")
                .addJavadoc(
                        "Creates instance of {@link $L}, reads values from preferences (see {@link $L#$L}) and removes binding." +
                        " This is useful when preference values are to be read only once and no further actions are needed." +
                        "\n" +
                        "@param target Instance of class {@link $L} to which generated class instance will be bound." +
                        "\n",
                        genClass.getTypeName(),
                        genClass.getTypeName(),
                        "readAll",
                        targetClass.getTypeName())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(targetClass.getClassName(), targetClass.getSimpleNameLowerCase())
                .addStatement("$T instance = new $T($L)", genClassName, genClassName, targetClass.getSimpleNameLowerCase())
                .addStatement("instance.readAll()")
                .addStatement("instance.unbind()")
                .build();
    }

    private static MethodSpec getSaticWriteMethod(PrefClass targetClass) {
        PrefClass genClass = targetClass.getGeneratedClass();
        ClassName genClassName = ClassName.get(genClass.getClassPackageName(), genClass.getSimpleName());
        return MethodSpec.methodBuilder("write")
                .addJavadoc(
                        "Creates instance of {@link $L}, writes values to preferences (see {@link $L#$L}) and removes binding." +
                        " This is useful when preference values are to be read only once and no further actions are needed." +
                        "\n" +
                        "@param target Instance of class {@link $L} to which generated class instance will be bound." +
                        "\n",
                        genClass.getTypeName(),
                        genClass.getTypeName(),
                        "writeAll",
                        targetClass.getTypeName())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(targetClass.getClassName(), targetClass.getSimpleNameLowerCase())
                .addStatement("$T instance = new $T($L)", genClassName, genClassName, targetClass.getSimpleNameLowerCase())
                .addStatement("instance.writeAll()")
                .addStatement("instance.unbind()")
                .build();
    }

    private static MethodSpec getStaticBindMethod(PrefClass targetClass) {
        PrefClass genClass = targetClass.getGeneratedClass();
        ClassName genClassName = ClassName.get(genClass.getClassPackageName(), genClass.getSimpleName());
        return MethodSpec.methodBuilder("bind")
                .addJavadoc(
                        "Creates instance of {@link $L} and binds it with target class instance {@link $L}." +
                        " This is useful when different operations are required other than one time read/write operations, such" +
                        " as lazy reading/writing, reading/writing of specific fields, configuring change listeners etc." +
                        "\n" +
                        "@param target Instance of class {@link $L} to which generated class instance will be bound." +
                        "@return Instance of this class." +
                        "\n",
                        genClass.getTypeName(),
                        targetClass.getTypeName(),
                        targetClass.getTypeName())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(targetClass.getClassName(), targetClass.getSimpleNameLowerCase())
                .returns(genClassName)
                .addStatement("return new $T($L)", genClassName, targetClass.getSimpleNameLowerCase())
                .build();
    }

    private static MethodSpec getStaticReadAndBindMethod(PrefClass targetClass) {
        PrefClass genClass = targetClass.getGeneratedClass();
        ClassName genClassName = ClassName.get(genClass.getClassPackageName(), genClass.getSimpleName());
        return MethodSpec.methodBuilder("readAndBind")
                .addJavadoc(
                        "Creates instance of {@link $L}, reads values from preferences (see {@link $L#$L}) and binds" +
                        " it with target class instance {@link $L}." +
                        " This is useful when different operations are required other than one time read/write operations, such" +
                        " as lazy reading/writing, reading/writing of specific fields, configuring change listeners etc." +
                        "\n" +
                        "@param target Instance of class {@link $L} to which generated class instance will be bound." +
                        "\n" +
                        "@return Instance of this class." +
                        "\n",
                        genClass.getTypeName(),
                        genClass.getTypeName(),
                        "readAll",
                        targetClass.getTypeName(),
                        targetClass.getClassName())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(targetClass.getClassName(), targetClass.getSimpleNameLowerCase())
                .returns(genClassName)
                .addStatement("$T instance = new $T($L)", genClassName, genClassName, targetClass.getSimpleNameLowerCase())
                .addStatement("instance.readAll()")
                .addStatement("return instance")
                .build();
    }

    private static MethodSpec getStaticWriteAndBindMethod(PrefClass targetClass) {
        PrefClass genClass = targetClass.getGeneratedClass();
        ClassName genClassName = ClassName.get(genClass.getClassPackageName(), genClass.getSimpleName());
        return MethodSpec.methodBuilder("writeAndBind")
                .addJavadoc(
                        "Creates instance of {@link $L}, writes values to preferences (see {@link $L#$L}) and binds" +
                        " it with target class instance {@link $L}." +
                        " This is useful when different operations are required other than one time read/write operations, such" +
                        " as lazy reading/writing, reading/writing of specific fields, configuring change listeners etc." +
                        "\n" +
                        "@param target Instance of class {@link $L} to which generated class instance will be bound." +
                        "\n" +
                        "@return Instance of this class.",
                        genClass.getTypeName(),
                        genClass.getTypeName(),
                        "writeAll",
                        targetClass.getTypeName(),
                        targetClass.getClassName())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(targetClass.getClassName(), targetClass.getSimpleNameLowerCase())
                .returns(genClassName)
                .addStatement("$T instance = new $T($L)", genClassName, genClassName, targetClass.getSimpleNameLowerCase())
                .addStatement("instance.writeAll()")
                .addStatement("return instance")
                .build();
    }

    private static MethodSpec getConstructor(ClassName targetClassName) {
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .addParameter(targetClassName, "target")
                .addStatement("$L = target", targetClassFieldName)
                .addStatement("$L = $L", repoFieldName, repoFieldGetter)
                .build();
    }

    private static MethodSpec getPreferenceChangeListenerMethod(List<PrefField> vars) {
        List<PrefField> observables = vars
                .stream()
                .filter(PrefField::isObservable)
                .collect(Collectors.toList());

        ClassName preferenceClassName = ClassName.get("android.content", "SharedPreferences");

        MethodSpec.Builder preferenceChangeListenerMethodBuilder = MethodSpec.methodBuilder("onSharedPreferenceChanged")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(preferenceClassName ,"prefs")
                .addParameter(String.class, "key");

        controlFlowCounter = 0;
        observables.forEach(prefField -> {
            preferenceChangeListenerMethodBuilder.beginControlFlow("$L (key.equals($S))",
                    controlFlowCounter == 0 ? "if" : "else if",
                    prefField.getPreferenceName());
            setSaveOldValueStatement(prefField, preferenceChangeListenerMethodBuilder);
            setPreferenceReadStatement(prefField, preferenceChangeListenerMethodBuilder);
            setUpdateMethodCallStatement(prefField, preferenceChangeListenerMethodBuilder);
            preferenceChangeListenerMethodBuilder.endControlFlow();
            controlFlowCounter++;
        });

        return preferenceChangeListenerMethodBuilder.build();
    }

    private static MethodSpec getUnbindMethod() {
        return MethodSpec.methodBuilder("unbind")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addStatement("stopObserveChanges()")
                .addStatement("$L = null", repoFieldName)
                .addStatement("$L = null", targetClassFieldName)
                .build();
    }

    private static MethodSpec getSetTypeDefaultsMethod(List<PrefField> vars) {
        MethodSpec.Builder setDefaultsMethodBuilder = MethodSpec.methodBuilder("setTypeDefaults")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class);
        for (PrefField var : vars) {
            setFieldClearStatement(var, setDefaultsMethodBuilder);
        }
        return setDefaultsMethodBuilder.build();
    }

    private static MethodSpec getReadAllMethod(List<PrefField> vars) {
        MethodSpec.Builder readAllMethodBuilder = MethodSpec.methodBuilder("readAll")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class);

        for (PrefField var : vars) {
            setPreferenceReadStatement(var, readAllMethodBuilder);
        }
        return readAllMethodBuilder.build();
    }

    private static MethodSpec getWriteAllMethod(List<PrefField> vars) {
        MethodSpec.Builder writeAllMethodBuilder = MethodSpec.methodBuilder("writeAll")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class);

        for (PrefField var : vars) {
            setPreferenceWriteStatement(var, writeAllMethodBuilder);
        }
        return writeAllMethodBuilder.build();
    }

    private static MethodSpec getObserveChangesMethod() {
        return MethodSpec.methodBuilder("observeChanges")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addStatement(
                    "$L.registerOnSharedPreferenceChangeListener(this)",
                    repoFieldName)
                .build();
    }

    private static MethodSpec getStopObserveChangesMethod() {
        return MethodSpec.methodBuilder("stopObserveChanges")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addStatement(
                    "$L.unregisterOnSharedPreferenceChangeListener(this)",
                    repoFieldName)
                .build();
    }

    private static MethodSpec getPreferenceRepositoryMethod() {
        ClassName preferenceRepositoryClassName = ClassName.get(libPackageName, repoClass);
        return MethodSpec.methodBuilder("getPreferenceRepository")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(preferenceRepositoryClassName)
                .addStatement("return $L", repoFieldName)
                .build();
    }

    private static MethodSpec getFieldReadMethod(PrefField var, PrefClass clazz) {
        MethodSpec.Builder readMethodBuilder = MethodSpec.methodBuilder("read" + var.getFieldNameCapitalized())
                .addJavadoc(
                        "Finds value using key '$L' from {@link android.content.SharedPreferences} and sets it to field '$L'" +
                        " in target class {@link $L}." +
                        "\n",
                        var.getPreferenceName(),
                        var.getFieldName(),
                        clazz.getTypeName())
                .addModifiers(Modifier.PUBLIC);
        setPreferenceReadStatement(var, readMethodBuilder);
        return readMethodBuilder.build();
    }

    private static MethodSpec getFieldWriteMethod(PrefField var, PrefClass clazz) {
        MethodSpec.Builder writeMethodBuilder = MethodSpec.methodBuilder("write" + var.getFieldNameCapitalized())
                .addJavadoc(
                        "Writes value of field '$L' from target class {@link $L} to {@link android.content.SharedPreferences} with" +
                        "key '$L'." +
                        "\n",
                        var.getFieldName(),
                        clazz.getTypeName(),
                        var.getPreferenceName())
                .addModifiers(Modifier.PUBLIC);
        setPreferenceWriteStatement(var, writeMethodBuilder);
        return writeMethodBuilder.build();
    }


    private static void setSaveOldValueStatement(PrefField var, MethodSpec.Builder methodBuilder) {
        if (!var.getSubscribeMethodName().isEmpty()) {
            if (var.isPrivate()) {
                methodBuilder.addStatement("$T oldValue = $L.get$L()",
                        var.getFieldType(),
                        targetClassFieldName,
                        var.getFieldNameCapitalized());
            } else {
                methodBuilder.addStatement("$T oldValue = $L.$L",
                        var.getFieldType(),
                        targetClassFieldName,
                        var.getFieldName());
            }
        }
    }

    private static void setUpdateMethodCallStatement(PrefField var, MethodSpec.Builder methodBuilder) {
        if (!var.getSubscribeMethodName().isEmpty()) {
            methodBuilder
                    .addStatement(
                            "$L.$L(oldValue)",
                            targetClassFieldName,
                            var.getSubscribeMethodName());
        }
    }

    private static TypeName getConverterTypeParameters(PrefField var) {
        int size = var.getTypeArgumentsSize();
        if (size == 0) {
            return TypeName.get(var.getTransformer());
        } else if (size == 1) {
            return ParameterizedTypeName.get(
                    (ClassName) ClassName.get(var.getTransformer()),
                    TypeName.get(var.getTypeParam1())
            );
        } else {
            return ParameterizedTypeName.get(
                    (ClassName) ClassName.get(var.getTransformer()),
                    TypeName.get(var.getTypeParam1()),
                    TypeName.get(var.getTypeParam2())
            );
        }
    }

    private static void setPreferenceReadStatement(PrefField var, MethodSpec.Builder methodBuilder) {
        if (var.isPrivate()) {
            if (var.getTransformer() != null) {
                methodBuilder.addStatement(String.format("$L.set$T(($T) new $T(){}.convertRead($L.get($S, %s)))", var.getDefaultValueType()),
                        targetClassFieldName,
                        var.getFieldNameCapitalized(),
                        var.getFieldType(),
                        getConverterTypeParameters(var),
                        repoFieldName,
                        var.getPreferenceName(),
                        var.getDefaultValue());
            } else {
                methodBuilder.addStatement(String.format("$L.set$L($L.get($S, %s))", var.getDefaultValueType()),
                        targetClassFieldName,
                        var.getFieldNameCapitalized(),
                        repoFieldName,
                        var.getPreferenceName(),
                        var.getDefaultValue());
            }
        } else {
            if (var.getTransformer() != null) {
                methodBuilder.addStatement(String.format("$L.$L = ($T) new $T(){}.convertRead($L.get($S, %s))", var.getDefaultValueType()),
                        targetClassFieldName,
                        var.getFieldName(),
                        var.getFieldType(),
                        getConverterTypeParameters(var),
                        repoFieldName,
                        var.getPreferenceName(),
                        var.getDefaultValue());
            } else {
                methodBuilder.addStatement(String.format("$L.$L = $L.get($S, %s)", var.getDefaultValueType()),
                        targetClassFieldName,
                        var.getFieldName(),
                        repoFieldName,
                        var.getPreferenceName(),
                        var.getDefaultValue());
            }
        }
    }

    private static void setPreferenceWriteStatement(PrefField var, MethodSpec.Builder methodBuilder) {
        if (var.isPrivate()) {
            if (var.getTransformer() != null) {
                methodBuilder.addStatement("$L.put($S, new $T(){}.convertWrite($L.get$L()))",
                        repoFieldName,
                        var.getPreferenceName(),
                        getConverterTypeParameters(var),
                        targetClassFieldName,
                        var.getFieldNameCapitalized());
            } else {
                methodBuilder.addStatement("$L.put($S, $L.get$L())",
                        repoFieldName,
                        var.getPreferenceName(),
                        targetClassFieldName,
                        var.getFieldNameCapitalized());
            }
        } else {
            if (var.getTransformer() != null) {
                methodBuilder.addStatement("$L.put($S, new $T(){}.convertWrite($L.$L))",
                        repoFieldName,
                        var.getPreferenceName(),
                        getConverterTypeParameters(var),
                        targetClassFieldName,
                        var.getFieldName());
            } else {
                methodBuilder.addStatement("$L.put($S, $L.$L)",
                        repoFieldName,
                        var.getPreferenceName(),
                        targetClassFieldName,
                        var.getFieldName());
            }
        }
    }

    private static void setFieldClearStatement(PrefField var, MethodSpec.Builder methodBuilder) {
        ClassName defaultValueClassName = ClassName.get(libPackageName, "DefaultValue");
        if (var.isPrivate()) {
            methodBuilder.addStatement("$L.set$L($T.forObject($L.get$L()))",
                    targetClassFieldName,
                    var.getFieldNameCapitalized(),
                    defaultValueClassName,
                    targetClassFieldName,
                    var.getFieldNameCapitalized());
        } else {
            methodBuilder.addStatement("$L.$L = $T.forObject($L.$L)",
                    targetClassFieldName,
                    var.getFieldName(),
                    defaultValueClassName,
                    targetClassFieldName,
                    var.getFieldName());
        }
    }
}
