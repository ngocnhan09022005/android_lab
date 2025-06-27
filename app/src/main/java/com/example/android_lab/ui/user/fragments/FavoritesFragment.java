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
import com.example.android_lab.models.Product;
import com.example.android_lab.ui.adapter.ProductAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FavoritesFragment extends Fragment {

    private RecyclerView rvFavorites;
    private TextView tvEmptyState;
    private ProductAdapter productAdapter;
    private final List<Product> favoriteList = new ArrayList<>();
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
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(requireContext(), favoriteList);
        rvFavorites.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvFavorites.setAdapter(productAdapter);

        rvFavorites.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(
                requireContext(), R.anim.layout_animation_fall_down));
    }

    private void setupFavoritesListener() {
        String uid = Objects.requireNonNull(auth.getCurrentUser()).getUid();

        favoritesListener = db.collection("favorites")
                .document(uid)
                .collection("items")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(getContext(), "Lỗi tải yêu thích", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value == null) return;

                    favoriteList.clear();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        Product product = doc.toObject(Product.class);
                        if (product != null) {
                            product.setId(doc.getId());
                            favoriteList.add(product);
                        }
                    }

                    productAdapter.notifyDataSetChanged();
                    tvEmptyState.setVisibility(favoriteList.isEmpty() ? View.VISIBLE : View.GONE);
                });
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
