package com.example.android_lab.ui.user.fragments;

import android.os.Bundle;
import android.os.Handler;
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
import com.example.android_lab.models.Food;
import com.example.android_lab.ui.adapter.FoodAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private EditText etSearch;
    private ImageButton btnSearch;
    private RecyclerView rvSearchResults;
    private FoodAdapter foodAdapter;
    private List<Food> foodList;
    private FirebaseFirestore db;
    private Handler searchHandler;
    private static final long SEARCH_DELAY = 500;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        searchHandler = new Handler();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        initViews(view);
        setupRecyclerView();
        setupSearchView();
        return view;
    }

    private void initViews(View view) {
        etSearch = view.findViewById(R.id.etSearch);
        btnSearch = view.findViewById(R.id.btnSearch);
        rvSearchResults = view.findViewById(R.id.rvSearchResults);
        db = FirebaseFirestore.getInstance();
        foodList = new ArrayList<>();
    }

    private void setupRecyclerView() {
        foodAdapter = new FoodAdapter(requireContext(), foodList);
        rvSearchResults.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvSearchResults.setAdapter(foodAdapter);
    }

    private void setupSearchView() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchHandler.removeCallbacksAndMessages(null);
                searchHandler.postDelayed(() -> performSearch(s.toString()), SEARCH_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnSearch.setOnClickListener(v -> performSearch(etSearch.getText().toString()));
    }

    private void performSearch(String query) {
        if (query.isEmpty()) {
            foodList.clear();
            foodAdapter.notifyDataSetChanged();
            return;
        }

        String searchQuery = query.toLowerCase().trim();
        db.collection("foods")
            .whereGreaterThanOrEqualTo("name", searchQuery)
            .whereLessThanOrEqualTo("name", searchQuery + "\uf8ff")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                foodList.clear();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Food food = document.toObject(Food.class);
                    food.setId(document.getId());
                    foodList.add(food);
                }
                foodAdapter.notifyDataSetChanged();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(requireContext(), "Lỗi tìm kiếm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        searchHandler.removeCallbacksAndMessages(null);
        rvSearchResults.setAdapter(null);
        etSearch.removeTextChangedListener(null);
    }
}