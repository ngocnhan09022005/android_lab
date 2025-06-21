package com.example.android_lab.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.activity.OnBackPressedCallback;

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

        if (savedInstanceState != null) {
            currentMenuIndex = savedInstanceState.getInt("currentMenuIndex", 0);
        }

        // Thêm xử lý back button mới
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                    getSupportFragmentManager().popBackStack();
                    // Cập nhật currentMenuIndex dựa trên fragment hiện tại
                    Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
                    if (currentFragment instanceof HomeFragment) {
                        currentMenuIndex = 0;
                    } else if (currentFragment instanceof SearchFragment) {
                        currentMenuIndex = 1;
                    } else if (currentFragment instanceof FavoritesFragment) {
                        currentMenuIndex = 2;
                    } else if (currentFragment instanceof ProfileFragment) {
                        currentMenuIndex = 3;
                    }
                    updateMenuIcons(currentMenuIndex);
                } else {
                    setEnabled(false);
                    MainActivity.this.getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
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
        if (homeFragment == null) homeFragment = new HomeFragment();
        if (searchFragment == null) searchFragment = new SearchFragment();
        if (favoritesFragment == null) favoritesFragment = new FavoritesFragment();
        if (profileFragment == null) profileFragment = new ProfileFragment();
    }

    private void setupMenuClickListeners() {
        menuHome.setOnClickListener(v -> {
            if (currentMenuIndex != 0) {
                updateMenuIcons(0);
                loadFragment(homeFragment, 0);
            }
        });

        menuSearch.setOnClickListener(v -> {
            if (currentMenuIndex != 1) {
                updateMenuIcons(1);
                loadFragment(searchFragment, 1);
            }
        });

        menuFavorites.setOnClickListener(v -> {
            if (currentMenuIndex != 2) {
                updateMenuIcons(2);
                loadFragment(favoritesFragment, 2);
            }
        });

        menuProfile.setOnClickListener(v -> {
            if (currentMenuIndex != 3) {
                updateMenuIcons(3);
                loadFragment(profileFragment, 3);
            }
        });
    }

    private void updateMenuIcons(int selectedIndex) {
        // Reset all icons and text to default color
        iconHome.setImageResource(R.drawable.ic_home);
        iconSearch.setImageResource(R.drawable.ic_search);
        iconFavorites.setImageResource(R.drawable.ic_favorite);
        iconProfile.setImageResource(R.drawable.ic_account);

        textHome.setTextColor(getColor(R.color.text_color_unselected));
        textSearch.setTextColor(getColor(R.color.text_color_unselected));
        textFavorites.setTextColor(getColor(R.color.text_color_unselected));
        textProfile.setTextColor(getColor(R.color.text_color_unselected));

        // Update selected icon and text
        switch (selectedIndex) {
            case 0:
                iconHome.setImageResource(R.drawable.ic_home_selected);
                textHome.setTextColor(getColor(R.color.text_color_selected));
                break;
            case 1:
                iconSearch.setImageResource(R.drawable.ic_search_selected);
                textSearch.setTextColor(getColor(R.color.text_color_selected));
                break;
            case 2:
                iconFavorites.setImageResource(R.drawable.ic_favorite_selected);
                textFavorites.setTextColor(getColor(R.color.text_color_selected));
                break;
            case 3:
                iconProfile.setImageResource(R.drawable.ic_account_selected);
                textProfile.setTextColor(getColor(R.color.text_color_selected));
                break;
        }
        currentMenuIndex = selectedIndex;
    }

    private void loadFragment(Fragment fragment, int menuIndex) {
        if (fragment == null) return;

        // Kiểm tra xem fragment hiện tại có đang được hiển thị không
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (currentFragment != null && currentFragment.getClass().equals(fragment.getClass())) {
            return;
        }

        // Xác định animation dựa vào hướng chuyển fragment
        int enterAnim = menuIndex > currentMenuIndex ? R.anim.slide_in_right : R.anim.slide_in_left;
        int exitAnim = menuIndex > currentMenuIndex ? R.anim.slide_out_left : R.anim.slide_out_right;

        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(enterAnim, exitAnim, enterAnim, exitAnim)
                .replace(R.id.fragmentContainer, fragment)
                .setReorderingAllowed(true)
                .addToBackStack(null)  // Thêm vào back stack để xử lý back button
                .commit();

        currentMenuIndex = menuIndex;
        updateMenuIcons(menuIndex);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentMenuIndex", currentMenuIndex);
    }
}
