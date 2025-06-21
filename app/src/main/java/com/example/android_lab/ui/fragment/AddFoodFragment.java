package com.example.android_lab.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button; // ✅ Đúng import
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.android_lab.R;
import com.example.android_lab.data.model.Food;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddFoodFragment extends Fragment {
    private EditText etName, etPrice, etImageUrl;
    private Button btnAdd;
    private DatabaseReference foodRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_food, container, false);

        etName = view.findViewById(R.id.etFoodName);
        etPrice = view.findViewById(R.id.etFoodPrice);
        etImageUrl = view.findViewById(R.id.etFoodImageUrl);
        btnAdd = view.findViewById(R.id.btnAddFood);

        foodRef = FirebaseDatabase.getInstance().getReference("foods");

        btnAdd.setOnClickListener(v -> addFoodToFirebase());

        return view;
    }

    private void addFoodToFirebase() {
        String name = etName.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();

        if (name.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);
        String id = foodRef.push().getKey();

        Food food = new Food(id, name, price, imageUrl, true);

        foodRef.child(id).setValue(food)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(getContext(), "Thêm thành công", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
