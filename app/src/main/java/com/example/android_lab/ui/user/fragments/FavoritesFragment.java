package com.example.android_lab.ui.user.fragments;

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
import com.example.android_lab.models.Food;
import com.example.android_lab.ui.adapter.FoodAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

        rvFavorites.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(
            requireContext(), R.anim.layout_animation_fall_down));
    }

    private void setupFavoritesListener() {
        Toast.makeText(getContext(), "Tính năng chưa phát triển" ,Toast.LENGTH_SHORT).show();
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
