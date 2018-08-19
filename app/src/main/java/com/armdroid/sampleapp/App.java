package com.armdroid.sampleapp;

import android.app.Application;

import com.armdroid.smartpreferences.SmartPreferences;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SmartPreferences.initialize(this);
    }
}
