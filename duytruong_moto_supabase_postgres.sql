-- ========================================================================================
-- DUY TRƯƠNG MOTOR - SUPABASE POSTGRESQL DATABASE SCRIPT
-- Hoàn toàn tương thích với Supabase PostgreSQL (0 syntax errors)
-- ========================================================================================

-- 1. XÓA BẢNG CŨ NẾU CÓ ĐỂ TRÁNH XUNG ĐỘT
DROP TABLE IF EXISTS chat_messages CASCADE;
DROP TABLE IF EXISTS reviews CASCADE;
DROP TABLE IF EXISTS order_items CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS products CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS stores CASCADE;
DROP TABLE IF EXISTS user_addresses CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS roles CASCADE;
DROP TABLE IF EXISTS user_levels CASCADE;

-- 2. TẠO CÁC BẢNG (POSTGRESQL SYNTAX)
CREATE TABLE user_levels (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    min_points BIGINT NOT NULL DEFAULT 0,
    discount_rate NUMERIC(5,2) NOT NULL DEFAULT 0.00
);

CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    firstname VARCHAR(50) NOT NULL,
    lastname VARCHAR(50) NOT NULL,
    slug VARCHAR(100),
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    password VARCHAR(255) NOT NULL,
    id_card VARCHAR(20),
    avatar VARCHAR(500),
    role VARCHAR(20) NOT NULL,
    is_email_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_phone_active BOOLEAN NOT NULL DEFAULT TRUE,
    otp_code VARCHAR(10),
    otp_expiry TIMESTAMP,
    e_wallet NUMERIC(18,2) NOT NULL DEFAULT 0.00,
    point BIGINT NOT NULL DEFAULT 0,
    user_level_id BIGINT REFERENCES user_levels(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_addresses (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    recipient_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    street VARCHAR(255) NOT NULL,
    ward VARCHAR(100) NOT NULL,
    district VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    is_default BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE stores (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    bio TEXT,
    slug VARCHAR(255) NOT NULL UNIQUE,
    owner_id BIGINT NOT NULL REFERENCES users(id),
    address VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_open BOOLEAN NOT NULL DEFAULT TRUE,
    rating NUMERIC(3,2) NOT NULL DEFAULT 5.00,
    e_wallet NUMERIC(18,2) NOT NULL DEFAULT 0.00
);

CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    image VARCHAR(500),
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    price NUMERIC(18,2) NOT NULL,
    promotional_price NUMERIC(18,2) NOT NULL,
    quantity INT NOT NULL DEFAULT 5,
    sold INT NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_selling BOOLEAN NOT NULL DEFAULT TRUE,
    rating NUMERIC(3,2) NOT NULL DEFAULT 5.00,
    review_count INT NOT NULL DEFAULT 0,
    store_id BIGINT NOT NULL REFERENCES stores(id),
    category_id BIGINT NOT NULL REFERENCES categories(id),
    engine_capacity INT DEFAULT 0,
    horsepower INT DEFAULT 0,
    odo VARCHAR(100),
    year VARCHAR(10),
    badges VARCHAR(255),
    list_images TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    store_id BIGINT NOT NULL REFERENCES stores(id),
    total_amount NUMERIC(18,2) NOT NULL,
    discount_amount NUMERIC(18,2) NOT NULL DEFAULT 0.00,
    final_amount NUMERIC(18,2) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    payment_method VARCHAR(50) NOT NULL DEFAULT 'VNPAY_QR',
    delivery_address TEXT,
    note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id),
    product_id BIGINT NOT NULL REFERENCES products(id),
    quantity INT NOT NULL DEFAULT 1,
    price NUMERIC(18,2) NOT NULL,
    total_price NUMERIC(18,2) NOT NULL
);

