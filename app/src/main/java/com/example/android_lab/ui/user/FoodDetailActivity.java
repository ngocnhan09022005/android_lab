package com.example.android_lab.ui.user;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.android_lab.R;
import com.example.android_lab.models.Food;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class FoodDetailActivity extends AppCompatActivity {

    private ImageView imgFood;
    private TextView tvName, tvPrice, tvDescription, tvQuantity;
    private Food food;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        imgFood = findViewById(R.id.imgFoodDetail);
        tvName = findViewById(R.id.tvFoodNameDetail);
        tvPrice = findViewById(R.id.tvFoodPriceDetail);
        tvDescription = findViewById(R.id.tvFoodDescriptionDetail);
        tvQuantity = findViewById(R.id.tvFoodQuantityDetail);
        Button btnAddToCart = findViewById(R.id.btnAddToCartDetail);

        food = (Food) getIntent().getSerializableExtra("food");

        if (food != null) {
            showFoodDetails(food);
        }

        btnAddToCart.setOnClickListener(v -> {
            String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("cart")
                    .child(uid).child(food.getId());

            cartRef.get().addOnSuccessListener(snapshot -> {
                if (snapshot.exists()) {
                    Food existingFood = snapshot.getValue(Food.class);
                    int currentQuantity = (existingFood != null) ? existingFood.getQuantity() : 0;
                    food.setQuantity(currentQuantity + 1);
                } else {
                    food.setQuantity(1);
                }

                cartRef.setValue(food)
                        .addOnSuccessListener(unused -> cartRef.child("type").setValue("food"))
                        .addOnSuccessListener(unused -> Toast.makeText(this, "Đã thêm vào giỏ", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());

            }).addOnFailureListener(e ->
                    Toast.makeText(this, "Lỗi đọc giỏ hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }

    private void showFoodDetails(Food food) {
        tvName.setText(food.getName());
        tvPrice.setText(String.format("%,.0f₫", food.getPrice()));
        tvDescription.setText(food.getDescription());
        tvQuantity.setText(String.valueOf(food.getQuantity()));

        Glide.with(this)
                .load(food.getImageUrl())
                .placeholder(R.drawable.placeholder_food)
                .error(R.drawable.placeholder_food)
                .into(imgFood);
    }
}
