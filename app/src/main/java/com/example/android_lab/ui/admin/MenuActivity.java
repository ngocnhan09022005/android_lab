package com.example.android_lab.ui.admin;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvMenuFood;
    private ProgressBar progressBar;
    private FoodAdapter foodAdapter;
    private Button btnBack;
    private final List<Food> foodList = new ArrayList<>();
    private final DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference("foods");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_admin);

        initViews();
        setupRecyclerView();
        loadFoodData();

        btnBack.setOnClickListener(v -> finish());
    }

    private void initViews() {
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        rvMenuFood = findViewById(R.id.rvMenuFood);
        progressBar = findViewById(R.id.progressBar);
        swipeRefreshLayout.setOnRefreshListener(this::loadFoodData);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupRecyclerView() {
        foodAdapter = new FoodAdapter(this, foodList);
        rvMenuFood.setLayoutManager(new LinearLayoutManager(this));
        rvMenuFood.setAdapter(foodAdapter);
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
                        food.setId(child.getKey());
                        foodList.add(food);
                    }
                }
                foodAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MenuActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
