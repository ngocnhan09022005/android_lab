# Tích hợp n8n Webhook + Retrofit để xử lý QR code thanh toán (Android Java)

Tài liệu này cập nhật mục tiêu: Ứng dụng Android (Java) sẽ sử dụng Retrofit để gọi API Webhook do n8n cung cấp nhằm xử lý QR code thanh toán. Firebase vẫn dùng như hiện trạng (đã có `google-services.json`), và n8n được cấu hình dưới dạng Webhook.

Mục lục
- Tổng quan luồng xử lý
- Thiết lập n8n Webhook cho QR thanh toán
- Hợp đồng API (request/response)
- Tích hợp Retrofit trong Android app (Java)
- Cấu hình base URL và HTTP (HTTP/HTTPS, emulator)
- Build & chạy nhanh
- Thông tin đăng nhập quản trị
- Ghi chú bảo mật và kiểm thử

---

## 1) Tổng quan luồng xử lý

1. Ứng dụng quét QR -> nhận được chuỗi QR (qrData) + thông tin đơn hàng (orderId, amount, currency).
2. App gọi API Webhook n8n qua Retrofit: POST /webhook/payment/qr (hoặc /webhook-test/payment/qr trong chế độ test).
3. n8n:
   - Nhận payload từ Webhook.
   - Kiểm tra/chuẩn hóa dữ liệu (Function node).
   - (Tuỳ chọn) Gọi gateway thanh toán/ghi log/ghi CSDL qua các node n8n khác.
   - Trả về JSON kết quả qua node Respond to Webhook.
4. App nhận response và cập nhật UI/trạng thái thanh toán.

---

## 2) Thiết lập n8n Webhook cho QR thanh toán

