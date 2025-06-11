package com.example.android_lab.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import com.example.android_lab.R;

public class SearchFragment extends Fragment {
    private EditText etSearch;
    private Button btnSearch;
    private TextView tvSearchResult;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        initViews(view);
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        etSearch = view.findViewById(R.id.etSearch);
        btnSearch = view.findViewById(R.id.btnSearch);
        tvSearchResult = view.findViewById(R.id.tvSearchResult);
    }

    private void setupClickListeners() {
        btnSearch.setOnClickListener(v -> {
            String searchText = etSearch.getText().toString().trim();

            if (searchText.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập từ khóa", Toast.LENGTH_SHORT).show();
                return;
            }

            performSearch(searchText);
        });
    }

    private void performSearch(String query) {
        // Giả lập kết quả tìm kiếm
        tvSearchResult.setText("Đã tìm kiếm: \"" + query + "\"\n\nKết quả:\n- Kết quả 1\n- Kết quả 2\n- Kết quả 3");

        Toast.makeText(getContext(), "Tìm kiếm thành công!", Toast.LENGTH_SHORT).show();
    }
}