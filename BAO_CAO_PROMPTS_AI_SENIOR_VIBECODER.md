# BÁO CÁO PHƯƠNG PHÁP & TẬP PROMPTS AI SENIOR VIBE-CODING TOÀN DIỆN
### HỆ THỐNG WEBSITE CHUỖI CỬA HÀNG SIÊU MÔ TÔ PKL & XE LUXURY "DUY TRƯƠNG MOTOR"
**Khoa Công Nghệ Thông Tin – Trường Đại Học Sư Phạm Kỹ Thuật TP. Hồ Chí Minh (HCMUTE)**  
**Môn học:** Lập trình Web – HK1 Năm học 2026-2027 | **Đề tài:** 02 (Nhóm 17)

---

## 📌 THÔNG TIN NHÓM THỰC HIỆN
| STT | Họ và Tên | MSSV | Vai Trò & Nhiệm Vụ |
| :---: | :--- | :---: | :--- |
| **1** | **Trương Quốc Duy** | **24133009** | **Nhóm trưởng (Leader)** – Thiết kế kiến trúc Prompt, Fullstack Spring Boot 3 + React 18 SPA, CSDL, Bảo mật Spring Security 6 & JWT, VNPAY, WebSocket |
| **2** | **Đỗ Trọng Khôi** | **20133056** | **Thành viên** – Phân tích mô hình ERD, Kiểm thử QA Prompt, Dữ liệu xe cào cào/mô tô, Kịch bản test luồng |
| **3** | **Lê Nghĩa Tình** | **20133096** | **Thành viên** – Đặc tả nghiệp vụ SRS, Quy trình vận chuyển Shipper 6 chặng, Báo cáo Word & Kịch bản Video Demo |

