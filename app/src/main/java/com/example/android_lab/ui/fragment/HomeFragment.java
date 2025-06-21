package com.example.android_lab.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.android_lab.R;
import com.example.android_lab.data.model.Food;
import com.example.android_lab.ui.adapter.BannerAdapter;
import com.example.android_lab.ui.adapter.PopularFoodAdapter;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {
    private SwipeRefreshLayout swipeRefreshLayout;
    private ViewPager2 bannerViewPager;
    private RecyclerView rvPopularFood;
    private ProgressBar progressBar;
    private PopularFoodAdapter popularFoodAdapter;
    private DatabaseReference databaseRef;
    private Handler handler;
    private Runnable bannerRunnable;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadPopularFoods();  // Reload khi vuốt
        });

        initializeViews(view);
        setupBanner();
        setupRecyclerView();
        loadPopularFoods();
        return view;
    }

    private void initializeViews(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        bannerViewPager = view.findViewById(R.id.bannerViewPager);
        rvPopularFood = view.findViewById(R.id.rvPopularFood);
        progressBar = view.findViewById(R.id.progressBar);
        databaseRef = FirebaseDatabase.getInstance().getReference("foods");
        handler = new Handler(Looper.getMainLooper());

        swipeRefreshLayout.setOnRefreshListener(this::loadPopularFoods);
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

    private void setupRecyclerView() {
        popularFoodAdapter = new PopularFoodAdapter(requireContext());
        rvPopularFood.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvPopularFood.setAdapter(popularFoodAdapter);
    }

    private void loadPopularFoods() {
        if (!isAdded()) return;

        progressBar.setVisibility(View.VISIBLE);

        Query popularFoodsQuery = databaseRef.orderByChild("popular").equalTo(true);
        popularFoodsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
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
                Toast.makeText(requireContext(), "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
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
