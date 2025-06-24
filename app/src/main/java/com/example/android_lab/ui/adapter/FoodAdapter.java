package com.example.android_lab.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.android_lab.R;
import com.example.android_lab.models.Food;
import com.example.android_lab.ui.user.FoodDetailActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;
import java.util.Objects;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {
    private final Context context;
    private final List<Food> foodList;

    public FoodAdapter(Context context, List<Food> foodList) {
        this.context = context;
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        holder.bind(context, foodList.get(position));
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    static class FoodViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFood;
        TextView tvName, tvPrice;
        Button btnAddToCart;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.imgFood);
            tvName = itemView.findViewById(R.id.tvFoodName);
            tvPrice = itemView.findViewById(R.id.tvFoodPrice);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }

        void bind(Context context, Food food) {
            tvName.setText(food.getName());
            tvPrice.setText(String.format("%,.0f₫", food.getPrice()));
            Glide.with(context).load(food.getImageUrl()).into(imgFood);

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
                            .addOnSuccessListener(unused -> Toast.makeText(context, "Đã thêm vào giỏ", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(context, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());

                }).addOnFailureListener(e ->
                        Toast.makeText(context, "Lỗi đọc giỏ hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            });

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, FoodDetailActivity.class);
                intent.putExtra("food", food);
                context.startActivity(intent);
            });
        }
    }
}