- **Kho mã nguồn GitHub:** [https://github.com/24133009-ops/duy-truong-moto-ecommerce](https://github.com/24133009-ops/duy-truong-moto-ecommerce)
- **Website Live Demo:** [https://duytruong-motor.vercel.app](https://duytruong-motor.vercel.app)
- **Backend API RESTful:** [https://duy-truong-moto-ecommerce.onrender.com](https://duy-truong-moto-ecommerce.onrender.com)

---

## 💎 PHẦN 1. TRIẾT LÝ "SENIOR VIBE-CODING" & KHUNG NGUYÊN TẮC PROMPTING 5 TẦNG

Trong quá trình phát triển dự án, Nhóm 17 không sử dụng các câu lệnh sơ sài (naive prompts) như *"hãy viết web bán xe"*. Nhóm áp dụng phương pháp **Senior Vibe-Coding** có kiểm soát kỹ thuật cao, kết hợp giữa **Domain-Driven Design (DDD)**, **Context Engineering** và **Defensive Programming**.

### Khung Nguyên Tắc Prompting 5 Tầng (The 5-Layer Prompt Framework)
```
┌────────────────────────────────────────────────────────┐
│ Layer 5: Metacognition & Verification (Self-Correction)│
├────────────────────────────────────────────────────────┤
│ Layer 4: Defensive Coding & Best Practices (OWASP Top) │
├────────────────────────────────────────────────────────┤
│ Layer 3: Chain-of-Thought (CoT) & Step Decomposition   │
├────────────────────────────────────────────────────────┤
│ Layer 2: Domain Context & Industry Benchmark           │
├────────────────────────────────────────────────────────┤
│ Layer 1: Persona & System Framing (Senior Architect)   │
└────────────────────────────────────────────────────────┘
```

1. **Layer 1 – Persona & System Framing:** Định vị AI vào vai trò Senior Software Architect & Tech Lead 10+ năm kinh nghiệm trong các hệ thống E-Commerce xe sang và Fintech.
2. **Layer 2 – Domain Context & Industry Benchmark:** Cung cấp bối cảnh thực tế: Chuỗi showroom Duy Trương Motor tham chiếu chuẩn mực giao diện và quy trình bán hàng của **Vương Khang Motor (vuongkhangmotor.com)**.
3. **Layer 3 – Chain-of-Thought (CoT) & Step Decomposition:** Bẻ nhỏ bài toán phức tạp thành các bước tuần tự (Entities $\rightarrow$ Repository $\rightarrow$ Service $\rightarrow$ Controller $\rightarrow$ Frontend Component).
4. **Layer 4 – Defensive Coding & Best Practices:** Ép buộc các quy chuẩn: Dùng `BigDecimal` cho giá tiền, mã hóa BCrypt, cơ chế kiểm tra `sold > 10` cho Guest, review $\ge$ 50 ký tự, chống lỗi N+1 Query.
5. **Layer 5 – Metacognition & Verification:** Bắt AI tự review code theo các tiêu chí rubric chấm điểm của giảng viên trước khi kết xuất mã nguồn.

---

## 🚀 PHẦN 2. TẬP PROMPTS THỰC CHIẾN THEO CÁC GIAI ĐOẠN DỰ ÁN

### 🎯 Prompt 1. Phân Tích Nghiệp Vụ & Thiết Kế CSDL Chuỗi Showroom (19 Bảng Quan Hệ)
```markdown
[SYSTEM PROMPT]
You are a Principal Database Architect specialized in high-concurrency Luxury E-Commerce Systems. 
We are building a production-grade web application: 'DUY TRƯƠNG MOTOR' - a multi-store luxury motorcycle 
and supercar dealership chain in Vietnam (Showroom Quận 1 HCM, Cầu Giấy Hà Nội, Hải Châu Đà Nẵng), 
benchmarked against the market-leading UI/UX of Vương Khang Motor (vuongkhangmotor.com).

[TASK & OBJECTIVE]
Design an enterprise-grade Relational Database Schema (PostgreSQL 15 / Microsoft SQL Server 2022) with 19 distinct tables:
1. User & Identity: users, roles, user_roles, user_levels (Silver, Gold, Diamond VIP with dynamic discount rates), addresses.
2. Multi-Store Architecture: stores, commissions (platform fee for vendor showroom partners).
3. Product & Catalog: categories (supporting slug hierarchy: oto, dirtbike, superbike, hyper-naked, cruiser, adventure, scooter, gear-parts), 
   products (MUST use DECIMAL(18,2) for vehicle prices reaching 30 Billion VND, inventory quantity, sold counter, specifications: 
   engine capacity, horsepower, odo, year, badges JSON), styles, style_values, product_style_values.
4. Transaction & Orders: orders (status: PENDING, CONFIRMED, PREPARING, SHIPPING, DELIVERED, COMPLETED, CANCELLED), order_items, deliveries.
5. Cart & Interactions: carts (database-backed cart associated with user_id, ensuring cross-device persistence), cart_items, 
   wishlists, reviews (MUST enforce strict constraint: content length >= 50 characters, rating 1-5 stars).
6. Real-time Communication: chat_messages (sender, receiver, content, timestamps for STOMP WebSocket).

[OUTPUT CONSTRAINTS]
- Provide clean, idempotent DDL scripts with FOREIGN KEY CASCADE, proper INDEXES on slug, brand, price, and category_id.
- Ensure all datetime fields use TIMESTAMP WITH TIME ZONE (PostgreSQL) or DATETIME2 (SQL Server).
- Prevent floating-point rounding errors for currency calculations.
```
> **Kết quả đạt được:** File CSDL [duytruong_moto_db.sql](file:///d:/WEB/duytruong_moto_db.sql) chuẩn hóa 19 bảng với khóa ngoại, chỉ mục tìm kiếm và dữ liệu 60 mẫu xe siêu sang.

---

### 🎯 Prompt 2. Xây Dựng Backend Spring Boot 3 & Bảo Mật Spring Security 6 (5 Vai Trò & Email OTP)
```markdown
[ROLE]: Senior Java Backend Architect (Spring Boot 3.2.5, Java 17 LTS, Spring Security 6).

[PROBLEM TO SOLVE]:
We need a robust, production-ready Authentication and Authorization subsystem supporting 5 User Roles:
- ROLE_GUEST: Can view products, top > 10 sold bikes, calculate loan installments, and view store locations.
- ROLE_USER: Can manage DB-cart, place orders, make VNPAY payments, submit reviews (>= 50 chars), chat with staff.
- ROLE_VENDOR: Manages store inventory, updates bike status, views showroom sales commissions.
- ROLE_SHIPPER: Dedicated mobile-responsive portal to accept and transition orders through 6 delivery stages.
- ROLE_ADMIN: Full system control, user level point upgrades, revenue dashboards, store management.

[IMPLEMENTATION REQUIREMENTS]:
1. JwtAuthenticationFilter: Stateless filter intercepting Authorization: Bearer <token>, parsing HMAC-SHA256 secret.
2. SecurityFilterChain: Clean Lambda DSL configuration:
   - Permit public access to: /, /products/**, /api/auth/**, /css/**, /js/**, /images/**, /ws/**.
   - Enforce role-based guards on /admin/**, /vendor/**, /shipper/**, /checkout/**.
   - Configure CORS allowing frontend origins: http://localhost:5173, https://duytruong-motor.vercel.app.
3. Email OTP Engine:
   - Use JavaMailSender with TLS port 587.
   - Generate cryptographically secure 6-digit OTP via SecureRandom.
   - Persist OTP in User table with expiration timestamp (LocalDateTime.now().plusMinutes(5)).
   - Format rich luxury HTML email template with Duy Truong Motor gold branding.
```
> **Kết quả đạt được:** Đăng ký tài khoản nhận ngay mã OTP kích hoạt qua Gmail SMTP thật (`duytq4906@gmail.com`), bảo mật phân quyền 5 vai trò không kẽ hở.

---

### 🎯 Prompt 3. Tích Hợp Cổng Thanh Toán VNPAY Sandbox & WebSocket STOMP Live Chat
```markdown
[CONTEXT]: E-Commerce Luxury Bike Showroom Checkout Flow.
[TASK 1: VNPAY GATEWAY INTEGRATION]
Implement VnPayService and VnPayController:
1. Generate secure transaction payload following VNPAY API v2.1.0 specifications.
2. Construct parameter map with alphabetical sorting: vnp_Version, vnp_Command, vnp_TmnCode, 
   vnp_Amount (multiply by 100), vnp_CurrCode='VND', vnp_TxnRef, vnp_OrderInfo, vnp_ReturnUrl, vnp_IpAddr.
3. Calculate Secure Hash using HMAC-SHA512 algorithm with secret key.
4. Implement Return URL handler validating checksum, updating Order status to 'CONFIRMED' on response code '00'.
5. Render a luxury payment waiting modal with dynamic QR Code and a 15-minute countdown timer.

[TASK 2: WEBSOCKET STOMP LIVE CHAT]
Implement WebSocketConfig with @EnableWebSocketMessageBroker:
1. Register StompEndpoint at '/ws' with SockJS fallback and CORS allowed origins.
2. Enable simple broker at '/topic' (public showroom broadcasts) and '/queue' (private 1-on-1 advisor chats).
3. Create ChatMessage entity with sender, receiver, content, showroomId, and timestamp.
4. Implement ChatController handling @MessageMapping('/chat.sendMessage') with auto-reply advisor AI bot.
```
> **Kết quả đạt được:** Khách hàng thanh toán quét mã QR VNPAY thật, xác thực chữ ký số HMAC-SHA512 tự động cập nhật đơn hàng; live chat WebSocket mượt mà.

---

### 🎯 Prompt 4. Thiết Kế Giao Diện Frontend React 18 Dark Luxury & Vanilla CSS Đẳng Cấp
```markdown
[PERSONA]: Principal Frontend UI/UX Designer & Senior React Engineer.
[AESTHETIC PHILOSOPHY]:
Design a state-of-the-art Dark Luxury aesthetic inspired by high-end supercar showrooms:
- Color Tokens: Deep Obsidian (#080a10, #0f1422), Titanium Gold (#d4af37, #ffd700), Carbon Fiber accents (#1e2640).
- Typography: Montserrat, Outfit, Roboto - crisp contrast, bold racing lettering.
- Strict Rule: DO NOT USE TAILWIND. Write 100% pure Vanilla CSS with modular custom properties (--gold-primary, etc.).

[KEY UI COMPONENTS TO IMPLEMENT]:
1. Left Vertical Mega Menu & Frosted Glass Panel:
   - Sidebar tabs: MOTOR, Ô TÔ, XE NHỎ, PHỤ KIỆN, BOOKING, DỊCH VỤ, BLOG.
   - Interactive flyout panel with backdrop-filter: blur(25px), gold border glow, and organized multi-column brands grid.
2. Dynamic Catalog & Live Filter System:
   - Category Pills: Tất Cả, Siêu Xe Ô Tô, Xe Cào Cào Địa Hình, Superbike, Xe Nhỏ, Hyper Naked, Cruiser, Phụ Kiện.
   - Dual Brand Filter: Normalizing Vietnamese diacritics via normalize('NFD').replace(/[\u0300-\u036f]/g, '').
   - Price range slider up to 30 Billion VND.
3. Floating Role Switcher HUD (Top/Bottom fixed):
   - Fast-switch active persona between Guest, User, Vendor, Shipper, Admin to demo all grading requirements seamlessly.
4. Luxury Bike Cards:
   - High-res authentic bike images, gold price tag, horsepower/engine chips, badge pills ('HQCN', 'Bao Test Hãng').
   - Quick Add-to-Cart with flying micro-animation and drawer slide-out.
```
> **Kết quả đạt được:** Giao diện SPA Dark Luxury tại [duytruong-motor.vercel.app](https://duytruong-motor.vercel.app), hiệu ứng mượt mà và thanh dock chuyển nhanh 5 vai trò cho hội đồng chấm thi.

---

### 🎯 Prompt 5. Debugging Chuyên Sâu, Xử Lý Lỗi Tiếng Việt & Đồng Bộ Toàn Bộ 60 Sản Phẩm
```markdown
[SENIOR DEBUGGING & REFACTORING PROMPT]:
Analyze and resolve the following synchronization gaps between Frontend and Backend:

1. Data Desynchronization:
   - Frontend mockData.js contains 60 luxury vehicles across 9 categories (including Kia Carnival, Volkswagen Teramont, 
     KTM 450 EXC, AGV Helmets, Dainese Suits, Ohlins Shocks).
   - Backend DataInitializer.java was skipping initialization when userRepository.count() > 0.
   - REFACTOR: Modify DataInitializer.run() to inspect products by unique slug. If any product is absent, 
     persist it immediately with full specifications (price, promotionalPrice, engine, color, brand, store, rating, sold > 10).

2. Diacritics Brand Matching:
   - When a user clicks 'MŨ BẢO HIỂM AGV' in Mega Menu, regex /[^a-z0-9]/g strips accents to 'mbohimagv'.
   - REFACTOR: Implement Unicode NFD normalization (normalize('NFD').replace(/[\u0300-\u036f]/g, '')) 
     and map Mega Menu clicks to clean brand identifiers ('AGV', 'DAINESE', 'ALPINESTARS', 'SC PROJECT', 'OHLINS').

3. Controller Query Parameter Binding:
   - ProductController previously only accepted Long categoryId, ignoring slug queries like '?category=oto'.
   - REFACTOR: Add @RequestParam(required = false) String category, resolve slug via CategoryRepository.findBySlug(), 
     and pass resolved ID to filterProducts service.
```
> **Kết quả đạt được:** Xóa sạch lỗi lọc rỗng ("0 mẫu xe"), đồng bộ 100% toàn bộ 60 siêu xe và đồ bảo hộ trên cả 2 nền tảng.

---

## 📊 PHẦN 3. BẢNG MAPPING GIỮA PROMPTS VÀ CÁC TIÊU CHÍ RUBRIC CHẤM ĐIỂM
| Tiêu Chí Rubric Đề Tài 2 | Yêu Cầu Cốt Lõi Của Giảng Viên | Prompt Áp Dụng | Minh Chứng Trong Mã Nguồn |
| :--- | :--- | :---: | :--- |
| **1. Kiến trúc CSDL & Multi-Store** | 19 bảng CSDL quan hệ, hỗ trợ chuỗi nhiều showroom, tính chiết khấu hoa hồng. | **Prompt 1** | `duytruong_moto_db.sql`, 19 JPA Entities, `StoreRepository`, `Commission` |
| **2. Phân quyền 5 vai trò & OTP** | Guest, User, Vendor, Shipper, Admin. Đăng ký tài khoản gửi OTP về email. | **Prompt 2** | `SecurityConfig.java`, `JwtUtils.java`, `EmailOtpService.java` (Gmail SMTP) |
| **3. Giỏ hàng & Đánh giá >= 50 ký tự** | Giỏ hàng lưu database cho User đăng nhập; Review bắt buộc $\ge$ 50 ký tự. | **Prompt 1 & 2** | `CartService.java`, `CartItem.java`, `ReviewService.java` (`length >= 50`) |
| **4. Thanh toán VNPAY & QR Code** | Tích hợp cổng VNPAY Sandbox thật, kiểm tra checksum HMAC-SHA512, modal mã QR. | **Prompt 3** | `VnPayConfig.java`, `VnPayService.java`, `VnPayController.java` |
| **5. Tư vấn Chat Real-time** | Chat thời gian thực giữa khách hàng và showroom tư vấn qua WebSocket. | **Prompt 3** | `WebSocketConfig.java`, `ChatController.java`, STOMP `/topic`, `/queue` |
| **6. Phân hệ Vận chuyển Shipper** | Quy trình giao xe sàn trượt VIP 6 chặng, cập nhật trạng thái đơn hàng. | **Prompt 2 & 4** | `ShipperController.java`, `shipper-portal`, 6 stage tracker |
| **7. Giao diện Dark Luxury SPA** | Giao diện đẳng cấp Vương Khang Motor, Mega Menu, không dùng Tailwind, Role HUD. | **Prompt 4 & 5** | `frontend/src/App.jsx`, `index.css`, Mega Menu Frosted Glass |

---

## 🏆 KẾT LUẬN
Phương pháp **Senior Vibe-Coding** của Nhóm 17 đã chứng minh khả năng làm chủ công nghệ và tư duy kiến trúc phần mềm chuyên nghiệp. AI không thay thế tư duy của sinh viên mà đóng vai trò là một **Copilot thông minh**, giúp tối ưu hóa thời gian thực thi, đảm bảo chuẩn bảo mật và mang đến trải nghiệm người dùng hoàn hảo nhất cho Đề tài 2.
