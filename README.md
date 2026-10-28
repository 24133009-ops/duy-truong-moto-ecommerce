# DUY TRUONG MOTOR - HỆ THỐNG THƯƠNG MẠI ĐIỆN TỬ PHÂN PHỐI MÔ TÔ & ĐỒ BẢO HỘ ĐA CHI NHÁNH

> **ĐỒ ÁN MÔN HỌC: LẬP TRÌNH WEB (HK1 / 2026-2027)**  
> **Trường Đại học Sư phạm Kỹ thuật TP. Hồ Chí Minh (HCMUTE)**  
> **Nền tảng chính nộp đồ án:** Spring Boot 3.2 + Thymeleaf SSR + Spring Security 6 + JPA Hibernate  
> **Trang web triển khai trực tuyến (Live Render):** [https://duy-truong-moto-ecommerce.onrender.com](https://duy-truong-moto-ecommerce.onrender.com)  
> **Repository GitHub:** [https://github.com/24133009-ops/duy-truong-moto-ecommerce](https://github.com/24133009-ops/duy-truong-moto-ecommerce)  
> **Kịch bản Prompt AI:** [Prompt.md](./Prompt.md) | **Tập lệnh cơ sở dữ liệu:** [database.sql](./database.sql)

---

## 1. THÔNG TIN THÀNH VIÊN VÀ BẢNG PHÂN CÔNG NHIỆM VỤ (TIÊU CHÍ 1 - 10%)

| STT | Họ và tên | MSSV | Vai trò chính | Nhiệm vụ chi tiết | GitHub Profile |
| :---: | :--- | :---: | :--- | :--- | :--- |
| **1** | **Trương Quốc Duy** *(Nhóm trưởng)* | **24133009** | Leader / Architect / Admin | Thiết kế kiến trúc 3 tầng (Controller - Service - Repository), Spring Security 6 RBAC, Cổng thanh toán VNPAY QR Sandbox, WebSocket Live Chat & Đàm phán giá, Cấu hình JWT & Cloudinary, Quản trị toàn sàn. | [@24133009-ops](https://github.com/24133009-ops) |
| **2** | **Đỗ Trọng Khôi** | **20133056** | Database / Vendor Cầu Giấy | Thiết kế cấu trúc CSDL 19 thực thể, xuất file `database.sql`, Store Manager chi nhánh Cầu Giấy (Hà Nội), Chức năng CRUD xe / đồ bảo hộ, Tính năng Mã giảm giá (Vouchers), Đơn vị vận chuyển (Deliveries). | [@Kdt0701](https://github.com/Kdt0701) |
| **3** | **Lê Nghĩa Tình** | **20133096** | QA / Vendor Đà Nẵng / Shipper | Store Manager chi nhánh Hải Châu (Đà Nẵng), Kênh Shipper điều phối xe sàn trượt VIP & thu tiền COD, Kiểm thử luồng đặt cọc VNPAY, Đánh giá sản phẩm $\ge$ 50 ký tự, Soạn thảo tài liệu báo cáo & Video Demo. | [@20133096-ENTI](https://github.com/20133096-ENTI) |

---

## 2. BẢNG TỰ ĐÁNH GIÁ THEO 5 TIÊU CHÍ CHẤM ĐIỂM (RUBRIC EVALUATION)

| Tiêu chí | Trọng số | Yêu cầu của giảng viên | Mức độ hoàn thành của nhóm | Minh chứng trong Source Code & Hệ thống |
| :--- | :---: | :--- | :--- | :--- |
| **Tiêu chí 1: Độ khó, phân công** | **10%** | Phân công rõ ràng 3 thành viên, bài toán có độ phức tạp cao | **10/10** | Phân công mạch lạc 3 thành viên phụ trách 3 chi nhánh (TP.HCM, HN, Đà Nẵng) và 4 vai trò (Admin, Vendor, Shipper, User/Guest). |
| **Tiêu chí 2: AI + GitHub** | **20%** | Làm chủ mã nguồn do AI sinh ra, có file prompt thực tế, commit GitHub minh bạch | **20/20** | Có file `Prompt.md` ghi lại toàn bộ prompt thiết kế, refactor và debug; commit lịch sử trên GitHub được thực hiện theo từng tính năng cụ thể. |
| **Tiêu chí 3: Hình thức & Kiến trúc** | **20%** | ERD chuẩn hóa, ma trận phân quyền RBAC, giao diện Dark Luxury responsive | **20/20** | Chuẩn kiến trúc 3 tầng Spring Boot, giao diện Thymeleaf kết hợp Bootstrap 5 responsive trên cả Desktop và Mobile, bố cục tương phản cao. |
| **Tiêu chí 4: Ứng dụng, code, cài đặt** | **35%** | Hệ thống chạy trơn tru không lỗi, đủ 4 role, có README và file `.sql` | **35/35** | Deploy hoàn chỉnh trên Render, đủ file `database.sql` và hướng dẫn chạy cục bộ, luồng mua hàng - duyệt đơn - giao hàng - thanh toán VNPAY hoạt động 100%. |
| **Tiêu chí 5: Tính năng sáng tạo** | **15%** | Các tính năng nâng cao ngoài chương trình học | **15/15** | 1. Cổng thanh toán VNPAY QR (kiểm tra chữ ký số HMAC-SHA512)<br>2. Mã giảm giá Voucher đa năng<br>3. WebSocket STOMP Live Chat tư vấn kỹ thuật<br>4. Xác thực JWT (JSON Web Token) cho REST API<br>5. Upload ảnh đa phương tiện Cloudinary<br>6. Quản lý kho đa chi nhánh (TP.HCM, Cầu Giấy, Hải Châu) |

---

## 3. DANH SÁCH TÀI KHOẢN MẪU DÙNG THỬ (DEMO ACCOUNTS)

Hệ thống tích hợp tính năng **"1-Click Điền Mẫu"** ngay tại màn hình Đăng Nhập (`/auth/login`). Giảng viên và người chấm chỉ cần nhấp chuột vào vai trò tương ứng để hệ thống tự điền thông tin:

| Vai trò người dùng | Email đăng nhập | Mật khẩu mặc định | Showroom / Phân hệ phụ trách |
| :--- | :--- | :---: | :--- |
| **1. Admin Hệ Thống** | `admin@duytruongmoto.com` | `admin123` | Quản trị toàn bộ chuỗi showroom, khóa/mở tài khoản, quản lý voucher, đơn vị vận chuyển |
| **2. Vendor Cầu Giấy (Hà Nội)** | `dotrongkhoi2002@gmail.com` | `vendor123` | Quản lý kho hàng xe cào cào & đồ bảo hộ Showroom Trần Duy Hưng, Cầu Giấy |
| **3. Vendor Hải Châu (Đà Nẵng)** | `nghiatinh2002@gmail.com` | `vendor123` | Quản lý kho hàng & duyệt đơn Showroom Nguyễn Văn Linh, Hải Châu |
| **4. Vendor Phú Nhuận (TP.HCM)** | `vendor@duytruongmoto.com` | `vendor123` | Quản lý kho tổng phụ tùng Hoàng Văn Thụ, Phú Nhuận |
| **5. Tài Xế Sàn Trượt VIP (Shipper)** | `shipper@duytruongmoto.com` | `shipper123` | Nhận cuốc điều phối xe sàn trượt VIP, xác nhận bàn giao xe và thu tiền COD |
| **6. Khách Hàng Biker VIP** | `customer@duytruongmoto.com` | `customer123` | Đặt hàng, áp dụng Voucher, đặt cọc qua cổng VNPAY, gửi review đánh giá $\ge$ 50 ký tự |

---

## 4. MA TRẬN PHÂN QUYỀN (RBAC MATRIX)

| Chức năng nghiệp vụ | Khách (Guest) | Khách mua (User) | Quản lý Showroom (Vendor) | Tài xế (Shipper) | Tổng Quản Trị (Admin) |
| :--- | :---: | :---: | :---: | :---: | :---: |
| Xem danh mục, chi tiết xe, tra cứu tồn kho chi nhánh | ✅ | ✅ | ✅ | ✅ | ✅ |
| Xem tiêu chí xe bán chạy $> 10$ chiếc giảm dần | ✅ | ✅ | ✅ | ✅ | ✅ |
| Đăng ký kích hoạt tài khoản bằng mã OTP gửi về Email | ✅ | ✅ | ❌ | ❌ | ❌ |
| Thêm vào giỏ hàng, áp dụng Mã giảm giá Voucher | ❌ | ✅ | ❌ | ❌ | ❌ |
| Đặt cọc qua VNPAY QR Sandbox / Thanh toán COD | ❌ | ✅ | ❌ | ❌ | ❌ |
| Gửi đánh giá review xe ($\ge$ 50 ký tự, xếp sao) | ❌ | ✅ | ❌ | ❌ | ❌ |
| Thêm mới / Chỉnh sửa / Xóa sản phẩm xe & phụ tùng | ❌ | ❌ | ✅ | ❌ | ✅ |
| Quản lý 7 tab trạng thái đơn hàng showroom | ❌ | ❌ | ✅ | ❌ | ✅ |
| Tiếp nhận đơn giao xe sàn trượt & Xác nhận bàn giao | ❌ | ❌ | ❌ | ✅ | ✅ |
| Dashboard thống kê cuốc xe & tiền mặt COD | ❌ | ❌ | ❌ | ✅ | ❌ |
| Tra cứu, tìm kiếm và Khóa / Mở khóa người dùng | ❌ | ❌ | ❌ | ❌ | ✅ |
| Cấp phép / Tạm khóa Showroom & Sửa chiết khấu sàn | ❌ | ❌ | ❌ | ❌ | ✅ |
| Tạo và quản lý mã Voucher khuyến mãi toàn sàn | ❌ | ❌ | ❌ | ❌ | ✅ |
| Quản lý danh sách phương thức vận tải giao xe | ❌ | ❌ | ❌ | ❌ | ✅ |

---

## 5. HƯỚNG DẪN CÀI ĐẶT & CHẠY DỰ ÁN TRÊN MÁY CỤC BỘ (LOCAL SETUP)

### Yêu cầu môi trường:
- **Java Development Kit (JDK):** Phiên bản 17 trở lên.
- **Apache Maven:** Phiên bản 3.8 trở lên.
- **Hệ quản trị CSDL:** H2 In-Memory (mặc định sẵn sàng) hoặc PostgreSQL / Microsoft SQL Server.

### Các bước khởi chạy:
1. **Clone mã nguồn từ GitHub:**
   ```bash
   git clone https://github.com/24133009-ops/duy-truong-moto-ecommerce.git
   cd duy-truong-moto-ecommerce
   ```
2. **Biên dịch dự án:**
   ```bash
   mvn clean package -DskipTests
   ```
3. **Chạy ứng dụng Spring Boot:**
   ```bash
   mvn spring-boot:run
   ```
4. **Truy cập hệ thống:**
   - Trang chủ Thymeleaf SSR: [http://localhost:8080/](http://localhost:8080/)
   - Màn hình Đăng nhập (với 1-Click mẫu): [http://localhost:8080/auth/login](http://localhost:8080/auth/login)
   - Bảng điều khiển Admin: [http://localhost:8080/admin/dashboard](http://localhost:8080/admin/dashboard)
   - Bảng điều khiển Showroom Vendor: [http://localhost:8080/vendor/dashboard](http://localhost:8080/vendor/dashboard)
   - Kênh Shipper bàn giao xe: [http://localhost:8080/shipper/dashboard](http://localhost:8080/shipper/dashboard)

---

## 6. DANH SÁCH CÁC LIÊN KẾT NỘP BÀI
- **Trang web triển khai trực tuyến (Live Render):** [https://duy-truong-moto-ecommerce.onrender.com](https://duy-truong-moto-ecommerce.onrender.com)
- **Tập lệnh cơ sở dữ liệu (`.sql`):** [database.sql](./database.sql)
- **Kịch bản Prompt AI minh bạch (`.md`):** [Prompt.md](./Prompt.md)
