import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import matplotlib.patches as patches
import numpy as np
import os

os.makedirs('d:/WEB/report_assets', exist_ok=True)

# -------------------------------------------------------------
# 1. Architecture Diagram
# -------------------------------------------------------------
def create_architecture_diagram():
    fig, ax = plt.subplots(figsize=(14, 9), dpi=300)
    ax.set_facecolor('#0b0e14')
    fig.patch.set_facecolor('#0b0e14')
    ax.set_xlim(0, 14)
    ax.set_ylim(0, 9)
    ax.axis('off')

    # Title
    ax.text(7, 8.5, 'KIẾN TRÚC TỔNG THỂ HỆ THỐNG "DUY TRƯƠNG MOTO"', 
            fontsize=18, fontweight='bold', color='#e5c07b', ha='center', va='center', fontfamily='sans-serif')
    ax.text(7, 8.1, 'Mô hình Micro-Frontend React SPA kết hợp Spring Boot 3 RESTful & SQL Server 2022', 
            fontsize=11, color='#abb2bf', ha='center', va='center', fontfamily='sans-serif')

    # Layer 1: Client
    p1 = patches.FancyBboxPatch((0.8, 5.6), 5.8, 2.0, boxstyle="round,pad=0.2", ec="#61afef", fc="#161b22", lw=2)
    ax.add_patch(p1)
    ax.text(1.1, 7.3, '1. TẦNG CLIENT (REACT SPA FRONTEND)', fontsize=12, fontweight='bold', color="#61afef")
    ax.text(1.1, 6.8, '• React 18 + Vite (SPA) trên cổng 5173\n• 100% Vanilla CSS Dark Luxury Design System\n• Role Switcher HUD (Guest, User, Vendor, Admin, Shipper)\n• WebSocket STOMP Client (Live Chat tư vấn real-time)\n• Responsive Mobile/Tablet/Desktop View', 
            fontsize=9.5, color="#e6edf3", va='top', fontfamily='sans-serif')

    # Layer 2: API Gateway & Security
    p2 = patches.FancyBboxPatch((7.4, 5.6), 5.8, 2.0, boxstyle="round,pad=0.2", ec="#98c379", fc="#161b22", lw=2)
    ax.add_patch(p2)
    ax.text(7.7, 7.3, '2. BẢO MẬT & API GATEWAY', fontsize=12, fontweight='bold', color="#98c379")
    ax.text(7.7, 6.8, '• Spring Security 6 + Stateless Session Architecture\n• JWT (JSON Web Token) HMAC-SHA256 Auth Filter\n• BCrypt Password Encryption (Strength 12)\n• CORS Filter cho React SPA giao tiếp an toàn\n• OTP Email Verification Service (TTL 5 phút)', 
            fontsize=9.5, color="#e6edf3", va='top', fontfamily='sans-serif')

    # Layer 3: Business Logic (Spring Boot Backend)
    p3 = patches.FancyBboxPatch((0.8, 3.0), 5.8, 2.0, boxstyle="round,pad=0.2", ec="#e5c07b", fc="#161b22", lw=2)
    ax.add_patch(p3)
    ax.text(1.1, 4.7, '3. TẦNG XỬ LÝ NGHIỆP VỤ (SPRING BOOT 3)', fontsize=12, fontweight='bold', color="#e5c07b")
    ax.text(1.1, 4.2, '• 10 REST & MVC Controllers (Auth, Product, Cart, Order...)\n• 9 Service Components (VnPayService, CloudinaryService...)\n• WebSocket Message Broker (/topic, /app/chat)\n• Spring Data JPA & Hibernate ORM trên Java 17 LTS\n• DataInitializer: Tự động seed dữ liệu mẫu khi start app', 
            fontsize=9.5, color="#e6edf3", va='top', fontfamily='sans-serif')

    # Layer 4: Persistence Layer (SQL Server)
    p4 = patches.FancyBboxPatch((7.4, 3.0), 5.8, 2.0, boxstyle="round,pad=0.2", ec="#c678dd", fc="#161b22", lw=2)
    ax.add_patch(p4)
    ax.text(7.7, 4.7, '4. TẦNG LƯU TRỮ DỮ LIỆU (SQL SERVER 2022)', fontsize=12, fontweight='bold', color="#c678dd")
    ax.text(7.7, 4.2, '• Database: DuyTruongMotoDB (Port 1433)\n• 19 Bảng quan hệ toàn vẹn (PK, FK, Indexes, Constraints)\n• User Levels: Đồng, Bạc, Vàng, Kim Cương\n• Quản lý chuỗi showroom: TP.HCM, Hà Nội, Đà Nẵng\n• Giỏ hàng lưu trữ Database & Lịch sử đơn hàng 6 chặng', 
            fontsize=9.5, color="#e6edf3", va='top', fontfamily='sans-serif')

    # Layer 5: Cloud Integrations
    p5 = patches.FancyBboxPatch((0.8, 0.6), 12.4, 1.8, boxstyle="round,pad=0.2", ec="#d19a66", fc="#161b22", lw=2)
    ax.add_patch(p5)
    ax.text(1.1, 2.1, '5. TÍCH HỢP DỊCH VỤ ĐÁM MÂY & BÊN THỨ BA (EXTERNAL CLOUD SERVICES)', fontsize=12, fontweight='bold', color="#d19a66")
    ax.text(1.1, 1.6, '• Cổng thanh toán VNPAY QR Sandbox: Thuật toán mã hóa HMAC-SHA512 Checksum, đếm ngược 15 phút, xác thực IPN tự động.\n• Cloudinary Cloud Media Storage: API upload trực tiếp ảnh/video xe phân khối lớn và đa phương tiện đánh giá.\n• Gmail SMTP Server (JavaMailSender): Gửi email mã OTP kích hoạt tài khoản HTML template đẹp mắt.\n• STOMP WebSocket Protocol: Kênh trao đổi dữ liệu hai chiều full-duplex cho Chatbot và tư vấn kỹ thuật trực tuyến.', 
            fontsize=9.5, color="#e6edf3", va='top', fontfamily='sans-serif')

    # Connective arrows
    ax.annotate('', xy=(7.2, 6.6), xytext=(6.8, 6.6),
                arrowprops=dict(arrowstyle="<->", color="#e5c07b", lw=2))
    ax.annotate('', xy=(3.7, 5.4), xytext=(3.7, 5.2),
                arrowprops=dict(arrowstyle="<->", color="#e5c07b", lw=2))
    ax.annotate('', xy=(10.3, 5.4), xytext=(10.3, 5.2),
                arrowprops=dict(arrowstyle="<->", color="#e5c07b", lw=2))
    ax.annotate('', xy=(7.2, 4.0), xytext=(6.8, 4.0),
                arrowprops=dict(arrowstyle="<->", color="#e5c07b", lw=2))
    ax.annotate('', xy=(7.0, 2.8), xytext=(7.0, 2.6),
                arrowprops=dict(arrowstyle="<->", color="#e5c07b", lw=2))

    plt.tight_layout()
    plt.savefig('d:/WEB/report_assets/architecture_diagram.png', dpi=300, facecolor='#0b0e14')
    plt.close()
    print("Saved architecture_diagram.png")

