package com.example.android_lab.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.android_lab.R;

public class Register extends AppCompatActivity {

    private EditText editName, editPassword, editEmail;
    private ImageView btnRegister;
    private TextView btnLoginNow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);

        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editName.getText().toString().trim();
                String email = editEmail.getText().toString().trim();
                String password = editPassword.getText().toString().trim();
                checkRegister(name, email, password);
            }
        });

        btnLoginNow = findViewById(R.id.btnLogin);
        btnLoginNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void checkRegister(String name, String email, String password) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            Toast.makeText(this, "Invalid Data", Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.length() < 6) {
            Toast.makeText(this, "Name must be than 6", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (name.length() < 6) {
            Toast.makeText(this, "Password must be than 6", Toast.LENGTH_SHORT).show();
            return;
        }
         else if (!isValidEmail(email)) {
            Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Register succesfully", Toast.LENGTH_SHORT).show();
    }

    private boolean isValidEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}