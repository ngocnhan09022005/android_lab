package com.example.android_lab.ui.user;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.android_lab.R;
import com.example.android_lab.models.Food;
import com.example.android_lab.ui.adapter.FoodAdapter;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class MenuFoodActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvMenuFood;
    private ProgressBar progressBar;
    private Button btnBack;

    private final List<Food> foodList = new ArrayList<>();
    private FoodAdapter foodAdapter;
    private final DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference("foods");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_food);

        initViews();
        setupRecyclerView();
        setupListeners();

        loadFoodData();
    }

    private void initViews() {
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        rvMenuFood = findViewById(R.id.rvMenuFood);
        progressBar = findViewById(R.id.progressBar);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupRecyclerView() {
        foodAdapter = new FoodAdapter(this, foodList);
        rvMenuFood.setLayoutManager(new LinearLayoutManager(this));
        rvMenuFood.setAdapter(foodAdapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        swipeRefreshLayout.setOnRefreshListener(this::loadFoodData);
    }

    private void loadFoodData() {
        progressBar.setVisibility(View.VISIBLE);
        foodRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                foodList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Food food = child.getValue(Food.class);
                    if (food != null) {
                        food.setId(child.getKey()); // Gán ID để dùng cho cart hoặc detail
                        foodList.add(food);
                    }
                }

                foodAdapter.notifyDataSetChanged();
                hideLoading();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MenuFoodActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                hideLoading();
            }
        });
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
    }
}
