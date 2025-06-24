package com.example.android_lab.ui.user.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.android_lab.R;
import com.example.android_lab.models.Drink;
import com.example.android_lab.models.Food;
import com.example.android_lab.ui.adapter.PopularDrinkAdapter;
import com.example.android_lab.ui.user.MenuDrinkActivity;
import com.example.android_lab.ui.user.MenuFoodActivity;
import com.example.android_lab.ui.adapter.BannerAdapter;
import com.example.android_lab.ui.adapter.PopularFoodAdapter;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {
    private SwipeRefreshLayout swipeRefreshLayout;
    private ViewPager2 bannerViewPager;
    private RecyclerView rvPopularFood, rvPopularDrink;
    private ProgressBar progressBar;
    private PopularFoodAdapter popularFoodAdapter;
    private PopularDrinkAdapter popularDrinkAdapter;
    private DatabaseReference foodRef, drinkRef;
    private Handler handler;
    private Runnable bannerRunnable;
    private TextView viewMenuFood, viewMenuDrink;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadPopularFoods();
            loadPopularDrinks();
        });

        initializeViews(view);
        setupBanner();
        setupRecyclerViews();
        loadPopularFoods();
        loadPopularDrinks();

        viewMenuFood.setOnClickListener(v -> startActivity(new Intent(requireContext(), MenuFoodActivity.class)));
        viewMenuDrink.setOnClickListener(v -> startActivity(new Intent(requireContext() , MenuDrinkActivity.class)));
        return view;
    }

    private void initializeViews(View view) {
        bannerViewPager = view.findViewById(R.id.bannerViewPager);
        rvPopularFood = view.findViewById(R.id.rvPopularFood);
        rvPopularDrink = view.findViewById(R.id.rvPopularDrink);
        progressBar = view.findViewById(R.id.progressBar);
        viewMenuFood = view.findViewById(R.id.tvViewMenuFood);
        viewMenuDrink = view.findViewById(R.id.tvViewMenuDrink);

        foodRef = FirebaseDatabase.getInstance().getReference("foods");
        drinkRef = FirebaseDatabase.getInstance().getReference("drinks");
        handler = new Handler(Looper.getMainLooper());
    }

    private void setupBanner() {
        List<Integer> bannerImages = Arrays.asList(
                R.drawable.banner1,
                R.drawable.banner2,
                R.drawable.banner3
        );

        BannerAdapter bannerAdapter = new BannerAdapter(requireContext(), bannerImages);
        bannerViewPager.setAdapter(bannerAdapter);
        bannerViewPager.setOffscreenPageLimit(3);

        bannerRunnable = new Runnable() {
            @Override
            public void run() {
                if (isAdded() && !isDetached()) {
                    int currentItem = bannerViewPager.getCurrentItem();
                    int totalItems = bannerAdapter.getItemCount();
                    int nextItem = (currentItem + 1) % totalItems;
                    bannerViewPager.setCurrentItem(nextItem, true);
                    handler.postDelayed(this, 3000);
                }
            }
        };
    }

    private void setupRecyclerViews() {
        // Food
        popularFoodAdapter = new PopularFoodAdapter(requireContext());
        rvPopularFood.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvPopularFood.setAdapter(popularFoodAdapter);

        // Drink
        popularDrinkAdapter = new PopularDrinkAdapter(requireContext());
        rvPopularDrink.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvPopularDrink.setAdapter(popularDrinkAdapter);
    }

    private void loadPopularFoods() {
        if (!isAdded()) return;
        progressBar.setVisibility(View.VISIBLE);

        Query query = foodRef.orderByChild("popular").equalTo(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Food> foods = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Food food = child.getValue(Food.class);
                    if (food != null) {
                        food.setId(child.getKey());
                        foods.add(food);
                    }
                }
                popularFoodAdapter.updateData(foods);
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(requireContext(), "Lỗi tải món ăn", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPopularDrinks() {
        if (!isAdded()) return;

        Query query = drinkRef.orderByChild("popular").equalTo(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Drink> drinks = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Drink drink = child.getValue(Drink.class);
                    if (drink != null) {
                        drink.setId(child.getKey());
                        drinks.add(drink);
                    }
                }
                popularDrinkAdapter.updateData(drinks);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Lỗi tải đồ uống", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(bannerRunnable, 3000);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(bannerRunnable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(bannerRunnable);
    }
}

