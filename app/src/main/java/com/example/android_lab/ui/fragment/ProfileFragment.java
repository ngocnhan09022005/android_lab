package com.example.android_lab.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.android_lab.R;
import com.example.android_lab.ui.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {
    private TextView tvUsername;
    private TextView tvEmail;
    private Button btnLogout;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initViews(view);
        setupLogoutButton();
        setupSwipeToRefresh();
        loadUserProfile();
        return view;
    }

    private void initViews(View view) {
        tvUsername = view.findViewById(R.id.tvUsername);
        tvEmail = view.findViewById(R.id.tvEmail);
        btnLogout = view.findViewById(R.id.btnLogout);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }


    private void setupLogoutButton() {
        btnLogout.setOnClickListener(v -> logout());
    }

    private void setupSwipeToRefresh() {
        swipeRefreshLayout.setOnRefreshListener(this::loadUserProfile);
    }
    private void loadUserProfile() {
        swipeRefreshLayout.setRefreshing(true);
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            db.collection("users")
                    .document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String username = documentSnapshot.getString("name");
                            String email = documentSnapshot.getString("email");

                            tvUsername.setText(username);
                            tvEmail.setText(email);
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    })
                    .addOnFailureListener(e -> {
                        swipeRefreshLayout.setRefreshing(false);
                        // Optionally show error
                    });
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void logout() {
        auth.signOut();
        startActivity(new Intent(requireActivity(), LoginActivity.class));
        requireActivity().finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserProfile();
    }
}
