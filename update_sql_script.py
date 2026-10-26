# Update generate_supabase_sql.py to include all new categories and vehicles
import re

extra_categories = """(7, 'Pô Độ & Đồ Bảo Hộ Cào Cào', 'gear-parts', 'https://images.unsplash.com/photo-1578328819058-b69f3a3b0f6b?auto=format&fit=crop&w=800&q=80', FALSE),
(8, 'Siêu Xe Ô Tô & Xe Sang', 'oto', '/images/lamborghini_huracan.jpg', FALSE),
(9, 'Xe Nhỏ & Tay Ga Sưu Tầm', 'xenho', '/images/honda_monkey_125.jpg', FALSE);"""

extra_products = """
-- Siêu Xe Ô Tô & Xe Nhỏ Bổ Sung
(201, 'Lamborghini Huracán EVO 5.2L V10 2023', 'lamborghini-huracan-evo-v10-2023', 'Siêu xe thể thao biểu tượng nước Ý động cơ V10 hút khí tự nhiên 640 mã lực, tăng tốc 0-100 km/h chỉ 2.9 giây, pô titan thể thao.', 17500000000.00, 16800000000.00, 2, 14, TRUE, TRUE, 5.00, 18, 1, 8, 5204, 640, 'Lướt 3.200 km', '2023', 'HQCN, Pô Titan, Gói Corsa', '/images/lamborghini_huracan.jpg'),
(202, 'Porsche 911 GT3 RS Weissach Package 2024', 'porsche-911-gt3-rs-weissach-2024', 'Vua đường đua thương mại mạnh nhất thế giới với gói nâng cấp thể thao Weissach Package, cánh gió DRS khí động học chủ động.', 19200000000.00, 18500000000.00, 1, 11, TRUE, TRUE, 5.00, 15, 1, 8, 3996, 525, 'Xe Mới 100%', '2024', 'Weissach Package, DRS, Mâm Magie', '/images/porsche_911_gt3rs.jpg'),
(203, 'Mercedes-AMG G63 V8 Biturbo Edition 1 2023', 'mercedes-amg-g63-v8-biturbo-2023', 'Vua địa hình SUV hạng sang Mercedes-AMG G63 với ống xả thể thao đặt bên sườn xe uy lực trứ danh, 3 khóa vi sai độc lập.', 11800000000.00, 11200000000.00, 3, 16, TRUE, TRUE, 4.98, 20, 2, 8, 3982, 585, 'Lướt 6.200 km', '2023', 'HQCN, Pô Bên Hông, Edition 1', '/images/mercedes_g63.jpg'),
(204, 'Ferrari 488 Pista V8 Twin-Turbo 2022', 'ferrari-488-pista-v8-twin-turbo-2022', 'Siêu ngựa Ý 710 mã lực thừa hưởng công nghệ xe đua 488 Challenge, hốc hút gió S-Duct khí động học phía trước, khung xe carbon siêu nhẹ.', 21800000000.00, 21000000000.00, 1, 9, TRUE, TRUE, 5.00, 12, 1, 8, 3902, 710, 'Lướt 4.500 km', '2022', 'Ngựa Ý Độc Bản, Pô Inconel, Carbon', '/images/ferrari_488_pista.jpg'),
(208, 'VinFast VF9 Plus 7 Chỗ Dual-Motor AWD 2024', 'vinfast-vf9-plus-dual-motor-2024', 'Siêu SUV điện full-size cỡ lớn sang trọng bậc nhất Việt Nam, thiết kế Pininfarina Ý, nội thất ghế cơ trưởng bọc da Nappa cao cấp.', 2250000000.00, 21500000000.00, 4, 19, TRUE, TRUE, 4.95, 25, 1, 8, 0, 402, 'Lướt 8.000 km', '2024', 'SUV Điện Full-Size, Ghế Cơ Trưởng, 10 Năm BH', '/images/vinfast_vf9.jpg'),
(209, 'Chevrolet Corvette C8 Stingray Z51 2023', 'chevrolet-corvette-c8-z51-2023', 'Siêu xe động cơ đặt giữa thế hệ mới của Corvette C8, gói hiệu năng Z51 bao gồm phanh Brembo lớn hơn và vi sai eLSD.', 6800000000.00, 6500000000.00, 2, 11, TRUE, TRUE, 4.95, 14, 1, 8, 6162, 495, 'Lướt 3.500 km', '2023', 'Gói Z51, Pô Thể Thao, HQCN', '/images/chevrolet_corvette_c8.jpg'),
(211, 'Ford Mustang GT 5.0 V8 Fastback 2024', 'ford-mustang-gt-5-0-v8-2024', 'Ngựa hoang cơ bắp Mỹ Mustang GT thế hệ mới với động cơ Coyote V8 5.0L nạp khí tự nhiên gầm rú đầy kích thích.', 4500000000.00, 4250000000.00, 2, 15, TRUE, TRUE, 4.96, 17, 2, 8, 5038, 486, 'Lướt 1.200 km', '2024', 'Coyote V8, Phanh Brembo 6 Piston, HQCN', '/images/ford_mustang_gt.jpg'),
(212, 'Toyota Land Cruiser LC300 V6 Twin-Turbo 2024', 'toyota-land-cruiser-lc300-2024', 'Vua địa hình SUV việt dã đỉnh cao của Toyota, khung gầm TNGA-F mới giảm 200kg, hệ thống Multi-Terrain Select.', 5600000000.00, 5350000000.00, 3, 18, TRUE, TRUE, 4.98, 22, 3, 8, 3444, 409, 'Lướt 6.800 km', '2024', 'Vua Giữ Giá, TSS An Toàn, HQCN', '/images/toyota_lc300.jpg'),
(210, 'Mazda MX-5 Miata RF Grand Touring 2024', 'mazda-mx5-miata-rf-2024', 'Mẫu xe mui trần biểu tượng cảm giác lái Jinba Ittai, mui cứng gập điện RF 13 giây, phân bổ trọng lượng 50:50.', 2650000000.00, 2490000000.00, 3, 14, TRUE, TRUE, 4.90, 16, 1, 8, 1998, 181, 'Xe Mới 100%', '2024', 'Mui Cứng RF, Loa Bose, HQCN', '/images/mazda_mx5.jpg'),
(214, 'Jeep Wrangler Rubicon 4x4 Unlimited 2024', 'jeep-wrangler-rubicon-2024', 'Chiến thần vượt địa hình của Mỹ với cầu trước và cầu sau Dana 44 siêu bền, ngắt thanh cân bằng điện tử Sway Bar.', 4150000000.00, 3950000000.00, 2, 11, TRUE, TRUE, 4.92, 13, 2, 8, 1995, 270, 'Lướt 4.100 km', '2024', 'Vua Địa Hình Mỹ, Mui Tháo Rời, HQCN', '/images/jeep_wrangler.jpg'),
(222, 'Nissan GT-R R35 Nismo Special Edition 2024', 'nissan-gtr-r35-nismo-2024', 'Huyền thoại siêu xe Godzilla của Nhật Bản phiên bản Nismo Special Edition, động cơ Takumi lắp thủ công 600 mã lực.', 11500000000.00, 10800000000.00, 1, 11, TRUE, TRUE, 4.99, 19, 1, 8, 3799, 600, 'Lướt 1.800 km', '2024', 'Godzilla Đỉnh Cao, Takumi Handcrafted, Carbon', '/images/nissan_gtr.jpg'),
(301, 'Honda Monkey 125cc Limited Edition 2024', 'honda-monkey-125cc-limited-2024', 'Mẫu xe khỉ Honda Monkey 125cc biểu tượng phong cách đường phố thế giới, bình xăng xi mạ chrome bóng loáng, phuộc USD thể thao.', 145000000.00, 135000000.00, 5, 21, TRUE, TRUE, 5.00, 24, 1, 9, 125, 9, 'Lướt 500 km', '2024', 'Sưu Tầm Độc Bản, Phuộc USD Vàng, HQCN', '/images/honda_monkey_125.jpg'),
(304, 'BMW CE 04 Futuristic Electric Super Scooter 2024', 'bmw-ce-04-electric-scooter-2024', 'Siêu phi thuyền tay ga điện tương lai BMW CE 04 với thiết kế phá cách, pin lithium-ion cao áp, màn hình TFT 10.25 inch.', 580000000.00, 540000000.00, 3, 15, TRUE, TRUE, 4.98, 18, 1, 9, 0, 42, 'Xe Mới 100%', '2024', 'Phi Thuyền Tương Lai, TFT 10.25 Inch, HQCN', '/images/bmw_ce04.jpg'),
(112, 'Suzuki Hayabusa GSX-1300R Thần Gió 2024', 'suzuki-hayabusa-gsx-1300r-2024', 'Huyền thoại Thần Gió Suzuki Hayabusa thế hệ thứ 3 với công nghệ điều khiển điện tử thông minh S.I.R.S.', 790000000.00, 750000000.00, 3, 20, TRUE, TRUE, 4.97, 26, 1, 2, 1340, 190, 'Lướt 2.100 km', '2024', 'Huyền Thoại Thần Gió, Quickshifter 2 Chiều, HQCN', '/images/suzuki_hayabusa.jpg'),
(111, 'Royal Enfield Continental GT 650 Cafe Racer 2024', 'royal-enfield-continental-gt-650-2024', 'Tuyệt tác Cafe Racer cổ điển từ thương hiệu xe Anh Quốc Royal Enfield với bình xăng mạ chrome bóng loáng.', 220000000.00, 199000000.00, 4, 16, TRUE, TRUE, 4.88, 17, 2, 4, 648, 47, 'Lướt 1.200 km', '2024', 'Cafe Racer Cổ Điển, Pô Kép Trầm Ấm, HQCN', '/images/royal_enfield_gt650.jpg'),
(114, 'MV Agusta Brutale 1000 RR Nurburgring Edition', 'mv-agusta-brutale-1000-rr-nurburgring', 'Kiệt tác Hyper Naked siêu hiếm từ hãng xe nghệ thuật Ý MV Agusta, động cơ 4 xylanh van titan thừa hưởng từ xe đua WorldSBK.', 1450000000.00, 1380000000.00, 2, 11, TRUE, TRUE, 4.99, 15, 1, 3, 998, 208, 'Lướt 800 km', '2024', 'Kiệt Tác Nghệ Thuật Ý, Mâm Carbon, Pô Arrow Titan', '/images/mv_agusta_brutale.jpg');"""

with open("d:/WEB/generate_supabase_sql.py", "r", encoding="utf-8") as f:
    code = f.read()

# Replace categories line
old_cat = "(7, 'Pô Độ & Đồ Bảo Hộ Cào Cào', 'gear-parts', 'https://images.unsplash.com/photo-1578328819058-b69f3a3b0f6b?auto=format&fit=crop&w=800&q=80', FALSE);"
code = code.replace(old_cat, extra_categories)

# Replace end of products
code = code.replace(";\n\n-- Cập nhật chuỗi sequence", extra_products + "\n\n-- Cập nhật chuỗi sequence")

with open("d:/WEB/generate_supabase_sql.py", "w", encoding="utf-8") as f:
    f.write(code)

print("Updated generate_supabase_sql.py")
