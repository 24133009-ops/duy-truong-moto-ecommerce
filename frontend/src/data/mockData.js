// DUY TRƯƠNG MOTOR - DỮ LIỆU ĐỒ ÁN CHUỖI SHOWROOM MÔ TÔ & XE CÀO CÀO UY TÍN
// Phong cách thiết kế chuẩn theo Vương Khang Motor (vuongkhangmotor.com)

export const SHOWROOMS = [
  {
    id: 1,
    name: "Duy Trương Motor - Chi nhánh Phú Nhuận TP.HCM (Trụ Sở Chính)",
    city: "TP. Hồ Chí Minh",
    address: "Số 123 Đường Hoàng Văn Thụ, Phường 8, Quận Phú Nhuận, TP. HCM",
    phone: "0903.456.789 - 0933.333.382",
    avatar: "https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?auto=format&fit=crop&w=300&q=80",
    cover: "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=1200&q=80",
    rating: 5.0,
    active: true,
    bio: "Showroom trưng bày siêu môtô PKL & xe cào cào địa hình lớn nhất miền Nam. Đầy đủ xưởng Dynojet, phòng test xe và đội xe cứu hộ bàn giao tận nhà."
  },
  {
    id: 2,
    name: "Duy Trương Motor - Chi nhánh Cầu Giấy Hà Nội",
    city: "Hà Nội",
    address: "Số 168 Đường Trần Duy Hưng, Phường Trung Hòa, Quận Cầu Giấy, Hà Nội",
    phone: "0901.234.567",
    avatar: "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=300&q=80",
    cover: "https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?auto=format&fit=crop&w=1200&q=80",
    rating: 4.95,
    active: true,
    bio: "Showroom tiêu chuẩn châu Âu tại Thủ đô Hà Nội. Chuyên phân phối các dòng xe BMW, Ducati, KTM Cào Cào, Harley-Davidson HQCN."
  },
  {
    id: 3,
    name: "Duy Trương Motor - Chi nhánh Hải Châu Đà Nẵng",
    city: "Đà Nẵng",
    address: "Số 52 Đường Nguyễn Văn Linh, Phường Nam Dương, Quận Hải Châu, Đà Nẵng",
    phone: "0902.999.888",
    avatar: "https://images.unsplash.com/photo-1558981806-ec527fa84c39?auto=format&fit=crop&w=300&q=80",
    cover: "https://images.unsplash.com/photo-1609630875171-b1321377ee65?auto=format&fit=crop&w=1200&q=80",
    rating: 4.9,
    active: true,
    bio: "Trung tâm dịch vụ bảo dưỡng, phụ tùng đồ chơi xe cào cào và môtô phân khối lớn trọng điểm miền Trung."
  }
];

export const CATEGORIES = [
  { id: 'all', name: 'Tất Cả Xe & Đồ Chơi', icon: 'fa-border-all' },
  { id: 'dirtbike', name: 'Xe Cào Cào - Địa Hình', icon: 'fa-mountain' },
  { id: 'superbike', name: 'Superbike / Thể Thao', icon: 'fa-motorcycle' },
  { id: 'hyper-naked', name: 'Hyper Naked Đường Phố', icon: 'fa-bolt' },
  { id: 'cruiser', name: 'Cruiser & Chopper Mỹ', icon: 'fa-road' },
  { id: 'adventure', name: 'Adventure Phượt Bụi', icon: 'fa-compass' },
  { id: 'scooter', name: 'Tay Ga Độc Lạ Ý', icon: 'fa-gem' },
  { id: 'gear-parts', name: 'Pô Độ & Đồ Cào Cào', icon: 'fa-helmet-safety' }
];

