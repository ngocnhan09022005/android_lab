package com.example.android_lab.ui.user.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_lab.R;
import com.example.android_lab.models.Product;
import com.example.android_lab.ui.adapter.ProductAdapter;
import com.google.firebase.database.*;

import java.util.*;

public class SearchFragment extends Fragment {

    private EditText etSearch;
    private ImageButton btnSearch;
    private RecyclerView rvSearchResults;
    private ListView lvSearchHistory;

    private ProductAdapter productAdapter;
    private List<Product> productList;
    private List<String> historyList;

    private ArrayAdapter<String> historyAdapter;
    private SharedPreferences sharedPreferences;
    private static final String HISTORY_KEY = "search_history";

    private final Handler searchHandler = new Handler();
    private static final long SEARCH_DELAY = 500;

    private DatabaseReference productsRef;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        initViews(view);
        setupRecyclerView();
        setupSearchView();
        loadSearchHistory();
        return view;
    }

    private void initViews(View view) {
        etSearch = view.findViewById(R.id.etSearch);
        btnSearch = view.findViewById(R.id.btnSearch);
        rvSearchResults = view.findViewById(R.id.rvSearchResults);
        lvSearchHistory = view.findViewById(R.id.lvSearchHistory);

        productList = new ArrayList<>();
        productsRef = FirebaseDatabase.getInstance().getReference("products");

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        historyList = new ArrayList<>();
        historyAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, historyList);
        lvSearchHistory.setAdapter(historyAdapter);

        lvSearchHistory.setOnItemClickListener((parent, v, position, id) -> {
            String keyword = historyList.get(position);
            etSearch.setText(keyword);
            etSearch.setSelection(keyword.length());
            performSearch(keyword);
        });
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(requireContext(), productList);
        rvSearchResults.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvSearchResults.setAdapter(productAdapter);
    }

    private void setupSearchView() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchHandler.removeCallbacksAndMessages(null);

                String query = s.toString().trim();
                if (query.isEmpty()) {
                    rvSearchResults.setVisibility(View.GONE);
                    lvSearchHistory.setVisibility(View.VISIBLE);
                } else {
                    lvSearchHistory.setVisibility(View.GONE);
                    searchHandler.postDelayed(() -> performSearch(query), SEARCH_DELAY);
                }
            }
        });

        btnSearch.setOnClickListener(v -> {
            String query = etSearch.getText().toString().trim();
            if (!query.isEmpty()) performSearch(query);
        });
    }

    private void performSearch(String queryText) {
        saveSearchHistory(queryText);

        lvSearchHistory.setVisibility(View.GONE);
        rvSearchResults.setVisibility(View.VISIBLE);
        productList.clear();
        productAdapter.notifyDataSetChanged();

        String searchKey = queryText.substring(0, 1).toUpperCase() + queryText.substring(1);
        Query query = productsRef.orderByChild("name")
                .startAt(searchKey)
                .endAt(searchKey + "\uf8ff");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Product product = data.getValue(Product.class);
                    if (product != null) {
                        product.setId(data.getKey());
                        productList.add(product);
                    }
                }
                productAdapter.notifyDataSetChanged();
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi tải sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveSearchHistory(String keyword) {
        keyword = keyword.trim();
        if (keyword.isEmpty()) return;

        List<String> current = new ArrayList<>(getSearchHistory());
        current.remove(keyword);
        current.add(0, keyword);
        if (current.size() > 10) current = current.subList(0, 10);

        sharedPreferences.edit().putString(HISTORY_KEY, String.join(",", current)).apply();
        historyList.clear();
        historyList.addAll(current);
        historyAdapter.notifyDataSetChanged();
    }

    private List<String> getSearchHistory() {
        String raw = sharedPreferences.getString(HISTORY_KEY, "");
        if (raw.isEmpty()) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(raw.split(",")));
    }

    private void loadSearchHistory() {
        historyList.clear();
        historyList.addAll(getSearchHistory());
        historyAdapter.notifyDataSetChanged();
        lvSearchHistory.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        searchHandler.removeCallbacksAndMessages(null);
    }
}
