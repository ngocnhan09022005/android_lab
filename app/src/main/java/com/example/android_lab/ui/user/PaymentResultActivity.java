package com.example.android_lab.ui.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.android_lab.R;

public class PaymentResultActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_result);

        TextView tvResult = findViewById(R.id.tvPaymentResult);

        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data != null) {
            String status = data.getQueryParameter("status");
            if ("success".equals(status)) {
                tvResult.setText("✅ Thanh toán thành công!");
            } else {
                tvResult.setText("❌ Thanh toán bị hủy.");
            }
        } else {
            Toast.makeText(this, "Không nhận được dữ liệu thanh toán", Toast.LENGTH_SHORT).show();
        }
    }
}
