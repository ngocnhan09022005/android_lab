package com.example.android_lab.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import com.example.android_lab.R;
import com.example.android_lab.ui.auth.LoginActivity;
import com.example.android_lab.ui.admin.fragments.AddProductFragment;
import com.google.firebase.auth.FirebaseAuth;

public class AdminActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);

        initViews();
        initEvents();
        initWindowInsets();

        if (savedInstanceState == null) {
            loadFragment(new AddProductFragment());
        }
    }

    private void initViews() {
        btnLogout = findViewById(R.id.btnAdminLogout);
        auth = FirebaseAuth.getInstance();
    }

    private void initEvents() {
        btnLogout.setOnClickListener(v -> logout());

        findViewById(R.id.menuHome).setOnClickListener(v ->
                loadFragment(new AddProductFragment())
        );

        // TODO: add more menu navigation buttons here if needed
        // findViewById(R.id.menuFoodList).setOnClickListener(...)
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

    private void initWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
