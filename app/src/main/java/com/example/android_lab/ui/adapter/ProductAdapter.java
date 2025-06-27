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
import com.example.android_lab.models.Product;
import com.example.android_lab.ui.user.ProductDetailActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.List;
import java.util.Objects;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final Context context;
    private final List<Product> productList;
    private final DatabaseReference cartBaseRef;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        this.cartBaseRef = FirebaseDatabase.getInstance().getReference("cart").child(uid);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        holder.bind(context, productList.get(position), cartBaseRef);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProduct;
        TextView tvName, tvPrice;
        Button btnAddToCart;

        public ProductViewHolder(View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }

        public void bind(Context context, Product product, DatabaseReference cartBaseRef) {
            tvName.setText(product.getName());
            tvPrice.setText(String.format("%,.0f₫", product.getPrice()));

            Glide.with(context)
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.placeholder_food)
                    .error(R.drawable.placeholder_food)
                    .into(imgProduct);

            btnAddToCart.setOnClickListener(v -> addToCart(context, cartBaseRef, product));

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("product", product);
                context.startActivity(intent);
            });
        }

        private void addToCart(Context context, DatabaseReference cartBaseRef, Product product) {
            if (product.getId() == null) {
                Toast.makeText(context, "Sản phẩm không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseReference cartRef = cartBaseRef.child(product.getId());

            cartRef.get().addOnSuccessListener(snapshot -> {
                int currentQty = 0;
                if (snapshot.exists()) {
                    Product existing = snapshot.getValue(Product.class);
                    currentQty = (existing != null) ? existing.getQuantity() : 0;
                }

                product.setQuantity(currentQty + 1);
                if (product.getDescription() == null) product.setDescription("Không có mô tả");
                if (product.getType() == null || product.getType().isEmpty()) product.setType("product");

                cartRef.setValue(product)
                        .addOnSuccessListener(unused ->
                                Toast.makeText(context, "Đã thêm vào giỏ", Toast.LENGTH_SHORT).show()
                        )
                        .addOnFailureListener(e ->
                                Toast.makeText(context, "Lỗi thêm giỏ: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                        );

            }).addOnFailureListener(e ->
                    Toast.makeText(context, "Lỗi đọc giỏ: " + e.getMessage(), Toast.LENGTH_SHORT).show()
            );
        }
    }
}
