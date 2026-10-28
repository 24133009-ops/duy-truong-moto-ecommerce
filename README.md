# DUY TRUONG MOTO - HỆ THỐNG TMĐT TÍCH HỢP QUẢN LÝ KHO ĐA CHI NHÁNH CHUỖI ĐỒ BẢO HỘ & PHỤ TÙNG MÔ TÔ

> **Dự án môn học Lập trình Web - HK1 (2026-2027)**  
> **Mô hình chuỗi cửa hàng:** Chuỗi Showroom & Cửa hàng Đồ bảo hộ, Phụ tùng mô tô **Duy Truong Moto** (3 chi nhánh: TP.HCM, Hà Nội, Đà Nẵng)  
> **Quy mô nhóm:** Đúng **3 thành viên**

---

## 1. THÔNG TIN ĐỒ ÁN & THÀNH VIÊN NHÓM 17
- **Trường:** Đại học Sư phạm Kỹ thuật TP. Hồ Chí Minh (HCMUTE)
- **Khoa:** Công nghệ Thông tin - Môn học: Lập trình Web (HK1 / 2026-2027)
- **Tên đề tài chuẩn:** **Xây dựng hệ thống thương mại điện tử tích hợp quản lý kho đa chi nhánh cho chuỗi cửa hàng trang thiết bị bảo hộ và phụ tùng xe mô tô phân khối lớn "Duy Trương Moto"**
- **Nền tảng công nghệ:** Spring Boot 3 (Backend REST API) & React 18 SPA (Frontend Dark Luxury) + SQL Server 2022

