// Duy Truong Moto Luxury Data & Chains

export const SHOWROOMS = [
  {
    id: 1,
    name: "Duy Truong Moto - Flagship Showroom Quận 1",
    city: "TP. Hồ Chí Minh",
    address: "Số 88 Đường Nguyễn Hữu Cảnh, Phường 22, Quận 1, TP. HCM",
    phone: "0903.456.789",
    avatar: "https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?auto=format&fit=crop&w=300&q=80",
    cover: "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=1200&q=80",
    rating: 4.9,
    active: true,
    bio: "Showroom trưng bày siêu môtô nhập khẩu lớn nhất miền Nam. Đầy đủ xưởng Dynojet và phòng bàn giao xe VIP."
  },
  {
    id: 2,
    name: "Duy Truong Moto - Showroom Cầu Giấy",
    city: "Hà Nội",
    address: "Số 168 Đường Trần Duy Hưng, Cầu Giấy, Hà Nội",
    phone: "0901.234.567",
    avatar: "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=300&q=80",
    cover: "https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?auto=format&fit=crop&w=1200&q=80",
    rating: 5.0,
    active: true,
    bio: "Showroom tiêu chuẩn châu Âu tại Thủ đô Hà Nội. Chuyên phân phối các dòng xe BMW, Ducati, Harley-Davidson."
  },
  {
    id: 3,
    name: "Duy Truong Moto - Showroom Hải Châu",
    city: "Đà Nẵng",
    address: "Số 52 Đường Nguyễn Văn Linh, Quận Hải Châu, Đà Nẵng",
    phone: "0902.999.888",
    avatar: "https://images.unsplash.com/photo-1558981806-ec527fa84c39?auto=format&fit=crop&w=300&q=80",
    cover: "https://images.unsplash.com/photo-1558981285-6f0c94958bb6?auto=format&fit=crop&w=1200&q=80",
    rating: 4.8,
    active: true,
    bio: "Trung tâm dịch vụ bảo dưỡng và showroom xe phân khối lớn trọng điểm miền Trung."
  }
];

export const CATEGORIES = [
  { id: 'all', name: 'Tất Cả Sản Phẩm', icon: 'fa-border-all' },
  { id: 'superbike', name: 'Superbike / Sport', icon: 'fa-motorcycle' },
  { id: 'hyper-naked', name: 'Hyper Naked', icon: 'fa-bolt' },
  { id: 'cruiser', name: 'Cruiser & Touring', icon: 'fa-road' },
  { id: 'exhaust-parts', name: 'Pô Độ & Phụ Tùng', icon: 'fa-wrench' },
  { id: 'gear', name: 'Đồ Bảo Hộ AGV', icon: 'fa-helmet-safety' }
];

