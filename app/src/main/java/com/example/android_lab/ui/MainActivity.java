package com.example.android_lab.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.android_lab.R;
import com.example.android_lab.ui.fragment.FavoritesFragment;
import com.example.android_lab.ui.fragment.HomeFragment;
import com.example.android_lab.ui.fragment.ProfileFragment;
import com.example.android_lab.ui.fragment.SearchFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private LinearLayout menuHome, menuSearch, menuFavorites, menuProfile;
    private ImageView iconHome, iconSearch, iconFavorites, iconProfile;
    private TextView textHome, textSearch, textFavorites, textProfile;

    private HomeFragment homeFragment;
    private SearchFragment searchFragment;
    private FavoritesFragment favoritesFragment;
    private ProfileFragment profileFragment;

    private int currentMenuIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Kiểm tra nếu là lần đầu mở app
        SharedPreferences prefs = getSharedPreferences("intro_prefs", MODE_PRIVATE);
        boolean isFirstLaunch = prefs.getBoolean("isFirstLaunch", true);

        if (isFirstLaunch) {
            startActivity(new Intent(this, IntroActivity.class));
            prefs.edit().putBoolean("isFirstLaunch", false).apply();
            finish();
            return;
        }

        // 2. Kiểm tra đăng nhập Firebase
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // 3. Nếu hợp lệ -> load giao diện chính
        setContentView(R.layout.activity_main);

        initViews();
        initFragments();
        setupMenuClickListeners();
        loadFragment(homeFragment, 0);
    }

    private void initViews() {
        menuHome = findViewById(R.id.menuHome);
        menuSearch = findViewById(R.id.menuSearch);
        menuFavorites = findViewById(R.id.menuFavorites);
        menuProfile = findViewById(R.id.menuProfile);

        iconHome = findViewById(R.id.iconHome);
        iconSearch = findViewById(R.id.iconSearch);
        iconFavorites = findViewById(R.id.iconFavorites);
        iconProfile = findViewById(R.id.iconProfile);

        textHome = findViewById(R.id.textHome);
        textSearch = findViewById(R.id.textSearch);
        textFavorites = findViewById(R.id.textFavorites);
        textProfile = findViewById(R.id.textProfile);
    }

    private void initFragments() {
        homeFragment = new HomeFragment();
        searchFragment = new SearchFragment();
        favoritesFragment = new FavoritesFragment();
        profileFragment = new ProfileFragment();
    }

    private void setupMenuClickListeners() {
        menuHome.setOnClickListener(v -> {
            if (currentMenuIndex != 0) loadFragment(homeFragment, 0);
        });

        menuSearch.setOnClickListener(v -> {
            if (currentMenuIndex != 1) loadFragment(searchFragment, 1);
        });

        menuFavorites.setOnClickListener(v -> {
            if (currentMenuIndex != 2) loadFragment(favoritesFragment, 2);
        });

        menuProfile.setOnClickListener(v -> {
            if (currentMenuIndex != 3) loadFragment(profileFragment, 3);
        });
    }

    private void loadFragment(Fragment fragment, int menuIndex) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
        currentMenuIndex = menuIndex;
    }

    @Override
    public void onBackPressed() {
        if (currentMenuIndex != 0) {
            loadFragment(homeFragment, 0);
        } else {
            super.onBackPressed();
        }
    }
}
