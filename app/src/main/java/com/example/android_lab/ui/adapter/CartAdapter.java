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
import com.example.android_lab.models.Food;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private final List<Food> cartList;
    private final OnRemoveClickListener removeClickListener;

    public interface OnRemoveClickListener {
        void onRemove(Food food);
    }

    public CartAdapter(List<Food> cartList, OnRemoveClickListener removeClickListener) {
        this.cartList = cartList;
        this.removeClickListener = removeClickListener;
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
        Food food = cartList.get(position);

        holder.tvName.setText(food.getName());
        holder.tvPrice.setText(String.format("%,.0f₫", food.getPrice()));
        Glide.with(holder.itemView.getContext())
                .load(food.getImageUrl())
                .into(holder.imgFood);

        holder.btnRemove.setOnClickListener(v -> removeClickListener.onRemove(food));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), com.example.android_lab.ui.FoodDetailActivity.class);
            intent.putExtra("food", food);
            holder.itemView.getContext().startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return cartList.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice;
        ImageView imgFood, btnRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvFoodName);
            tvPrice = itemView.findViewById(R.id.tvFoodPrice);
            imgFood = itemView.findViewById(R.id.imgFood);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}
