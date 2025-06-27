package com.example.android_lab.ui.user.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.android_lab.R;
import com.example.android_lab.ui.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private LinearLayout layoutName, layoutAddress, layoutEmail, layoutPhone, layoutPassword;
    private TextView btnLogout;
    private SwipeRefreshLayout swipeRefreshLayout;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

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
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        btnLogout = view.findViewById(R.id.btnLogout);

        layoutName = view.findViewById(R.id.layoutName);
        layoutAddress = view.findViewById(R.id.layoutAddress);
        layoutEmail = view.findViewById(R.id.layoutEmail);
        layoutPhone = view.findViewById(R.id.layoutPhone);
        layoutPassword = view.findViewById(R.id.layoutPassword);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Gán label thủ công
        setLabel(layoutName, "Họ tên:");
        setLabel(layoutAddress, "Địa chỉ:");
        setLabel(layoutEmail, "Email:");
        setLabel(layoutPhone, "Số điện thoại:");
        setLabel(layoutPassword, "Mật khẩu:");
    }

    private void setLabel(LinearLayout layout, String labelText) {
        TextView label = layout.findViewById(R.id.label);
        label.setText(labelText);
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
                            setValue(layoutName, documentSnapshot.getString("name"));
                            setValue(layoutAddress, documentSnapshot.getString("address"));
                            setValue(layoutEmail, documentSnapshot.getString("email"));
                            setValue(layoutPhone, documentSnapshot.getString("phone"));
                            setValue(layoutPassword, "************"); // Ẩn mật khẩu
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    })
                    .addOnFailureListener(e -> swipeRefreshLayout.setRefreshing(false));
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void logout() {
        auth.signOut();
        startActivity(new Intent(requireActivity(), LoginActivity.class));
        requireActivity().finish();
    }

    private void setLabelFromTag(LinearLayout layout) {
        TextView label = layout.findViewById(R.id.label);
        if (layout.getTag() != null) {
            label.setText(layout.getTag().toString());
        }
    }

    private void setValue(LinearLayout layout, String value) {
        TextView valueText = layout.findViewById(R.id.value);
        valueText.setText(value != null ? value : "Không có");
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserProfile();
    }
}
