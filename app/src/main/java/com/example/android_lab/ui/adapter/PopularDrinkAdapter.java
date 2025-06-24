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
import com.example.android_lab.models.Drink;
import com.example.android_lab.ui.user.DrinkDetailActivity;
import java.util.ArrayList;
import java.util.List;

public class PopularDrinkAdapter extends RecyclerView.Adapter<PopularDrinkAdapter.PopularDrinkViewHolder>{
    private final Context context;
    private final List<Drink> drinkList;

    public PopularDrinkAdapter(Context context) {
        this.context = context;
        this.drinkList = new ArrayList<>();
    }

    public void updateData(List<Drink> newList) {
        drinkList.clear();
        drinkList.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PopularDrinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_drink, parent, false);
        return new PopularDrinkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularDrinkViewHolder holder, int position) {
        holder.bind(context, drinkList.get(position));
    }

    @Override
    public int getItemCount() {
        return drinkList.size();
    }
    static class PopularDrinkViewHolder extends RecyclerView.ViewHolder {
        ImageView imgDrink;
        TextView tvName, tvPrice;
        Button btnAddToCart;

        public PopularDrinkViewHolder(@NonNull View itemView) {
            super(itemView);
            imgDrink = itemView.findViewById(R.id.imgDrink);
            tvName = itemView.findViewById(R.id.tvDrinkName);
            tvPrice = itemView.findViewById(R.id.tvDrinkPrice);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }

        void bind(Context context, Drink drink) {
            tvName.setText(drink.getName());
            tvPrice.setText(String.format("%,.0f₫", drink.getPrice()));
            Glide.with(context).load(drink.getImageUrl()).into(imgDrink);

            btnAddToCart.setVisibility(View.GONE);

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, DrinkDetailActivity.class);
                intent.putExtra("drink", drink);
                context.startActivity(intent);
            });
        }
    }
}
