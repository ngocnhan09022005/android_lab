package com.example.android_lab;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.appcompat.app.AppCompatDelegate;
import com.example.android_lab.utils.DatabaseSeeder;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        boolean isSeeded = prefs.getBoolean("isSeeded", false);

        if (!isSeeded) {
            new DatabaseSeeder().seedFoods();
            prefs.edit().putBoolean("isSeeded", true).apply();
            Log.d("MyApp", "Running seedFoods()...");
        }
    }
}
