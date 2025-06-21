package com.example.android_lab.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.android_lab.R;
import com.example.android_lab.ui.fragment.AddFoodFragment;
import com.google.firebase.auth.FirebaseAuth;

public class AdminActivity extends AppCompatActivity {

    private Button btnLogout;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);

        initViews(); // ✅ Gọi hàm khởi tạo view đúng lúc

        btnLogout.setOnClickListener(v -> logout());

        // Load màn mặc định
        loadFragment(new AddFoodFragment());

        // Xử lý click bottom menu
        findViewById(R.id.menuHome).setOnClickListener(v -> loadFragment(new AddFoodFragment()));

        // Inset để không che UI bởi status/nav bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        btnLogout = findViewById(R.id.btnAdminLogout);
        auth = FirebaseAuth.getInstance();
    }

    private void logout() {
        auth.signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}