### Danh sách thành viên nhóm:
1. **Trương Quốc Duy** (MSSV: **24133009** | GitHub: [@24133009-ops](https://github.com/24133009-ops) | Email: `duytq4906@gmail.com`) - **Nhóm trưởng (Leader)**: Xây dựng Backend Spring Boot 3, Frontend React 18, CSDL SQL Server, Bảo mật Spring Security 6 RBAC, VNPAY QR, WebSocket Live Chat. Quản lý Tổng Sàn & Showroom Phú Nhuận TP.HCM.
2. **Đỗ Trọng Khôi** (MSSV: **20133056** | GitHub: [@Kdt0701](https://github.com/Kdt0701) | Email: `dotrongkhoi2002@gmail.com`) - **Thành viên**: Phân tích CSDL 19 bảng, Quản trị Store Manager Chi nhánh Cầu Giấy - Hà Nội, Quản lý kho Đồ bảo hộ (AGV, Dainese) & Phụ tùng xe.
3. **Lê Nghĩa Tình** (MSSV: **20133096** | GitHub: [@20133096-ENTI](https://github.com/20133096-ENTI) | Email: `nghiatinh2002@gmail.com`) - **Thành viên**: Đặc tả yêu cầu nghiệp vụ SRS, Quản trị Store Manager Chi nhánh Hải Châu - Đà Nẵng, Quy trình vận chuyển Shipper 6 chặng, Báo cáo Word & Video Demo.

### 🌐 Liên kết đồ án:
- **Website Demo trực tuyến:** [https://duy-truong-moto-ecommerce.onrender.com/](https://duy-truong-moto-ecommerce.onrender.com/)
- **Repository GitHub:** [https://github.com/24133009-ops/duy-truong-moto-ecommerce](https://github.com/24133009-ops/duy-truong-moto-ecommerce)
- **File Prompt:** [Prompt.md](./Prompt.md)
- **Thư mục tài liệu đồ án:** Thư mục [`Nhom_17/`](./Nhom_17/) gồm đầy đủ Báo cáo Word, CSDL SQL, Source code zip, Kịch bản Video Demo, Hướng dẫn chấm Rubric.

---

## 2. TÀI KHOẢN MẪU DÙNG THỬ (DEMO ACCOUNTS)

Hệ thống hỗ trợ tính năng **1-Click Điền tài khoản mẫu** ngay tại trang Đăng nhập giúp thao tác kiểm thử nhanh chóng:

| STT | Vai trò | Email đăng nhập | Mật khẩu | Chức năng & Phân quyền truy cập |
| :---: | :--- | :--- | :---: | :--- |
| **1** | **Admin Quản Trị (Duy Trương)** | `admin@duytruongmoto.com` | `admin123` | Quản trị toàn bộ hệ thống chuỗi 3 chi nhánh, quản lý người dùng, duyệt sản phẩm và xem báo cáo doanh thu. |
| **2** | **Khách Hàng VIP (Biker)** | `customer@gmail.com` | `user123` | Mua đồ bảo hộ, phụ tùng & xe mô tô, chọn kho lấy gần nhất, giỏ hàng DB, thanh toán VNPAY QR, đánh giá sản phẩm. |
| **3** | **Showroom Phú Nhuận (Vendor)** | `vendor@duytruongmoto.com` | `vendor123` | Quản lý kho hàng, cập nhật số lượng tồn kho và xử lý/duyệt đơn hàng thuộc Showroom Phú Nhuận (TP.HCM). |
| **4** | **Tài Xế Sàn Trượt VIP (Shipper)** | `shipper@duytruongmoto.com` | `shipper123` | Tiếp nhận và cập nhật trạng thái vận chuyển xe cứu hộ sàn trượt qua 6 giai đoạn bàn giao tận nơi. |

---

## 3. LIÊN KẾT TRUY CẬP HỆ THỐNG & CÁC PHÂN HỆ

### 🌐 Truy cập trực tiếp hệ thống:
- **Website chính thức (Online Demo):** [https://duy-truong-moto-ecommerce.onrender.com/](https://duy-truong-moto-ecommerce.onrender.com/)
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

---

## 4. 💡 CÁC TÍNH NĂNG TRỌNG TÂM LÀM NỔI BẬT MÔ HÌNH CHUỖI CỬA HÀNG

### 🛡️ 1. Bộ lọc phân loại thông minh (Smart Category Filtering)
Hệ thống phân tách mạch lạc giữa các nhóm sản phẩm nghiệp vụ:
- **Đồ cho người lái (Rider Gear):** Mũ bảo hiểm AGV Pista / LS2 Carbon, Áo giáp đua túi khí Dainese D-Air, Găng tay Alpinestars GP, Giày bảo hộ.
- **Đồ cho xe (Bike Parts):** Hệ thống pô độ Akrapovic Full Titanium, Pô SC Project WorldSBK, Phuộc Ohlins TTX GP, Dầu nhớt Motul 300V.
- **Showroom Xe Mô Tô & Cào Cào:** Dòng sản phẩm Flagship xe cào cào địa hình KTM, Husqvarna, Kawasaki KLX và siêu môtô Ducati, BMW Motorrad.

### 📍 2. Tra cứu tồn kho theo từng chi nhánh (Branch Inventory Lookup)
- Khi khách hàng xem bất kỳ sản phẩm nào, hệ thống hiển thị bảng trạng thái tồn kho thời gian thực tại cả 3 Showroom:
  - 🟢 **Showroom Phú Nhuận (TP.HCM):** Còn hàng (Giao hỏa tốc 2h hoặc nhận tại showroom).
  - 🟢 **Showroom Cầu Giấy (Hà Nội):** Còn hàng (Giao trong ngày hoặc nhận tại showroom).
  - 🟡 **Showroom Hải Châu (Đà Nẵng):** Điều chuyển liên kho từ TP.HCM trong 24h - 48h.
- Khách hàng có thể nhấn nút **"Chọn xuất kho từ chi nhánh này"** để tối ưu thời gian giao nhận.

### 🔐 3. Phân quyền Admin đa cấp (Role-Based Access Control - RBAC)
- **Cấp 3 - Super Admin (Tổng Quản Trị Hệ Thống):** Giám sát toàn bộ 3 chi nhánh, quản trị doanh thu toàn chuỗi, phân quyền tài khoản.
- **Cấp 2 - Store Manager / Vendor (Quản Lý Chi Nhánh):** Chỉ quản lý kho hàng, nhập xuất kho và duyệt đơn của chi nhánh mình phụ trách.
- **Cấp 2 - Staff Shipper (Nhân Viên Giao Nhận):** Theo dõi lộ trình điều phối xe sàn trượt VIP qua 6 chặng bàn giao.
- **Cấp 1 - Customer (Thành Viên Biker VIP):** Giỏ hàng DB đa thiết bị, thanh toán cọc VNPAY, gửi đánh giá $\ge$ 50 ký tự.
- **Cấp 0 - Guest (Khách Vãng Lai):** Xem kho hàng, lọc sản phẩm bán chạy kỷ lục $> 10$ chiếc.
