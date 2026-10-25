# DUY TRUONG MOTO - HỆ THỐNG TMĐT CHUỖI SHOWROOM SIÊU XE PHÂN KHỐI LỚN & XE LUXURY

> **Dự án cuối kỳ Chuyên ngành Kỹ thuật Phần mềm / Công nghệ Web**  
> **Mô hình chuỗi cửa hàng:** Chuỗi Showroom xe Luxury **Duy Truong Moto** (Chi nhánh TP.HCM, Hà Nội, Đà Nẵng)  
> **Quy mô nhóm:** Đúng **3 thành viên**

---

## 1. CÔNG NGHỆ ÁP DỤNG TRONG ĐỒ ÁN
- **Backend Core:** Java 17 + Spring Boot 3.2.5 (Kiến trúc 3 tầng chuẩn `Controller` - `Service` - `Repository`).
- **Database:** Microsoft SQL Server 2022 (`DuyTruongMotoDB`), Spring Data JPA, Hibernate ORM.
- **Frontend & Giao diện:** Thymeleaf + Bootstrap 5.3 + FontAwesome 6 + CSS Dark Luxury (Titanium Gold & Racing Carbon).
- **Bảo mật & Xác thực:** Spring Security 6 + BCrypt Password Encoder + JWT (JSON Web Token) cho REST/WebSocket.
- **Xác thực OTP:** JavaMailSender gửi mã OTP 6 số ngẫu nhiên qua Email khi Đăng ký & Quên mật khẩu.
- **Thanh toán trực tuyến:** Cổng thanh toán **VNPAY Sandbox** (Mã hóa Checksum HMAC SHA512 bảo mật giao dịch).
- **Lưu trữ đám mây:** **Cloudinary API** phục vụ upload ảnh xe chất lượng cao và video review pô xe.
- **Thời gian thực:** **WebSocket STOMP** qua SockJS phục vụ Live Chat tư vấn Showroom và cập nhật trạng thái đơn hàng.

---

## 2. PHÂN CÔNG NHIỆM VỤ CHI TIẾT CHO NHÓM 3 THÀNH VIÊN

| Thành viên | Vai trò phụ trách | Các phân hệ chức năng đảm nhiệm | Tiêu chí kỹ thuật đánh giá |
| :--- | :--- | :--- | :--- |
| **Thành viên 1**<br>*(Nguyễn Văn A - Trưởng nhóm)* | **Kiến trúc Hệ thống, Security & Phân hệ Admin/Manager** | - Khởi tạo cấu trúc dự án 3 tầng & CSDL 18 bảng SQL Server.<br>- Cấu hình Spring Security 6, mã hóa BCrypt & JWT.<br>- Xác thực Email OTP kích hoạt tài khoản & Quên mật khẩu.<br>- Phân hệ Admin/Manager: Quản lý User, Cấp phép/Khóa Showroom (UC_018), Quản lý Danh mục (UC_030), Quản lý Hoa hồng sàn (UC_035), Thống kê doanh thu toàn chuỗi. | Spring Security, JWT, JavaMailSender, JPA Specification & Transactions. |
| **Thành viên 2**<br>*(Trần Thị B - Client Lead)* | **Giao diện Người dùng & Luồng Khách hàng (Guest & User)** | - Thiết kế Giao diện Dark Luxury chuẩn Showroom xe PKL.<br>- Trang chủ Guest: Top xe bán chạy trên 10 chiếc (sắp xếp giảm dần).<br>- Trang tìm kiếm & Bộ lọc nâng cao (Giá, Phân khối, Hãng).<br>- Trang chi tiết xe: Thông số kỹ thuật, chọn phiên bản.<br>- Giỏ hàng đồng bộ Database (UC_012).<br>- Đặt hàng, quản lý sổ địa chỉ (UC_010), lịch sử đơn qua 6 trạng thái.<br>- Đánh giá xe: Validate tối thiểu 50 ký tự và ảnh/video. | Thymeleaf Layout, Bootstrap 5 Responsive, Client & Server Validation, DB Cart. |
| **Thành viên 3**<br>*(Lê Văn C - Vendor & Shipper Lead)* | **Phân hệ Showroom (Vendor), Giao vận (Shipper) & Tính năng sáng tạo** | - Phân hệ Showroom: Đăng ký shop, Quản lý kho xe, Thống kê doanh thu & Ví tiền Showroom (UC_021, UC_022, UC_024).<br>- Tích hợp Cloudinary Service tải lên nhiều ảnh xe & video.<br>- Phân hệ Shipper: Tiếp nhận đơn, xác nhận bàn giao xe và giấy tờ.<br>- Tích hợp cổng thanh toán VNPAY Sandbox (Tạo URL & Xử lý IPN Return).<br>- Tích hợp WebSocket STOMP Live Chat giữa Khách và Showroom. | WebSocket STOMP, VNPAY HMAC-SHA512, Cloudinary REST API, Shipper Flow. |

---

## 3. TÀI KHOẢN MẪU DÙNG THỬ (DEMO ACCOUNTS)

| Vai trò | Email đăng nhập | Mật khẩu | Chức năng truy cập |
| :--- | :--- | :--- | :--- |
| **Admin / Manager** | `admin@duytruongmoto.com` | `admin123` | Quản trị toàn hệ thống, cấp phép showroom, thống kê chuỗi |
| **Vendor (Showroom Q1)** | `vendor@duytruongmoto.com` | `vendor123` | Quản lý kho xe Showroom Quận 1, đơn hàng, ví tiền showroom |
| **Shipper (Vận tải)** | `shipper@duytruongmoto.com` | `shipper123` | Đội xe sàn trượt VIP, xác nhận bàn giao xe và giấy tờ |
| **Khách hàng (User)** | `customer@gmail.com` | `user123` | Mua xe, giỏ hàng, đặt cọc VNPAY, theo dõi đơn, đánh giá xe |

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
