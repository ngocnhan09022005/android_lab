package com.example.android_lab.ui.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.android_lab.R;
import com.example.android_lab.models.CartItem;
import com.example.android_lab.models.Food;
import com.example.android_lab.models.Drink;
import com.example.android_lab.ui.user.FoodDetailActivity;
import com.example.android_lab.ui.user.DrinkDetailActivity;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    public interface OnRemoveClickListener {
        void onRemove(CartItem item);
    }

    public interface OnQuantityChangeListener {
        void onQuantityChange(CartItem item, int newQuantity);
    }

    private final List<CartItem> cartList;
    private final OnRemoveClickListener removeClickListener;
    private final OnQuantityChangeListener quantityChangeListener;

    public CartAdapter(List<CartItem> cartList,
                       OnRemoveClickListener removeClickListener,
                       OnQuantityChangeListener quantityChangeListener) {
        this.cartList = cartList;
        this.removeClickListener = removeClickListener;
        this.quantityChangeListener = quantityChangeListener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartList.get(position);

        holder.tvName.setText(item.getName());
        holder.tvPrice.setText(String.format("%,.0f₫", item.getPrice()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));

        Glide.with(holder.itemView.getContext())
                .load(item.getImageUrl())
                .into(holder.imgFood);

        holder.btnRemove.setOnClickListener(v -> removeClickListener.onRemove(item));

        holder.btnIncrease.setOnClickListener(v -> {
            int newQty = item.getQuantity() + 1;
            quantityChangeListener.onQuantityChange(item, newQty);
        });

        holder.btnDecrease.setOnClickListener(v -> {
            int currentQty = item.getQuantity();
            if (currentQty > 1) {
                int newQty = currentQty - 1;
                quantityChangeListener.onQuantityChange(item, newQty);
            } else {
                removeClickListener.onRemove(item);
            }
        });


        holder.itemView.setOnClickListener(v -> {
            Intent intent;
            if (item instanceof Food) {
                Food food = (Food) item;

                // Nếu thiếu mô tả, gán mặc định để tránh lỗi
                if (food.getDescription() == null || food.getDescription().isEmpty()) {
                    food.setDescription("Không có mô tả");
                }

                intent = new Intent(holder.itemView.getContext(), FoodDetailActivity.class);
                intent.putExtra("food", food);
            } else {
                intent = new Intent(holder.itemView.getContext(), DrinkDetailActivity.class);
                intent.putExtra("drink", (Drink) item);
            }
            holder.itemView.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvQuantity;
        ImageView imgFood, btnRemove, btnIncrease, btnDecrease;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvFoodName);
            tvPrice = itemView.findViewById(R.id.tvFoodPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            imgFood = itemView.findViewById(R.id.imgFood);
            btnRemove = itemView.findViewById(R.id.btnRemove);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
        }
    }
}
