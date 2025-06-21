package com.example.android_lab.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.android_lab.R;
import com.example.android_lab.data.model.Food;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;

public class PopularFoodAdapter extends RecyclerView.Adapter<PopularFoodAdapter.PopularFoodViewHolder> {
    private Context context;
    private List<Food> foodList;
    private OnItemClickListener listener;
    private DatabaseReference databaseRef;

    public interface OnItemClickListener {
        void onItemClick(Food food);
    }

    public PopularFoodAdapter(Context context) {
        this.context = context;
        this.foodList = new ArrayList<>();
        this.databaseRef = FirebaseDatabase.getInstance().getReference("foods");
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public PopularFoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_popular_food, parent, false);
        return new PopularFoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularFoodViewHolder holder, int position) {
        Food food = foodList.get(position);
        holder.bind(food, listener);
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public void updateData(List<Food> newFoodList) {
        foodList.clear();
        foodList.addAll(newFoodList);
        notifyDataSetChanged();
    }

    public static class PopularFoodViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgFood;
        private TextView txtFoodName;
        private TextView txtFoodPrice;

        public PopularFoodViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.imgFood);
            txtFoodName = itemView.findViewById(R.id.tvFoodName);
            txtFoodPrice = itemView.findViewById(R.id.tvFoodPrice);
        }

        public void bind(final Food food, final OnItemClickListener listener) {
            txtFoodName.setText(food.getName());
            txtFoodPrice.setText(String.format("$%.2f", food.getPrice()));

            if (food.getImageUrl() != null && !food.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(food.getImageUrl())
                        .placeholder(R.drawable.placeholder_food)
                        .error(R.drawable.error_food)
                        .into(imgFood);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(food);
                }
            });
        }
    }
}
