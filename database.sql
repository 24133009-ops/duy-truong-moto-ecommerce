-- =============================================================================
-- ĐỒ ÁN MÔN HỌC: XÂY DỰNG WEBSITE THƯƠNG MẠI ĐIỆN TỬ
-- HỆ THỐNG PHÂN PHỐI MÔ TÔ PHÂN KHỐI LỚN & ĐỒ BẢO HỘ DUY TRUONG MOTOR
-- CÔNG NGHỆ: Spring Boot 3.2, Thymeleaf SSR, Spring Security 6, JPA Hibernate
-- =============================================================================

-- 1. BẢNG PHÂN HẠNG THÀNH VIÊN (USER_LEVELS)
CREATE TABLE IF NOT EXISTS user_levels (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    min_point BIGINT NOT NULL DEFAULT 0,
    discount DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. BẢNG NGƯỜI DÙNG (USERS)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    firstname VARCHAR(50) NOT NULL,
    lastname VARCHAR(50) NOT NULL,
    slug VARCHAR(100) UNIQUE,
    id_card VARCHAR(20) UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER',
    user_level_id BIGINT,
    point BIGINT NOT NULL DEFAULT 0,
    e_wallet DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    is_email_active BOOLEAN NOT NULL DEFAULT FALSE,
    is_phone_active BOOLEAN NOT NULL DEFAULT FALSE,
    is_blocked BOOLEAN NOT NULL DEFAULT FALSE,
    otp_code VARCHAR(10),
    otp_expiry TIMESTAMP,
    avatar VARCHAR(500),
    cover VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_level_id) REFERENCES user_levels(id)
);

-- 3. BẢNG CHÍNH SÁCH HOA HỒNG SÀN (COMMISSIONS)
CREATE TABLE IF NOT EXISTS commissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    cost DECIMAL(5,2) NOT NULL DEFAULT 5.00,
    description VARCHAR(500),
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. BẢNG SHOWROOM CHI NHÁNH (STORES)
CREATE TABLE IF NOT EXISTS stores (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    slug VARCHAR(150) NOT NULL UNIQUE,
    bio TEXT,
    address VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    avatar VARCHAR(500),
    cover VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_open BOOLEAN NOT NULL DEFAULT TRUE,
    commission_id BIGINT,
    owner_id BIGINT,
    point BIGINT DEFAULT 0,
    rating DOUBLE DEFAULT 5.0,
    e_wallet DECIMAL(18,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (commission_id) REFERENCES commissions(id),
    FOREIGN KEY (owner_id) REFERENCES users(id)
);

-- 5. BẢNG DANH MỤC SẢN PHẨM (CATEGORIES)
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(100) NOT NULL UNIQUE,
    image VARCHAR(500),
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 6. BẢNG ĐƠN VỊ VẬN CHUYỂN (DELIVERIES)
CREATE TABLE IF NOT EXISTS deliveries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    price DECIMAL(18,2) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 7. BẢNG MÃ KHUYẾN MÃI (VOUCHERS)
CREATE TABLE IF NOT EXISTS vouchers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(500),
    discount_percent INT NOT NULL DEFAULT 0,
    max_discount_amount DECIMAL(18,2),
    min_order_amount DECIMAL(18,2),
    usage_limit INT DEFAULT 100,
    used_count INT DEFAULT 0,
    store_id BIGINT,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (store_id) REFERENCES stores(id)
);

-- 8. BẢNG SẢN PHẨM MÔ TÔ & ĐỒ BẢO HỘ (PRODUCTS)
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    price DECIMAL(18,2) NOT NULL,
    promotional_price DECIMAL(18,2),
    quantity INT NOT NULL DEFAULT 1,
    sold INT NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_selling BOOLEAN NOT NULL DEFAULT TRUE,
    main_image VARCHAR(500),
    engine_capacity VARCHAR(50),
    color VARCHAR(100),
    brand VARCHAR(100),
    rating DOUBLE DEFAULT 5.0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (store_id) REFERENCES stores(id),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- 9. BẢNG ĐƠN HÀNG (ORDERS)
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_code VARCHAR(50) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    store_id BIGINT NOT NULL,
    delivery_id BIGINT,
    shipper_id BIGINT,
    address VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    recipient_name VARCHAR(100),
    status VARCHAR(30) NOT NULL DEFAULT 'NOT_PROCESSED',
    is_paid_before BOOLEAN NOT NULL DEFAULT FALSE,
    payment_method VARCHAR(30) NOT NULL,
    amount_from_user DECIMAL(18,2) NOT NULL,
    amount_to_store DECIMAL(18,2) NOT NULL,
    amount_to_gd DECIMAL(18,2) NOT NULL,
    note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (store_id) REFERENCES stores(id),
    FOREIGN KEY (delivery_id) REFERENCES deliveries(id),
    FOREIGN KEY (shipper_id) REFERENCES users(id)
);

