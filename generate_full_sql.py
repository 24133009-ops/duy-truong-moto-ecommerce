import os

sql_content = """/*
========================================================================================
  ĐẠI HỌC SƯ PHẠM KỸ THUẬT THÀNH PHỐ HỒ CHÍ MINH (HCMUTE)
  KHOA CÔNG NGHỆ THÔNG TIN - MÔN HỌC: LẬP TRÌNH WEB (HK1 / 2026-2027)
  ĐỀ TÀI 2: HỆ THỐNG WEBSITE CHUỖI CỬA HÀNG MÔ TÔ PKL & XE CÀO CÀO "DUY TRƯƠNG MOTOR"
  (Phong cách thiết kế chuẩn theo showroom Vương Khang Motor - vuongkhangmotor.com)
  NHÓM: 17 - ĐỀ TÀI: 2
  DANH SÁCH THÀNH VIÊN:
    1. Trương Quốc Duy   - MSSV: 24133009 - Lớp: 24133A (Nhóm trưởng / Leader)
    2. Đỗ Trọng Khôi    - MSSV: 20133056 - Lớp: 201331A (Thành viên)
    3. Lê Nghĩa Tình    - MSSV: 20133096 - Lớp: 201331A (Thành viên)
  
  CÔNG NGHỆ:
    - Backend: Spring Boot 3.2.5 + Spring Security 6 + Data JPA + Java 17 LTS
    - Frontend: React 18 + Vite + 100% Vanilla CSS Dark Luxury (Phông chữ chuẩn tiếng Việt UTF-8)
    - Database: Microsoft SQL Server 2022 (DuyTruongMotoDB)
    - Tích hợp: Cổng VNPAY QR Sandbox, Cloudinary CDN, WebSocket STOMP, Mail OTP
========================================================================================
*/

USE master;
GO

IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'DuyTruongMotoDB')
BEGIN
    CREATE DATABASE [DuyTruongMotoDB];
    PRINT N'>>> Đã tạo thành công cơ sở dữ liệu DuyTruongMotoDB';
END
GO

USE [DuyTruongMotoDB];
GO

SET ANSI_NULLS ON;
GO
SET QUOTED_IDENTIFIER ON;
GO

-- 1. BẢNG HẠNG THÀNH VIÊN (user_levels)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'user_levels')
BEGIN
    CREATE TABLE [dbo].[user_levels] (
        [id] BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        [name] NVARCHAR(100) NOT NULL UNIQUE,
        [min_points] BIGINT NOT NULL DEFAULT 0,
        [discount_rate] DECIMAL(5,2) NOT NULL DEFAULT 0.00
    );
END
GO

-- 2. BẢNG VAI TRÒ (roles)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'roles')
BEGIN
    CREATE TABLE [dbo].[roles] (
        [id] BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        [name] VARCHAR(50) NOT NULL UNIQUE
    );
END
GO

-- 3. BẢNG NGƯỜI DÙNG (users)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'users')
BEGIN
    CREATE TABLE [dbo].[users] (
        [id] BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        [firstname] NVARCHAR(50) NOT NULL,
        [lastname] NVARCHAR(50) NOT NULL,
        [slug] VARCHAR(100) NULL,
        [email] VARCHAR(100) NOT NULL UNIQUE,
        [phone] VARCHAR(20) NULL,
        [password] VARCHAR(255) NOT NULL,
        [id_card] VARCHAR(20) NULL,
        [avatar] NVARCHAR(500) NULL,
        [role] VARCHAR(20) NOT NULL,
        [is_email_active] BIT NOT NULL DEFAULT 1,
        [is_phone_active] BIT NOT NULL DEFAULT 1,
        [otp_code] VARCHAR(10) NULL,
        [otp_expiry] DATETIME2 NULL,
        [e_wallet] DECIMAL(18,2) NOT NULL DEFAULT 0.00,
        [point] BIGINT NOT NULL DEFAULT 0,
        [user_level_id] BIGINT NULL,
        [created_at] DATETIME2 DEFAULT GETDATE(),
        [updated_at] DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT [FK_users_user_levels] FOREIGN KEY ([user_level_id]) REFERENCES [dbo].[user_levels]([id])
    );
END
GO

-- 4. BẢNG SỔ ĐỊA CHỈ (user_addresses - Tối đa 6 địa chỉ)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'user_addresses')
BEGIN
    CREATE TABLE [dbo].[user_addresses] (
        [id] BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        [user_id] BIGINT NOT NULL,
        [recipient_name] NVARCHAR(100) NOT NULL,
        [phone] VARCHAR(20) NOT NULL,
        [street] NVARCHAR(255) NOT NULL,
        [ward] NVARCHAR(100) NOT NULL,
        [district] NVARCHAR(100) NOT NULL,
        [city] NVARCHAR(100) NOT NULL,
        [is_default] BIT NOT NULL DEFAULT 0,
        CONSTRAINT [FK_user_addresses_users] FOREIGN KEY ([user_id]) REFERENCES [dbo].[users]([id]) ON DELETE CASCADE
    );
END
GO

-- 5. BẢNG SHOWROOM / CỬA HÀNG (stores)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'stores')
BEGIN
    CREATE TABLE [dbo].[stores] (
        [id] BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        [name] NVARCHAR(150) NOT NULL,
        [bio] NVARCHAR(500) NULL,
        [slug] VARCHAR(150) NOT NULL UNIQUE,
        [owner_id] BIGINT NOT NULL,
        [address] NVARCHAR(255) NULL,
        [city] NVARCHAR(100) NULL,
        [phone] VARCHAR(30) NULL,
        [is_active] BIT NOT NULL DEFAULT 1,
        [is_open] BIT NOT NULL DEFAULT 1,
        [avatar] NVARCHAR(500) NULL,
        [point] BIGINT NOT NULL DEFAULT 0,
        [rating] DECIMAL(3,2) NOT NULL DEFAULT 5.00,
        [e_wallet] DECIMAL(18,2) NOT NULL DEFAULT 0.00,
        [created_at] DATETIME2 DEFAULT GETDATE(),
        [updated_at] DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT [FK_stores_users] FOREIGN KEY ([owner_id]) REFERENCES [dbo].[users]([id])
    );
END
GO

-- 6. BẢNG CHÍNH SÁCH HOA HỒNG SÀN (commissions)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'commissions')
BEGIN
    CREATE TABLE [dbo].[commissions] (
        [id] BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        [name] NVARCHAR(100) NOT NULL,
        [cost] DECIMAL(5,2) NOT NULL,
        [description] NVARCHAR(255) NULL,
        [is_deleted] BIT NOT NULL DEFAULT 0
    );
END
GO

-- 7. BẢNG DANH MỤC SẢN PHẨM (categories)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'categories')
BEGIN
    CREATE TABLE [dbo].[categories] (
        [id] BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        [name] NVARCHAR(100) NOT NULL,
        [slug] VARCHAR(100) NOT NULL UNIQUE,
        [image] NVARCHAR(500) NULL,
        [is_deleted] BIT NOT NULL DEFAULT 0
    );
END
GO

-- 8. BẢNG SẢN PHẨM MÔ TÔ PKL & XE CÀO CÀO (products)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'products')
BEGIN
    CREATE TABLE [dbo].[products] (
        [id] BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        [name] NVARCHAR(255) NOT NULL,
        [slug] VARCHAR(255) NOT NULL,
        [description] NVARCHAR(MAX) NULL,
        [price] DECIMAL(18,2) NOT NULL,
        [promotional_price] DECIMAL(18,2) NULL,
        [quantity] INT NOT NULL DEFAULT 1,
        [sold] INT NOT NULL DEFAULT 0,
        [is_active] BIT NOT NULL DEFAULT 1,
        [is_selling] BIT NOT NULL DEFAULT 1,
        [rating] DECIMAL(3,2) NOT NULL DEFAULT 5.00,
        [review_count] INT NOT NULL DEFAULT 0,
        [store_id] BIGINT NOT NULL,
        [category_id] BIGINT NOT NULL,
        [list_images] NVARCHAR(MAX) NULL,
        [engine_capacity] INT NULL,
        [horsepower] INT NULL,
        [odo] NVARCHAR(50) NULL,
        [year] NVARCHAR(10) NULL,
        [badges] NVARCHAR(255) NULL,
        [created_at] DATETIME2 DEFAULT GETDATE(),
        [updated_at] DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT [FK_products_stores] FOREIGN KEY ([store_id]) REFERENCES [dbo].[stores]([id]),
        CONSTRAINT [FK_products_categories] FOREIGN KEY ([category_id]) REFERENCES [dbo].[categories]([id])
    );
END
GO

-- 9. BẢNG GIỎ HÀNG (carts)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'carts')
BEGIN
    CREATE TABLE [dbo].[carts] (
        [id] BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        [user_id] BIGINT NOT NULL UNIQUE,
        [created_at] DATETIME2 DEFAULT GETDATE(),
        [updated_at] DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT [FK_carts_users] FOREIGN KEY ([user_id]) REFERENCES [dbo].[users]([id]) ON DELETE CASCADE
    );
END
GO

-- 10. BẢNG CHI TIẾT GIỎ HÀNG (cart_items)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'cart_items')
BEGIN
    CREATE TABLE [dbo].[cart_items] (
        [id] BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        [cart_id] BIGINT NOT NULL,
        [product_id] BIGINT NOT NULL,
        [quantity] INT NOT NULL DEFAULT 1,
        [created_at] DATETIME2 DEFAULT GETDATE(),
        [updated_at] DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT [FK_cart_items_carts] FOREIGN KEY ([cart_id]) REFERENCES [dbo].[carts]([id]) ON DELETE CASCADE,
        CONSTRAINT [FK_cart_items_products] FOREIGN KEY ([product_id]) REFERENCES [dbo].[products]([id])
    );
END
GO

-- 11. BẢNG ĐƠN HÀNG (orders - 6 Trạng thái: PENDING, CONFIRMED, PREPARING, SHIPPING, DELIVERED, COMPLETED/CANCELLED)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'orders')
BEGIN
    CREATE TABLE [dbo].[orders] (
        [id] BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        [order_code] VARCHAR(50) NOT NULL UNIQUE,
        [user_id] BIGINT NOT NULL,
        [store_id] BIGINT NOT NULL,
        [recipient_name] NVARCHAR(100) NOT NULL,
        [recipient_phone] VARCHAR(20) NOT NULL,
        [delivery_address] NVARCHAR(255) NOT NULL,
        [payment_method] VARCHAR(30) NOT NULL DEFAULT 'COD',
        [status] VARCHAR(30) NOT NULL DEFAULT 'PENDING',
        [total_amount] DECIMAL(18,2) NOT NULL,
        [shipping_fee] DECIMAL(18,2) NOT NULL DEFAULT 0.00,
        [discount_amount] DECIMAL(18,2) NOT NULL DEFAULT 0.00,
        [final_amount] DECIMAL(18,2) NOT NULL,
        [note] NVARCHAR(500) NULL,
        [created_at] DATETIME2 DEFAULT GETDATE(),
        [updated_at] DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT [FK_orders_users] FOREIGN KEY ([user_id]) REFERENCES [dbo].[users]([id]),
        CONSTRAINT [FK_orders_stores] FOREIGN KEY ([store_id]) REFERENCES [dbo].[stores]([id])
    );
END
GO

-- 12. BẢNG CHI TIẾT ĐƠN HÀNG (order_items)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'order_items')
BEGIN
    CREATE TABLE [dbo].[order_items] (
        [id] BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        [order_id] BIGINT NOT NULL,
        [product_id] BIGINT NOT NULL,
        [product_name] NVARCHAR(255) NOT NULL,
        [product_price] DECIMAL(18,2) NOT NULL,
        [quantity] INT NOT NULL,
        [total_price] DECIMAL(18,2) NOT NULL,
        CONSTRAINT [FK_order_items_orders] FOREIGN KEY ([order_id]) REFERENCES [dbo].[orders]([id]) ON DELETE CASCADE,
        CONSTRAINT [FK_order_items_products] FOREIGN KEY ([product_id]) REFERENCES [dbo].[products]([id])
    );
END
GO

-- 13. BẢNG GIAO DỊCH THANH TOÁN VNPAY (transactions)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'transactions')
BEGIN
    CREATE TABLE [dbo].[transactions] (
        [id] BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        [order_id] BIGINT NOT NULL,
        [user_id] BIGINT NOT NULL,
        [vnp_txn_ref] VARCHAR(50) NOT NULL,
        [amount] DECIMAL(18,2) NOT NULL,
        [payment_method] VARCHAR(30) NOT NULL,
        [status] VARCHAR(30) NOT NULL,
        [vnp_transaction_no] VARCHAR(50) NULL,
        [vnp_response_code] VARCHAR(10) NULL,
        [created_at] DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT [FK_transactions_orders] FOREIGN KEY ([order_id]) REFERENCES [dbo].[orders]([id]),
        CONSTRAINT [FK_transactions_users] FOREIGN KEY ([user_id]) REFERENCES [dbo].[users]([id])
    );
END
GO

-- 14. BẢNG ĐÁNH GIÁ SẢN PHẨM (reviews - Bắt buộc >= 50 ký tự + Cloudinary Media)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'reviews')
BEGIN
    CREATE TABLE [dbo].[reviews] (
        [id] BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        [user_id] BIGINT NOT NULL,
        [product_id] BIGINT NOT NULL,
        [rating] INT NOT NULL CHECK ([rating] >= 1 AND [rating] <= 5),
        [content] NVARCHAR(MAX) NOT NULL,
        [media_url] NVARCHAR(500) NULL,
        [is_approved] BIT NOT NULL DEFAULT 1,
        [created_at] DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT [FK_reviews_users] FOREIGN KEY ([user_id]) REFERENCES [dbo].[users]([id]),
        CONSTRAINT [FK_reviews_products] FOREIGN KEY ([product_id]) REFERENCES [dbo].[products]([id])
    );
END
GO

-- 15. BẢNG TIN NHẮN TƯ VẤN WEBSOCKET (chat_messages)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'chat_messages')
BEGIN
    CREATE TABLE [dbo].[chat_messages] (
        [id] BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        [sender_id] BIGINT NOT NULL,
        [receiver_id] BIGINT NOT NULL,
        [content] NVARCHAR(MAX) NOT NULL,
        [is_read] BIT NOT NULL DEFAULT 0,
        [created_at] DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT [FK_chat_messages_sender] FOREIGN KEY ([sender_id]) REFERENCES [dbo].[users]([id]),
        CONSTRAINT [FK_chat_messages_receiver] FOREIGN KEY ([receiver_id]) REFERENCES [dbo].[users]([id])
    );
END
GO

-- ========================================================================================
-- PHẦN DỮ LIỆU MẪU (SEED DATA CHUẨN ĐỂ KIỂM THỬ)
-- ========================================================================================

SET IDENTITY_INSERT [dbo].[user_levels] ON;
IF NOT EXISTS (SELECT 1 FROM [dbo].[user_levels] WHERE [id] = 1)
BEGIN
    INSERT INTO [dbo].[user_levels] ([id], [name], [min_points], [discount_rate]) VALUES
    (1, N'Thành viên Đồng', 0, 0.00),
    (2, N'Thành viên Bạc', 500, 2.00),
    (3, N'Thành viên Vàng', 2000, 5.00),
    (4, N'Thành viên Kim Cương VIP', 5000, 10.00);
END
SET IDENTITY_INSERT [dbo].[user_levels] OFF;
GO

SET IDENTITY_INSERT [dbo].[roles] ON;
IF NOT EXISTS (SELECT 1 FROM [dbo].[roles] WHERE [id] = 1)
BEGIN
    INSERT INTO [dbo].[roles] ([id], [name]) VALUES
    (1, 'ROLE_GUEST'),
    (2, 'ROLE_USER'),
    (3, 'ROLE_VENDOR'),
    (4, 'ROLE_ADMIN'),
    (5, 'ROLE_SHIPPER');
END
SET IDENTITY_INSERT [dbo].[roles] OFF;
GO

SET IDENTITY_INSERT [dbo].[users] ON;
IF NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [email] = 'admin@duytruongmotor.com')
BEGIN
    INSERT INTO [dbo].[users] ([id], [firstname], [lastname], [slug], [email], [phone], [password], [role], [is_email_active], [is_phone_active], [e_wallet], [point], [user_level_id]) VALUES
    (101, N'Duy', N'Trương Quốc', 'truong-quoc-duy-admin', 'admin@duytruongmotor.com', '0901234567', '$2a$10$UTyagYfhS5lBv5f5CqVUtOlhU/5koZ4SY5vUxeJVTi.DGl6PZ/RTW', 'ROLE_ADMIN', 1, 1, 500000000.00, 9999, 4),
    (102, N'Khôi', N'Đỗ Trọng', 'do-trong-khoi-manager', 'manager@duytruongmotor.com', '0902345678', '$2a$10$CAUexBEVvowOq228gIL6NuX3bd5bWJQswpt44G7Uglbi87vn5swHu', 'ROLE_MANAGER', 1, 1, 200000000.00, 3000, 3),
    (103, N'Tình', N'Lê Nghĩa', 'le-nghia-tinh-vendor', 'vendor@duytruongmotor.com', '0903456789', '$2a$10$dolsVyEflyLlyNqACvdOueuk.VuElGNL2TXmAR7iC4CKPoIvI0UTq', 'ROLE_VENDOR', 1, 1, 150000000.00, 2500, 3),
    (104, N'Tuấn', N'Vận Chuyển', 'tuan-shipper-vip', 'shipper@duytruongmotor.com', '0904567890', '$2a$10$ABfB1sDkBTMfBJvmOSXFleoP850.uDGhWKOE.hfbW75/ylw/sRiI2', 'ROLE_SHIPPER', 1, 1, 0.00, 100, 1),
    (105, N'Hoàng', N'Biker VIP', 'hoang-biker-vip', 'customer@gmail.com', '0905678901', '$2a$10$TCLnF9pAaaP9j/OcRzldDOwOgqfWqb.v0yNNBmXviOcMCQ1yj5NPC', 'ROLE_USER', 1, 1, 75000000.00, 850, 2);
END
SET IDENTITY_INSERT [dbo].[users] OFF;
GO

SET IDENTITY_INSERT [dbo].[stores] ON;
IF NOT EXISTS (SELECT 1 FROM [dbo].[stores] WHERE [slug] = 'duy-truong-motor-phu-nhuan')
BEGIN
    INSERT INTO [dbo].[stores] ([id], [name], [bio], [slug], [owner_id], [address], [city], [phone], [is_active], [is_open], [rating], [e_wallet]) VALUES
    (1, N'Duy Trương Motor - Chi nhánh Phú Nhuận TP.HCM (Trụ Sở Chính)', N'Showroom trưng bày siêu môtô PKL & xe cào cào địa hình lớn nhất miền Nam tại 123 Hoàng Văn Thụ', 'duy-truong-motor-phu-nhuan', 103, N'Số 123 Đường Hoàng Văn Thụ, Phường 8, Quận Phú Nhuận, TP. HCM', N'TP. Hồ Chí Minh', '0903.456.789 - 0933.333.382', 1, 1, 5.00, 450000000.00),
    (2, N'Duy Trương Motor - Chi nhánh Cầu Giấy Hà Nội', N'Showroom chuyên phân phối xe cào cào KTM, Husqvarna và siêu môtô Châu Âu tại Thủ đô Hà Nội', 'duy-truong-motor-cau-giay', 103, N'Số 168 Đường Trần Duy Hưng, Phường Trung Hòa, Quận Cầu Giấy, Hà Nội', N'Hà Nội', '0901.234.567', 1, 1, 4.95, 320000000.00),
    (3, N'Duy Trương Motor - Chi nhánh Hải Châu Đà Nẵng', N'Trung tâm dịch vụ bảo dưỡng, phụ tùng pô độ và cào cào địa hình miền Trung', 'duy-truong-motor-hai-chau', 103, N'Số 52 Đường Nguyễn Văn Linh, Phường Nam Dương, Quận Hải Châu, Đà Nẵng', N'Đà Nẵng', '0902.999.888', 1, 1, 4.90, 210000000.00);
END
SET IDENTITY_INSERT [dbo].[stores] OFF;
GO

SET IDENTITY_INSERT [dbo].[categories] ON;
IF NOT EXISTS (SELECT 1 FROM [dbo].[categories] WHERE [slug] = 'dirtbike')
BEGIN
    INSERT INTO [dbo].[categories] ([id], [name], [slug], [image], [is_deleted]) VALUES
    (1, N'Xe Cào Cào - Xe Địa Hình', 'dirtbike', 'https://images.unsplash.com/photo-1544829099-b9a0c07fad1a?auto=format&fit=crop&w=800&q=80', 0),
    (2, N'Superbike / Thể Thao', 'superbike', 'https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?auto=format&fit=crop&w=800&q=80', 0),
    (3, N'Hyper Naked Đường Phố', 'hyper-naked', 'https://images.unsplash.com/photo-1558981408-db0ecd8a1ee4?auto=format&fit=crop&w=800&q=80', 0),
    (4, N'Cruiser & Chopper Mỹ', 'cruiser', 'https://images.unsplash.com/photo-1558981806-ec527fa84c39?auto=format&fit=crop&w=800&q=80', 0),
    (5, N'Adventure Phượt Bụi', 'adventure', 'https://images.unsplash.com/photo-1609630875171-b1321377ee65?auto=format&fit=crop&w=800&q=80', 0),
    (6, N'Xe Tay Ga Độc Lạ Ý', 'scooter', 'https://images.unsplash.com/photo-1558981285-6f0c94958bb6?auto=format&fit=crop&w=800&q=80', 0),
    (7, N'Pô Độ & Đồ Bảo Hộ Cào Cào', 'gear-parts', 'https://images.unsplash.com/photo-1578328819058-b69f3a3b0f6b?auto=format&fit=crop&w=800&q=80', 0);
END
SET IDENTITY_INSERT [dbo].[categories] OFF;
GO

SET IDENTITY_INSERT [dbo].[products] ON;
IF NOT EXISTS (SELECT 1 FROM [dbo].[products] WHERE [slug] = 'ktm-300-exc-tpi-2024')
BEGIN
    INSERT INTO [dbo].[products] ([id], [name], [slug], [description], [price], [promotional_price], [quantity], [sold], [is_active], [is_selling], [rating], [review_count], [store_id], [category_id], [engine_capacity], [horsepower], [odo], [year], [badges], [list_images]) VALUES
    -- Xe cào cào địa hình
    (101, N'KTM 300 EXC TPI Factory Edition 2024', 'ktm-300-exc-tpi-2024', N'Vua cào cào Enduro 2 thì số 1 thế giới với công nghệ phun xăng điện tử TPI độc quyền, phuộc trước hành trình ngược WP XPLOR 48mm điều chỉnh độc lập, chảng ba CNC cam anodize, mâm nan hoa DID DirtStar gia cường vượt mọi địa hình bùn lầy, dốc đá hiểm trở.', 385000000.00, 368000000.00, 4, 17, 1, 1, 5.00, 14, 1, 1, 293, 54, N'Lướt 2.100 km', N'2024', N'HQCN Sang Tên, Bao Test Hãng, Bao Quay Đầu', 'https://images.unsplash.com/photo-1544829099-b9a0c07fad1a?auto=format&fit=crop&w=800&q=80'),
    (102, N'KTM 450 EXC-F Six Days Argentina Edition', 'ktm-450-exc-f-six-days', N'Chiến mã cào cào Enduro thi đấu quốc tế phiên bản Six Days danh giá. Động cơ 450cc 4 thì cực kỳ bền bỉ và bốc ở dải tua đầu, trang bị bảo vệ gầm nhôm nguyên khối, quạt tản nhiệt thông minh và hệ thống kiểm soát lực kéo Traction Control đa cấp độ.', 465000000.00, 445000000.00, 3, 15, 1, 1, 4.95, 12, 1, 1, 450, 63, N'Xe Mới 100%', N'2024', N'HQCN, Six Days Limited, Bao Giao Xe Toàn Quốc', 'https://images.unsplash.com/photo-1508974239320-0a029497e820?auto=format&fit=crop&w=800&q=80'),
    (103, N'Husqvarna FE 350 Pro Edition Thụy Điển', 'husqvarna-fe-350-pro', N'Tuyệt tác cào cào cao cấp đến từ Thụy Điển. Sự kết hợp hoàn hảo giữa trọng lượng nhẹ như xe 250cc và sức kéo mạnh mẽ của xe 450cc. Khung phụ bằng sợi carbon composite 2 mảnh, cùm phanh cùm côn thủy lực Brembo và mâm căm nhôm Excel cao cấp.', 420000000.00, 399000000.00, 3, 12, 1, 1, 4.90, 10, 2, 1, 350, 45, N'Lướt 3.400 km', N'2023', N'HQCN, Bao Rút Hồ Sơ, Bao Quay Đầu', 'https://images.unsplash.com/photo-1609630875171-b1321377ee65?auto=format&fit=crop&w=800&q=80'),
    (104, N'Honda CRF450R Motocross AMA Champion', 'honda-crf450r-motocross', N'Chiến mã cào cào bay vô địch các giải đua cào cào địa hình danh giá AMA Motocross và Supercross của Mỹ. Khung nhôm đôi thế hệ thứ 7, phuộc trước Showa ti 49mm hành trình nhún 310mm nuốt trọn mọi cú nhảy tiếp đất từ độ cao 5 mét.', 345000000.00, 325000000.00, 5, 19, 1, 1, 5.00, 16, 1, 1, 450, 58, N'Xe Mới 100%', N'2024', N'Nhập Khẩu Hải Quan, Bao Test, Bao Ship', 'https://images.unsplash.com/photo-1544829099-b9a0c07fad1a?auto=format&fit=crop&w=800&q=80'),
    (105, N'Kawasaki KLX300 Dual-Sport Đa Địa Hình', 'kawasaki-klx300-dual-sport', N'Dòng xe cào cào phố và phượt rừng bền bỉ được anh em biker săn đón nhiều nhất tại Duy Trương Motor. Yên xe êm ái, bảo dưỡng tiết kiệm, máy 300cc êm ái chạy phố hàng ngày nhẹ nhàng và leo đèo dốc off-road cực kỳ tự tin.', 185000000.00, 178000000.00, 6, 22, 1, 1, 4.88, 18, 3, 1, 292, 33, N'Lướt 4.200 km (Biển TP)', N'2023', N'HQCN Sang Tên 24h, Bao Rút Gốc, Biển TP VIP', 'https://images.unsplash.com/photo-1558980394-4c7c9299fe96?auto=format&fit=crop&w=800&q=80'),
    (106, N'Aprilia SX 125 Supermoto Bánh Trơn Chạy Phố', 'aprilia-sx-125-supermoto', N'Chiếc cào cào phố dáng Supermoto bánh trơn cực ngầu đến từ hãng xe đua nước Ý. Hệ thống phuộc up-side-down 41mm cứng cáp, phanh đĩa trước 300mm có ABS chống bó cứng an toàn, dáng ngồi cao ráo tôn dáng người lái.', 98000000.00, 92000000.00, 7, 16, 1, 1, 4.85, 11, 1, 1, 125, 15, N'Lướt 1.800 km', N'2024', N'HQCN, Bằng A1 Chạy Được, Hỗ Trợ Trả Góp', 'https://images.unsplash.com/photo-1558981359-219d6364c9c8?auto=format&fit=crop&w=800&q=80'),

    -- Siêu mô tô PKL
    (1, N'Ducati Panigale V4 S 2025', 'ducati-panigale-v4-s', N'Đỉnh cao tốc độ đến từ nước Ý với khối động cơ Desmosedici Stradale V4 1.103cc, công suất 215.5 HP, phuộc điện tử Ohlins Smart EC 2.0, mâm hợp kim nhôm đúc Marchesini siêu nhẹ.', 1180000000.00, 1150000000.00, 4, 18, 1, 1, 5.00, 15, 1, 2, 1103, 216, N'Xe Mới 100%', N'2025', N'HQCN, Biển VIP, Bao Test Hãng', '/images/ducati_panigale_v4s.jpg'),
    (2, N'Kawasaki Ninja H2 Carbon Supercharged', 'kawasaki-ninja-h2-carbon', N'Quái vật siêu nạp Supercharged độc nhất vô nhị sản sinh 231 HP, ốp dàn áo sợi carbon cao cấp, công nghệ phanh Brembo Stylema đỉnh cao.', 1350000000.00, 1290000000.00, 2, 16, 1, 1, 4.98, 14, 1, 2, 998, 231, N'Lướt 3.200 km', N'2024', N'Độc Nhất VN, HQCN, Bao Quay Đầu', '/images/kawasaki_ninja_h2.jpg'),
    (3, N'BMW S1000RR M Package 2025', 'bmw-s1000rr-m-package', N'Chiến mã nước Đức với công nghệ van biến thiên ShiftCam danh tiếng, cánh gió khí động học M Winglets và mâm Carbon thể thao siêu nhẹ.', 1099000000.00, 1050000000.00, 4, 19, 1, 1, 5.00, 16, 2, 2, 999, 210, N'Lướt 1.500 km', N'2025', N'HQCN, Bản M Cao Cấp, Mâm Carbon', '/images/bmw_s1000rr.jpg'),
    (4, N'Harley-Davidson Fat Boy 114 Vivid Black', 'harley-davidson-fat-boy-114', N'Biểu tượng cơ bắp Mỹ với khối động cơ Milwaukee-Eight 114 ci (1.868cc), âm thanh pô uy lực và vành đúc nguyên khối Lakester đặc trưng.', 899000000.00, 860000000.00, 5, 13, 1, 1, 4.88, 11, 2, 4, 1868, 94, N'Lướt 2.800 km', N'2023', N'HQCN, Cơ Bắp Mỹ, Bao Test Hãng', '/images/harley_fatboy.jpg'),
    (5, N'Ducati Streetfighter V4 SP2 Special Edition', 'ducati-streetfighter-v4-sp2', N'Mẫu hypernaked đỉnh cao sở hữu nồi khô STM-EVO SBK danh tiếng, mâm carbon, màu sơn Winter Test huyền thoại đậm chất xe đua MotoGP.', 1250000000.00, 1200000000.00, 2, 15, 1, 1, 4.97, 13, 1, 3, 1103, 208, N'Xe Mới 100%', N'2024', N'Bản Giới Hạn SP2, HQCN, Nồi Khô Leng Keng', '/images/ducati_streetfighter.jpg'),
    (6, N'BMW R1250GS Adventure Triple Black', 'bmw-r1250gs-adventure', N'Vua địa hình thế giới với động cơ Boxer 1.254cc ShiftCam, bình xăng 30 lít, hệ thống phuộc Dynamic ESA cân bằng tự động theo tải trọng.', 799000000.00, 770000000.00, 5, 24, 1, 1, 4.96, 20, 2, 5, 1254, 136, N'Lướt 6.500 km', N'2023', N'Vua Phượt, HQCN, Full 3 Thùng Nhôm', '/images/bmw_r1250gs.jpg'),
    (7, N'Vespa 946 Christian Dior Limited Edition', 'vespa-946-christian-dior', N'Tuyệt phẩm thời trang giới hạn do Vespa bắt tay cùng nhà mốt Christian Dior Paris thiết kế. Họa tiết Dior Oblique đặc trưng dưới yên xe, đi kèm túi hành lý và mũ bảo hiểm đồng bộ chính hãng.', 750000000.00, 720000000.00, 2, 11, 1, 1, 4.99, 10, 1, 6, 125, 12, N'Odo 350 km (Như Mới)', N'2022', N'Siêu Hiếm, Full Túi Thùng Dior, HQCN', '/images/vespa_946.jpg'),
    (8, N'Mũ Cào Cào LS2 MX703 X-Force Carbon & Kính 100% Armega', 'mu-ls2-mx703-carbon', N'Mũ bảo hiểm cào cào cao cấp bằng sợi carbon nguyên khối siêu nhẹ chuẩn thi đấu quốc tế, lót kháng khuẩn tháo giặt, tặng kèm kính cào cào 100% Armega tráng gương.', 12500000.00, 11200000.00, 15, 28, 1, 1, 4.95, 22, 1, 7, 0, 0, N'Mới 100% Full Box', N'2024', N'Chính Hãng, Carbon Siêu Nhẹ, Tặng Kính 100%', 'https://images.unsplash.com/photo-1578328819058-b69f3a3b0f6b?auto=format&fit=crop&w=800&q=80'),
    (9, N'Hệ Thống Pô Akrapovic Full Titanium Racing Cho Môtô & Cào Cào', 'po-akrapovic-full-titanium', N'Hệ thống xả đua chính hãng Akrapovic làm hoàn toàn từ hợp kim Titanium siêu nhẹ, giúp tăng thêm 7.8 HP và giảm 6.2kg trọng lượng xe.', 125000000.00, 118000000.00, 8, 32, 1, 1, 4.99, 25, 1, 7, 0, 0, N'Mới 100% Full Box', N'2024', N'Chính Hãng Akrapovic, Bao Lắp Đặt, Dynojet Test', 'https://images.unsplash.com/photo-1558980664-769d59546b3d?auto=format&fit=crop&w=800&q=80'),

    -- 10 SẢN PHẨM MỚI BỔ SUNG (ĐỦ 25 MẪU XE CHUẨN SHOWROOM)
    (107, N'Ducati DesertX Rally 2024 (Vua Cào Cào Sa Mạc)', 'ducati-desertx-rally-2024', N'Chiến mã cào cào Rally chuyên nghiệp chinh phục Dakar Rally với phuộc trước KYB Closed Cartridge 48mm hành trình 250mm, mâm căm Excel Takasago bản hẹp và đĩa xích nhôm Ergal.', 499000000.00, 485000000.00, 3, 14, 1, 1, 4.97, 11, 1, 1, 937, 110, N'Lướt 900 km', N'2024', N'Bản Rally Cao Cấp, HQCN, Phuộc KYB Đua', '/images/ducati_desertx.jpg'),
    (108, N'Yamaha WR450F Rally Champion 2024', 'yamaha-wr450f-rally-champion', N'Mẫu xe cào cào thi đấu danh tiếng cỗ máy ngược 450cc của Yamaha Racing. Tích hợp kết nối điện thoại tinh chỉnh bản đồ xăng lửa Power Tuner App tức thì ngay trên đường chạy.', 415000000.00, 395000000.00, 4, 16, 1, 1, 4.92, 13, 2, 1, 450, 58, N'Xe Mới 100%', N'2024', N'HQCN, Yamaha Racing, Cào Cào Chuyên Nghiệp', '/images/yamaha_wr450f.jpg'),
    (10, N'Yamaha YZF-R1M Carbon 2024 (Chiến Mã MotoGP CP4)', 'yamaha-yzf-r1m-carbon-2024', N'Phiên bản trường đua cao cấp nhất của dòng R1 huyền thoại với dàn áo full sợi carbon, phuộc điện tử Ohlins ERS thế hệ mới và động cơ Crossplane 4 thì gầm rú mê hồn.', 880000000.00, 850000000.00, 3, 17, 1, 1, 5.00, 16, 1, 2, 998, 200, N'Lướt 1.900 km', N'2024', N'Bản R1M Carbon VIP, HQCN, Âm Thanh CP4', '/images/yamaha_r1m.jpg'),
    (11, N'Honda CBR1000RR-R Fireblade SP 2024', 'honda-cbr1000rr-r-fireblade-sp-2024', N'Siêu môtô thể thao thương mại nhanh nhất lịch sử Honda mang công nghệ khí động học khí nén MotoGP RC213V-S, phuộc Ohlins Smart EC và phanh Brembo Stylema.', 960000000.00, 930000000.00, 2, 15, 1, 1, 4.96, 12, 1, 2, 1000, 217, N'Lướt 2.400 km', N'2024', N'HQCN, Cánh Gió MotoGP, Phuộc Ohlins Điện', '/images/honda_cbr1000rrr.jpg'),
    (12, N'KTM 1290 Super Duke R EVO (The Beast)', 'ktm-1290-super-duke-r-evo', N'Quái thú Naked-bike sở hữu mô-men xoắn khủng khiếp 140 Nm từ động cơ V-Twin LC8 1.301cc, hệ thống treo bán chủ động WP APEX bán điện tử thế hệ thứ 2.', 850000000.00, 820000000.00, 4, 18, 1, 1, 4.94, 14, 2, 3, 1301, 180, N'Xe Mới 100%', N'2024', N'Quái Thú Nước Áo, HQCN, Phuộc Điện WP', '/images/ktm_1290_superduke.jpg'),
    (13, N'Kawasaki Z1000R Edition 2024 (Thần Sấm Biker Việt)', 'kawasaki-z1000r-edition-2024', N'Mẫu xe thần tượng của giới biker Việt Nam phiên bản đặc biệt R Edition trang bị giảm xóc sau Ohlins S46DR1S có núm chỉnh tay và heo phanh Brembo M50 nguyên khối.', 479000000.00, 459000000.00, 6, 26, 1, 1, 4.91, 24, 1, 3, 1043, 142, N'Lướt 4.500 km', N'2024', N'Bản R Cao Cấp, Phuộc Ohlins, Phanh Brembo', '/images/kawasaki_z1000r.jpg'),
    (14, N'Triumph Rocket 3 GT 2.500cc (Cơ Bắp Khủng Nhất TG)', 'triumph-rocket-3-gt-2500cc', N'Kỷ lục môtô thương mại sở hữu dung tích xy-lanh lớn nhất hành tinh 2.458cc, mô-men xoắn vô song 221 Nm cho cú đề-pa xé gió nhưng cực kỳ êm ái trên những chặng tour dài.', 890000000.00, 865000000.00, 2, 12, 1, 1, 4.98, 11, 2, 4, 2458, 167, N'Lướt 3.100 km', N'2023', N'Động Cơ Khủng Nhất Thế Giới, HQCN', '/images/triumph_rocket3.jpg'),
    (15, N'Ducati Diavel V4 Redline 2025', 'ducati-diavel-v4-redline-2025', N'Kiệt tác cơ bắp cơ động đạt giải Thiết kế môtô đẹp nhất thế giới. Trái tim V4 Granturismo 1.158cc êm ái mượt mà, gắp đơn nhôm và đèn hậu LED tổ ong đa điểm ấn tượng.', 980000000.00, 950000000.00, 3, 15, 1, 1, 4.97, 13, 1, 4, 1158, 168, N'Xe Mới 100%', N'2025', N'Đỉnh Cao Cruiser Ý, HQCN, Đèn LED Tổ Ong', '/images/ducati_diavel_v4.jpg'),
    (16, N'Honda Africa Twin CRF1100L Adventure Sports ES', 'honda-africa-twin-crf1100l-adventure-sports-es', N'Vua phượt việt dã đỉnh cao trang bị hộp số ly hợp kép DCT hoặc số côn tay, phuộc điện tử Showa EERA và màn hình cảm ứng TFT 6.5 inch hỗ trợ Apple CarPlay.', 690000000.00, 665000000.00, 4, 21, 1, 1, 4.93, 19, 3, 5, 1084, 102, N'Lướt 5.200 km', N'2023', N'Bản Adventure Sports ES, Full 3 Thùng Nhôm, HQCN', '/images/honda_africa_twin.jpg'),
    (17, N'Yamaha TMAX 560 Tech MAX 2024 (Vua Maxi-Scooter)', 'yamaha-tmax-560-tech-max-2024', N'Mẫu Maxi-scooter thể thao đẳng cấp nhất thế giới trang bị kính chắn gió chỉnh điện, sưởi ấm yên và tay lái, ga tự động Cruise Control và màn hình TFT 7 inch.', 485000000.00, 465000000.00, 5, 23, 1, 1, 4.89, 18, 1, 6, 562, 48, N'Lướt 1.200 km', N'2024', N'Vua Ga Lớn, Bản Tech MAX Full Option, HQCN', '/images/yamaha_tmax560.jpg');
END
SET IDENTITY_INSERT [dbo].[products] OFF;
GO

PRINT N'=============================================================================';
PRINT N'>>> ĐÃ TẠO VÀ CẬP NHẬT CƠ SỞ DỮ LIỆU DUYTRUONGMOTODB (DUY TRƯƠNG MOTOR)';
PRINT N'>>> ĐÃ THÊM DANH MỤC XE CÀO CÀO ĐỊA HÌNH (KTM, HUSQVARNA, CRF, KLX, APRILIA)';
PRINT N'>>> ĐÃ CẬP NHẬT CAM KẾT CHUẨN VƯƠNG KHANG MOTOR (MUA NGƯỜI CHÁN - BÁN NGƯỜI CẦN)';
PRINT N'=============================================================================';
GO
"""

with open('d:/WEB/duytruong_moto_db.sql', 'w', encoding='utf-8') as f:
    f.write(sql_content)

print("Generated clean, self-contained SQL script at d:/WEB/duytruong_moto_db.sql")
