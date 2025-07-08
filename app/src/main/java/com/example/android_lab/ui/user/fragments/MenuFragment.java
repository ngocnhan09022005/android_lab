package com.example.android_lab.ui.user.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.android_lab.R;
import com.example.android_lab.models.Product;
import com.example.android_lab.ui.adapter.ProductAdapter;
import com.google.firebase.database.*;
import java.util.*;

public class MenuFragment extends Fragment {
    private EditText etSearch;
    private ImageButton btnSearch;
    private RecyclerView rvMenu;
    private ProductAdapter productAdapter;
    private List<Product> allProducts; // Danh sách tất cả sản phẩm
    private List<Product> filteredProducts; // Danh sách hiển thị
    private DatabaseReference productsRef;
    private boolean isLoaded = false; // Biến kiểm tra xem sản phẩm đã được tải hay chưa

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        initViews(view);
        setupRecyclerView();
        if (!isLoaded) {
            loadAllProducts();
            isLoaded = true;
        }
        setupSearchView();
        return view;
    }

    private void initViews(View view) {
        etSearch = view.findViewById(R.id.etSearch);
        btnSearch = view.findViewById(R.id.btnSearch);
        rvMenu = view.findViewById(R.id.rvMenuFood);
        allProducts = new ArrayList<>();
        filteredProducts = new ArrayList<>();
        productsRef = FirebaseDatabase.getInstance().getReference("products");
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(requireContext(), filteredProducts, false);
        rvMenu.setLayoutManager(new GridLayoutManager(requireContext(), 1));
        rvMenu.setAdapter(productAdapter);
    }

    private void loadAllProducts() {
        productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allProducts.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Product product = data.getValue(Product.class);
                    if (product != null) {
                        product.setId(data.getKey());
                        allProducts.add(product);
                    }
                }
                // Hiển thị toàn bộ sản phẩm ban đầu
                filteredProducts.clear();
                filteredProducts.addAll(allProducts);
                productAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi tải sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSearchView() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString().trim());
            }
        });
        btnSearch.setOnClickListener(v -> {
            filterProducts(etSearch.getText().toString().trim());
        });
    }

    private void filterProducts(String query) {
        filteredProducts.clear();
        if (query.isEmpty()) {
            filteredProducts.addAll(allProducts);
        } else {
            for (Product p : allProducts) {
                if (p.getName() != null && p.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredProducts.add(p);
                }
            }
        }
        productAdapter.notifyDataSetChanged();
    }
}
