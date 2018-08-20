package com.armdroid.sampleapp;

import com.armdroid.smartpreferences.PreferenceTransformer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public abstract class GsonTransformer<To> implements PreferenceTransformer<String, To> {

    @Override
    public To convertRead(String from) {
        Type superClass = getClass().getGenericSuperclass();
        Type genericType = ((ParameterizedType) superClass).getActualTypeArguments()[0];
        return new Gson().fromJson(from, genericType);
    }

    @Override
    public List<To> convertReadList(String from) {
        Type superClass = getClass().getGenericSuperclass();
        Type genericType = ((ParameterizedType) superClass).getActualTypeArguments()[0];
        Type listGenericType = TypeToken.getParameterized(ArrayList.class, genericType).getType();
        return new Gson().fromJson(from, listGenericType);
    }

    @Override
    public String convertWrite(To to) {
        Type superClass = getClass().getGenericSuperclass();
        Type genericType = ((ParameterizedType) superClass).getActualTypeArguments()[0];
        return new Gson().toJson(to, genericType);
    }

    @Override
    public String convertWriteList(List<To> to) {
        Type superClass = getClass().getGenericSuperclass();
        Type genericType = ((ParameterizedType) superClass).getActualTypeArguments()[0];
        Type listGenericType = TypeToken.getParameterized(ArrayList.class, genericType).getType();
        return new Gson().toJson(to, listGenericType);
    }
}