Yêu cầu: n8n đang chạy (local: http://localhost:5678). Có thể chạy bằng Docker:
```bash
docker run -it --rm \
  -p 5678:5678 \
  -v ~/.n8n:/home/node/.n8n \
  --name n8n \
  n8nio/n8n
```

Các bước trong giao diện n8n:
1. Tạo Workflow mới.
2. Thêm node “Webhook”
   - HTTP Method: POST
   - Path: payment/qr
   - Mode: Production (sử dụng endpoint /webhook/payment/qr). Trong quá trình dev, bạn có thể dùng “Test” với /webhook-test/payment/qr.
   - Response: Keep the response for later node (sẽ dùng node Respond to Webhook).
3. Thêm node “Function” để validate và chuẩn hóa đầu vào. Ví dụ code:
   ```javascript
   // Function Node: Validate & normalize QR payload
   // Input: items[0].json = { qrData, amount, currency, orderId, meta }
   // Output: normalized fields + pseudo processing
   const item = items[0].json;

   // Basic validation
   if (!item.qrData || typeof item.qrData !== 'string') {
     throw new Error('qrData is required and must be a string');
   }
   if (item.amount == null || isNaN(Number(item.amount))) {
     throw new Error('amount is required and must be a number');
   }

   // Normalize currency
   const currency = (item.currency || 'VND').toUpperCase();

   // Example: parse QR (tuỳ theo format QR thực tế của bạn)
   // Ở đây giả định qrData có thể là chuỗi chứa orderId=...&amount=...
   // Nếu bạn có parser cụ thể, thay thế logic bên dưới.
   const parsed = {};
   try {
     const pairs = item.qrData.split('&');
     for (const p of pairs) {
       const [k, v] = p.split('=');
       if (k && v) parsed[k] = decodeURIComponent(v);
     }
   } catch (e) {
     // fallback nếu qrData không ở dạng key=value
   }

   const orderId = item.orderId || parsed.orderId || `ORD-${Date.now()}`;

   // Build normalized payload
   const normalized = {
     orderId,
     amount: Number(item.amount),
     currency,
     qrRaw: item.qrData,
     meta: item.meta || {},
     processedAt: new Date().toISOString(),
   };

   return [{ json: normalized }];
   ```
4. (Tuỳ chọn) Thêm các node xử lý:
   - HTTP Request: Gọi cổng thanh toán hoặc service backend của bạn để tạo giao dịch.
   - Set/Function: Chuyển đổi dữ liệu về response chuẩn.
   - Write Database/Google Sheets: Lưu log giao dịch.
5. Thêm node “Respond to Webhook”
   - Response Code: 200
   - Response Data: “Last node output” hoặc thiết lập “JSON” và trả về trường mong muốn, ví dụ:
     - Response Body: Expression `{{$json}}` nếu bạn muốn trả nguyên payload đã normalize
     - Hoặc tạo một object custom: 
       ```json
       {
         "status": "ok",
         "message": "QR processed",
         "orderId": "{{$json.orderId}}",
         "amount": "{{$json.amount}}",
         "currency": "{{$json.currency}}"
       }
       ```
6. Lưu (Save) và Activate workflow.

Kiểm thử nhanh (test mode):
```bash
curl -X POST http://localhost:5678/webhook-test/payment/qr \
  -H "Content-Type: application/json" \
  -d '{
    "qrData": "orderId=12345&amount=150000&note=demo",
    "amount": 150000,
    "currency": "VND",
    "meta": {"source":"android-debug"}
  }'
```

---

## 3) Hợp đồng API (contract)

- Endpoint (dev test): POST http://localhost:5678/webhook-test/payment/qr
- Endpoint (prod): POST http://<host>:5678/webhook/payment/qr
- Headers: Content-Type: application/json
- Request (ví dụ):
```json
{
  "qrData": "orderId=12345&amount=150000",
  "amount": 150000,
  "currency": "VND",
  "orderId": "12345",
  "meta": {
    "source": "android",
    "extra": "optional"
  }
}
```
- Response (ví dụ):
```json
{
  "status": "ok",
  "message": "QR processed",
  "orderId": "12345",
  "amount": 150000,
  "currency": "VND",
  "processedAt": "2025-08-10T10:02:35.000Z"
}
```

Lưu ý: Tùy cách bạn cấu hình node “Respond to Webhook”, response có thể là object normalize tùy chỉnh như trên.

---

## 4) Tích hợp Retrofit trong Android app (Java)

Thêm dependency (app/build.gradle):
```groovy
dependencies {
    implementation platform('com.google.firebase:firebase-bom:33.1.2')
    implementation 'com.google.firebase:firebase-analytics'

    implementation 'com.squareup.retrofit2:retrofit:2.11.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.11.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.12.0'
}
```

Khai báo BASE URL:
- Dùng BuildConfig (khuyến nghị cho debug/release):
```groovy
android {
    defaultConfig {
        // Dùng 10.0.2.2 cho emulator trỏ về localhost máy tính
        buildConfigField "String", "N8N_BASE_URL", "\"http://10.0.2.2:5678/\""
    }
    buildTypes {
        debug {
            buildConfigField "String", "N8N_BASE_URL", "\"http://10.0.2.2:5678/\""
        }
        release {
            // Sản xuất nên dùng HTTPS
            buildConfigField "String", "N8N_BASE_URL", "\"https://your-n8n-domain/\""
            // proguard/r8 rules cho Gson/Retrofit nếu cần
        }
    }
}
```

Các file mã nguồn mẫu (sửa package cho phù hợp dự án của bạn):

- N8nApi.java: định nghĩa endpoint Retrofit
- RetrofitClient.java: tạo Retrofit instance
- PaymentQrRequest.java, PaymentQrResponse.java: model request/response
- Ví dụ sử dụng: gọi API sau khi quét QR

Xem chi tiết file ở phía dưới (khối mã “file”).

---

## 5) Cấu hình HTTP/HTTPS và emulator

- Emulator Android truy cập “localhost” của máy tính qua IP đặc biệt: 10.0.2.2
- Thiết bị thật: dùng IP LAN của máy tính (vd: http://192.168.1.10:5678)
- Android 9+ chặn cleartext HTTP theo mặc định:
  - Trong giai đoạn dev, có thể bật cleartext cho host nội bộ bằng networkSecurityConfig (file mẫu bên dưới).
  - Sản xuất: Bắt buộc dùng HTTPS cho n8n (reverse proxy qua Nginx/Caddy + TLS).

---

## 6) Build & chạy nhanh

- Mở dự án với Android Studio, sync Gradle.
- Chạy n8n local, import workflow hoặc tạo theo hướng dẫn trên, Activate.
- Run app (chọn thiết bị/emulator) và thực hiện quét QR -> app gọi n8n webhook qua Retrofit.

---

## 7) Thông tin đăng nhập quản trị

- Email: name@gmail.com
- Mật khẩu: 123456

Chỉ sử dụng cho môi trường dev/test. Không công khai trên sản xuất.

---

## 8) Ghi chú bảo mật và kiểm thử

- Dùng HTTPS và secret key/Basic Auth/JWT trên Webhook nếu public internet.
- Hạn chế IP hoặc đặt Reverse Proxy với rate limit.
- Không log thông tin nhạy cảm (thẻ, mã bí mật) trong n8n hoặc app.
- Kiểm thử bằng curl/Postman trước khi tích hợp app để xác thực hợp đồng API.