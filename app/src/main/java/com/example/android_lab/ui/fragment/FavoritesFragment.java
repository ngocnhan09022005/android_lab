package com.example.android_lab.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.example.android_lab.R;

public class FavoritesFragment extends Fragment {

    private Button btnAddFavorite;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        initViews(view);
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        btnAddFavorite = view.findViewById(R.id.btnAddFavorite);
    }

    private void setupClickListeners() {
        btnAddFavorite.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Đã thêm vào yêu thích!", Toast.LENGTH_SHORT).show();
        });
    }
}