-- 10. BẢNG CHI TIẾT ĐƠN HÀNG (ORDER_ITEMS)
CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    count INT NOT NULL,
    price DECIMAL(18,2) NOT NULL,
    selected_style VARCHAR(100),
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- =============================================================================
-- DỮ LIỆU KHỞI TẠO CHUẨN (MẬT KHẨU MÃ HÓA BCRYPT: $2a$10$...)
-- =============================================================================

-- Dữ liệu cấp bậc
INSERT INTO user_levels (id, name, min_point, discount) VALUES
(1, 'Khách hàng Thành Viên', 0, 0.00),
(2, 'Khách hàng Bạc VIP', 500, 2.00),
(3, 'Khách hàng Vàng VIP', 2000, 4.00),
(4, 'Khách hàng Kim Cương VIP', 5000, 7.00);

-- Dữ liệu tài khoản mẫu chuẩn 4 Roles:
-- 1. Admin: admin@duytruongmoto.com / admin123
-- 2. Vendor Cầu Giấy: dotrongkhoi2002@gmail.com / vendor123
-- 3. Vendor Hải Châu: nghiatinh2002@gmail.com / vendor123
-- 4. Vendor Phú Nhuận: vendor@duytruongmoto.com / vendor123
-- 5. Shipper: shipper@duytruongmoto.com / shipper123
-- 6. Customer: customer@duytruongmoto.com / customer123
INSERT INTO users (id, firstname, lastname, slug, email, phone, password, role, is_email_active, is_phone_active, is_blocked) VALUES
(1, 'Duy', 'Trương Quốc', 'duy-truong-admin', 'admin@duytruongmoto.com', '0909111999', '$2a$10$vI8aWBnW3fID.3G1.7tXy.k0QzTfHj8Yj8N5V3sQhF2Q2C.vA2K6m', 'ROLE_ADMIN', TRUE, TRUE, FALSE),
(2, 'Khôi', 'Đỗ Trọng', 'do-trong-khoi', 'dotrongkhoi2002@gmail.com', '0903112002', '$2a$10$eEfxI0F.m2pU5P.k2p9fheF.o9tH6bF8j3H0k6y0Z9w1Q4V7c5j4O', 'ROLE_VENDOR', TRUE, TRUE, FALSE),
(3, 'Tình', 'Lê Nghĩa', 'le-nghia-tinh', 'nghiatinh2002@gmail.com', '0905112002', '$2a$10$eEfxI0F.m2pU5P.k2p9fheF.o9tH6bF8j3H0k6y0Z9w1Q4V7c5j4O', 'ROLE_VENDOR', TRUE, TRUE, FALSE),
(4, 'Minh', 'Showroom Phú Nhuận', 'minh-showroom-phunhuan', 'vendor@duytruongmoto.com', '0903111222', '$2a$10$eEfxI0F.m2pU5P.k2p9fheF.o9tH6bF8j3H0k6y0Z9w1Q4V7c5j4O', 'ROLE_VENDOR', TRUE, TRUE, FALSE),
(5, 'Tuấn', 'Vận Chuyển', 'tuan-shipper', 'shipper@duytruongmoto.com', '0904333444', '$2a$10$aE5W2A.3J.rE7m2fU5y7.e7W.K3Z1x9P.k7k6k5j4j3j2j1j0Z9w1', 'ROLE_SHIPPER', TRUE, TRUE, FALSE),
(6, 'Hoàng', 'Biker VIP', 'customer@duytruongmoto.com', '0905555777', '$2a$10$bF6X3B.4K.sF8n3gV6z8.f8X.L4A2y0Q.l8l7l6k5k4k3k2k1a0x2', 'ROLE_USER', TRUE, TRUE, FALSE);

