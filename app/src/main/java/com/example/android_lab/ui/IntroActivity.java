package com.example.android_lab.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android_lab.R;

public class IntroActivity extends AppCompatActivity {
    private ImageView btnGetStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        btnGetStarted = findViewById(R.id.imageView);

        btnGetStarted.setOnClickListener(v -> {
            // Đánh dấu đã xem intro
            SharedPreferences prefs = getSharedPreferences("intro_prefs", MODE_PRIVATE);
            prefs.edit().putBoolean("isFirstLaunch", false).apply();

            startActivity(new Intent(IntroActivity.this, LoginActivity.class));
            finish();
        });
    }
}