export const INITIAL_PRODUCTS = [
  {
    id: 1,
    name: "Ducati Panigale V4 S 2026",
    brand: "Ducati",
    category: "superbike",
    storeId: 1,
    storeName: "Showroom Quận 1",
    price: 950000000,
    promotionalPrice: 920000000,
    quantity: 5,
    sold: 18, // > 10 for Guest
    rating: 5.0,
    engine: "1.103 cc Desmosedici Stradale V4",
    power: "215.5 HP",
    color: "Đỏ Rosso Corsa",
    image: "https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?auto=format&fit=crop&w=800&q=80",
    description: "Siêu môtô hàng đầu thế giới với hệ thống phuộc điện tử Ohlins Smart EC 2.0, mâm hợp kim nhôm rèn Marchesini siêu nhẹ, cánh gió kép khí động học Biplane Wings.",
    isSelling: true
  },
  {
    id: 2,
    name: "Kawasaki Ninja H2 Carbon Supercharged",
    brand: "Kawasaki",
    category: "superbike",
    storeId: 1,
    storeName: "Showroom Quận 1",
    price: 1350000000,
    promotionalPrice: 1290000000,
    quantity: 3,
    sold: 14, // > 10 for Guest
    rating: 4.9,
    engine: "998 cc Supercharged Độc Quyền",
    power: "231 HP",
    color: "Đen Nhám Carbon",
    image: "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=800&q=80",
    description: "Tuyệt tác siêu nạp với sức mạnh 231 mã lực, dàn áo sợi carbon gia cường CFRP, phanh Brembo Stylema đĩa kép 330mm.",
    isSelling: true
  },
  {
    id: 3,
    name: "BMW S1000RR M Package 2026",
    brand: "BMW",
    category: "superbike",
    storeId: 2,
    storeName: "Showroom Cầu Giấy",
    price: 1099000000,
    promotionalPrice: 1050000000,
    quantity: 4,
    sold: 19, // > 10 for Guest
    rating: 5.0,
    engine: "999 cc 4-Xi Lanh Thẳng Hàng ShiftCam",
    power: "210 HP",
    color: "Trắng Đỏ Xanh M Motorsport",
    image: "https://images.unsplash.com/photo-1558981806-ec527fa84c39?auto=format&fit=crop&w=800&q=80",
    description: "Vua tốc độ 'Cá mập' phiên bản M Performance với mâm carbon nguyên khối, ắc quy M siêu nhẹ, gắp sau M và cánh gió khí động học M Winglets.",
    isSelling: true
  },
  {
    id: 4,
    name: "Harley-Davidson Fat Boy 114 V-Twin",
    brand: "Harley-Davidson",
    category: "cruiser",
    storeId: 2,
    storeName: "Showroom Cầu Giấy",
    price: 899000000,
    promotionalPrice: 870000000,
    quantity: 6,
    sold: 12, // > 10 for Guest
    rating: 4.8,
    engine: "1.868 cc Milwaukee-Eight 114",
    power: "94 HP (161 Nm Torque)",
    color: "Vàng Đồng Vivid Black",
    image: "https://images.unsplash.com/photo-1558981285-6f0c94958bb6?auto=format&fit=crop&w=800&q=80",
    description: "Biểu tượng cơ bắp Mỹ với khối động cơ V-Twin 1.868cc cực kỳ uy lực, mâm đúc nhôm nguyên khối Lakester bản to 240mm.",
    isSelling: true
  },
  {
    id: 5,
    name: "Ducati Streetfighter V4 S",
    brand: "Ducati",
    category: "hyper-naked",
    storeId: 1,
    storeName: "Showroom Quận 1",
    price: 890000000,
    promotionalPrice: 860000000,
    quantity: 7,
    sold: 15, // > 10 for Guest
    rating: 4.9,
    engine: "1.103 cc Desmosedici Stradale",
    power: "208 HP",
    color: "Xám Nhám Dark Stealth",
    image: "https://images.unsplash.com/photo-1558981408-db0ecd8a1ee4?auto=format&fit=crop&w=800&q=80",
    description: "Mẫu Hyper Naked mạnh mẽ nhất hành tinh, ghi-đông cao thể thao, cánh gió Biplane Wings gia tăng lực ép 28kg ở 270 km/h.",
    isSelling: true
  },
  {
    id: 6,
    name: "Ống Xả Akrapovic Full System Titanium Cho Panigale V4",
    brand: "Akrapovic",
    category: "exhaust-parts",
    storeId: 1,
    storeName: "Showroom Quận 1",
    price: 145000000,
    promotionalPrice: 138000000,
    quantity: 15,
    sold: 25, // > 10 for Guest
    rating: 5.0,
    engine: "Titanium Cao Cấp",
    power: "+12 HP",
    color: "Titan Mờ Tự Nhiên",
    image: "https://images.unsplash.com/photo-1558980664-769d59546b3d?auto=format&fit=crop&w=800&q=80",
    description: "Hệ thống pô đua Akrapovic full titan chính hãng, trọng lượng nhẹ hơn pô zin 6.5kg, âm thanh gầm rú đặc trưng của trường đua GP.",
    isSelling: true
  },
  {
    id: 7,
    name: "Mũ Bảo Hiểm AGV Pista GP RR Carbon ECE 22.06",
    brand: "AGV",
    category: "gear",
    storeId: 2,
    storeName: "Showroom Cầu Giấy",
    price: 38000000,
    promotionalPrice: 35000000,
    quantity: 20,
    sold: 42, // > 10 for Guest
    rating: 5.0,
    engine: "100% Sợi Carbon",
    power: "Chuẩn FIM Racing",
    color: "Carbon Bóng Nhũ Bạc",
    image: "https://images.unsplash.com/photo-1558981359-219d6364c9c8?auto=format&fit=crop&w=800&q=80",
    description: "Nón bảo hiểm chuẩn đua MotoGP đẳng cấp nhất thế giới, tầm nhìn góc rộng 190 độ, kính quang học Class 1 chống trầy và đọng sương.",
    isSelling: true
  },
  {
    id: 8,
    name: "Honda CBR1000RR-R Fireblade SP 30th Anniversary",
    brand: "Honda",
    category: "superbike",
    storeId: 3,
    storeName: "Showroom Đà Nẵng",
    price: 1050000000,
    promotionalPrice: 999000000,
    quantity: 4,
    sold: 16, // > 10 for Guest
    rating: 4.9,
    engine: "1.000 cc DOHC 4-Xi Lanh",
    power: "215 HP",
    color: "Tricolor Kỷ Niệm 30 Năm",
    image: "https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?auto=format&fit=crop&w=800&q=80",
    description: "Phiên bản kỷ niệm 30 năm huyền thoại Fireblade với phuộc điện tử Ohlins Smart EC NPX và heo phanh Brembo Stylema đường đua.",
    isSelling: true
  }
];