export const INITIAL_PRODUCTS = [
  // --- NHÓM XE CÀO CÀO - XE ĐỊA HÌNH (DIRT BIKE / ENDURO / MOTOCROSS) ---
  {
    id: 101,
    name: "KTM 300 EXC TPI Factory Edition 2024",
    brand: "KTM",
    category: "dirtbike",
    storeId: 1,
    storeName: "Showroom Phú Nhuận TP.HCM",
    price: 385000000,
    promotionalPrice: 368000000,
    priceDisplay: "368 Triệu",
    quantity: 4,
    sold: 17, // > 10 cho Guest
    rating: 5.0,
    engine: "293.2 cc 2-Thì TPI Phun Xăng Điện Tử",
    power: "54 HP (Trọng lượng siêu nhẹ 103.4 kg)",
    color: "Cam Đen Factory Racing",
    odo: "Lướt 2.100 km",
    year: "2024",
    badges: ["HQCN Sang Tên", "Bao Test Hãng", "Bao Quay Đầu"],
    image: "https://images.unsplash.com/photo-1544829099-b9a0c07fad1a?auto=format&fit=crop&w=800&q=80",
    description: "Vua cào cào Enduro 2 thì số 1 thế giới với công nghệ phun xăng điện tử TPI độc quyền, phuộc trước hành trình ngược WP XPLOR 48mm điều chỉnh độc lập, chảng ba CNC cam anodize, mâm nan hoa DID DirtStar gia cường vượt mọi địa hình bùn lầy, dốc đá hiểm trở.",
    isSelling: true
  },
  {
    id: 102,
    name: "KTM 450 EXC-F Six Days Argentina Edition",
    brand: "KTM",
    category: "dirtbike",
    storeId: 1,
    storeName: "Showroom Phú Nhuận TP.HCM",
    price: 465000000,
    promotionalPrice: 445000000,
    priceDisplay: "445 Triệu",
    quantity: 3,
    sold: 15, // > 10 cho Guest
    rating: 4.95,
    engine: "449.9 cc SOHC 4-Van Làm Mát Chất Lỏng",
    power: "63 HP (Trọng lượng 105.5 kg)",
    color: "Trắng Xanh Cam Six Days",
    odo: "Xe Mới 100%",
    year: "2024",
    badges: ["HQCN", "Six Days Limited", "Bao Giao Xe Toàn Quốc"],
    image: "https://images.unsplash.com/photo-1508974239320-0a029497e820?auto=format&fit=crop&w=800&q=80",
    description: "Chiến mã cào cào Enduro thi đấu quốc tế phiên bản Six Days danh giá. Động cơ 450cc 4 thì cực kỳ bền bỉ và bốc ở dải tua đầu, trang bị bảo vệ gầm nhôm nguyên khối, quạt tản nhiệt thông minh và hệ thống kiểm soát lực kéo Traction Control đa cấp độ.",
    isSelling: true
  },
  {
    id: 103,
    name: "Husqvarna FE 350 Pro Edition Thụy Điển",
    brand: "Husqvarna",
    category: "dirtbike",
    storeId: 2,
    storeName: "Showroom Cầu Giấy Hà Nội",
    price: 420000000,
    promotionalPrice: 399000000,
    priceDisplay: "399 Triệu",
    quantity: 3,
    sold: 12, // > 10 cho Guest
    rating: 4.9,
    engine: "349.7 cc DOHC Piston Rèn Siêu Nhẹ",
    power: "45 HP (Tỉ số nén 12.3:1)",
    color: "Trắng Xanh Vàng Bắc Âu",
    odo: "Lướt 3.400 km",
    year: "2023",
    badges: ["HQCN", "Bao Rút Hồ Sơ", "Bao Quay Đầu"],
    image: "https://images.unsplash.com/photo-1609630875171-b1321377ee65?auto=format&fit=crop&w=800&q=80",
    description: "Tuyệt tác cào cào cao cấp đến từ Thụy Điển. Sự kết hợp hoàn hảo giữa trọng lượng nhẹ như xe 250cc và sức kéo mạnh mẽ của xe 450cc. Khung phụ bằng sợi carbon composite 2 mảnh, cùm phanh cùm côn thủy lực Brembo và mâm căm nhôm Excel cao cấp.",
    isSelling: true
  },
  {
    id: 104,
    name: "Honda CRF450R Motocross AMA Champion",
    brand: "Honda",
    category: "dirtbike",
    storeId: 1,
    storeName: "Showroom Phú Nhuận TP.HCM",
    price: 345000000,
    promotionalPrice: 325000000,
    priceDisplay: "325 Triệu",
    quantity: 5,
    sold: 19, // > 10 cho Guest
    rating: 5.0,
    engine: "449.8 cc Unicam 4 Van Làm Mát Kép",
    power: "58 HP",
    color: "Đỏ Trắng HRC Honda Racing",
    odo: "Xe Mới 100%",
    year: "2024",
    badges: ["Nhập Khẩu Hải Quan", "Bao Test", "Bao Ship"],
    image: "https://images.unsplash.com/photo-1544829099-b9a0c07fad1a?auto=format&fit=crop&w=800&q=80",
    description: "Chiến mã cào cào bay vô địch các giải đua cào cào địa hình danh giá AMA Motocross và Supercross của Mỹ. Khung nhôm đôi thế hệ thứ 7, phuộc trước Showa ti 49mm hành trình nhún 310mm nuốt trọn mọi cú nhảy tiếp đất từ độ cao 5 mét.",
    isSelling: true
  },
  {
    id: 105,
    name: "Kawasaki KLX300 Dual-Sport Đa Địa Hình",
    brand: "Kawasaki",
    category: "dirtbike",
    storeId: 3,
    storeName: "Showroom Hải Châu Đà Nẵng",
    price: 185000000,
    promotionalPrice: 178000000,
    priceDisplay: "178 Triệu",
    quantity: 6,
    sold: 22, // > 10 cho Guest
    rating: 4.88,
    engine: "292 cc DOHC Phun Xăng Điện Tử",
    power: "33 HP (Làm mát bằng két nước)",
    color: "Xanh Lá Đen Lime Green",
    odo: "Lướt 4.200 km (Biển TP)",
    year: "2023",
    badges: ["HQCN Sang Tên 24h", "Bao Rút Gốc", "Biển TP VIP"],
    image: "https://images.unsplash.com/photo-1558980394-4c7c9299fe96?auto=format&fit=crop&w=800&q=80",
    description: "Dòng xe cào cào phố và phượt rừng bền bỉ được anh em biker săn đón nhiều nhất tại Duy Trương Motor. Yên xe êm ái, bảo dưỡng tiết kiệm, máy 300cc êm ái chạy phố hàng ngày nhẹ nhàng và leo đèo dốc off-road cực kỳ tự tin.",
    isSelling: true
  },
  {
    id: 106,
    name: "Aprilia SX 125 Supermoto Bánh Trơn Chạy Phố",
    brand: "Aprilia",
    category: "dirtbike",
    storeId: 1,
    storeName: "Showroom Phú Nhuận TP.HCM",
    price: 98000000,
    promotionalPrice: 92000000,
    priceDisplay: "92 Triệu",
    quantity: 7,
    sold: 16, // > 10 cho Guest
    rating: 4.85,
    engine: "124.2 cc 4-Thì 4-Van DOHC Euro 5",
    power: "15 HP (Bằng lái A1)",
    color: "Đỏ Đen Thể Thao SX",
    odo: "Lướt 1.800 km",
    year: "2024",
    badges: ["HQCN", "Bằng A1 Chạy Được", "Hỗ Trợ Trả Góp"],
    image: "https://images.unsplash.com/photo-1558981359-219d6364c9c8?auto=format&fit=crop&w=800&q=80",
    description: "Chiếc cào cào phố dáng Supermoto bánh trơn cực ngầu đến từ hãng xe đua nước Ý. Hệ thống phuộc up-side-down 41mm cứng cáp, phanh đĩa trước 300mm có ABS chống bó cứng an toàn, dáng ngồi cao ráo tôn dáng người lái.",
    isSelling: true
  },

  // --- NHÓM SUPERBIKE / SPORTBIKE ---
  {
    id: 1,
    name: "Ducati Panigale V4 S 2025",
    brand: "Ducati",
    category: "superbike",
    storeId: 1,
    storeName: "Showroom Phú Nhuận TP.HCM",
    price: 1180000000,
    promotionalPrice: 1150000000,
    priceDisplay: "1 Tỷ 150 Tr",
    quantity: 4,
    sold: 18, // > 10 cho Guest
    rating: 5.0,
    engine: "1.103 cc Desmosedici Stradale V4",
    power: "215.5 HP (Tốc độ tối đa > 310 km/h)",
    color: "Đỏ Rosso Corsa Ý",
    odo: "Xe Mới 100%",
    year: "2025",
    badges: ["HQCN", "Biển VIP", "Bao Test Hãng"],
    image: "https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?auto=format&fit=crop&w=800&q=80",
    description: "Tuyệt tác tốc độ đỉnh cao của Ducati với khối động cơ MotoGP, phuộc điện tử Ohlins Smart EC 2.0 điều chỉnh theo thời gian thực, mâm hợp kim nhôm rèn Marchesini siêu nhẹ, cánh gió kép khí động học Biplane Wings.",
    isSelling: true
  },
  {
    id: 2,
    name: "Kawasaki Ninja H2 Carbon Supercharged",
    brand: "Kawasaki",
    category: "superbike",
    storeId: 1,
    storeName: "Showroom Phú Nhuận TP.HCM",
    price: 1350000000,
    promotionalPrice: 1290000000,
    priceDisplay: "1 Tỷ 290 Tr",
    quantity: 2,
    sold: 16, // > 10 cho Guest
    rating: 4.98,
    engine: "998 cc Siêu Nạp Supercharged Độc Quyền",
    power: "231 HP (Âm thanh họng xả rít uy lực)",
    color: "Đen Nhám Sợi Carbon",
    odo: "Lướt 3.200 km",
    year: "2024",
    badges: ["Độc Nhất VN", "HQCN", "Bao Quay Đầu"],
    image: "https://images.unsplash.com/photo-1615172282427-9a57ef2d142e?auto=format&fit=crop&w=800&q=80",
    description: "Quái vật siêu nạp sản sinh 231 mã lực, dàn áo sợi carbon CFRP cao cấp phủ sơn tự liền sẹo độc quyền của Kawasaki, hệ thống phanh Brembo Stylema đĩa kép 330mm.",
    isSelling: true
  },
  {
    id: 3,
    name: "BMW S1000RR M Package 2025 (Cá Mập Lên Cánh)",
    brand: "BMW",
    category: "superbike",
    storeId: 2,
    storeName: "Showroom Cầu Giấy Hà Nội",
    price: 1099000000,
    promotionalPrice: 1050000000,
    priceDisplay: "1 Tỷ 050 Tr",
    quantity: 4,
    sold: 19, // > 10 cho Guest
    rating: 5.0,
    engine: "999 cc 4-Xi Lanh Thẳng Hàng ShiftCam",
    power: "210 HP (Van biến thiên công nghệ F1)",
    color: "Trắng Đỏ Xanh M Motorsport",
    odo: "Lướt 1.500 km",
    year: "2025",
    badges: ["HQCN", "Bản M Cao Cấp", "Mâm Carbon"],
    image: "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=800&q=80",
    description: "Vua tốc độ 'Cá mập' phiên bản M Performance cao cấp nhất với mâm Carbon M nguyên khối, gắp sau M điều chỉnh độ cao, cánh gió M Winglets khí động học tạo lực ép 17.1kg ở 300 km/h.",
    isSelling: true
  },

  // --- NHÓM CRUISER & TOURING ---
  {
    id: 4,
    name: "Harley-Davidson Fat Boy 114 Vivid Black",
    brand: "Harley-Davidson",
    category: "cruiser",
    storeId: 2,
    storeName: "Showroom Cầu Giấy Hà Nội",
    price: 899000000,
    promotionalPrice: 860000000,
    priceDisplay: "860 Triệu",
    quantity: 5,
    sold: 13, // > 10 cho Guest
    rating: 4.88,
    engine: "1.868 cc Milwaukee-Eight 114 ci",
    power: "94 HP (Mô-men xoắn 161 Nm cực khủng)",
    color: "Đen Bóng Vivid Black & Chrome",
    odo: "Lướt 2.800 km",
    year: "2023",
    badges: ["HQCN", "Cơ Bắp Mỹ", "Bao Test Hãng"],
    image: "https://images.unsplash.com/photo-1558981806-ec527fa84c39?auto=format&fit=crop&w=800&q=80",
    description: "Biểu tượng cơ bắp phong trần Mỹ bất hủ. Mâm đúc nhôm nguyên khối Lakester bản to 240mm lốp sau, tiếng pô thì thầm uy lực rung chuyển lồng ngực mỗi cú vít ga.",
    isSelling: true
  },

  // --- NHÓM HYPERNAKED ---
  {
    id: 5,
    name: "Ducati Streetfighter V4 SP2 Special Edition",
    brand: "Ducati",
    category: "hyper-naked",
    storeId: 1,
    storeName: "Showroom Phú Nhuận TP.HCM",
    price: 1250000000,
    promotionalPrice: 1200000000,
    priceDisplay: "1 Tỷ 200 Tr",
    quantity: 2,
    sold: 15, // > 10 cho Guest
    rating: 4.97,
    engine: "1.103 cc Desmosedici Stradale",
    power: "208 HP (Nồi khô STM-EVO SBK leng keng)",
    color: "Đen Nhám Winter Test MotoGP",
    odo: "Xe Mới 100%",
    year: "2024",
    badges: ["Bản Giới Hạn SP2", "HQCN", "Nồi Khô Leng Keng"],
    image: "https://images.unsplash.com/photo-1558981408-db0ecd8a1ee4?auto=format&fit=crop&w=800&q=80",
    description: "Mẫu Hyper Naked mạnh mẽ nhất hành tinh. Động cơ V4 kết hợp bộ nồi khô STM đua MotoGP âm thanh leng keng huyền thoại, mâm carbon 5 chấu siêu nhẹ và cánh gió đôi Biplane Wings.",
    isSelling: true
  },

  // --- NHÓM ADVENTURE PHƯỢT BỤI ---
  {
    id: 6,
    name: "BMW R1250GS Adventure Triple Black (Vua Địa Hình)",
    brand: "BMW",
    category: "adventure",
    storeId: 2,
    storeName: "Showroom Cầu Giấy Hà Nội",
    price: 799000000,
    promotionalPrice: 770000000,
    priceDisplay: "770 Triệu",
    quantity: 5,
    sold: 24, // > 10 cho Guest
    rating: 4.96,
    engine: "1.254 cc Động Cơ Boxer Đối Xứng ShiftCam",
    power: "136 HP (Bình xăng khủng 30 Lít)",
    color: "Đen Triple Black Huyền Bí",
    odo: "Lướt 6.500 km",
    year: "2023",
    badges: ["Vua Phượt", "HQCN", "Full 3 Thùng Nhôm"],
    image: "https://images.unsplash.com/photo-1609630875171-b1321377ee65?auto=format&fit=crop&w=800&q=80",
    description: "Vua việt dã thám hiểm thế giới. Động cơ Boxer 1.254cc êm ái đầm chắc, phuộc điện tử Dynamic ESA cân bằng tải trọng tự động, hỗ trợ khởi hành ngang dốc Hill Start Control và full bộ 3 thùng nhôm chính hãng.",
    isSelling: true
  },

  // --- NHÓM XE TAY GA ĐỘC LẠ Ý ---
  {
    id: 7,
    name: "Vespa 946 Christian Dior Limited Edition (Hàng Sưu Tầm)",
    brand: "Vespa",
    category: "scooter",
    storeId: 1,
    storeName: "Showroom Phú Nhuận TP.HCM",
    price: 750000000,
    promotionalPrice: 720000000,
    priceDisplay: "720 Triệu",
    quantity: 2,
    sold: 11, // > 10 cho Guest
    rating: 4.99,
    engine: "125 cc 3V i-Get Phun Xăng Điện Tử",
    power: "11.6 HP",
    color: "Trắng Ngọc Trai Sữa & Vàng Dior",
    odo: "Odo 350 km (Như Mới Đập Hộp)",
    year: "2022",
    badges: ["Siêu Hiếm", "Full Túi Thùng Dior", "HQCN"],
    image: "https://images.unsplash.com/photo-1558981285-6f0c94958bb6?auto=format&fit=crop&w=800&q=80",
    description: "Tuyệt phẩm thời trang giới hạn do Vespa bắt tay cùng nhà mốt Christian Dior Paris thiết kế. Họa tiết Dior Oblique đặc trưng dưới yên xe, đi kèm túi hành lý và mũ bảo hiểm đồng bộ chính hãng.",
    isSelling: true
  },

  // --- NHÓM PHỤ TÙNG & ĐỒ BẢO HỘ CÀO CÀO / BIKER ---
  {
    id: 8,
    name: "Mũ Bảo Hiểm Cào Cào LS2 MX703 X-Force Carbon Full Face",
    brand: "LS2",
    category: "gear-parts",
    storeId: 1,
    storeName: "Showroom Phú Nhuận TP.HCM",
    price: 12500000,
    promotionalPrice: 11200000,
    priceDisplay: "11.2 Triệu",
    quantity: 15,
    sold: 28, // > 10 cho Guest
    rating: 4.95,
    engine: "Vỏ Sợi Carbon 100% Siêu Nhẹ 1.300g",
    power: "Chuẩn FIM & ECE 22.06",
    color: "Carbon Vân 3K Bóng & Kính 100% Armega",
    odo: "Mới 100% Full Box",
    year: "2024",
    badges: ["Chính Hãng", "Carbon Siêu Nhẹ", "Tặng Kính 100%"],
    image: "https://images.unsplash.com/photo-1578328819058-b69f3a3b0f6b?auto=format&fit=crop&w=800&q=80",
    description: "Mũ bảo hiểm cào cào cao cấp bằng sợi carbon nguyên khối siêu nhẹ chuẩn thi đấu quốc tế, lót kháng khuẩn tháo giặt, khe hút gió làm mát tối đa khi bay nhảy trên sa mạc hoặc cung đường bùn đất.",
    isSelling: true
  },
  {
    id: 9,
    name: "Hệ Thống Pô Akrapovic Full Titanium Racing Cho Môtô & Cào Cào",
    brand: "Akrapovic",
    category: "gear-parts",
    storeId: 1,
    storeName: "Showroom Phú Nhuận TP.HCM",
    price: 125000000,
    promotionalPrice: 118000000,
    priceDisplay: "118 Triệu",
    quantity: 8,
    sold: 32, // > 10 cho Guest
    rating: 4.99,
    engine: "Hợp Kim Titanium Hàng Không Siêu Bền",
    power: "Tăng 7.8 HP • Giảm 6.2 kg Trọng Lượng",
    color: "Titanium Ánh Xanh Nhiệt Độ",
    odo: "Mới 100% Full Box Slovenia",
    year: "2024",
    badges: ["Chính Hãng Akrapovic", "Bao Lắp Đặt", "Dynojet Test"],
    image: "https://images.unsplash.com/photo-1558980664-769d59546b3d?auto=format&fit=crop&w=800&q=80",
    description: "Hệ thống ống xả đua danh tiếng số 1 thế giới làm thủ công tại Slovenia. Giúp xe giải nhiệt tức thì, âm thanh uy lực xé gió và được dyno map xăng chuẩn chỉ tại xưởng Duy Trương Motor.",
    isSelling: true
  }
];

