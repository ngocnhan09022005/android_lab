package com.example.android_lab.ui.user.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.android_lab.R;
import com.example.android_lab.models.Food;
import com.example.android_lab.ui.adapter.FoodAdapter;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class SearchFragment extends Fragment {
    private EditText etSearch;
    private ImageButton btnSearch;
    private RecyclerView rvSearchResults;
    private FoodAdapter foodAdapter;
    private List<Food> foodList;
    private DatabaseReference foodsRef, drinksRef;
    private Handler searchHandler;
    private static final long SEARCH_DELAY = 500;

    private ListView lvSearchHistory;
    private List<String> historyList;
    private ArrayAdapter<String> historyAdapter;
    private SharedPreferences sharedPreferences;
    private static final String HISTORY_KEY = "search_history";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchHandler = new Handler();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        initViews(view);
        setupRecyclerView();
        setupSearchView();
        return view;
    }

    private void initViews(View view) {
        etSearch = view.findViewById(R.id.etSearch);
        btnSearch = view.findViewById(R.id.btnSearch);
        rvSearchResults = view.findViewById(R.id.rvSearchResults);
        lvSearchHistory = view.findViewById(R.id.lvSearchHistory);

        foodList = new ArrayList<>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        foodsRef = dbRef.child("foods");
        drinksRef = dbRef.child("drinks");

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        historyList = new ArrayList<>(getSearchHistory());
        historyAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, historyList);
        lvSearchHistory.setAdapter(historyAdapter);

        lvSearchHistory.setOnItemClickListener((parent, v, position, id) -> {
            String keyword = historyList.get(position);
            etSearch.setText(keyword);
            performSearch(keyword);
        });
    }


    private void setupRecyclerView() {
        foodAdapter = new FoodAdapter(requireContext(), foodList);
        rvSearchResults.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvSearchResults.setAdapter(foodAdapter);
    }

    private void setupSearchView() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchHandler.removeCallbacksAndMessages(null);
                searchHandler.postDelayed(() -> performSearch(s.toString().trim()), SEARCH_DELAY);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnSearch.setOnClickListener(v -> performSearch(etSearch.getText().toString().trim()));
    }

    private void performSearch(String queryText) {
        if (queryText.isEmpty()) {
            foodList.clear();
            foodAdapter.notifyDataSetChanged();
            return;
        }

        saveSearchHistory(queryText); // Lưu từ khóa mới

        lvSearchHistory.setVisibility(View.GONE); // Ẩn history sau khi tìm
        rvSearchResults.setVisibility(View.VISIBLE);

        String searchKey = queryText.substring(0, 1).toUpperCase() + queryText.substring(1);
        Query foodQuery = foodsRef.orderByChild("name").startAt(searchKey).endAt(searchKey + "\uf8ff");
        Query drinkQuery = drinksRef.orderByChild("name").startAt(searchKey).endAt(searchKey + "\uf8ff");

        foodList.clear();

        foodQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Food item = data.getValue(Food.class);
                    if (item != null) foodList.add(item);
                }

                drinkQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            Food item = data.getValue(Food.class);
                            if (item != null) foodList.add(item);
                        }
                        foodAdapter.notifyDataSetChanged();
                    }

                    @Override public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Lỗi tải drinks", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi tải foods", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveSearchHistory(String keyword) {
        keyword = keyword.trim();
        if (keyword.isEmpty()) return;

        List<String> currentHistory = getSearchHistory();
        if (currentHistory.contains(keyword)) currentHistory.remove(keyword);
        currentHistory.add(0, keyword); // thêm mới lên đầu

        if (currentHistory.size() > 10) {
            currentHistory = currentHistory.subList(0, 10);
        }

        sharedPreferences.edit().putString(HISTORY_KEY, String.join(",", currentHistory)).apply();
        historyList.clear();
        historyList.addAll(currentHistory);
        historyAdapter.notifyDataSetChanged();
    }

    private List<String> getSearchHistory() {
        String historyString = sharedPreferences.getString(HISTORY_KEY, "");
        List<String> list = new ArrayList<>();
        if (!historyString.isEmpty()) {
            String[] items = historyString.split(",");
            for (String item : items) {
                list.add(item.trim());
            }
        }
        return list;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        searchHandler.removeCallbacksAndMessages(null);
    }
}
