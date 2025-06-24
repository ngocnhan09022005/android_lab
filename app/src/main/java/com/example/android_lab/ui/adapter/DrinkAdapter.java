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

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android_lab.R;
import com.example.android_lab.models.Drink;
import com.example.android_lab.ui.user.DrinkDetailActivity;
import com.example.android_lab.ui.user.FoodDetailActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Objects;

public class DrinkAdapter extends RecyclerView.Adapter<DrinkAdapter.DrinkViewHolder> {

    private final Context context;
    private final List<Drink> drinkList;
    public DrinkAdapter(Context context, List<Drink> drinkList) {
        this.context = context;
        this.drinkList = drinkList;
    }

    @Override
    public DrinkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_drink, parent, false);
        return new DrinkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DrinkViewHolder holder, int position) {
        holder.bind(context, drinkList.get(position));
    }

    @Override
    public int getItemCount() {
        return drinkList.size();
    }

    static class DrinkViewHolder extends RecyclerView.ViewHolder {
        ImageView imgDrink;
        TextView tvName, tvPrice;
        Button btnAddToCart;
        public DrinkViewHolder(View itemView) {
            super(itemView);
            imgDrink = itemView.findViewById(R.id.imgDrink);
            tvName = itemView.findViewById(R.id.tvDrinkName);
            tvPrice = itemView.findViewById(R.id.tvDrinkPrice);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }

        public void bind(Context context, Drink drink) {
            tvName.setText(drink.getName());
            tvPrice.setText(String.format("%,.0f₫", drink.getPrice()));

            Glide.with(context).load(drink.getImageUrl()).into(imgDrink);

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
                            .addOnSuccessListener(unused -> Toast.makeText(context, "Đã thêm vào giỏ", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(context, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());

                }).addOnFailureListener(e ->
                        Toast.makeText(context, "Lỗi đọc giỏ hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            });

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, DrinkDetailActivity.class);
                intent.putExtra("drink", drink);
                context.startActivity(intent);
            });

        }
    }
}
