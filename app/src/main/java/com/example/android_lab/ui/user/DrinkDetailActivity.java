package com.example.android_lab.ui.user;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.android_lab.R;
import com.example.android_lab.models.Drink;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class DrinkDetailActivity extends AppCompatActivity {
    private ImageView imgDrink, imgBackDetail;
    private TextView tvName, tvDescription, tvPrice;
    private Drink drink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_detail);

        imgDrink = findViewById(R.id.imgDrinkDetail);
        imgBackDetail = findViewById(R.id.imgBackDetail);
        tvName = findViewById(R.id.tvDrinkNameDetail);
        tvPrice = findViewById(R.id.tvDrinkPriceDetail);
        tvDescription = findViewById(R.id.tvDrinkDescriptionDetail);
        Button btnAddToCart = findViewById(R.id.btnAddToCartDetail);

        drink = (Drink) getIntent().getSerializableExtra("drink");
        if (drink != null) {
            showDrinkDetails(drink);
        }

        btnAddToCart.setOnClickListener(v -> {
            String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("cart")
                    .child(uid).child(drink.getId());

            cartRef.get().addOnSuccessListener(snapshot -> {
                if (snapshot.exists()) {
                    // Nếu đã có => tăng quantity
                    Drink existingDrink = snapshot.getValue(Drink.class);
                    int currentQuantity = (existingDrink != null) ? existingDrink.getQuantity() : 0;
                    drink.setQuantity(currentQuantity + 1);
                } else {
                    // Nếu chưa có => set quantity = 1
                    drink.setQuantity(1);
                }

                cartRef.setValue(drink)
                        .addOnSuccessListener(unused -> cartRef.child("type").setValue("drink"))
                        .addOnSuccessListener(unused -> Toast.makeText(this, "Đã thêm vào giỏ", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());

            }).addOnFailureListener(e ->
                    Toast.makeText(this, "Lỗi đọc giỏ hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        imgBackDetail.setOnClickListener(v -> onBackPressed());
    }

    private void showDrinkDetails(Drink drink) {
        tvName.setText(drink.getName());
        tvPrice.setText(String.format("%,.0f₫", drink.getPrice()));
        tvDescription.setText(drink.getDescription());
        Glide.with(this)
                .load(drink.getImageUrl())
                .placeholder(R.drawable.placeholder_drink)
                .error(R.drawable.placeholder_drink)
                .into(imgDrink);
    }
}
