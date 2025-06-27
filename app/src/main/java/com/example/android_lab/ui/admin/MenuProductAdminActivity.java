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
import com.example.android_lab.models.Product;
import com.example.android_lab.ui.adapter.ProductAdapter;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class MenuProductAdminActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvMenuProduct;
    private ProgressBar progressBar;
    private Button btnBack;

    private ProductAdapter productAdapter;
    private final List<Product> productList = new ArrayList<>();
    private final DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("products");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_admin);

        initViews();
        setupRecyclerView();
        loadProductData();

        btnBack.setOnClickListener(v -> finish());
    }

    private void initViews() {
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        rvMenuProduct = findViewById(R.id.rvMenuFood); // giữ nguyên ID layout XML
        progressBar = findViewById(R.id.progressBar);
        btnBack = findViewById(R.id.btnAdminBack);
        swipeRefreshLayout.setOnRefreshListener(this::loadProductData);
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(this, productList);
        rvMenuProduct.setLayoutManager(new LinearLayoutManager(this));
        rvMenuProduct.setAdapter(productAdapter);
    }

    private void loadProductData() {
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
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MenuProductAdminActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
