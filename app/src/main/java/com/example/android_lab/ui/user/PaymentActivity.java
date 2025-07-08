package com.example.android_lab.ui.user;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.android_lab.R;
import com.example.android_lab.models.CartItem;
import com.example.android_lab.services.ApiClient;
import com.example.android_lab.services.PaymentApiService;
import com.example.android_lab.services.PaymentRequest;
import com.example.android_lab.services.PaymentResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {

    private TextView tvAmount, tvNote, tvExpiredAt, btnConfirmPayment;
    private ImageView imgQr, btnBack;
    private Button btnDownloadImage;
    private String qrUrl = "";
    private long expiredAtEpoch = 0;

    private ArrayList<CartItem> cartItems;

    private final Handler handler = new Handler();
    private final Runnable countdownRunnable = new Runnable() {
        @Override
        public void run() {
            updateCountdown();
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        tvAmount = findViewById(R.id.tvAmount);
        tvNote = findViewById(R.id.tvNote);
        imgQr = findViewById(R.id.imgQr);
        btnDownloadImage = findViewById(R.id.btnDownloadImage);
        btnBack = findViewById(R.id.btnBack);
        tvExpiredAt = findViewById(R.id.tvExpiredAt);
        btnConfirmPayment = findViewById(R.id.btnProceed);

        btnBack.setOnClickListener(v -> onBackPressed());
        btnDownloadImage.setOnClickListener(v -> downloadQrImage(qrUrl));
        btnDownloadImage.setVisibility(View.GONE);

        cartItems = (ArrayList<CartItem>) getIntent().getSerializableExtra("cartItems");
        double amount = getIntent().getDoubleExtra("amount", 0);

        // Build contactInfo
        String name = getIntent().getStringExtra("name");
        String phone = getIntent().getStringExtra("phone");
        String address = getIntent().getStringExtra("address");
        String contactInfo = "Tên: " + name + "\nSĐT: " + phone + "\nĐịa chỉ: " + address;

        // Build productListString
        StringBuilder productListBuilder = new StringBuilder();
        double total = 0;
        if (cartItems != null) {
            for (CartItem item : cartItems) {
                productListBuilder.append(item.getName())
                        .append(" x ").append(item.getQuantity())
                        .append(" - ")
                        .append(String.format("%,.0f₫", item.getPrice() * item.getQuantity()))
                        .append("\n");
                total += item.getPrice() * item.getQuantity();
            }
        }
        String productListString = productListBuilder.toString();

        // Gọi API tạo QR và hiển thị QR, note, amount đúng như cũ
        String userId = FirebaseAuth.getInstance().getUid();
        String orderId = "ORDER_" + System.currentTimeMillis();
        String note = "Thanh toán đơn hàng " + orderId;
        callN8nApi(userId, orderId, amount, note);
        // KHÔNG lưu đơn hàng ở đây!

        double finalTotal = total;
        btnConfirmPayment.setOnClickListener(v -> {
            // Khi bấm xác nhận mới lưu đơn hàng và chuyển sang màn hình thành công
            saveOrderToFirebase(userId, orderId, amount, contactInfo, productListString, finalTotal);
            // Xóa giỏ hàng sau khi xác nhận thanh toán
            if (userId != null) {
                DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("cart").child(userId);
                cartRef.removeValue();
            }
            startActivity(new android.content.Intent(this, PaymentSuccessActivity.class));
            finish();
        });
    }

    private void saveOrderToFirebase(String userId, String orderId, double amount, String contactInfo, String productListString, double total) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("payments").child(userId).child(orderId);
        HashMap<String, Object> data = new HashMap<>();
        data.put("orderId", orderId);
        data.put("amount", amount);
        data.put("status", "pending");
        data.put("date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
        data.put("id", orderId);
        data.put("name", getIntent().getStringExtra("name"));
        data.put("phone", getIntent().getStringExtra("phone"));
        data.put("address", getIntent().getStringExtra("address"));
        data.put("contactInfo", contactInfo);
        data.put("productListString", productListString);
        data.put("total", total);
        ref.setValue(data);
    }

    private void callN8nApi(String userId, String orderId, double amount, String note) {
        PaymentApiService api = ApiClient.getClient().create(PaymentApiService.class);
        long now = System.currentTimeMillis() / 1000;
        long expiredAt = now + 15 * 60;
        int roundedAmount = (int) Math.round(amount);

        PaymentRequest request = new PaymentRequest(userId, orderId, roundedAmount, note, expiredAt);

        api.createPayment(request).enqueue(new Callback<PaymentResponse>() {
            @Override
            public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PaymentResponse res = response.body();

                    qrUrl = res.getQrImageUrl();
                    expiredAtEpoch = res.getExpiredAt();
                    btnDownloadImage.setVisibility(View.VISIBLE);

                    tvAmount.setText(String.format("%,d₫", res.getAmount()));
                    tvNote.setText(res.getNote());
                    Glide.with(PaymentActivity.this).load(qrUrl).into(imgQr);
                    // Lắng nghe trạng thái đơn hàng, chỉ chuyển sang thành công khi đã thanh toán
                    listenOrderStatus(userId, orderId);
                } else {
                    Toast.makeText(PaymentActivity.this, "❌ Lỗi tạo mã QR", Toast.LENGTH_SHORT).show();
                    expiredAtEpoch = expiredAt;
                    tvNote.setText(note);
                }
                startCountdown();
            }

            @Override
            public void onFailure(Call<PaymentResponse> call, Throwable t) {
                Toast.makeText(PaymentActivity.this, "❌ Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                expiredAtEpoch = expiredAt;
                tvNote.setText(note);
                startCountdown();
            }
        });
    }

    private void listenOrderStatus(String userId, String orderId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("payments").child(userId).child(orderId).child("status");
        ref.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull com.google.firebase.database.DataSnapshot snapshot) {
                String status = snapshot.getValue(String.class);
                if ("paid".equals(status)) {
                    goToSuccessAndClearCart();
                }
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull com.google.firebase.database.DatabaseError error) {}
        });
    }

    private void goToSuccessAndClearCart() {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId != null) {
            DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("cart").child(userId);
            cartRef.removeValue();
        }
        startActivity(new android.content.Intent(this, PaymentSuccessActivity.class));
        finish();
    }

    private void startCountdown() {
        handler.removeCallbacks(countdownRunnable); // clear trước
        handler.post(countdownRunnable);
    }

    private void updateCountdown() {
        long now = System.currentTimeMillis() / 1000;
        long secondsLeft = expiredAtEpoch - now;

        if (secondsLeft <= 0) {
            tvExpiredAt.setText("⛔ QR đã hết hạn");
            btnDownloadImage.setEnabled(false);
            imgQr.setAlpha(0.5f); // làm mờ QR khi hết hạn
            handler.removeCallbacks(countdownRunnable);
            return;
        }

        long minutes = secondsLeft / 60;
        long seconds = secondsLeft % 60;
        tvExpiredAt.setText(String.format(Locale.getDefault(), "⏳ Còn lại: %02d:%02d", minutes, seconds));
    }

    private void downloadQrImage(String url) {
        if (url == null || url.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy ảnh QR để tải", Toast.LENGTH_SHORT).show();
            return;
        }

        String fileName = "QR_" + System.currentTimeMillis() + ".png";
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle("Tải ảnh QR");
        request.setDescription("Đang tải mã QR về máy...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        DownloadManager manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        manager.enqueue(request);

        Toast.makeText(this, "Đang tải ảnh QR về máy...", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(countdownRunnable); // tránh chạy ngầm
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (expiredAtEpoch > System.currentTimeMillis() / 1000) {
            handler.post(countdownRunnable);
        }
    }
}
