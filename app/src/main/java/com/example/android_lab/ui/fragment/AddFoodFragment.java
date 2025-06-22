package com.example.android_lab.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.android_lab.R;
import com.example.android_lab.helps.ImageHelper;
import com.example.android_lab.models.Food;
import com.example.android_lab.utils.ImageUploader;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddFoodFragment extends Fragment {

    private EditText etName, etPrice;
    private Button btnAdd, btnPickImage;
    private ImageView imgPreview;
    private Uri imageUri;
    private boolean isUploading = false;

    private DatabaseReference foodRef;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_food, container, false);
        initViews(view);
        initFirebase();
        initImagePicker();

        btnPickImage.setOnClickListener(v -> ImageHelper.openGallery(imagePickerLauncher));
        btnAdd.setOnClickListener(v -> addFood());

        return view;
    }

    private void initViews(View view) {
        etName = view.findViewById(R.id.etFoodName);
        etPrice = view.findViewById(R.id.etFoodPrice);
        btnAdd = view.findViewById(R.id.btnAddFood);
        btnPickImage = view.findViewById(R.id.btnPickImage);
        imgPreview = view.findViewById(R.id.imgPreview);
    }

    private void initFirebase() {
        foodRef = FirebaseDatabase.getInstance().getReference("foods");
    }

    private void initImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    requireActivity();
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        imgPreview.setVisibility(View.VISIBLE);
                        imgPreview.setImageURI(imageUri);
                    }
                }
        );
    }

    private void addFood() {
        if (isUploading) {
            showToast("Đang xử lý, vui lòng chờ...");
            return;
        }

        String name = etName.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();

        if (!validateInput(name, priceStr, imageUri)) return;

        isUploading = true;
        btnAdd.setEnabled(false);

        double price = Double.parseDouble(priceStr);
        String foodId = foodRef.push().getKey();

        if (foodId == null) {
            showToast("Không tạo được ID món");
            isUploading = false;
            btnAdd.setEnabled(true);
            return;
        }

        Food food = new Food(foodId, name, price, "", true);

        ImageUploader.uploadImage(imageUri, foodId, new ImageUploader.UploadCallback() {
            @Override
            public void onSuccess(String downloadUrl) {
                if (!isAdded()) return;
                food.setImageUrl(downloadUrl);
                saveFoodData(food);
            }

            @Override
            public void onFailure(Exception e) {
                if (!isAdded()) return;
                isUploading = false;
                btnAdd.setEnabled(true);
                showToast("Lỗi upload ảnh: " + e.getMessage());
            }
        });
    }

    private boolean validateInput(String name, String priceStr, Uri imageUri) {
        if (name.isEmpty() || priceStr.isEmpty() || imageUri == null) {
            showToast("Vui lòng nhập đầy đủ và chọn ảnh");
            return false;
        }

        try {
            Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            showToast("Giá không hợp lệ");
            return false;
        }

        return true;
    }

    private void saveFoodData(Food food) {
        foodRef.child(food.getId()).setValue(food)
                .addOnSuccessListener(unused -> {
                    if (!isAdded()) return;
                    showToast("Thêm món thành công");
                    // Reset fields
                    etName.setText("");
                    etPrice.setText("");
                    imgPreview.setImageURI(null);
                    imgPreview.setVisibility(View.GONE);
                    imageUri = null;
                    isUploading = false;
                    btnAdd.setEnabled(true);
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;
                    showToast("Lỗi thêm món: " + e.getMessage());
                    isUploading = false;
                    btnAdd.setEnabled(true);
                });
    }

    private void showToast(String msg) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
