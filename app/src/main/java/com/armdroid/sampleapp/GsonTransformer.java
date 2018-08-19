package com.armdroid.sampleapp;

import com.armdroid.smartpreferences.PreferenceTransformer;
import com.google.gson.Gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class GsonTransformer<To> implements PreferenceTransformer<String, To> {

    @Override
    public To convertRead(String from) {
        Type superClass = getClass().getGenericSuperclass();
        Type genericType = ((ParameterizedType) superClass).getActualTypeArguments()[0];
        return new Gson().fromJson(from, genericType);
    }

    @Override
    public String convertWrite(To to) {
        Type superClass = getClass().getGenericSuperclass();
        Type genericType = ((ParameterizedType) superClass).getActualTypeArguments()[0];
        return new Gson().toJson(genericType);
    }
}
