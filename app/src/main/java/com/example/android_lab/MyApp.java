package com.example.android_lab;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.appcompat.app.AppCompatDelegate;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }
}
