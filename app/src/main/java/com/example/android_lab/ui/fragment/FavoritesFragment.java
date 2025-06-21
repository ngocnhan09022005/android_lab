package com.example.android_lab.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_lab.R;
import com.example.android_lab.data.model.Food;
import com.example.android_lab.ui.adapter.FoodAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {
    private RecyclerView rvFavorites;
    private TextView tvEmptyState;
    private FoodAdapter foodAdapter;
    private List<Food> favoritesList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ListenerRegistration favoritesListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        initViews(view);
        setupRecyclerView();
        setupFavoritesListener();
        return view;
    }

    private void initViews(View view) {
        rvFavorites = view.findViewById(R.id.rvFavorites);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        favoritesList = new ArrayList<>();
    }

    private void setupRecyclerView() {
        foodAdapter = new FoodAdapter(requireContext(), favoritesList);
        rvFavorites.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvFavorites.setAdapter(foodAdapter);

        // Add animation
        rvFavorites.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(
            requireContext(), R.anim.layout_animation_fall_down));
    }

    private void setupFavoritesListener() {
        String userId = auth.getCurrentUser().getUid();
        favoritesListener = db.collection("users")
            .document(userId)
            .collection("favorites")
            .addSnapshotListener((snapshots, e) -> {
                if (e != null) {
                    Toast.makeText(requireContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                favoritesList.clear();
                if (snapshots == null || snapshots.isEmpty()) {
                    showEmptyState();
                } else {
                    hideEmptyState();
                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                            case MODIFIED:
                                loadFoodDetails(dc.getDocument().getId());
                                break;
                            case REMOVED:
                                removeFood(dc.getDocument().getId());
                                break;
                        }
                    }
                }
            });
    }

    private void loadFoodDetails(String foodId) {
        db.collection("foods")
            .document(foodId)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Food food = documentSnapshot.toObject(Food.class);
                    if (food != null) {
                        food.setId(documentSnapshot.getId());
                        int index = getFoodIndex(food.getId());
                        if (index == -1) {
                            favoritesList.add(food);
                            foodAdapter.notifyItemInserted(favoritesList.size() - 1);
                        } else {
                            favoritesList.set(index, food);
                            foodAdapter.notifyItemChanged(index);
                        }
                    }
                }
            });
    }

    private void removeFood(String foodId) {
        int index = getFoodIndex(foodId);
        if (index != -1) {
            favoritesList.remove(index);
            foodAdapter.notifyItemRemoved(index);
            if (favoritesList.isEmpty()) {
                showEmptyState();
            }
        }
    }

    private int getFoodIndex(String foodId) {
        for (int i = 0; i < favoritesList.size(); i++) {
            if (favoritesList.get(i).getId().equals(foodId)) {
                return i;
            }
        }
        return -1;
    }

    private void showEmptyState() {
        rvFavorites.setVisibility(View.GONE);
        tvEmptyState.setVisibility(View.VISIBLE);
    }

    private void hideEmptyState() {
        rvFavorites.setVisibility(View.VISIBLE);
        tvEmptyState.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (favoritesListener != null) {
            favoritesListener.remove();
        }
        rvFavorites.setAdapter(null);
    }
}
