package com.armdroid.smartpreferences;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class SmartPreferences {

    /**
     * Required initialization call in order to use the features of SmartPreferences library.
     * Library sets up shared preferences that are kept in filesDir/{PACKAGE_NAME}.preferences file
     * Normally, this method should be called in the {@link Application#onCreate()} method of class
     * inheriting from {@link android.app.Application}.
     * @param applicationContext Application context of the app.
     */
    public static void initialize(Context applicationContext) {
        String fileName = applicationContext.getPackageName() + ".preferences";
        SharedPreferences preferences = applicationContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        PreferenceRepository.getInstance().setPreferences(preferences);
    }

}
