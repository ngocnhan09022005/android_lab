package com.example.android_lab.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.example.android_lab.R;
public class ProfileFragment extends Fragment {

    private Button btnEditProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initViews(view);
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
    }

    private void setupClickListeners() {
        btnEditProfile.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Chức năng chỉnh sửa thông tin", Toast.LENGTH_SHORT).show();
        });
    }
}