# -------------------------------------------------------------
# 2. ERD Diagram
# -------------------------------------------------------------
def create_erd_diagram():
    fig, ax = plt.subplots(figsize=(15, 10), dpi=300)
    ax.set_facecolor('#0f172a')
    fig.patch.set_facecolor('#0f172a')
    ax.set_xlim(0, 15)
    ax.set_ylim(0, 10)
    ax.axis('off')

    ax.text(7.5, 9.5, 'SƠ ĐỒ THỰC THỂ QUAN HỆ (ERD) - DUYTRUONGMOTODB (19 BẢNG)', 
            fontsize=17, fontweight='bold', color='#38bdf8', ha='center', va='center')

    boxes = [
        # Col 1: Users & Auth
        (0.5, 6.8, 3.2, 2.2, "USERS\n(Người dùng)", "#38bdf8", "• id (PK)\n• username, password\n• email, phone\n• is_email_active\n• role_id (FK)\n• user_level_id (FK)"),
        (0.5, 4.0, 3.2, 2.2, "ROLES & USER_LEVELS\n(Vai trò & Hạng VIP)", "#38bdf8", "ROLES: id (PK), name\n(GUEST, USER, VENDOR, ADMIN, SHIPPER)\n\nUSER_LEVELS: id (PK), name\n(Đồng, Bạc, Vàng, Kim Cương)\n• min_points, discount_rate"),
        (0.5, 1.2, 3.2, 2.2, "USER_ADDRESSES\n(Sổ địa chỉ <= 6)", "#38bdf8", "• id (PK)\n• user_id (FK)\n• recipient_name, phone\n• street, ward, district, city\n• is_default"),

        # Col 2: Stores & Products
        (4.2, 6.8, 3.2, 2.2, "STORES\n(Showroom Chuỗi)", "#4ade80", "• id (PK)\n• name, address, city\n• owner_id (FK Users)\n• is_active, rating\n(TP.HCM, Hà Nội, Đà Nẵng)"),
        (4.2, 4.0, 3.2, 2.2, "PRODUCTS\n(Xe Mô Tô PKL)", "#4ade80", "• id (PK), name, slug\n• price, promotional_price\n• engine_capacity (cc)\n• horsepower (HP)\n• sold_count (>10 guest)\n• store_id (FK), category_id (FK)"),
        (4.2, 1.2, 3.2, 2.2, "CATEGORIES & STYLES\n(Danh mục & Thuộc tính)", "#4ade80", "CATEGORIES: id (PK), name\n(Sportbike, Hypernaked, Cruiser...)\nSTYLES: id, name (Màu sắc, Phiên bản)\nSTYLE_VALUES: id, style_id, value"),

        # Col 3: Carts & Orders
        (7.9, 6.8, 3.2, 2.2, "CARTS & CART_ITEMS\n(Giỏ hàng Database)", "#fbbf24", "CARTS: id (PK), user_id (FK)\n\nCART_ITEMS:\n• id (PK), cart_id (FK)\n• product_id (FK)\n• quantity, unit_price"),
        (7.9, 4.0, 3.2, 2.2, "ORDERS & ORDER_ITEMS\n(Đơn hàng 6 chặng)", "#fbbf24", "ORDERS: id (PK), code\n• user_id (FK), store_id (FK)\n• total_amount, status\n• payment_method (COD, VNPAY)\nORDER_ITEMS: id, order_id, product_id"),
        (7.9, 1.2, 3.2, 2.2, "TRANSACTIONS & COMMISSIONS\n(Giao dịch VNPAY & Hoa hồng)", "#fbbf24", "TRANSACTIONS: id, order_id\n• amount, vnp_txn_ref, status\n\nCOMMISSIONS: id, store_id\n• order_id, rate (5%), fee"),

        # Col 4: Delivery, Review, Chat
        (11.5, 6.8, 3.0, 2.2, "DELIVERIES\n(Vận chuyển Xe Thùng VIP)", "#f43f5e", "• id (PK), order_id (FK)\n• shipper_id (FK Users)\n• tracking_code\n• vehicle_plate (Xe cứu hộ)\n• status (6 giai đoạn)"),
        (11.5, 4.0, 3.0, 2.2, "REVIEWS\n(Đánh giá >= 50 ký tự)", "#f43f5e", "• id (PK), user_id (FK)\n• product_id (FK)\n• rating (1-5 sao)\n• content (>=50 chars)\n• media_url (Cloudinary)"),
        (11.5, 1.2, 3.0, 2.2, "CHAT_MESSAGES & WISHLISTS\n(Realtime STOMP & Yêu thích)", "#f43f5e", "CHAT_MESSAGES:\n• id, sender_id, receiver_id\n• content, created_at\n\nWISHLISTS:\n• id, user_id, product_id"),
    ]

    for (x, y, w, h, title, col, text) in boxes:
        p = patches.FancyBboxPatch((x, y), w, h, boxstyle="round,pad=0.15", ec=col, fc="#1e293b", lw=1.5)
        ax.add_patch(p)
        ax.text(x + w/2, y + h - 0.25, title, fontsize=9.5, fontweight='bold', color=col, ha='center', va='top')
        ax.text(x + 0.15, y + h - 0.65, text, fontsize=7.8, color="#cbd5e1", va='top')

    plt.tight_layout()
    plt.savefig('d:/WEB/report_assets/erd_diagram.png', dpi=300, facecolor='#0f172a')
    plt.close()
    print("Saved erd_diagram.png")