export const INITIAL_REVIEWS = [
  {
    id: 1,
    productId: 1,
    userName: "Hoàng Biker",
    userAvatar: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=200&q=80",
    stars: 5,
    date: "24/10/2026",
    content: "Đã nhận bàn giao xe Panigale V4 S tại Showroom Duy Truong Quận 1. Xe giao tận nhà bằng xe sàn trượt thủy lực bọc nhung rất sang trọng và an toàn, tiếng pô nổ ấm và uy lực kinh khủng! Rất hài lòng về thái độ tư vấn chuyên nghiệp của đội ngũ.",
    mediaUrl: "https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?auto=format&fit=crop&w=400&q=80"
  },
  {
    id: 2,
    productId: 3,
    userName: "Đức Nguyễn BMW",
    userAvatar: "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?auto=format&fit=crop&w=200&q=80",
    stars: 5,
    date: "20/10/2026",
    content: "Chiếc S1000RR M Package này chạy trên cao tốc cực kỳ đầm chắc, công nghệ ShiftCam bốc ở dải tua thấp và bứt phá dữ dội ở tua cao. Thủ tục thanh toán cọc qua VNPAY rất nhanh chóng chỉ 1 phút là có xác nhận hợp đồng ngay.",
    mediaUrl: null
  }
];

export const DEMO_USERS = {
  guest: { role: "GUEST", name: "Khách vãng lai", email: "", avatar: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=150&q=80" },
  customer: { role: "USER", name: "Hoàng Biker (VIP Bạc)", email: "customer@gmail.com", points: 350, wallet: 50000000, avatar: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=150&q=80" },
  vendor: { role: "VENDOR", name: "Minh (Trưởng Showroom Q1)", email: "vendor@duytruongmoto.com", storeId: 1, wallet: 150000000, avatar: "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?auto=format&fit=crop&w=150&q=80" },
  shipper: { role: "SHIPPER", name: "Tuấn (Tài Xế Sàn Trượt VIP)", email: "shipper@duytruongmoto.com", phone: "0904.567.890", avatar: "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=150&q=80" },
  admin: { role: "ADMIN", name: "Duy Trường (Tổng Quản Trị)", email: "admin@duytruongmoto.com", avatar: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80" }
};
