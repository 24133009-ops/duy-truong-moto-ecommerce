# DUY TRUONG MOTOR - HỆ THỐNG THƯƠNG MẠI ĐIỆN TỬ PHÂN PHỐI MÔ TÔ & ĐỒ BẢO HỘ ĐA CHI NHÁNH

> **ĐỒ ÁN MÔN HỌC: LẬP TRÌNH WEB (HK1 / 2026-2027)**  
> **Trường Đại học Sư phạm Kỹ thuật TP. Hồ Chí Minh (HCMUTE)**  
> **Nền tảng chính nộp đồ án:** Spring Boot 3.2 + Thymeleaf SSR + Spring Security 6 + JPA Hibernate  
> **Trang web triển khai trực tuyến (Live Render):** [https://duy-truong-moto-ecommerce.onrender.com](https://duy-truong-moto-ecommerce.onrender.com)  
> **Repository GitHub:** [https://github.com/24133009-ops/duy-truong-moto-ecommerce](https://github.com/24133009-ops/duy-truong-moto-ecommerce)  
> **Kịch bản Prompt AI:** [Prompt.md](./Prompt.md) | **Tập lệnh cơ sở dữ liệu:** [database.sql](./database.sql)

---

## 1. THÔNG TIN THÀNH VIÊN VÀ BẢNG PHÂN CÔNG 4 NHÓM ROLE (TIÊU CHÍ 1 - 10%)

Để đảm bảo đáp ứng chuẩn xác tiêu chí đánh giá theo 4 nhóm vai trò của đề tài môn học (**User + Guest**, **Vendor**, **Admin**, **Shipper**), nhóm phân công nhiệm vụ chuyên biệt cho từng thành viên như sau:

| STT | Họ và tên | MSSV | Vai trò chính phụ trách | Nhiệm vụ chi tiết trong hệ thống | GitHub Profile |
| :---: | :--- | :---: | :--- | :--- | :--- |
| **1** | **Trương Quốc Duy** *(Nhóm trưởng)* | **24133009** | **ADMIN** / Kiến trúc hệ thống | • Thiết kế kiến trúc 3 tầng (Controller - Service - Repository).<br>• Cấu hình Spring Security 6, RBAC phân quyền chặt chẽ, chống lỗ hổng IDOR.<br>• Tích hợp Cổng thanh toán VNPAY QR Sandbox, chữ ký số HMAC-SHA512.<br>• Tích hợp WebSocket STOMP Live Chat, dịch vụ gửi Mail OTP qua Brevo API HTTPS.<br>• Phân hệ Admin: Quản lý người dùng (khóa/mở), cấp phép Showroom, thống kê toàn sàn. | [@24133009-ops](https://github.com/24133009-ops) |
| **2** | **Đỗ Trọng Khôi** | **20133056** | **USER + GUEST** / Thiết kế CSDL | • Thiết kế cấu trúc CSDL 19 thực thể quan hệ, xuất và chuẩn hóa `database.sql`.<br>• Phân hệ Khách (Guest): Trang chủ Dark Luxury, Mega Menu thương hiệu, bộ lọc tìm kiếm đa tiêu chí (giá, hãng, chi nhánh), phân trang xe mới/bán chạy $>10$ xe.<br>• Phân hệ Khách hàng (User): Đăng ký kích hoạt OTP, Giỏ hàng DB, Đàm phán giá sàn (Floor Price), áp dụng Voucher, Đặt cọc VNPAY/COD, Lịch sử đơn hàng, Đánh giá xe $\ge 50$ ký tự. | [@Kdt0701](https://github.com/Kdt0701) |
| **3** | **Lê Nghĩa Tình** | **20133096** | **VENDOR & SHIPPER** / QA & Video | • Phân hệ Quản lý Showroom (Vendor): Quản lý tồn kho xe/phụ tùng, thêm/sửa sản phẩm, xử lý đơn hàng qua 7 trạng thái, bàn giao xe cho shipper.<br>• Phân hệ Giao nhận (Shipper): Tiếp nhận cuốc xe sàn trượt VIP, cập nhật 6 giai đoạn vận chuyển, xác nhận bàn giao tận nơi và thu tiền mặt COD.<br>• Kiểm thử chức năng (QA), viết kịch bản và thực hiện Video Demo đồ án. | [@20133096-ENTI](https://github.com/20133096-ENTI) |

---

## 2. BẢNG TỰ ĐÁNH GIÁ THEO 5 TIÊU CHÍ CHẤM ĐIỂM (RUBRIC EVALUATION)

| Tiêu chí | Trọng số | Yêu cầu của giảng viên | Mức độ tự đánh giá | Minh chứng trong Source Code & Hệ thống |
| :--- | :---: | :--- | :---: | :--- |
| **Tiêu chí 1: Độ khó, phân công** | **10%** | Phân công rõ ràng từng người theo đủ 4 nhóm role, bài toán phức tạp | **10/10** | Phân công bao phủ trọn vẹn 4 nhóm vai trò (Duy: Admin; Khôi: User+Guest; Tình: Vendor & Shipper), không trùng lặp vai trò. |
| **Tiêu chí 2: AI + GitHub** | **20%** | Sử dụng AI thành thạo, làm chủ mã nguồn, commit lịch sử minh bạch | **20/20** | Có file `Prompt.md` lưu lại toàn bộ câu lệnh tương tác, mỗi thành viên hiểu rõ code của mình; lịch sử git commit phân nhánh rõ ràng. |
| **Tiêu chí 3: Hình thức & Giao diện** | **20%** | Cấu trúc dữ liệu chuẩn, sơ đồ ERD, giao diện responsive Desktop/Mobile | **20/20** | Thiết kế Dark Luxury tương phản cao, chuẩn UI xe phân khối lớn, responsive mượt mà từ 375px đến 1920px, sơ đồ ERD chi tiết trong báo cáo. |
| **Tiêu chí 4: Ứng dụng, code, cài đặt** | **35%** | Hệ thống chạy trơn tru, đủ 4 role, file `.sql` chuẩn xác, cài đặt dễ dàng | **35/35** | Deploy hoạt động ổn định trên Render; file `database.sql` chuẩn hóa với mật khẩu BCrypt; toàn bộ luồng nghiệp vụ 4 role hoạt động 100%. |
| **Tiêu chí 5: Tính năng sáng tạo** | **15%** | Các tính năng mở rộng ngoài chương trình học (Yêu cầu $\ge 7-8$ nội dung để đạt trọn vẹn 15 điểm) | **15/15** *(Đạt trọn vẹn với 8+ tính năng)* | **Nhóm phát triển thành công 8 tính năng nâng cao chuyên sâu** có mã nguồn thực tế và kiểm thử tự động, vượt mức 7-8 tính năng theo yêu cầu thang điểm tối đa (Xem chi tiết tại Mục 5). |

---

## 3. DANH SÁCH TÀI KHOẢN MẪU DÙNG THỬ (DEMO ACCOUNTS)

Hệ thống tích hợp tính năng **"1-Click Điền Tài Khoản Mẫu"** ngay tại màn hình Đăng Nhập (`/auth/login`). Giảng viên và người chấm chỉ cần nhấp chuột vào vai trò tương ứng để hệ thống tự điền thông tin:

| STT | Vai trò người dùng | Email đăng nhập | Mật khẩu mặc định | Showroom / Phân hệ phụ trách |
| :---: | :--- | :--- | :---: | :--- |
| **1** | **Admin Hệ Thống (Trương Quốc Duy)** | `admin@duytruongmoto.com` | `admin123` | Quản trị toàn bộ chuỗi showroom, khóa/mở người dùng, quản lý voucher, đơn vị vận chuyển |
| **2** | **Vendor Cầu Giấy HN (Đỗ Trọng Khôi)** | `dotrongkhoi2002@gmail.com` | `vendor123` | Quản lý kho hàng xe cào cào & đồ bảo hộ Showroom Trần Duy Hưng, Cầu Giấy |
| **3** | **Vendor Hải Châu ĐN (Lê Nghĩa Tình)** | `nghiatinh2002@gmail.com` | `vendor123` | Quản lý kho hàng & duyệt đơn Showroom Nguyễn Văn Linh, Hải Châu |
| **4** | **Vendor Phú Nhuận TP.HCM (Minh Quản Lý)** | `vendor@duytruongmoto.com` | `vendor123` | Quản lý kho tổng phụ tùng Hoàng Văn Thụ, Phú Nhuận |
| **5** | **Tài Xế Sàn Trượt VIP (Shipper)** | `shipper@duytruongmoto.com` | `shipper123` | Nhận cuốc điều phối xe sàn trượt VIP, xác nhận bàn giao 6 giai đoạn và thu COD |
| **6** | **Khách Hàng Biker VIP (User)** | `customer@duytruongmoto.com` | `customer123` | Mua sắm xe, áp dụng Voucher, đặt cọc qua cổng VNPAY, gửi review đánh giá $\ge$ 50 ký tự |

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
| Thêm mới / Chỉnh sửa / Xóa sản phẩm xe & phụ tùng | ❌ | ❌ | ✅ *(kho mình)* | ❌ | ✅ |
| Quản lý 7 tab trạng thái đơn hàng showroom | ❌ | ❌ | ✅ | ❌ | ✅ |
| Tiếp nhận đơn giao xe sàn trượt & Bàn giao 6 giai đoạn | ❌ | ❌ | ❌ | ✅ | ✅ |
| Dashboard thống kê cuốc xe & tiền mặt COD | ❌ | ❌ | ❌ | ✅ | ❌ |
| Tra cứu, tìm kiếm và Khóa / Mở khóa người dùng | ❌ | ❌ | ❌ | ❌ | ✅ |
| Cấp phép / Tạm khóa Showroom & Sửa chiết khấu sàn | ❌ | ❌ | ❌ | ❌ | ✅ |
| Tạo và quản lý mã Voucher khuyến mãi toàn sàn | ❌ | ❌ | ❌ | ❌ | ✅ |
| Quản lý danh sách phương thức vận tải giao xe | ❌ | ❌ | ❌ | ❌ | ✅ |

---

## 5. MINH CHỨNG CHI TIẾT 8 TÍNH NĂNG SÁNG TẠO ĐẠT TRỌN VẸN 15/15 ĐIỂM (TIÊU CHÍ 5)

Theo quy định đánh giá tiêu chí Sáng tạo, dự án phải đạt từ **7 đến 8 nội dung nâng cao ngoài chương trình** để nhận mức điểm tối đa 15%. Hệ thống Duy Truong Motor đã triển khai hoàn thiện và kiểm thử thành công **8 tính năng nâng cao thực tế**:

| STT | Tên tính năng sáng tạo | Vị trí file Source Code triển khai | Ý nghĩa nghiệp vụ & Giá trị công nghệ |
| :---: | :--- | :--- | :--- |
| **1** | **Cổng thanh toán VNPAY QR Sandbox** | `com.duytruongmoto.ecommerce.service.VNPayService`<br>`com.duytruongmoto.ecommerce.controller.CheckoutController` | Tích hợp cổng thanh toán trực tuyến chính thức, xác thực mã băm chữ ký điện tử HMAC-SHA512 hai chiều, chống giả mạo trạng thái thanh toán và hỗ trợ Idempotency an toàn. |
| **2** | **Hệ thống Mã giảm giá Voucher đa năng** | `com.duytruongmoto.ecommerce.service.VoucherService`<br>`com.duytruongmoto.ecommerce.entity.Voucher` | Hỗ trợ giảm giá theo % hoặc số tiền cố định, quản lý hạn mức sử dụng (`usage_limit`), kiểm tra đơn hàng tối thiểu (`min_order_amount`), tự động khấu trừ và khóa mã khi hết hạn. |
| **3** | **WebSocket STOMP Live Chat hai chiều** | `com.duytruongmoto.ecommerce.config.WebSocketConfig`<br>`com.duytruongmoto.ecommerce.controller.ChatController` | Kênh tư vấn kỹ thuật trực tuyến thời gian thực giữa Khách hàng và Quản lý Showroom sử dụng giao thức SockJS/STOMP qua kết nối WSS, không cần tải lại trang. |
| **4** | **Cơ chế đàm phán trả giá & Bảo vệ Giá sàn (Floor Price)** | `com.duytruongmoto.ecommerce.controller.api.ProductApiController`<br>`com.duytruongmoto.ecommerce.entity.Product` | Cho phép Biker gửi đề xuất trả giá trực tiếp trên từng xe mô tô. Backend tự động đối soát với `floorPrice` trong DB: nếu $P_{\text{proposed}} < P_{\text{floor}}$ sẽ bị từ chối tự động để bảo vệ biên lợi nhuận showroom. |
| **5** | **Khóa lạc quan (Optimistic Concurrency Control `@Version`)** | `com.duytruongmoto.ecommerce.entity.Product`<br>`com.duytruongmoto.ecommerce.service.OrderService` | Trang bị trường `@Version private Long version;` trên thực thể xe để chống hiện tượng Race Condition khi hai khách hàng cùng đặt cọc chiếc xe mô tô độc bản duy nhất trong cùng 1 tích tắc. |
| **6** | **Kiến trúc bảo mật kép: Session SSR + JWT Token cho REST API** | `com.duytruongmoto.ecommerce.security.JwtUtils`<br>`com.duytruongmoto.ecommerce.controller.api.AuthApiController` | Hệ thống vừa bảo mật giao diện Thymeleaf SSR bằng Spring Security Cookie Session, vừa cung cấp cổng RESTful API chuẩn hóa kèm xác thực Bearer JWT Token cho ứng dụng di động của Shipper. |
| **7** | **Lưu trữ & Tối ưu hóa ảnh đám mây Cloudinary** | `com.duytruongmoto.ecommerce.config.CloudinaryConfig`<br>`com.duytruongmoto.ecommerce.service.CloudinaryService` | Tự động tải lên hình ảnh xe môtô phân khối lớn và đồ bảo hộ lên Cloudinary CDN, tự động nén dung lượng và tối ưu hóa hiển thị nhanh chóng trên mọi thiết bị. |
| **8** | **Chuỗi cung ứng Đa chi nhánh & Vận tải sàn trượt VIP 6 giai đoạn** | `com.duytruongmoto.ecommerce.service.OrderService`<br>`com.duytruongmoto.ecommerce.entity.OrderStatus` | Quản lý tồn kho phân tán tại 3 miền (Phú Nhuận TP.HCM, Cầu Giấy HN, Hải Châu ĐN). Điều phối đội xe cứu hộ sàn trượt thủy lực VIP với quy trình bàn giao 6 giai đoạn khép kín và thu tiền mặt COD. |
| *Phụ* | *Dịch vụ Email OTP thích ứng đa tầng (Brevo API HTTPS)* | `com.duytruongmoto.ecommerce.service.EmailOtpService` | Vượt qua hạn chế của nền tảng Cloud Render (chặn cổng SMTP 25, 465, 587) bằng cách tích hợp trực tiếp Brevo Transactional Email REST API qua giao thức HTTPS. |

---

## 6. BẢO MẬT JWT & MINH CHỨNG REST API

Trong kiến trúc hệ thống, **JWT (JSON Web Token)** được áp dụng để bảo vệ các RESTful API endpoints thuộc tiền tố `/api/**`. Mục đích nhằm phục vụ ứng dụng di động dành cho Tài xế Shipper hoặc tích hợp hệ thống tra cứu tồn kho cho các Showroom đối tác mà không cần phụ thuộc vào HTTP Session.

### Minh chứng gọi API với JWT:

**1. Đăng nhập lấy Token (`POST /api/auth/login`):**
```bash
curl -X POST https://duy-truong-moto-ecommerce.onrender.com/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"customer@duytruongmoto.com\",\"password\":\"customer123\"}"
```
*Kết quả trả về:*
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjdXN0b21lckBkdXl0cnVvbmdtb3RvLmNvbSIsImlhdCI6MTY5ODU...\"
}
```

**2. Sử dụng Bearer Token truy vấn danh sách xe (`GET /api/products`):**
```bash
curl -X GET https://duy-truong-moto-ecommerce.onrender.com/api/products \
  -H "Authorization: Bearer <TOKEN_NHẬN_ĐƯỢC>"
```

---

## 7. THÔNG TIN THẺ TEST CỔNG VNPAY SANDBOX

Khi tiến hành kiểm thử chức năng Đặt cọc trực tuyến qua cổng **VNPAY Sandbox**, vui lòng sử dụng thông tin thẻ thử nghiệm chính thức do VNPAY cung cấp:

| Thông tin trường | Dữ liệu kiểm thử |
| :--- | :--- |
| **Ngân hàng chấp nhận:** | **NCB** (Ngân hàng Quốc Dân) |
| **Số thẻ thanh toán:** | `9704198526191432198` |
| **Tên chủ thẻ:** | `NGUYEN VAN A` |
| **Ngày phát hành:** | `07/15` |
| **Mã xác thực OTP:** | `123456` |

---

## 8. HƯỚNG DẪN CÀI ĐẶT & CHẠY DỰ ÁN TRÊN MÁY CỤC BỘ (LOCAL SETUP)

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
2. **Chạy kiểm thử đơn vị & kiểm tra phân quyền:**
   ```bash
   mvn test
   ```
3. **Biên dịch và chạy ứng dụng Spring Boot:**
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

## 9. DANH SÁCH LIÊN KẾT BÀN GIAO NỘP BÀI
- **Trang web trực tuyến (Live Render):** [https://duy-truong-moto-ecommerce.onrender.com](https://duy-truong-moto-ecommerce.onrender.com)
- **Tập lệnh cơ sở dữ liệu (`.sql`):** [database.sql](./database.sql)
- **Kịch bản Prompt AI minh bạch (`.md`):** [Prompt.md](./Prompt.md)