# -------------------------------------------------------------
# 3. OTP Registration Flowchart
# -------------------------------------------------------------
def create_otp_flowchart():
    fig, ax = plt.subplots(figsize=(13, 6), dpi=300)
    ax.set_facecolor('#0f172a')
    fig.patch.set_facecolor('#0f172a')
    ax.set_xlim(0, 13)
    ax.set_ylim(0, 6)
    ax.axis('off')

    ax.text(6.5, 5.5, 'SƠ ĐỒ QUY TRÌNH ĐĂNG KÝ VÀ XÁC THỰC OTP QUA EMAIL (TTL 5 PHÚT)', 
            fontsize=15, fontweight='bold', color='#38bdf8', ha='center', va='center')

    steps = [
        (0.6, 2.2, 2.0, 1.6, "1. Nhập thông tin\nĐăng ký\n(Username, Email,\nPassword, Phone)", "#38bdf8"),
        (3.0, 2.2, 2.0, 1.6, "2. Mã hóa BCrypt\n& Tạo mã OTP\n6 chữ số ngẫu nhiên\nLưu cache TTL 5m", "#4ade80"),
        (5.4, 2.2, 2.0, 1.6, "3. Gửi Mail OTP\nJavaMailSender\nGửi template HTML\nđến hộp thư khách", "#fbbf24"),
        (7.8, 2.2, 2.0, 1.6, "4. Khách nhập OTP\nKiểm tra hợp lệ\n(Trùng khớp &\nchưa quá 5 phút)", "#f43f5e"),
        (10.2, 2.2, 2.2, 1.6, "5. Kích hoạt tài khoản\nisEmailActive = true\nPhân quyền ROLE_USER\nĐăng nhập JWT", "#a855f7"),
    ]

    for (x, y, w, h, txt, col) in steps:
        p = patches.FancyBboxPatch((x, y), w, h, boxstyle="round,pad=0.2", ec=col, fc="#1e293b", lw=2)
        ax.add_patch(p)
        ax.text(x + w/2, y + h/2, txt, fontsize=9.5, color="#f8fafc", ha='center', va='center', fontweight='bold')

    for i in range(len(steps)-1):
        x_from = steps[i][0] + steps[i][2] + 0.05
        x_to = steps[i+1][0] - 0.05
        ax.annotate('', xy=(x_to, 3.0), xytext=(x_from, 3.0),
                    arrowprops=dict(arrowstyle="->", color="#e2e8f0", lw=2))

    plt.tight_layout()
    plt.savefig('d:/WEB/report_assets/otp_flowchart.png', dpi=300, facecolor='#0f172a')
    plt.close()
    print("Saved otp_flowchart.png")

