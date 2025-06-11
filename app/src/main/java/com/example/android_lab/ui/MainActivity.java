package com.example.android_lab.ui;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.android_lab.R;
import com.example.android_lab.ui.fragment.FavoritesFragment;
import com.example.android_lab.ui.fragment.HomeFragment;
import com.example.android_lab.ui.fragment.ProfileFragment;
import com.example.android_lab.ui.fragment.SearchFragment;

public class MainActivity extends AppCompatActivity {

    // Menu items
    private LinearLayout menuHome, menuSearch, menuFavorites, menuProfile;
    private ImageView iconHome, iconSearch, iconFavorites, iconProfile;
    private TextView textHome, textSearch, textFavorites, textProfile;

    // Fragment instances
    private HomeFragment homeFragment;
    private SearchFragment searchFragment;
    private FavoritesFragment favoritesFragment;
    private ProfileFragment profileFragment;

    // Current selected menu
    private int currentMenuIndex = 0; // 0=Home, 1=Search, 2=Favorites, 3=Profile

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initFragments();
        setupMenuClickListeners();

        // Load default fragment (Home)
        loadFragment(homeFragment, 0);
    }

    private void initViews() {
        // Menu containers
        menuHome = findViewById(R.id.menuHome);
        menuSearch = findViewById(R.id.menuSearch);
        menuFavorites = findViewById(R.id.menuFavorites);
        menuProfile = findViewById(R.id.menuProfile);

        // Icons
        iconHome = findViewById(R.id.iconHome);
        iconSearch = findViewById(R.id.iconSearch);
        iconFavorites = findViewById(R.id.iconFavorites);
        iconProfile = findViewById(R.id.iconProfile);

        // Texts
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
            if (currentMenuIndex != 0) {
                loadFragment(homeFragment, 0);
            }
        });

        menuSearch.setOnClickListener(v -> {
            if (currentMenuIndex != 1) {
                loadFragment(searchFragment, 1);
            }
        });

        menuFavorites.setOnClickListener(v -> {
            if (currentMenuIndex != 2) {
                loadFragment(favoritesFragment, 2);
            }
        });

        menuProfile.setOnClickListener(v -> {
            if (currentMenuIndex != 3) {
                loadFragment(profileFragment, 3);
            }
        });
    }

    private void loadFragment(Fragment fragment, int menuIndex) {
        // Load fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();

        // Update menu appearance
        updateMenuAppearance(menuIndex);
        currentMenuIndex = menuIndex;
    }

    private void updateMenuAppearance(int selectedIndex) {
        resetMenuAppearance();
        switch (selectedIndex) {
            case 0:
                iconHome.setColorFilter(ContextCompat.getColor(this, R.color.primary_color));
                textHome.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
                break;
            case 1:
                iconSearch.setColorFilter(ContextCompat.getColor(this, R.color.primary_color));
                textSearch.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
                break;
            case 2:
                iconFavorites.setColorFilter(ContextCompat.getColor(this, R.color.primary_color));
                textFavorites.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
                break;
            case 3:
                iconProfile.setColorFilter(ContextCompat.getColor(this, R.color.primary_color));
                textProfile.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
                break;
        }
    }

    private void resetMenuAppearance() {
        int defaultColor = ContextCompat.getColor(this, R.color.gray_color);

        iconHome.setColorFilter(defaultColor);
        iconSearch.setColorFilter(defaultColor);
        iconFavorites.setColorFilter(defaultColor);
        iconProfile.setColorFilter(defaultColor);

        textHome.setTextColor(defaultColor);
        textSearch.setTextColor(defaultColor);
        textFavorites.setTextColor(defaultColor);
        textProfile.setTextColor(defaultColor);
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