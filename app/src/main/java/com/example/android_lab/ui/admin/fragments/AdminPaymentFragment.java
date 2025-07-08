package com.example.android_lab.ui.admin.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.android_lab.R;
import com.example.android_lab.models.PaymentHistoryItem;
import com.example.android_lab.ui.adapter.PaymentHistoryAdapter;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class AdminPaymentFragment extends Fragment {
    private RecyclerView rvPayments;
    private ProgressBar progressBar;
    private PaymentHistoryAdapter adapter;
    private final List<PaymentHistoryItem> paymentList = new ArrayList<>();
    private DatabaseReference paymentRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_payment, container, false);
        rvPayments = view.findViewById(R.id.rvPayments);
        progressBar = view.findViewById(R.id.progressBar);
        adapter = new PaymentHistoryAdapter(paymentList);
        rvPayments.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPayments.setAdapter(adapter);
        paymentRef = FirebaseDatabase.getInstance().getReference("payment_history");
        loadPayments();
        return view;
    }

    private void loadPayments() {
        progressBar.setVisibility(View.VISIBLE);
        paymentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                paymentList.clear();
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    for (DataSnapshot paymentSnap : userSnap.getChildren()) {
                        PaymentHistoryItem item = paymentSnap.getValue(PaymentHistoryItem.class);
                        if (item != null) paymentList.add(item);
                    }
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi tải danh sách thanh toán", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
