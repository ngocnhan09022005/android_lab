package com.example.android_lab.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import com.example.android_lab.R;

public class HomeFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        // Khởi tạo các view nếu cần
        // TextView textView = view.findViewById(R.id.textView);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Code chạy khi fragment được hiển thị
    }
}
