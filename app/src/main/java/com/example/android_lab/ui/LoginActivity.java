package com.example.android_lab.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.android_lab.R;
import com.example.android_lab.models.User;

public class LoginActivity extends AppCompatActivity {
    private Button btnBack, btnLogin;
    private EditText emailEdit, passwordEdit;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEdit = findViewById(R.id.etEmail);
        passwordEdit = findViewById(R.id.etPassword);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = new User();
                user.setEmail(emailEdit.getText().toString().trim());
                user.setPassword(passwordEdit.getText().toString().trim());

                checkLogin(user.getEmail(), user.getPassword());
            }
        });
    }
    private void checkLogin(String email, String password) {
        if (email.equals("name@gmail.com") && password.equals("password")) {
            Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Email hoac mat khau khong chinh xac", Toast.LENGTH_SHORT).show();
        }
    }
}