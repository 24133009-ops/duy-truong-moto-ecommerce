# DUY TRUONG MOTO - HỆ THỐNG TMĐT CHUỖI SHOWROOM SIÊU XE PHÂN KHỐI LỚN & XE LUXURY

> **Dự án cuối kỳ Chuyên ngành Kỹ thuật Phần mềm / Công nghệ Web**  
> **Mô hình chuỗi cửa hàng:** Chuỗi Showroom xe Luxury **Duy Truong Moto** (Chi nhánh TP.HCM, Hà Nội, Đà Nẵng)  
> **Quy mô nhóm:** Đúng **3 thành viên**

---

## 1. THÔNG TIN ĐỒ ÁN & THÀNH VIÊN NHÓM 17
- **Trường:** Đại học Sư phạm Kỹ thuật TP. Hồ Chí Minh (HCMUTE)
- **Khoa:** Công nghệ Thông tin - Môn học: Lập trình Web (HK1 / 2026-2027)
- **Đề tài:** 02 - Hệ thống TMĐT Chuỗi Showroom Siêu Mô Tô PKL & Xe Luxury "Duy Trương Motor" (Chuỗi 3 chi nhánh: TP.HCM, Hà Nội, Đà Nẵng)

### Danh sách thành viên nhóm:
1. **Trương Quốc Duy** (MSSV: **24133009** - Lớp 24133A) - **Nhóm trưởng (Leader)**: Thiết kế kiến trúc Prompt, Fullstack Spring Boot 3 + React 18 SPA, CSDL, Bảo mật Spring Security 6 & JWT, VNPAY, WebSocket.
2. **Đỗ Trọng Khôi** (MSSV: **20133056** - Lớp 201331A) - **Thành viên**: Phân tích lược đồ CSDL, Test Prompts QA, Dữ liệu 60 mẫu xe & phụ kiện.
3. **Lê Nghĩa Tình** (MSSV: **20133096** - Lớp 201331A) - **Thành viên**: Đặc tả nghiệp vụ SRS, Quy trình vận chuyển Shipper 6 chặng, Báo cáo Word.

### 🌐 Liên kết trực tuyến chính thức:
- **Frontend SPA (React 18 Dark Luxury):** [https://duytruong-motor.vercel.app](https://duytruong-motor.vercel.app)
- **Backend API & Thymeleaf (Spring Boot 3):** [https://duy-truong-moto-ecommerce.onrender.com](https://duy-truong-moto-ecommerce.onrender.com)
- **Báo cáo Prompts AI Senior Vibe-Coding:** [BAO_CAO_PROMPTS_AI_SENIOR_VIBECODER.md](./BAO_CAO_PROMPTS_AI_SENIOR_VIBECODER.md) | File Word: [`Nhom_17/01_BaoCao_Word_Docx/BaoCao_AI_Prompts_Senior_VibeCoder_Nhom17.docx`](./Nhom_17/01_BaoCao_Word_Docx/BaoCao_AI_Prompts_Senior_VibeCoder_Nhom17.docx)
- **Gói nộp bài hoàn chỉnh:** Thư mục [`Nhom_17/`](./Nhom_17/) gồm đầy đủ Báo cáo Word, CSDL SQL, Source code zip, Kịch bản Video Demo, Hướng dẫn chấm Rubric.

---

## 2. CÔNG NGHỆ ÁP DỤNG TRONG ĐỒ ÁN
- **Backend Core:** Java 17 LTS + Spring Boot 3.2.5 (Kiến trúc 3 tầng chuẩn `Controller` - `Service` - `Repository`).
- **Database:** Microsoft SQL Server 2022 (`DuyTruongMotoDB`), PostgreSQL 15 (Supabase), Spring Data JPA, Hibernate ORM.
- **Frontend & Giao diện:** React 18 + Vite + 100% Vanilla CSS Dark Luxury (Titanium Gold & Racing Carbon) + Thymeleaf SSR.
- **Bảo mật & Phân quyền:** Spring Security 6 + BCrypt Password Encoder + JWT Stateless cho REST/WebSocket (5 vai trò: Guest, User, Vendor, Shipper, Admin).
- **Xác thực OTP thật:** JavaMailSender gửi mã OTP 6 số qua Gmail SMTP thật (`duytq4906@gmail.com`) với TTL 5 phút.
- **Thanh toán trực tuyến:** Cổng thanh toán **VNPAY Sandbox** (HMAC-SHA512 checksum, QR Code động timer 15 phút, IPN callback).
- **Lưu trữ đám mây:** **Cloudinary API** phục vụ upload ảnh xe chất lượng cao và video review pô xe.
- **Thời gian thực:** **WebSocket STOMP** qua SockJS phục vụ Live Chat tư vấn Showroom.

---

## 3. TÀI KHOẢN MẪU DÙNG THỬ (DEMO ACCOUNTS)

| Vai trò | Email đăng nhập | Mật khẩu | Chức năng truy cập |
| :--- | :--- | :--- | :--- |
| **Admin / Manager** | `admin@duytruongmoto.com` | `admin123` | Quản trị toàn hệ thống, cấp phép showroom, thống kê chuỗi |
| **Vendor (Showroom Q1)** | `vendor@duytruongmoto.com` | `vendor123` | Quản lý kho xe Showroom Quận 1, đơn hàng, ví tiền showroom |
| **Shipper (Vận tải)** | `shipper@duytruongmoto.com` | `shipper123` | Đội xe sàn trượt VIP, xác nhận bàn giao xe 6 giai đoạn |
| **Khách hàng (User)** | `customer@duytruongmoto.com` | `customer123` | Mua xe, giỏ hàng lưu DB, đặt cọc VNPAY, đánh giá xe $\ge$ 50 chars |

---

## 4. HƯỚNG DẪN CHẠY DỰ ÁN TRÊN MÁY TÍNH

### Bước 1: Khởi động cơ sở dữ liệu SQL Server
Đảm bảo SQL Server đang chạy và database `DuyTruongMotoDB` đã tồn tại:
```powershell
sqlcmd -S localhost -U duytruong_admin -P "Password123@" -d DuyTruongMotoDB -Q "SELECT DB_NAME();"
```

### Bước 2: Build & Chạy Spring Boot
```powershell
$env:JAVA_HOME = "C:\Users\Admin\.jdks\ms-17.0.20.1"
$env:Path = "C:\Users\Admin\.jdks\ms-17.0.20.1\bin;C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.3\plugins\maven-plugin\lib\maven3\bin;" + $env:Path

mvn clean compile
mvn package -DskipTests
java -jar target/duy-truong-moto-1.0.0.jar
```

### Bước 3: Truy cập ứng dụng
- Trang chủ Storefront: `http://localhost:8080/home`
- Đăng nhập: `http://localhost:8080/auth/login`
- Đăng ký nhận mã OTP Email: `http://localhost:8080/auth/register`
- Quản lý Showroom (Vendor): `http://localhost:8080/vendor/dashboard`
- Quản trị viên (Admin): `http://localhost:8080/admin/dashboard`
- Phân hệ Shipper: `http://localhost:8080/shipper/dashboard`