CREATE TABLE reviews (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    product_id BIGINT NOT NULL REFERENCES products(id),
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    content TEXT NOT NULL,
    media_url VARCHAR(500),
    is_approved BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE chat_messages (
    id BIGSERIAL PRIMARY KEY,
    sender_id BIGINT NOT NULL REFERENCES users(id),
    receiver_id BIGINT NOT NULL REFERENCES users(id),
    content TEXT NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========================================================================================
-- 3. NẠP DỮ LIỆU SEED (USER, STORE, 25 MẪU XE DUY TRƯƠNG MOTOR)
-- ========================================================================================

INSERT INTO user_levels (id, name, min_points, discount_rate) VALUES
(1, 'Thành viên Đồng', 0, 0.00),
(2, 'Thành viên Bạc', 500, 2.00),
(3, 'Thành viên Vàng', 2000, 5.00),
(4, 'Thành viên Kim Cương VIP', 5000, 10.00);

INSERT INTO roles (id, name) VALUES
(1, 'ROLE_GUEST'),
(2, 'ROLE_USER'),
(3, 'ROLE_VENDOR'),
(4, 'ROLE_ADMIN'),
(5, 'ROLE_SHIPPER');

INSERT INTO users (id, firstname, lastname, slug, email, phone, password, role, is_email_active, is_phone_active, e_wallet, point, user_level_id) VALUES
(101, 'Duy', 'Trương Quốc', 'truong-quoc-duy-admin', 'admin@duytruongmotor.com', '0901234567', '$2a$10$UTyagYfhS5lBv5f5CqVUtOlhU/5koZ4SY5vUxeJVTi.DGl6PZ/RTW', 'ROLE_ADMIN', TRUE, TRUE, 500000000.00, 9999, 4),
(102, 'Khôi', 'Đỗ Trọng', 'do-trong-khoi-manager', 'manager@duytruongmotor.com', '0902345678', '$2a$10$CAUexBEVvowOq228gIL6NuX3bd5bWJQswpt44G7Uglbi87vn5swHu', 'ROLE_MANAGER', TRUE, TRUE, 200000000.00, 3000, 3),
(103, 'Tình', 'Lê Nghĩa', 'le-nghia-tinh-vendor', 'vendor@duytruongmotor.com', '0903456789', '$2a$10$dolsVyEflyLlyNqACvdOueuk.VuElGNL2TXmAR7iC4CKPoIvI0UTq', 'ROLE_VENDOR', TRUE, TRUE, 150000000.00, 2500, 3),
(104, 'Tuấn', 'Vận Chuyển', 'tuan-shipper-vip', 'shipper@duytruongmotor.com', '0904567890', '$2a$10$ABfB1sDkBTMfBJvmOSXFleoP850.uDGhWKOE.hfbW75/ylw/sRiI2', 'ROLE_SHIPPER', TRUE, TRUE, 0.00, 100, 1),
(105, 'Hoàng', 'Biker VIP', 'hoang-biker-vip', 'customer@gmail.com', '0905678901', '$2a$10$TCLnF9pAaaP9j/OcRzldDOwOgqfWqb.v0yNNBmXviOcMCQ1yj5NPC', 'ROLE_USER', TRUE, TRUE, 75000000.00, 850, 2);

INSERT INTO stores (id, name, bio, slug, owner_id, address, city, phone, is_active, is_open, rating, e_wallet) VALUES
(1, 'Duy Trương Motor - Chi nhánh Phú Nhuận TP.HCM (Trụ Sở Chính)', 'Showroom trưng bày siêu môtô PKL & xe cào cào địa hình lớn nhất miền Nam tại 123 Hoàng Văn Thụ', 'duy-truong-motor-phu-nhuan', 103, 'Số 123 Đường Hoàng Văn Thụ, Phường 8, Quận Phú Nhuận, TP. HCM', 'TP. Hồ Chí Minh', '0903.456.789 - 0933.333.382', TRUE, TRUE, 5.00, 450000000.00),
(2, 'Duy Trương Motor - Chi nhánh Cầu Giấy Hà Nội', 'Showroom chuyên phân phối xe cào cào KTM, Husqvarna và siêu môtô Châu Âu tại Thủ đô Hà Nội', 'duy-truong-motor-cau-giay', 103, 'Số 168 Đường Trần Duy Hưng, Phường Trung Hòa, Quận Cầu Giấy, Hà Nội', 'Hà Nội', '0901.234.567', TRUE, TRUE, 4.95, 320000000.00),
(3, 'Duy Trương Motor - Chi nhánh Hải Châu Đà Nẵng', 'Trung tâm dịch vụ bảo dưỡng, phụ tùng pô độ và cào cào địa hình miền Trung', 'duy-truong-motor-hai-chau', 103, 'Số 52 Đường Nguyễn Văn Linh, Phường Nam Dương, Quận Hải Châu, Đà Nẵng', 'Đà Nẵng', '0902.999.888', TRUE, TRUE, 4.90, 210000000.00);

INSERT INTO categories (id, name, slug, image, is_deleted) VALUES
(1, 'Xe Cào Cào - Xe Địa Hình', 'dirtbike', 'https://images.unsplash.com/photo-1544829099-b9a0c07fad1a?auto=format&fit=crop&w=800&q=80', FALSE),
(2, 'Superbike / Thể Thao', 'superbike', 'https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?auto=format&fit=crop&w=800&q=80', FALSE),
(3, 'Hyper Naked Đường Phố', 'hyper-naked', 'https://images.unsplash.com/photo-1558981408-db0ecd8a1ee4?auto=format&fit=crop&w=800&q=80', FALSE),
(4, 'Cruiser & Chopper Mỹ', 'cruiser', 'https://images.unsplash.com/photo-1558981806-ec527fa84c39?auto=format&fit=crop&w=800&q=80', FALSE),
(5, 'Adventure Phượt Bụi', 'adventure', 'https://images.unsplash.com/photo-1609630875171-b1321377ee65?auto=format&fit=crop&w=800&q=80', FALSE),
(6, 'Xe Tay Ga Độc Lạ Ý', 'scooter', 'https://images.unsplash.com/photo-1558981285-6f0c94958bb6?auto=format&fit=crop&w=800&q=80', FALSE),
(7, 'Pô Độ & Đồ Bảo Hộ Cào Cào', 'gear-parts', 'https://images.unsplash.com/photo-1578328819058-b69f3a3b0f6b?auto=format&fit=crop&w=800&q=80', FALSE);

INSERT INTO products (id, name, slug, description, price, promotional_price, quantity, sold, is_active, is_selling, rating, review_count, store_id, category_id, engine_capacity, horsepower, odo, year, badges, list_images) VALUES
-- Cào cào địa hình
(101, 'KTM 300 EXC TPI Factory Edition 2024', 'ktm-300-exc-tpi-2024', 'Vua cào cào Enduro 2 thì số 1 thế giới với công nghệ phun xăng điện tử TPI độc quyền, phuộc trước hành trình ngược WP XPLOR 48mm điều chỉnh độc lập, chảng ba CNC cam anodize, mâm nan hoa DID DirtStar gia cường vượt mọi địa hình bùn lầy, dốc đá hiểm trở.', 385000000.00, 368000000.00, 4, 17, TRUE, TRUE, 5.00, 14, 1, 1, 293, 54, 'Lướt 2.100 km', '2024', 'HQCN Sang Tên, Bao Test Hãng, Bao Quay Đầu', '/images/ktm_300_exc.jpg'),
(102, 'KTM 450 EXC-F Six Days Argentina Edition', 'ktm-450-exc-f-six-days', 'Chiến mã cào cào Enduro thi đấu quốc tế phiên bản Six Days danh giá. Động cơ 450cc 4 thì cực kỳ bền bỉ và bốc ở dải tua đầu, trang bị bảo vệ gầm nhôm nguyên khối, quạt tản nhiệt thông minh và hệ thống kiểm soát lực kéo Traction Control đa cấp độ.', 465000000.00, 445000000.00, 3, 15, TRUE, TRUE, 4.95, 12, 1, 1, 450, 63, 'Xe Mới 100%', '2024', 'HQCN, Six Days Limited, Bao Giao Xe Toàn Quốc', '/images/ktm_450_exc.jpg'),
(103, 'Husqvarna FE 350 Pro Edition Thụy Điển', 'husqvarna-fe-350-pro', 'Tuyệt tác cào cào cao cấp đến từ Thụy Điển. Sự kết hợp hoàn hảo giữa trọng lượng nhẹ như xe 250cc và sức kéo mạnh mẽ của xe 450cc. Khung phụ bằng sợi carbon composite 2 mảnh, cùm phanh cùm côn thủy lực Brembo và mâm căm nhôm Excel cao cấp.', 420000000.00, 399000000.00, 3, 12, TRUE, TRUE, 4.90, 10, 2, 1, 350, 45, 'Lướt 3.400 km', '2023', 'HQCN, Bao Rút Hồ Sơ, Bao Quay Đầu', '/images/husqvarna_fe350.jpg'),
(104, 'Honda CRF450R Motocross AMA Champion', 'honda-crf450r-motocross', 'Chiến mã cào cào bay vô địch các giải đua cào cào địa hình danh giá AMA Motocross và Supercross của Mỹ. Khung nhôm đôi thế hệ thứ 7, phuộc trước Showa ti 49mm hành trình nhún 310mm nuốt trọn mọi cú nhảy tiếp đất từ độ cao 5 mét.', 345000000.00, 325000000.00, 5, 19, TRUE, TRUE, 5.00, 16, 1, 1, 450, 58, 'Xe Mới 100%', '2024', 'Nhập Khẩu Hải Quan, Bao Test, Bao Ship', '/images/honda_crf450r.jpg'),
(105, 'Kawasaki KLX300 Dual-Sport Đa Địa Hình', 'kawasaki-klx300-dual-sport', 'Dòng xe cào cào phố và phượt rừng bền bỉ được anh em biker săn đón nhiều nhất tại Duy Trương Motor. Yên xe êm ái, bảo dưỡng tiết kiệm, máy 300cc êm ái chạy phố hàng ngày nhẹ nhàng và leo đèo dốc off-road cực kỳ tự tin.', 185000000.00, 178000000.00, 6, 22, TRUE, TRUE, 4.88, 18, 3, 1, 292, 33, 'Lướt 4.200 km (Biển TP)', '2023', 'HQCN Sang Tên 24h, Bao Rút Gốc, Biển TP VIP', '/images/kawasaki_klx300.jpg'),
(106, 'Aprilia SX 125 Supermoto Bánh Trơn Chạy Phố', 'aprilia-sx-125-supermoto', 'Chiếc cào cào phố dáng Supermoto bánh trơn cực ngầu đến từ hãng xe đua nước Ý. Hệ thống phuộc up-side-down 41mm cứng cáp, phanh đĩa trước 300mm có ABS chống bó cứng an toàn, dáng ngồi cao ráo tôn dáng người lái.', 98000000.00, 92000000.00, 7, 16, TRUE, TRUE, 4.85, 11, 1, 1, 125, 15, 'Lướt 1.800 km', '2024', 'HQCN, Bằng A1 Chạy Được, Hỗ Trợ Trả Góp', '/images/aprilia_sx125.jpg'),

-- Siêu môtô PKL
(1, 'Ducati Panigale V4 S 2025', 'ducati-panigale-v4-s', 'Đỉnh cao tốc độ đến từ nước Ý với khối động cơ Desmosedici Stradale V4 1.103cc, công suất 215.5 HP, phuộc điện tử Ohlins Smart EC 2.0, mâm hợp kim nhôm đúc Marchesini siêu nhẹ.', 1180000000.00, 1150000000.00, 4, 18, TRUE, TRUE, 5.00, 15, 1, 2, 1103, 216, 'Xe Mới 100%', '2025', 'HQCN, Biển VIP, Bao Test Hãng', '/images/ducati_panigale_v4s.jpg'),
(2, 'Kawasaki Ninja H2 Carbon Supercharged', 'kawasaki-ninja-h2-carbon', 'Quái vật siêu nạp Supercharged độc nhất vô nhị sản sinh 231 HP, ốp dàn áo sợi carbon cao cấp, công nghệ phanh Brembo Stylema đỉnh cao.', 1350000000.00, 1290000000.00, 2, 16, TRUE, TRUE, 4.98, 14, 1, 2, 998, 231, 'Lướt 3.200 km', '2024', 'Độc Nhất VN, HQCN, Bao Quay Đầu', '/images/kawasaki_ninja_h2.jpg'),
(3, 'BMW S1000RR M Package 2025', 'bmw-s1000rr-m-package', 'Chiến mã nước Đức với công nghệ van biến thiên ShiftCam danh tiếng, cánh gió khí động học M Winglets và mâm Carbon thể thao siêu nhẹ.', 1099000000.00, 1050000000.00, 4, 19, TRUE, TRUE, 5.00, 16, 2, 2, 999, 210, 'Lướt 1.500 km', '2025', 'HQCN, Bản M Cao Cấp, Mâm Carbon', '/images/bmw_s1000rr.jpg'),
(4, 'Harley-Davidson Fat Boy 114 Vivid Black', 'harley-davidson-fat-boy-114', 'Biểu tượng cơ bắp Mỹ với khối động cơ Milwaukee-Eight 114 ci (1.868cc), âm thanh pô uy lực và vành đúc nguyên khối Lakester đặc trưng.', 899000000.00, 860000000.00, 5, 13, TRUE, TRUE, 4.88, 11, 2, 4, 1868, 94, 'Lướt 2.800 km', '2023', 'HQCN, Cơ Bắp Mỹ, Bao Test Hãng', '/images/harley_fatboy.jpg'),
(5, 'Ducati Streetfighter V4 SP2 Special Edition', 'ducati-streetfighter-v4-sp2', 'Mẫu hypernaked đỉnh cao sở hữu nồi khô STM-EVO SBK danh tiếng, mâm carbon, màu sơn Winter Test huyền thoại đậm chất xe đua MotoGP.', 1250000000.00, 1200000000.00, 2, 15, TRUE, TRUE, 4.97, 13, 1, 3, 1103, 208, 'Xe Mới 100%', '2024', 'Bản Giới Hạn SP2, HQCN, Nồi Khô Leng Keng', '/images/ducati_streetfighter.jpg'),
(6, 'BMW R1250GS Adventure Triple Black', 'bmw-r1250gs-adventure', 'Vua địa hình thế giới với động cơ Boxer 1.254cc ShiftCam, bình xăng 30 lít, hệ thống phuộc Dynamic ESA cân bằng tự động theo tải trọng.', 799000000.00, 770000000.00, 5, 24, TRUE, TRUE, 4.96, 20, 2, 5, 1254, 136, 'Lướt 6.500 km', '2023', 'Vua Phượt, HQCN, Full 3 Thùng Nhôm', '/images/bmw_r1250gs.jpg'),
(7, 'Vespa 946 Christian Dior Limited Edition', 'vespa-946-christian-dior', 'Tuyệt phẩm thời trang giới hạn do Vespa bắt tay cùng nhà mốt Christian Dior Paris thiết kế. Họa tiết Dior Oblique đặc trưng dưới yên xe, đi kèm túi hành lý và mũ bảo hiểm đồng bộ chính hãng.', 750000000.00, 720000000.00, 2, 11, TRUE, TRUE, 4.99, 10, 1, 6, 125, 12, 'Odo 350 km (Như Mới)', '2022', 'Siêu Hiếm, Full Túi Thùng Dior, HQCN', '/images/vespa_946.jpg'),
(8, 'Mũ Cào Cào LS2 MX703 X-Force Carbon & Kính 100% Armega', 'mu-ls2-mx703-carbon', 'Mũ bảo hiểm cào cào cao cấp bằng sợi carbon nguyên khối siêu nhẹ chuẩn thi đấu quốc tế, lót kháng khuẩn tháo giặt, tặng kèm kính cào cào 100% Armega tráng gương.', 12500000.00, 11200000.00, 15, 28, TRUE, TRUE, 4.95, 22, 1, 7, 0, 0, 'Mới 100% Full Box', '2024', 'Chính Hãng, Carbon Siêu Nhẹ, Tặng Kính 100%', '/images/ls2_helmet.jpg'),
(9, 'Hệ Thống Pô Akrapovic Full Titanium Racing Cho Môtô & Cào Cào', 'po-akrapovic-full-titanium', 'Hệ thống xả đua chính hãng Akrapovic làm hoàn toàn từ hợp kim Titanium siêu nhẹ, giúp tăng thêm 7.8 HP và giảm 6.2kg trọng lượng xe.', 125000000.00, 118000000.00, 8, 32, TRUE, TRUE, 4.99, 25, 1, 7, 0, 0, 'Mới 100% Full Box', '2024', 'Chính Hãng Akrapovic, Bao Lắp Đặt, Dynojet Test', '/images/akrapovic_exhaust.jpg'),

-- 10 Mẫu xe mới
(107, 'Ducati DesertX Rally 2024 (Vua Cào Cào Sa Mạc)', 'ducati-desertx-rally-2024', 'Chiến mã cào cào Rally chuyên nghiệp chinh phục Dakar Rally với phuộc trước KYB Closed Cartridge 48mm hành trình 250mm, mâm căm Excel Takasago bản hẹp và đĩa xích nhôm Ergal.', 499000000.00, 485000000.00, 3, 14, TRUE, TRUE, 4.97, 11, 1, 1, 937, 110, 'Lướt 900 km', '2024', 'Bản Rally Cao Cấp, HQCN, Phuộc KYB Đua', '/images/ducati_desertx.jpg'),
(108, 'Yamaha WR450F Rally Champion 2024', 'yamaha-wr450f-rally-champion', 'Mẫu xe cào cào thi đấu danh tiếng cỗ máy ngược 450cc của Yamaha Racing. Tích hợp kết nối điện thoại tinh chỉnh bản đồ xăng lửa Power Tuner App tức thì ngay trên đường chạy.', 415000000.00, 395000000.00, 4, 16, TRUE, TRUE, 4.92, 13, 2, 1, 450, 58, 'Xe Mới 100%', '2024', 'HQCN, Yamaha Racing, Cào Cào Chuyên Nghiệp', '/images/yamaha_wr450f.jpg'),
(10, 'Yamaha YZF-R1M Carbon 2024 (Chiến Mã MotoGP CP4)', 'yamaha-yzf-r1m-carbon-2024', 'Phiên bản trường đua cao cấp nhất của dòng R1 huyền thoại với dàn áo full sợi carbon, phuộc điện tử Ohlins ERS thế hệ mới và động cơ Crossplane 4 thì gầm rú mê hồn.', 880000000.00, 850000000.00, 3, 17, TRUE, TRUE, 5.00, 16, 1, 2, 998, 200, 'Lướt 1.900 km', '2024', 'Bản R1M Carbon VIP, HQCN, Âm Thanh CP4', '/images/yamaha_r1m.jpg'),
(11, 'Honda CBR1000RR-R Fireblade SP 2024', 'honda-cbr1000rr-r-fireblade-sp-2024', 'Siêu môtô thể thao thương mại nhanh nhất lịch sử Honda mang công nghệ khí động học khí nén MotoGP RC213V-S, phuộc Ohlins Smart EC và phanh Brembo Stylema.', 960000000.00, 930000000.00, 2, 15, TRUE, TRUE, 4.96, 12, 1, 2, 1000, 217, 'Lướt 2.400 km', '2024', 'HQCN, Cánh Gió MotoGP, Phuộc Ohlins Điện', '/images/honda_cbr1000rrr.jpg'),
(12, 'KTM 1290 Super Duke R EVO (The Beast)', 'ktm-1290-super-duke-r-evo', 'Quái thú Naked-bike sở hữu mô-men xoắn khủng khiếp 140 Nm từ động cơ V-Twin LC8 1.301cc, hệ thống treo bán chủ động WP APEX bán điện tử thế hệ thứ 2.', 850000000.00, 820000000.00, 4, 18, TRUE, TRUE, 4.94, 14, 2, 3, 1301, 180, 'Xe Mới 100%', '2024', 'Quái Thú Nước Áo, HQCN, Phuộc Điện WP', '/images/ktm_1290_superduke.jpg'),
(13, 'Kawasaki Z1000R Edition 2024 (Thần Sấm Biker Việt)', 'kawasaki-z1000r-edition-2024', 'Mẫu xe thần tượng của giới biker Việt Nam phiên bản đặc biệt R Edition trang bị giảm xóc sau Ohlins S46DR1S có núm chỉnh tay và heo phanh Brembo M50 nguyên khối.', 479000000.00, 459000000.00, 6, 26, TRUE, TRUE, 4.91, 24, 1, 3, 1043, 142, 'Lướt 4.500 km', '2024', 'Bản R Cao Cấp, Phuộc Ohlins, Phanh Brembo', '/images/kawasaki_z1000r.jpg'),
(14, 'Triumph Rocket 3 GT 2.500cc (Cơ Bắp Khủng Nhất TG)', 'triumph-rocket-3-gt-2500cc', 'Kỷ lục môtô thương mại sở hữu dung tích xy-lanh lớn nhất hành tinh 2.458cc, mô-men xoắn vô song 221 Nm cho cú đề-pa xé gió nhưng cực kỳ êm ái trên những chặng tour dài.', 890000000.00, 865000000.00, 2, 12, TRUE, TRUE, 4.98, 11, 2, 4, 2458, 167, 'Lướt 3.100 km', '2023', 'Động Cơ Khủng Nhất Thế Giới, HQCN', '/images/triumph_rocket3.jpg'),
(15, 'Ducati Diavel V4 Redline 2025', 'ducati-diavel-v4-redline-2025', 'Kiệt tác cơ bắp cơ động đạt giải Thiết kế môtô đẹp nhất thế giới. Trái tim V4 Granturismo 1.158cc êm ái mượt mà, gắp đơn nhôm và đèn hậu LED tổ ong đa điểm ấn tượng.', 980000000.00, 950000000.00, 3, 15, TRUE, TRUE, 4.97, 13, 1, 4, 1158, 168, 'Xe Mới 100%', '2025', 'Đỉnh Cao Cruiser Ý, HQCN, Đèn LED Tổ Ong', '/images/ducati_diavel_v4.jpg'),
(16, 'Honda Africa Twin CRF1100L Adventure Sports ES', 'honda-africa-twin-crf1100l-adventure-sports-es', 'Vua phượt việt dã đỉnh cao trang bị hộp số ly hợp kép DCT hoặc số côn tay, phuộc điện tử Showa EERA và màn hình cảm ứng TFT 6.5 inch hỗ trợ Apple CarPlay.', 690000000.00, 665000000.00, 4, 21, TRUE, TRUE, 4.93, 19, 3, 5, 1084, 102, 'Lướt 5.200 km', '2023', 'Bản Adventure Sports ES, Full 3 Thùng Nhôm, HQCN', '/images/honda_africa_twin.jpg'),
(17, 'Yamaha TMAX 560 Tech MAX 2024 (Vua Maxi-Scooter)', 'yamaha-tmax-560-tech-max-2024', 'Mẫu Maxi-scooter thể thao đẳng cấp nhất thế giới trang bị kính chắn gió chỉnh điện, sưởi ấm yên và tay lái, ga tự động Cruise Control và màn hình TFT 7 inch.', 485000000.00, 465000000.00, 5, 23, TRUE, TRUE, 4.89, 18, 1, 6, 562, 48, 'Lướt 1.200 km', '2024', 'Vua Ga Lớn, Bản Tech MAX Full Option, HQCN', '/images/yamaha_tmax560.jpg');

-- Cập nhật chuỗi sequence cho các bảng để tránh lỗi trùng ID khi tạo mới
SELECT setval('user_levels_id_seq', (SELECT MAX(id) FROM user_levels));
SELECT setval('roles_id_seq', (SELECT MAX(id) FROM roles));
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('stores_id_seq', (SELECT MAX(id) FROM stores));
SELECT setval('categories_id_seq', (SELECT MAX(id) FROM categories));
SELECT setval('products_id_seq', (SELECT MAX(id) FROM products));