# -------------------------------------------------------------
# 4. VNPAY QR Flowchart
# -------------------------------------------------------------
def create_vnpay_flowchart():
    fig, ax = plt.subplots(figsize=(13, 6), dpi=300)
    ax.set_facecolor('#0f172a')
    fig.patch.set_facecolor('#0f172a')
    ax.set_xlim(0, 13)
    ax.set_ylim(0, 6)
    ax.axis('off')

    ax.text(6.5, 5.5, 'SƠ ĐỒ QUY TRÌNH THANH TOÁN VNPAY QR SANDBOX TỰ ĐỘNG XÁC THỰC', 
            fontsize=15, fontweight='bold', color='#fbbf24', ha='center', va='center')

    steps = [
        (0.6, 2.2, 2.0, 1.6, "1. Chọn VNPAY\n& Bấm Đặt Hàng\nKiểm tra tồn kho xe\nLưu đơn PENDING", "#38bdf8"),
        (3.0, 2.2, 2.0, 1.6, "2. Sinh Checksum\nHMAC-SHA512\nVnPayService tạo URL\n& Mã QR động", "#4ade80"),
        (5.4, 2.2, 2.0, 1.6, "3. Khách quét QR\nTimer đếm ngược\n15 phút chính xác\nMô phỏng thanh toán", "#fbbf24"),
        (7.8, 2.2, 2.0, 1.6, "4. Webhook / IPN\nXác thực mã hash\nKiểm tra số tiền\nGhi nhận giao dịch", "#f43f5e"),
        (10.2, 2.2, 2.2, 1.6, "5. Hoàn tất đơn hàng\nStatus -> CONFIRMED\nBắn pháo hoa Confetti\nBàn giao Shipper", "#a855f7"),
    ]

    for (x, y, w, h, txt, col) in steps:
        p = patches.FancyBboxPatch((x, y), w, h, boxstyle="round,pad=0.2", ec=col, fc="#1e293b", lw=2)
        ax.add_patch(p)
        ax.text(x + w/2, y + h/2, txt, fontsize=9.5, color="#f8fafc", ha='center', va='center', fontweight='bold')

    for i in range(len(steps)-1):
        x_from = steps[i][0] + steps[i][2] + 0.05
        x_to = steps[i+1][0] - 0.05
        ax.annotate('', xy=(x_to, 3.0), xytext=(x_from, 3.0),
                    arrowprops=dict(arrowstyle="->", color="#fbbf24", lw=2))

    plt.tight_layout()
    plt.savefig('d:/WEB/report_assets/order_vnpay_flowchart.png', dpi=300, facecolor='#0f172a')
    plt.close()
    print("Saved order_vnpay_flowchart.png")

