package com.example.android_lab.ui.admin.fragments;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.android_lab.R;
import com.example.android_lab.models.Product;
import com.example.android_lab.utils.ImageHelper;
import com.example.android_lab.utils.ImageUploader;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddProductFragment extends Fragment {

    private EditText etName, etPrice, etDescription, quantity;
    private RadioGroup rgType;
    private RadioButton rbFood, rbDrink;
    private Switch btnSwitch;
    private Button btnAdd, btnPickImage, btnViewProductList;
    private ImageView imgPreview;
    private Uri imageUri;
    private boolean isUploading = false;

    private DatabaseReference productRef;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_product, container, false);
        initViews(view);
        initFirebase();
        initImagePicker();

        btnPickImage.setOnClickListener(v -> ImageHelper.openGallery(imagePickerLauncher));
        btnAdd.setOnClickListener(v -> addProduct());
        btnViewProductList.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), com.example.android_lab.ui.admin.MenuProductAdminActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void initViews(View view) {
        etName = view.findViewById(R.id.etProductName);
        etPrice = view.findViewById(R.id.etProductPrice);
        etDescription = view.findViewById(R.id.etProductDescription);
        quantity = view.findViewById(R.id.etProductQuantity);
        btnAdd = view.findViewById(R.id.btnAddProduct);
        btnSwitch = view.findViewById(R.id.btnSwitch);
        btnPickImage = view.findViewById(R.id.btnPickImage);
        imgPreview = view.findViewById(R.id.imgPreview);
        btnViewProductList = view.findViewById(R.id.btnViewProductList);
        rgType = view.findViewById(R.id.rgType);
        rbFood = view.findViewById(R.id.rbFood);
        rbDrink = view.findViewById(R.id.rbDrink);

    }

    private void initFirebase() {
        productRef = FirebaseDatabase.getInstance().getReference("products");
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

    private void addProduct() {
        if (isUploading) {
            showToast("Đang xử lý, vui lòng chờ...");
            return;
        }

        String name = etName.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String typee = rbFood.isChecked() ? "food" : "drink";
        boolean isPopular = btnSwitch.isChecked(); // vẫn giữ như cũ

        boolean switchValue = btnSwitch.isChecked();
        int quantityStr = Integer.parseInt(quantity.getText().toString().trim());

        if (!validateInput(name, priceStr, imageUri)) return;

        isUploading = true;
        btnAdd.setEnabled(false);

        double price = Double.parseDouble(priceStr);
        String productId = productRef.push().getKey();

        if (productId == null) {
            showToast("Không tạo được ID món");
            isUploading = false;
            btnAdd.setEnabled(true);
            return;
        }

        Product product = new Product(productId, name, price, "", isPopular, description, quantityStr, typee);

        ImageUploader.uploadImageProduct(imageUri, productId, new ImageUploader.UploadCallback() {
            @Override
            public void onSuccess(String downloadUrl) {
                if (!isAdded()) return;
                product.setImageUrl(downloadUrl);
                saveProductData(product);
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

    private void saveProductData(Product product) {
        productRef.child(product.getId()).setValue(product)
                .addOnSuccessListener(unused -> {
                    if (!isAdded()) return;
                    showToast("Thêm san pham thành công");
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
