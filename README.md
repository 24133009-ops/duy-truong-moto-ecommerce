# DUY TRUONG MOTO - HỆ THỐNG TMĐT CHUỖI SHOWROOM SIÊU XE PHÂN KHỐI LỚN & XE LUXURY

> **Dự án môn học Lập trình Web**  
> **Mô hình chuỗi cửa hàng:** Chuỗi Showroom xe Luxury **Duy Truong Moto** (Chi nhánh TP.HCM, Hà Nội, Đà Nẵng)  
> **Quy mô nhóm:** Đúng **3 thành viên**

---

## 1. THÔNG TIN ĐỒ ÁN & THÀNH VIÊN NHÓM 17
- **Trường:** Đại học Sư phạm Kỹ thuật TP. Hồ Chí Minh (HCMUTE)
- **Khoa:** Công nghệ Thông tin - Môn học: Lập trình Web (HK1 / 2026-2027)
- **Đề tài:** 02 - Hệ thống TMĐT Chuỗi Showroom Siêu Mô Tô PKL & Xe Luxury "Duy Trương Motor" (Chuỗi 3 chi nhánh: TP.HCM, Hà Nội, Đà Nẵng)

### Danh sách thành viên nhóm:
1. **Trương Quốc Duy** (MSSV: **24133009** - Lớp 24133A) - **Nhóm trưởng (Leader)**: Xây dựng Backend Spring Boot 3, Giao diện Web Thymeleaf & HTML/CSS/JS, CSDL SQL Server, Bảo mật Spring Security, VNPAY, WebSocket Live Chat.
2. **Đỗ Trọng Khôi** (MSSV: **20133056** - Lớp 201331A) - **Thành viên**: Phân tích lược đồ CSDL, Kiểm thử chức năng hệ thống, Dữ liệu 60 mẫu xe & phụ kiện.
3. **Lê Nghĩa Tình** (MSSV: **20133096** - Lớp 201331A) - **Thành viên**: Đặc tả yêu cầu nghiệp vụ SRS, Quy trình vận chuyển Shipper 6 chặng, Báo cáo Word & Kịch bản Video Demo.

### 🌐 Liên kết đồ án:
- **Website Demo trực tuyến:** [https://duytruong-motor.vercel.app](https://duytruong-motor.vercel.app)
- **File Prompt:** [Prompt.md](./Prompt.md)
- **Thư mục tài liệu đồ án:** Thư mục [`Nhom_17/`](./Nhom_17/) gồm đầy đủ Báo cáo Word, CSDL SQL, Source code zip, Kịch bản Video Demo, Hướng dẫn chấm Rubric.

---

## 2. TÀI KHOẢN MẪU DÙNG THỬ (DEMO ACCOUNTS)

| Vai trò | Email đăng nhập | Mật khẩu | Chức năng truy cập |
| :--- | :--- | :--- | :--- |
| **Admin / Manager** | `admin@duytruongmoto.com` | `admin123` | Quản trị toàn hệ thống, cấp phép showroom, thống kê chuỗi |
| **Vendor (Showroom Q1)** | `vendor@duytruongmoto.com` | `vendor123` | Quản lý kho xe Showroom Quận 1, đơn hàng, ví tiền showroom |
| **Shipper (Vận tải)** | `shipper@duytruongmoto.com` | `shipper123` | Đội xe sàn trượt VIP, xác nhận bàn giao xe 6 giai đoạn |
| **Khách hàng (User)** | `customer@duytruongmoto.com` | `customer123` | Mua xe, giỏ hàng lưu DB, đặt cọc VNPAY, đánh giá xe $\ge$ 50 chars |

---

## 3. HƯỚNG DẪN CÀI ĐẶT & CHẠY DỰ ÁN TRÊN MÁY TÍNH

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