-- Chính sách chiết khấu hoa hồng
INSERT INTO commissions (id, name, cost, description) VALUES
(1, 'Chiết khấu Tiêu chuẩn', 5.00, 'Chiết khấu 5% cho showroom đối tác chuỗi'),
(2, 'Chiết khấu Đại lý VIP', 3.00, 'Chiết khấu ưu đãi 3% cho showroom quy mô lớn');

-- Chuỗi 3 Showroom lớn
INSERT INTO stores (id, name, slug, bio, address, phone, avatar, cover, is_active, is_open, commission_id, owner_id) VALUES
(1, 'Duy Truong Moto - Trụ Sở & Kho Phụ Tùng Phú Nhuận TP.HCM', 'duy-truong-moto-showroom-quan-1', 'Kho tổng miền Nam chuyên xe PKL và đồ bảo hộ chính hãng Dainese, AGV, Alpinestars.', 'Số 123 Đường Hoàng Văn Thụ, Phường 8, Quận Phú Nhuận, TP. Hồ Chí Minh', '0903.456.789', 'https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=300&q=80', 'https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=1200&q=80', TRUE, TRUE, 1, 4),
(2, 'Duy Truong Moto - Showroom Cầu Giấy Hà Nội', 'duy-truong-moto-showroom-cau-giay-ha-noi', 'Đại lý phân phối xe cào cào chuyên nghiệp, nón fullface carbon và pô độ cao cấp.', 'Số 168 Đường Trần Duy Hưng, Cầu Giấy, Hà Nội', '024.3999.8888', 'https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?auto=format&fit=crop&w=300&q=80', 'https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?auto=format&fit=crop&w=1200&q=80', TRUE, TRUE, 1, 2),
(3, 'Duy Truong Moto - Showroom Hải Châu Đà Nẵng', 'duy-truong-moto-showroom-hai-chau-da-nang', 'Showroom miền Trung phục vụ các biker phượt Tây Bắc & Duyên Hải.', 'Số 52 Đường Nguyễn Văn Linh, Hải Châu, Đà Nẵng', '0236.3888.999', 'https://images.unsplash.com/photo-1599819811279-d5ad9cccf838?auto=format&fit=crop&w=300&q=80', 'https://images.unsplash.com/photo-1599819811279-d5ad9cccf838?auto=format&fit=crop&w=1200&q=80', TRUE, TRUE, 1, 3);

-- Đơn vị vận chuyển chuyên nghiệp
INSERT INTO deliveries (id, name, description, price) VALUES
(1, 'Giao xe sàn trượt thủy lực VIP (Chống trầy xước 100%)', 'Xe chuyên dụng sàn trượt hạ sát mặt đất, bàn giao tận nhà có bảo hiểm thân vỏ toàn phần', 1500000.00),
(2, 'Xe tải thùng kín chuyên dụng chuyển môtô', 'Xe thùng bạt có chằng buộc chuyên dụng cho xe PKL vận chuyển liên tỉnh', 800000.00),
(3, 'Giao hàng tiêu chuẩn (Phụ tùng, đồ bảo hộ)', 'Chuyển phát nhanh 2-3 ngày, đóng hộp chống sốc cho nón bảo hiểm và giáp bảo hộ', 50000.00);

-- Mã Voucher khuyến mãi
INSERT INTO vouchers (id, code, name, description, discount_percent, max_discount_amount, min_order_amount, usage_limit, used_count) VALUES
(1, 'BIKERVIP', 'Ưu đãi khách hàng thân thiết Biker VIP', 'Giảm 5% tối đa 30.000.000 đ cho các dòng mô tô phân khối lớn', 5, 30000000.00, 200000000.00, 100, 5),
(2, 'FREESHIP-VIP', 'Miễn phí 100% chi phí xe sàn trượt VIP', 'Hỗ trợ chi phí xe sàn trượt thủy lực VIP tận nhà cho khách hàng đặt cọc', 100, 2000000.00, 100000000.00, 200, 12),
(3, 'BAOHO-500K', 'Voucher Phụ Kiện Đồ Bảo Hộ', 'Giảm ngay 500.000 đ khi mua nón AGV, giáp Dainese hoặc găng Alpinestars', 10, 500000.00, 5000000.00, 500, 38);
