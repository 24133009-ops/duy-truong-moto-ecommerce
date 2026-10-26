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

## 3. LIÊN KẾT TRUY CẬP HỆ THỐNG & CÁC PHÂN HỆ

### 🌐 Truy cập trực tiếp hệ thống:
- **Website chính thức (Online Demo):** [https://duytruong-motor.vercel.app](https://duytruong-motor.vercel.app)
- **Môi trường chạy cục bộ (Local):** [http://localhost:5173](http://localhost:5173)
- **Backend API (Spring Boot 3):** [https://duy-truong-moto-ecommerce.onrender.com/api/products](https://duy-truong-moto-ecommerce.onrender.com/api/products) *(Local API: [http://localhost:8080/api/products](http://localhost:8080/api/products))*

### 👥 Trải nghiệm đầy đủ 5 vai trò trên hệ thống:
Hệ thống tích hợp thanh chuyển đổi vai trò (Role Switcher) ngay trên giao diện để thuận tiện cho việc trình chiếu và chấm điểm:
1. **Khách vãng lai & Khách mua xe (Guest / Customer):**
   - Xem danh mục 60 dòng xe, siêu xe & xe cào cào địa hình có bộ lọc đa tiêu chí.
   - Tiêu chí đề tài: Top xe bán chạy trên 10 chiếc sắp xếp giảm dần theo lượt bán.
   - Xem chi tiết xe, chọn màu sắc zin chính hãng, yêu cầu đổi màu cavet / Wrap Decal.
   - Tính năng **Bớt lộc / Giảm giá**: Áp dụng voucher hoặc đề xuất mức giá mong muốn.
   - Giỏ hàng & đặt cọc qua cổng thanh toán VNPAY QR, gửi đánh giá xe $\ge$ 50 ký tự.
2. **Quản lý Showroom (Vendor):**
   - Quản lý kho xe chi nhánh (thêm mới, chỉnh sửa giá, số lượng tồn kho).
   - Tiếp nhận và duyệt các đơn cọc xe của khách hàng.
3. **Quản trị viên (Admin):**
   - Giám sát toàn bộ hệ thống chuỗi 3 Showroom (Phú Nhuận, Cầu Giấy, Hải Châu).
   - Phân quyền tài khoản người dùng, xem thống kê doanh thu và báo cáo hệ thống.
4. **Đội xe vận tải (Shipper):**
   - Theo dõi lộ trình điều phối xe cứu hộ sàn trượt thủy lực VIP qua 6 giai đoạn bàn giao tận nhà.
