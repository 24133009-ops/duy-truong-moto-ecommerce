# 🚀 HƯỚNG DẪN TRIỂN KHAI (DEPLOY) WEBSITE DUY TRƯƠNG MOTOR LÊN MẠNG CHÍNH THỨC

Tài liệu này cung cấp 2 phương án chi tiết nhất để đưa toàn bộ hệ thống (Frontend React 18 SPA + Backend Spring Boot 3 + Database + VNPAY) từ localhost lên Internet với tên miền chính thức.

---

## 📌 PHƯƠNG ÁN 1: TRIỂN KHAI CLOUD MIỄN PHÍ (KHUYÊN DÙNG ĐỂ BÁO CÁO ĐỒ ÁN)
> **Ưu điểm:** Miễn phí 100%, không cần mua VPS, tự động cấp chứng chỉ bảo mật HTTPS/SSL, setup nhanh trong 10 phút.

### Bước 1: Deploy Frontend React lên Vercel (Miễn phí)
1. Đẩy mã nguồn dự án lên GitHub:
   ```bash
   git add .
   git commit -m "feat: ready for deployment"
   git push origin main
   ```
2. Truy cập [https://vercel.com](https://vercel.com) và đăng nhập bằng tài khoản GitHub.
3. Bấm **"Add New..."** -> **"Project"** -> Chọn repository `duy-truong-moto-ecommerce`.
4. Tại phần **Root Directory**, bấm **Edit** và chọn thư mục: `frontend`.
5. Cấu hình Build:
   - **Framework Preset:** `Vite`
   - **Build Command:** `npm run build`
   - **Output Directory:** `dist`
6. Bấm **"Deploy"**. Sau 60 giây, website của bạn sẽ chạy chính thức tại link dạng:
   👉 `https://duytruongmotor.vercel.app` (Hoặc thêm domain riêng miễn phí).

---

### Bước 2: Tạo Database Cloud Miễn Phí (Aiven / Supabase)
1. Truy cập [Aiven.io](https://aiven.io/) hoặc [Supabase.com](https://supabase.com) tạo tài khoản miễn phí.
2. Tạo 1 database mới (MySQL hoặc PostgreSQL/SQL Server).
3. Sử dụng công cụ (DBeaver, SQL Server Management Studio hoặc web console) chạy toàn bộ script tại file `duytruong_moto_db.sql`.
4. Copy chuỗi kết nối JDBC: `jdbc:postgresql://...` hoặc `jdbc:mysql://...` kèm User & Password.

---

### Bước 3: Deploy Backend Spring Boot 3 lên Render.com (Miễn phí)
1. Truy cập [https://render.com](https://render.com), bấm **"New +"** -> **"Web Service"**.
2. Kết nối với repo GitHub của dự án.
3. Cấu hình Service:
   - **Name:** `duytruong-moto-api`
   - **Runtime:** `Java` hoặc `Docker` (Render tự nhận diện `Dockerfile`)
   - **Instance Type:** `Free`
4. Tại mục **Environment Variables**, thêm các biến kết nối database:
   - `SPRING_DATASOURCE_URL`: *(URL database cloud ở Bước 2)*
   - `SPRING_DATASOURCE_USERNAME`: *(User database)*
   - `SPRING_DATASOURCE_PASSWORD`: *(Mật khẩu database)*
5. Bấm **"Create Web Service"**. Render sẽ tự động build jar và chạy tại link:
   👉 `https://duytruong-moto-api.onrender.com`

---

## 📌 PHƯƠNG ÁN 2: TRIỂN KHAI VPS CHUYÊN NGHIỆP VỚI TÊN MIỀN RIÊNG
> **Ưu điểm:** Tốc độ tải cực nhanh tại Việt Nam, mang tên miền thương hiệu riêng (`https://duytruongmotor.com`), tự chủ 100% tài nguyên, sẵn sàng vận hành kinh doanh thực tế.

### Chi phí dự kiến:
- Tên miền `.com` hoặc `.vn`: ~180.000đ - 450.000đ/năm (Mua tại Namecheap, PA Việt Nam, Mắt Bão).
- Thuê VPS Cloud (2 CPU, 4GB RAM, SSD): ~120.000đ - 180.000đ/tháng (Vietnix, TinoHost, DigitalOcean).

---

### Bước 1: Trỏ Tên Miền Về VPS
- Truy cập trang quản trị tên miền (DNS Management).
- Tạo 2 bản ghi:
  - **A Record:** `@` -> Trỏ về IP của VPS (ví dụ: `103.xxx.xxx.xxx`)
  - **A Record:** `www` -> Trỏ về IP của VPS

---

### Bước 2: Cài Đặt Docker & Docker Compose Trên VPS
Kết nối SSH vào VPS qua Terminal:
```bash
ssh root@103.xxx.xxx.xxx
```
Cập nhật hệ thống và cài đặt Docker:
```bash
sudo apt update && sudo apt upgrade -y
sudo apt install -y docker.io docker-compose git
sudo systemctl enable --now docker
```

---

### Bước 3: Clone Code & Khởi Chạy 1 Lệnh Duy Nhất (Docker Compose)
1. Clone dự án về VPS:
   ```bash
   git clone https://github.com/duytruong-moto/duy-truong-moto-ecommerce.git /var/www/duytruongmotor
   cd /var/www/duytruongmotor
   ```
2. Khởi chạy toàn bộ hệ thống bằng Docker Compose:
   ```bash
   docker-compose up -d --build
   ```
   *Docker Compose sẽ tự động kéo SQL Server, build Spring Boot 3 và build React SPA kèm Nginx phục vụ trên cổng 80.*

---

### Bước 4: Cài Đặt Chứng Chỉ Bảo Mật SSL (HTTPS) Miễn Phí Với Certbot
1. Cài đặt Certbot:
   ```bash
   sudo apt install -y certbot python3-certbot-nginx
   ```
2. Cấp chứng chỉ SSL:
   ```bash
   sudo certbot --nginx -d duytruongmotor.com -d www.duytruongmotor.com
   ```
3. Sau khi hoàn tất, website sẽ có ổ khóa bảo mật màu xanh: `https://duytruongmotor.com`.

---

## 💳 CẤU HÌNH THANH TOÁN VNPAY KHI LÊN PRODUCTION
Khi deploy lên domain chính thức, bạn cần cập nhật cấu hình VNPAY trong `application.properties`:
```properties
# Đổi return_url từ localhost thành domain chính thức:
vnpay.return_url=https://duytruongmotor.com/payment-result
```
Nếu đăng ký cổng thương mại điện tử chính thức với VNPAY, bạn sẽ được cấp:
- `vnpay.tmn_code`: Mã định danh doanh nghiệp thực tế.
- `vnpay.hash_secret`: Chuỗi bí mật tạo mã băm HMAC-SHA512 thực tế.
- `vnpay.payment_url`: `https://pay.vnpay.vn/vpcpay.html` (Môi trường thanh toán thật).