# -------------------------------------------------------------
# 5. Shipper 6-stage Flowchart
# -------------------------------------------------------------
def create_shipper_flowchart():
    fig, ax = plt.subplots(figsize=(14, 5.5), dpi=300)
    ax.set_facecolor('#0f172a')
    fig.patch.set_facecolor('#0f172a')
    ax.set_xlim(0, 14)
    ax.set_ylim(0, 5.5)
    ax.axis('off')

    ax.text(7, 5.0, 'QUY TRÌNH VẬN CHUYỂN SIÊU MÔ TÔ 6 CHẶNG (XE CỨU HỘ/XE THÙNG CHUYÊN DỤNG)', 
            fontsize=14, fontweight='bold', color='#f43f5e', ha='center', va='center')

    stages = [
        (0.5, 1.8, 1.9, 1.8, "Chặng 1\nPENDING\nChờ xác nhận\nđơn hàng", "#94a3b8"),
        (2.7, 1.8, 1.9, 1.8, "Chặng 2\nCONFIRMED\nĐại lý duyệt đơn\n& chuẩn bị xe", "#38bdf8"),
        (4.9, 1.8, 1.9, 1.8, "Chặng 3\nPREPARING\nKiểm tra kỹ thuật\n& chằng buộc xe", "#4ade80"),
        (7.1, 1.8, 1.9, 1.8, "Chặng 4\nSHIPPING\nXe flatbed vận chuyển\nTracking thời gian thực", "#fbbf24"),
        (9.3, 1.8, 1.9, 1.8, "Chặng 5\nDELIVERED\nBàn giao tận tay\nKhách nghiệm thu", "#f43f5e"),
        (11.5, 1.8, 2.0, 1.8, "Chặng 6\nCOMPLETED\nKhách xác nhận nhận xe\nKích hoạt bảo hành", "#a855f7"),
    ]

    for (x, y, w, h, txt, col) in stages:
        p = patches.FancyBboxPatch((x, y), w, h, boxstyle="round,pad=0.15", ec=col, fc="#1e293b", lw=2)
        ax.add_patch(p)
        ax.text(x + w/2, y + h/2, txt, fontsize=9, color="#f8fafc", ha='center', va='center', fontweight='bold')

    for i in range(len(stages)-1):
        x_from = stages[i][0] + stages[i][2] + 0.05
        x_to = stages[i+1][0] - 0.05
        ax.annotate('', xy=(x_to, 2.7), xytext=(x_from, 2.7),
                    arrowprops=dict(arrowstyle="->", color="#f43f5e", lw=2))

    plt.tight_layout()
    plt.savefig('d:/WEB/report_assets/shipper_flowchart.png', dpi=300, facecolor='#0f172a')
    plt.close()
    print("Saved shipper_flowchart.png")

# -------------------------------------------------------------
# 6. Review 50-chars & Cloudinary Flowchart
# -------------------------------------------------------------
def create_review_flowchart():
    fig, ax = plt.subplots(figsize=(13, 5.5), dpi=300)
    ax.set_facecolor('#0f172a')
    fig.patch.set_facecolor('#0f172a')
    ax.set_xlim(0, 13)
    ax.set_ylim(0, 5.5)
    ax.axis('off')

    ax.text(6.5, 5.0, 'QUY TRÌNH ĐÁNH GIÁ SẢN PHẨM: VALIDATION >= 50 KÝ TỰ & CLOUDINARY CDN', 
            fontsize=14, fontweight='bold', color='#4ade80', ha='center', va='center')

    steps = [
        (0.6, 1.8, 2.0, 1.8, "1. Mở Modal\nĐánh giá\nKhách đã mua xe\nchọn số sao (1-5)", "#38bdf8"),
        (3.0, 1.8, 2.0, 1.8, "2. Nhập nhận xét\nValidation Realtime\nBộ đếm ký tự hiển thị\nBắt buộc >= 50 ký tự", "#4ade80"),
        (5.4, 1.8, 2.0, 1.8, "3. Tải ảnh / video\nChọn ảnh chụp xe\nhoặc video pô nổ\nXem trước ngay", "#fbbf24"),
        (7.8, 1.8, 2.0, 1.8, "4. Cloudinary Upload\nĐẩy tệp lên CDN\nLưu trữ đa phương tiện\nTrả về Secure URL", "#f43f5e"),
        (10.2, 1.8, 2.2, 1.8, "5. Lưu Review\nGhi nhận vào DB\nTự động cập nhật\nĐiểm sao trung bình xe", "#a855f7"),
    ]

    for (x, y, w, h, txt, col) in steps:
        p = patches.FancyBboxPatch((x, y), w, h, boxstyle="round,pad=0.2", ec=col, fc="#1e293b", lw=2)
        ax.add_patch(p)
        ax.text(x + w/2, y + h/2, txt, fontsize=9.2, color="#f8fafc", ha='center', va='center', fontweight='bold')

    for i in range(len(steps)-1):
        x_from = steps[i][0] + steps[i][2] + 0.05
        x_to = steps[i+1][0] - 0.05
        ax.annotate('', xy=(x_to, 2.7), xytext=(x_from, 2.7),
                    arrowprops=dict(arrowstyle="->", color="#4ade80", lw=2))

    plt.tight_layout()
    plt.savefig('d:/WEB/report_assets/review_flowchart.png', dpi=300, facecolor='#0f172a')
    plt.close()
    print("Saved review_flowchart.png")

