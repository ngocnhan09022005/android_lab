// CartFragment.java
package com.example.android_lab.ui.user.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.android_lab.R;
import com.example.android_lab.models.CartItem;
import com.example.android_lab.models.Drink;
import com.example.android_lab.models.Food;
import com.example.android_lab.ui.adapter.CartAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CartFragment extends Fragment {

    private CartAdapter adapter;
    private final List<CartItem> cartList = new ArrayList<>();
    private TextView tvTotal;

    public CartFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        RecyclerView rvCart = view.findViewById(R.id.rvCart);
        tvTotal = view.findViewById(R.id.tvTotal);
        rvCart.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new CartAdapter(cartList, this::removeItem, this::updateQuantity);
        rvCart.setAdapter(adapter);

        loadCartDataFirebase();

        return view;
    }

    private void loadCartDataFirebase() {
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("cart").child(uid);

        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String type = child.child("type").getValue(String.class);
                    if ("food".equals(type)) {
                        Food food = child.getValue(Food.class);
                        if (food != null) cartList.add(food);
                    } else if ("drink".equals(type)) {
                        Drink drink = child.getValue(Drink.class);
                        if (drink != null) cartList.add(drink);
                    }
                }
                adapter.notifyDataSetChanged();
                calculateTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi tải giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculateTotal() {
        double total = 0;
        for (CartItem item : cartList) {
            total += item.getPrice() * item.getQuantity();
        }
        tvTotal.setText(String.format("%,.0f₫", total));
    }

    private void removeItem(CartItem item) {
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        FirebaseDatabase.getInstance().getReference("cart").child(uid).child(item.getId()).removeValue();
    }

    private void updateQuantity(CartItem item, int newQty) {
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("cart").child(uid).child(item.getId());

        ref.child("quantity").setValue(newQty)
                .addOnSuccessListener(unused -> calculateTotal())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi cập nhật số lượng", Toast.LENGTH_SHORT).show());
    }

}