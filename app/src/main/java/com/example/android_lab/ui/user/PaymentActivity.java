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
import com.example.android_lab.services.ApiClient;
import com.example.android_lab.services.PaymentApiService;
import com.example.android_lab.services.PaymentRequest;
import com.example.android_lab.services.PaymentResponse;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {

    private TextView tvAmount, tvNote, tvExpiredAt;
    private ImageView imgQr, btnBack;
    private Button btnDownloadImage;
    private String qrUrl = "";
    private long expiredAtEpoch = 0;

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

        btnBack.setOnClickListener(v -> onBackPressed());
        btnDownloadImage.setOnClickListener(v -> downloadQrImage(qrUrl));
        btnDownloadImage.setVisibility(View.GONE);

        double amount = getIntent().getDoubleExtra("amount", 0);
        String userId = FirebaseAuth.getInstance().getUid();
        String orderId = "ORDER_" + System.currentTimeMillis();
        String note = "Thanh toán đơn hàng " + orderId;

        callN8nApi(userId, orderId, amount, note);
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
