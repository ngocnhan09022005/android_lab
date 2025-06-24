package com.example.android_lab.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.android_lab.R;
import com.example.android_lab.models.Food;
import com.example.android_lab.ui.user.FoodDetailActivity;
import java.util.ArrayList;
import java.util.List;

public class PopularFoodAdapter extends RecyclerView.Adapter<PopularFoodAdapter.PopularFoodViewHolder> {
    private final Context context;
    private final List<Food> foodList;

    public PopularFoodAdapter(Context context) {
        this.context = context;
        this.foodList = new ArrayList<>();
    }

    public void updateData(List<Food> newList) {
        foodList.clear();
        foodList.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PopularFoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_food, parent, false);
        return new PopularFoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularFoodViewHolder holder, int position) {
        holder.bind(context, foodList.get(position));
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    static class PopularFoodViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFood;
        TextView tvName, tvPrice;
        Button btnAddToCart;

        public PopularFoodViewHolder(@NonNull View itemView) {
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

            btnAddToCart.setVisibility(View.GONE);

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, FoodDetailActivity.class);
                intent.putExtra("food", food);
                context.startActivity(intent);
            });
        }
    }
}