# -------------------------------------------------------------
# 7. Rubric Evaluation Chart
# -------------------------------------------------------------
def create_rubric_chart():
    fig, ax = plt.subplots(figsize=(10, 5.5), dpi=300)
    ax.set_facecolor('#0f172a')
    fig.patch.set_facecolor('#0f172a')

    categories = [
        'Tiêu chí 1:\nĐộ khó & Phân công\n(10đ)',
        'Tiêu chí 2:\nSử dụng AI & Github\n(20đ)',
        'Tiêu chí 3:\nChất lượng hình thức\n(20đ)',
        'Tiêu chí 4:\nChất lượng ứng dụng\n(35đ)',
        'Tiêu chí 5:\nTính sáng tạo (8 mục)\n(15đ)'
    ]
    max_scores = [10, 20, 20, 35, 15]
    achieved_scores = [10, 20, 20, 35, 15]

    x = np.arange(len(categories))
    width = 0.45

    bars = ax.bar(x, achieved_scores, width, color=['#38bdf8', '#4ade80', '#fbbf24', '#f43f5e', '#a855f7'], edgecolor='#ffffff', lw=1.5)

    ax.set_ylabel('Điểm Tự Chấm (Thang điểm 100)', color='#ffffff', fontsize=11, fontweight='bold')
    ax.set_title('BẢNG TỰ ĐÁNH GIÁ 5 TIÊU CHÍ RUBRIC - NHÓM 17 - ĐẠT 100/100 ĐIỂM (10.0/10.0)', 
                 color='#e5c07b', fontsize=13, fontweight='bold', pad=15)
    ax.set_xticks(x)
    ax.set_xticklabels(categories, color='#ffffff', fontsize=9.5)
    ax.set_ylim(0, 42)
    ax.tick_params(colors='#ffffff')
    ax.grid(axis='y', linestyle='--', alpha=0.25, color='#ffffff')

    for bar in bars:
        height = bar.get_height()
        ax.annotate(f'{height}/{height}đ\n(100%)',
                    xy=(bar.get_x() + bar.get_width() / 2, height),
                    xytext=(0, 6),
                    textcoords="offset points",
                    ha='center', va='bottom', color='#ffffff', fontweight='bold', fontsize=10)

    # Total score annotation
    ax.text(4, 38, 'TỔNG ĐIỂM ĐỀ NGHỊ: 100 / 100 (ĐIỂM A+)', 
            fontsize=12, fontweight='bold', color='#fbbf24', ha='right',
            bbox=dict(boxstyle='round,pad=0.5', facecolor='#1e293b', edgecolor='#fbbf24', lw=1.5))

    plt.tight_layout()
    plt.savefig('d:/WEB/report_assets/rubric_scores.png', dpi=300, facecolor='#0f172a')
    plt.close()
    print("Saved rubric_scores.png")

if __name__ == '__main__':
    create_architecture_diagram()
    create_erd_diagram()
    create_otp_flowchart()
    create_vnpay_flowchart()
    create_shipper_flowchart()
    create_review_flowchart()
    create_rubric_chart()
    print("All diagrams generated successfully!")
