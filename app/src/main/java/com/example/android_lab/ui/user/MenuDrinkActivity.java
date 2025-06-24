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
import com.example.android_lab.models.Drink;
import com.example.android_lab.ui.adapter.DrinkAdapter;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class MenuDrinkActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvMenuDrink;
    private ProgressBar progressBar;
    private Button btnBack;

    private final List<Drink> drinkList = new ArrayList<>();
    private DrinkAdapter drinkAdapter;
    private final DatabaseReference drinkRef = FirebaseDatabase.getInstance().getReference("drinks");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_drink);

        initViews();
        setupRecyclerView();
        setupListeners();

        loadDrinkData();
    }

    private void initViews() {
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        rvMenuDrink = findViewById(R.id.rvMenuDrink);
        progressBar = findViewById(R.id.progressBar);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupRecyclerView() {
        drinkAdapter = new DrinkAdapter(this, drinkList);
        rvMenuDrink.setLayoutManager(new LinearLayoutManager(this));
        rvMenuDrink.setAdapter(drinkAdapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        swipeRefreshLayout.setOnRefreshListener(this::loadDrinkData);
    }

    private void loadDrinkData() {
        progressBar.setVisibility(View.VISIBLE);
        drinkRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                drinkList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Drink drink = child.getValue(Drink.class);
                    if (drink != null) {
                        drink.setId(child.getKey());
                        drinkList.add(drink);
                    }
                }
                drinkAdapter.notifyDataSetChanged();
                hideLoading();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MenuDrinkActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                hideLoading();
            }
        });
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
    }
}