export const INITIAL_REVIEWS = [
  {
    id: 1,
    productId: 101, // KTM 300 EXC TPI
    userName: "Minh Biker Cào Cào",
    userAvatar: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80",
    stars: 5,
    date: "24/10/2026",
    content: "Chiếc KTM 300 EXC TPI này leo dốc đồi cát và vượt suối đá ở Lâm Đồng phải gọi là đỉnh cao tuyệt đối! Máy 2 thì phun xăng TPI cực êm, không bị hụt ga, phuộc WP XPLOR nhún êm ru không mỏi tay. Duy Trương Motor hỗ trợ rút hồ sơ gốc sang tên trong 1 nốt nhạc!",
    mediaUrl: "https://images.unsplash.com/photo-1544829099-b9a0c07fad1a?auto=format&fit=crop&w=800&q=80"
  },
  {
    id: 2,
    productId: 1, // Panigale V4 S
    userName: "Hoàng Biker VIP",
    userAvatar: "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=150&q=80",
    stars: 5,
    date: "25/10/2026",
    content: "Đã rước em Panigale V4 S này về đội! Bàn giao xe bằng xe sàn trượt thủy lực VIP tận sảnh toà nhà, xe được bọc vải nhung sạch bóng không một vết xước. Đội ngũ kỹ thuật viên xưởng Dyno căn chỉnh phuộc Ohlins rất chuyên nghiệp và nhiệt tình!",
    mediaUrl: "https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?auto=format&fit=crop&w=800&q=80"
  },
  {
    id: 3,
    productId: 105, // KLX300
    userName: "Quốc Tuấn Enduro",
    userAvatar: "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=150&q=80",
    stars: 5,
    date: "22/10/2026",
    content: "KLX300 quá lành và tiết kiệm xăng. Mình vừa bào tour Tây Bắc 1.200 km về xe chạy phà phà, máy mát rượi. Mua xe bên Duy Trương Motor an tâm nhất là cam kết bao quay đầu lỗ cực thấp, chính sách hậu mãi rõ ràng như Vương Khang!",
    mediaUrl: "https://images.unsplash.com/photo-1558980394-4c7c9299fe96?auto=format&fit=crop&w=800&q=80"
  }
];

export const DEMO_USERS = {
  guest: {
    role: "GUEST",
    name: "Khách Tham Quan",
    avatar: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=150&q=80",
    email: "guest@duytruongmotor.com",
    canBuy: false
  },
  customer: {
    role: "CUSTOMER",
    name: "Hoàng Biker (Khách VIP)",
    avatar: "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=150&q=80",
    email: "customer@gmail.com",
    points: 1250,
    canBuy: true
  },
  vendor: {
    role: "VENDOR",
    name: "Chủ Showroom Phú Nhuận",
    avatar: "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=150&q=80",
    email: "vendor@duytruongmotor.com",
    storeId: 1,
    storeName: "Chi nhánh Phú Nhuận TP.HCM",
    canBuy: true
  },
  admin: {
    role: "ADMIN",
    name: "Duy Trương (Tổng Quản Trị)",
    avatar: "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?auto=format&fit=crop&w=150&q=80",
    email: "admin@duytruongmotor.com",
    canBuy: true
  },
  shipper: {
    role: "SHIPPER",
    name: "Tài Xế Tuấn (Xe Sàn Trượt VIP)",
    avatar: "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?auto=format&fit=crop&w=150&q=80",
    email: "shipper@duytruongmotor.com",
    canBuy: false
  }
};
