package com.example.android_lab.ui.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.activity.OnBackPressedCallback;
import com.example.android_lab.R;
import com.example.android_lab.ui.auth.LoginActivity;
import com.example.android_lab.ui.user.fragments.CartFragment;
import com.example.android_lab.ui.user.fragments.FavoritesFragment;
import com.example.android_lab.ui.user.fragments.HomeFragment;
import com.example.android_lab.ui.user.fragments.ProfileFragment;
import com.example.android_lab.ui.user.fragments.SearchFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private LinearLayout menuHome, menuSearch, menuFavorites, menuProfile, menuCart;
    private ImageView iconHome, iconSearch, iconFavorites, iconProfile, iconCart;
    private TextView textHome, textSearch, textFavorites, textProfile, textCart;

    private HomeFragment homeFragment;
    private SearchFragment searchFragment;
    private FavoritesFragment favoritesFragment;
    private ProfileFragment profileFragment;
    private CartFragment cartFragment;

    private int currentMenuIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("intro_prefs", MODE_PRIVATE);
        boolean isFirstLaunch = prefs.getBoolean("isFirstLaunch", true);

        if (isFirstLaunch) {
            startActivity(new Intent(this, IntroActivity.class));
            prefs.edit().putBoolean("isFirstLaunch", false).apply();
            finish();
            return;
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

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
                    } else if (currentFragment instanceof CartFragment) {
                        currentMenuIndex = 2;
                    } else if (currentFragment instanceof FavoritesFragment) {
                        currentMenuIndex = 3;
                    } else if (currentFragment instanceof ProfileFragment) {
                        currentMenuIndex = 4;
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
        menuCart = findViewById(R.id.menuCart);

        iconHome = findViewById(R.id.iconHome);
        iconSearch = findViewById(R.id.iconSearch);
        iconFavorites = findViewById(R.id.iconFavorites);
        iconProfile = findViewById(R.id.iconProfile);
        iconCart = findViewById(R.id.iconCart);

        textHome = findViewById(R.id.textHome);
        textSearch = findViewById(R.id.textSearch);
        textFavorites = findViewById(R.id.textFavorites);
        textProfile = findViewById(R.id.textProfile);
        textCart = findViewById(R.id.textCart);
    }

    private void initFragments() {
        if (homeFragment == null) homeFragment = new HomeFragment();
        if (searchFragment == null) searchFragment = new SearchFragment();
        if (cartFragment == null) cartFragment = new CartFragment();
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

        menuCart.setOnClickListener(v -> {
            if (currentMenuIndex != 2) {
                updateMenuIcons(2);
                loadFragment(cartFragment, 2);
            }
        });


        menuFavorites.setOnClickListener(v -> {
            if (currentMenuIndex != 3) {
                updateMenuIcons(3);
                loadFragment(favoritesFragment, 3);
            }
        });

        menuProfile.setOnClickListener(v -> {
            if (currentMenuIndex != 4) {
                updateMenuIcons(4);
                loadFragment(profileFragment, 4);
            }
        });
    }

    private void updateMenuIcons(int selectedIndex) {
        // Reset tất cả
        iconHome.setImageResource(R.drawable.ic_home);
        iconSearch.setImageResource(R.drawable.ic_search);
        iconFavorites.setImageResource(R.drawable.ic_favorite);
        iconProfile.setImageResource(R.drawable.ic_account);
        iconCart.setImageResource(R.drawable.ic_cart);

        textHome.setTextColor(getColor(R.color.text_color_unselected));
        textSearch.setTextColor(getColor(R.color.text_color_unselected));
        textFavorites.setTextColor(getColor(R.color.text_color_unselected));
        textProfile.setTextColor(getColor(R.color.text_color_unselected));
        textCart.setTextColor(getColor(R.color.text_color_unselected));

        // Reset height
        textHome.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        textSearch.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        textFavorites.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        textProfile.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        textCart.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;

        switch (selectedIndex) {
            case 0:
                iconHome.setImageResource(R.drawable.ic_home_selected);
                textHome.setTextColor(getColor(R.color.text_color_selected));
                textHome.getLayoutParams().height = dpToPx(32);
                break;
            case 1:
                iconSearch.setImageResource(R.drawable.ic_search_selected);
                textSearch.setTextColor(getColor(R.color.text_color_selected));
                textSearch.getLayoutParams().height = dpToPx(32);
                break;
            case 2:
                iconCart.setImageResource(R.drawable.ic_cart_selected);
                textCart.setTextColor(getColor(R.color.text_color_selected));
                textCart.getLayoutParams().height = dpToPx(32);
                break;
            case 3:
                iconFavorites.setImageResource(R.drawable.ic_favorite_selected);
                textFavorites.setTextColor(getColor(R.color.text_color_selected));
                textFavorites.getLayoutParams().height = dpToPx(32);
                break;
            case 4:
                iconProfile.setImageResource(R.drawable.ic_account_selected);
                textProfile.setTextColor(getColor(R.color.text_color_selected));
                textProfile.getLayoutParams().height = dpToPx(32);
                break;
        }

        // Áp dụng thay đổi
        textHome.requestLayout();
        textSearch.requestLayout();
        textFavorites.requestLayout();
        textProfile.requestLayout();
        textCart.requestLayout();

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

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
