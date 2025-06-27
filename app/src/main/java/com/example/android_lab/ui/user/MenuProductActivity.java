package com.example.android_lab.ui.user;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.android_lab.R;
import com.example.android_lab.models.Product;
import com.example.android_lab.ui.adapter.ProductAdapter;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class MenuProductActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerViewProduct;
    private ProgressBar progressBar;
    private ImageView btnBack;

    private final List<Product> productList = new ArrayList<>();
    private ProductAdapter productAdapter;
    private final DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("products");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_product);

        initViews();
        setupRecyclerView();
        setupListeners();
        loadProducts();
    }

    private void initViews() {
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        recyclerViewProduct = findViewById(R.id.rvMenuFood);
        progressBar = findViewById(R.id.progressBar);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(this, productList);
        recyclerViewProduct.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProduct.setAdapter(productAdapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        swipeRefreshLayout.setOnRefreshListener(this::loadProducts);
    }

    private void loadProducts() {
        progressBar.setVisibility(View.VISIBLE);
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Product product = child.getValue(Product.class);
                    if (product != null) {
                        product.setId(child.getKey());
                        productList.add(product);
                    }
                }
                productAdapter.notifyDataSetChanged();
                hideLoading();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MenuProductActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                hideLoading();
            }
        });
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
    }
}
