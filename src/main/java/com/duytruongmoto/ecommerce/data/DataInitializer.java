package com.duytruongmoto.ecommerce.data;

import com.duytruongmoto.ecommerce.entity.*;
import com.duytruongmoto.ecommerce.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final UserLevelRepository userLevelRepository;
    private final CommissionRepository commissionRepository;
    private final DeliveryRepository deliveryRepository;
    private final CategoryRepository categoryRepository;
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final VoucherRepository voucherRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info(">>> Duy Truong Motor DataInitializer: Đang khởi tạo hệ thống...");

        try {
            syncDemoAccounts();
        } catch (Exception e) {
            log.error("Lỗi syncDemoAccounts: {}", e.getMessage());
        }

        Map<String, Category> catMap = new HashMap<>();
        try {
            catMap = initCategories();
        } catch (Exception e) {
            log.error("Lỗi initCategories: {}", e.getMessage());
        }

        Map<Integer, Store> storeMap = new HashMap<>();
        try {
            storeMap = initStores();
        } catch (Exception e) {
            log.error("Lỗi initStores: {}", e.getMessage());
        }

        try {
            try { initVouchers(); } catch (Exception e) { log.error("Lỗi initVouchers: {}", e.getMessage()); }
            initAll60Products(catMap, storeMap);
        } catch (Exception e) {
            log.error("Lỗi initAll60Products: {}", e.getMessage());
        }

        log.info(">>> Đã đồng bộ thành công {} sản phẩm trong kho Duy Truong Motor!", productRepository.count());
    }

    private void syncDemoAccounts() {
        // User Levels
        UserLevel silver = userLevelRepository.findAll().stream()
                .filter(ul -> ul.getName() != null && ul.getName().contains("Bạc"))
                .findFirst()
                .orElseGet(() -> userLevelRepository.save(UserLevel.builder().name("Thành viên Bạc").minPoint(0L).discount(BigDecimal.ZERO).isDeleted(false).build()));

        UserLevel gold = userLevelRepository.findAll().stream()
                .filter(ul -> ul.getName() != null && ul.getName().contains("Vàng"))
                .findFirst()
                .orElseGet(() -> userLevelRepository.save(UserLevel.builder().name("Khách hàng Vàng").minPoint(1000L).discount(BigDecimal.valueOf(3.0)).isDeleted(false).build()));

        UserLevel diamond = userLevelRepository.findAll().stream()
                .filter(ul -> ul.getName() != null && (ul.getName().contains("Kim Cương") || ul.getName().contains("VIP")))
                .findFirst()
                .orElseGet(() -> userLevelRepository.save(UserLevel.builder().name("Khách hàng Kim Cương VIP").minPoint(5000L).discount(BigDecimal.valueOf(7.0)).isDeleted(false).build()));

        // Commissions & Deliveries
        if (commissionRepository.count() == 0) {
            commissionRepository.save(Commission.builder().name("Chiết khấu Tiêu chuẩn").cost(BigDecimal.valueOf(5.0)).description("Chiết khấu 5% cho showroom đối tác").isDeleted(false).build());
            commissionRepository.save(Commission.builder().name("Chiết khấu Đại lý VIP").cost(BigDecimal.valueOf(3.0)).description("Chiết khấu ưu đãi 3%").isDeleted(false).build());
        }
        if (deliveryRepository.count() == 0) {
            deliveryRepository.save(Delivery.builder().name("Giao xe sàn trượt thủy lực VIP (Chống trầy xước 100%)").description("Xe chuyên dụng sàn trượt hạ đất, bàn giao tận phòng khách khách hàng").price(BigDecimal.valueOf(1500000)).isDeleted(false).build());
            deliveryRepository.save(Delivery.builder().name("Xe tải thùng kín chuyên dụng chuyển môtô").description("Xe tải thùng bạt chuyên chở xe PKL đi tỉnh").price(BigDecimal.valueOf(800000)).isDeleted(false).build());
            deliveryRepository.save(Delivery.builder().name("Giao hàng tiêu chuẩn (Phụ tùng, đồ bảo hộ)").description("Giao nhanh trong 2-3 ngày").price(BigDecimal.valueOf(50000)).isDeleted(false).build());
        }

        // 1. Admin Toàn Sàn - Trương Quốc Duy (pass: admin123)
        // Không có hạng khách, không tích điểm, không ví ảo (Chuẩn hóa tài khoản Quản trị viên hệ thống)
        upsertUser("Duy", "Trương Quốc", "duy-truong-admin", "admin@duytruongmoto.com", "0909111999", "admin123", Role.ROLE_ADMIN, null, 0L, BigDecimal.ZERO);

        // 2. Vendor Showroom Cầu Giấy Hà Nội - Đỗ Trọng Khôi (pass: vendor123)
        upsertUser("Khôi", "Đỗ Trọng", "do-trong-khoi", "dotrongkhoi2002@gmail.com", "0903112002", "vendor123", Role.ROLE_VENDOR, gold, 3000L, BigDecimal.valueOf(180000000));

        // 3. Vendor Showroom Hải Châu Đà Nẵng - Lê Nghĩa Tình (pass: vendor123)
        upsertUser("Tình", "Lê Nghĩa", "le-nghia-tinh", "nghiatinh2002@gmail.com", "0905112002", "vendor123", Role.ROLE_VENDOR, gold, 2500L, BigDecimal.valueOf(140000000));

        // 4. Vendor Showroom Phú Nhuận TP.HCM - Minh Quản Lý (pass: vendor123)
        upsertUser("Minh", "Showroom Phú Nhuận", "minh-showroom-phunhuan", "vendor@duytruongmoto.com", "0903111222", "vendor123", Role.ROLE_VENDOR, gold, 2000L, BigDecimal.valueOf(150000000));

        // 5. Shipper Vận Tải Sàn Trượt VIP (pass: shipper123)
        upsertUser("Tuấn", "Vận Chuyển", "tuan-shipper", "shipper@duytruongmoto.com", "0904333444", "shipper123", Role.ROLE_SHIPPER, silver, 100L, BigDecimal.ZERO);

        // 6. Khách Hàng VIP (pass: customer123)
        upsertUser("Hoàng", "Biker VIP", "hoang-biker-vip", "customer@duytruongmoto.com", "0905555777", "customer123", Role.ROLE_USER, diamond, 5500L, BigDecimal.valueOf(75000000));
        upsertUser("Hùng", "Biker VIP", "hung-biker-vip", "hung.biker@duytruongmoto.com", "0905666888", "customer123", Role.ROLE_USER, diamond, 5500L, BigDecimal.valueOf(75000000));

        cleanupDuplicateUsers();
    }

    private void upsertUser(String firstname, String lastname, String slug, String email, String phone, String plainPassword, Role role, UserLevel userLevel, Long point, BigDecimal eWallet) {
        try {
            User user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                // Phone uniqueness check (safe)
                if (phone != null) {
                    try {
                        if (userRepository.existsByPhone(phone)) {
                            phone = "09" + String.valueOf(System.currentTimeMillis()).substring(5);
                        }
                    } catch (Exception ex) {
                        phone = "09" + String.valueOf(System.currentTimeMillis()).substring(5);
                    }
                }
                // Slug uniqueness check (safe)
                String finalSlug = slug;
                try {
                    final String checkSlug = slug;
                    if (userRepository.findAll().stream().anyMatch(u -> u.getSlug() != null && u.getSlug().equals(checkSlug))) {
                        finalSlug = slug + "-" + System.currentTimeMillis();
                    }
                } catch (Exception ignored) {
                    finalSlug = slug + "-" + System.currentTimeMillis();
                }
                user = User.builder()
                        .firstname(firstname).lastname(lastname).slug(finalSlug)
                        .email(email).phone(phone)
                        .password(passwordEncoder.encode(plainPassword))
                        .role(role).userLevel(userLevel)
                        .point(point).eWallet(eWallet)
                        .isEmailActive(true).isPhoneActive(true)
                        .avatar("https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=200&q=80")
                        .build();
                userRepository.save(user);
                log.info(">>> [DataInit] CREATED user: {} | role: {} | pass: {}", email, role, plainPassword);
            } else {
                // Always re-encode password on every startup to ensure correct credentials
                user.setPassword(passwordEncoder.encode(plainPassword));
                user.setFirstname(firstname);
                user.setLastname(lastname);
                user.setRole(role);
                user.setIsBlocked(false);
                user.setIsEmailActive(true);
                user.setIsPhoneActive(true);
                user.setUserLevel(userLevel);
                if (user.getPoint() == null || user.getPoint() == 0) user.setPoint(point);
                if (user.getEWallet() == null) user.setEWallet(eWallet);
                userRepository.save(user);
                log.info(">>> [DataInit] UPDATED user: {} | role: {} | password reset -> {}", email, role, plainPassword);
            }
        } catch (Exception ex) {
            log.error(">>> [DataInit] FAILED upsert user {}: {}", email, ex.getMessage(), ex);
        }
    }


    private void cleanupDuplicateUsers() {
        try {
            User admin = userRepository.findByEmail("admin@duytruongmoto.com").orElse(null);
            User vendorMinh = userRepository.findByEmail("vendor@duytruongmoto.com").orElse(admin);
            User shipperTuan = userRepository.findByEmail("shipper@duytruongmoto.com").orElse(admin);
            User customerHoang = userRepository.findByEmail("customer@duytruongmoto.com").orElse(admin);

            // Reassign and delete old duplicate @duytruongmotor.com or duytq4906 accounts safely
            userRepository.findByEmail("admin@duytruongmotor.com").ifPresent(u -> {
                orderRepository.findAll().stream().filter(o -> o.getUser() != null && o.getUser().getId().equals(u.getId()))
                        .forEach(o -> { o.setUser(admin); orderRepository.save(o); });
                try { userRepository.delete(u); } catch (Exception ignored) {}
            });

            userRepository.findByEmail("vendor@duytruongmotor.com").ifPresent(u -> {
                storeRepository.findAll().stream().filter(st -> st.getOwner() != null && st.getOwner().getId().equals(u.getId()))
                        .forEach(st -> { st.setOwner(vendorMinh); storeRepository.save(st); });
                try { userRepository.delete(u); } catch (Exception ignored) {}
            });

            userRepository.findByEmail("shipper@duytruongmotor.com").ifPresent(u -> {
                orderRepository.findAll().stream().filter(o -> o.getShipper() != null && o.getShipper().getId().equals(u.getId()))
                        .forEach(o -> { o.setShipper(shipperTuan); orderRepository.save(o); });
                try { userRepository.delete(u); } catch (Exception ignored) {}
            });

            userRepository.findByEmail("customer@gmail.com").ifPresent(u -> {
                orderRepository.findAll().stream().filter(o -> o.getUser() != null && o.getUser().getId().equals(u.getId()))
                        .forEach(o -> { o.setUser(customerHoang); orderRepository.save(o); });
                try { userRepository.delete(u); } catch (Exception ignored) {}
            });

            userRepository.findByEmail("duytq4906@gmail.com").ifPresent(u -> {
                try { userRepository.delete(u); } catch (Exception ignored) {}
            });

            log.info(">>> Đã dọn dẹp triệt để các tài khoản trùng tên miền @duytruongmotor.com trên database hiện hữu!");
        } catch (Exception ex) {
            log.warn("Lưu ý khi dọn dẹp user trùng lặp: {}", ex.getMessage());
        }
    }

    private void initVouchers() {
        if (voucherRepository.count() == 0) {
            voucherRepository.save(Voucher.builder()
                .code("BIKERVIP")
                .name("Ưu đãi khách hàng thân thiết Biker VIP")
                .description("Giảm 5% tối đa 30.000.000 đ cho các dòng xe phân khối lớn")
                .discountPercent(5)
                .maxDiscountAmount(BigDecimal.valueOf(30000000))
                .minOrderAmount(BigDecimal.valueOf(200000000))
                .usageLimit(100)
                .usedCount(5)
                .isDeleted(false)
                .build());

            voucherRepository.save(Voucher.builder()
                .code("FREESHIP-VIP")
                .name("Miễn phí 100% chi phí xe sàn trượt VIP")
                .description("Hỗ trợ chi phí xe sàn trượt thủy lực VIP tận nhà cho khách hàng")
                .discountPercent(100)
                .maxDiscountAmount(BigDecimal.valueOf(2000000))
                .minOrderAmount(BigDecimal.valueOf(100000000))
                .usageLimit(200)
                .usedCount(12)
                .isDeleted(false)
                .build());

            voucherRepository.save(Voucher.builder()
                .code("BAOHO-500K")
                .name("Voucher Phụ Kiện Đồ Bảo Hộ")
                .description("Giảm ngay 500.000 đ cho nón AGV, giáp Dainese, găng Alpinestars")
                .discountPercent(10)
                .maxDiscountAmount(BigDecimal.valueOf(500000))
                .minOrderAmount(BigDecimal.valueOf(5000000))
                .usageLimit(500)
                .usedCount(38)
                .isDeleted(false)
                .build());

            log.info(">>> Đã khởi tạo thành công 3 mã khuyến mãi Voucher mẫu!");
        }
    }

    private Map<String, Category> initCategories() {
        Map<String, Category> catMap = new HashMap<>();
        String[][] catDefs = {
            {"Siêu Xe Ô Tô & Xe Sang", "oto", "/images/lamborghini_huracan.jpg"},
            {"Xe Nhỏ & Tay Ga Sưu Tầm", "xenho", "/images/honda_monkey_125.jpg"},
            {"Xe Cào Cào - Địa Hình", "dirtbike", "/images/ktm_300_exc.jpg"},
            {"Superbike / Thể Thao", "superbike", "/images/ducati_panigale_v4s.jpg"},
            {"Hyper Naked Đường Phố", "hyper-naked", "/images/ducati_streetfighter.jpg"},
            {"Cruiser & Chopper Mỹ", "cruiser", "/images/harley_fatboy.jpg"},
            {"Cruiser & Touring", "cruiser-touring", "/images/harley_fatboy.jpg"},
            {"Adventure Phượt Bụi", "adventure", "/images/bmw_r1250gs.jpg"},
            {"Tay Ga Độc Lạ Ý", "scooter", "/images/yamaha_tmax560.jpg"},
            {"Pô Độ & Đồ Cào Cào", "gear-parts", "/images/akrapovic_exhaust.jpg"},
            {"Pô Độ & Phụ Tùng Cao Cấp", "phu-tung-po-do", "/images/sc_project_exhaust.jpg"},
            {"Đồ Bảo Hộ & Nón Bảo Hiểm", "do-bao-ho", "/images/agv_helmet.jpg"}
        };

        for (String[] def : catDefs) {
            String name = def[0];
            String slug = def[1];
            String img = def[2];
            Category cat = categoryRepository.findBySlug(slug).orElseGet(() -> 
                categoryRepository.save(Category.builder().name(name).slug(slug).image(img).isDeleted(false).build())
            );
            catMap.put(slug, cat);
        }
        return catMap;
    }

    private Map<Integer, Store> initStores() {
        Map<Integer, Store> storeMap = new HashMap<>();
        User vendorMinh = userRepository.findByEmail("vendor@duytruongmoto.com").orElse(null);
        User vendorKhoi = userRepository.findByEmail("dotrongkhoi2002@gmail.com").orElse(vendorMinh);
        User vendorTinh = userRepository.findByEmail("nghiatinh2002@gmail.com").orElse(vendorMinh);
        Commission comm = commissionRepository.findAll().stream().findFirst().orElse(null);

        // Showroom 1: Phú Nhuận TP.HCM (Quản lý: Vendor Minh)
        Store s1 = storeRepository.findBySlug("duy-truong-moto-showroom-quan-1").orElseGet(() ->
            Store.builder().slug("duy-truong-moto-showroom-quan-1").build()
        );
        s1.setName("Duy Truong Moto - Trụ Sở & Kho Phụ Tùng Phú Nhuận TP.HCM");
        s1.setOwner(vendorMinh);
        s1.setBio("Trụ sở chính & kho tổng phụ tùng đồ bảo hộ mô tô lớn nhất miền Nam. Quản lý bởi Minh Showroom Phú Nhuận.");
        s1.setAddress("Số 123 Đường Hoàng Văn Thụ, Phường 8, Quận Phú Nhuận, TP. Hồ Chí Minh");
        s1.setPhone("0903.456.789");
        s1.setAvatar("https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?auto=format&fit=crop&w=300&q=80");
        s1.setCover("https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=1200&q=80");
        s1.setIsActive(true);
        s1.setIsOpen(true);
        s1.setCommission(comm);
        s1 = storeRepository.save(s1);
        storeMap.put(1, s1);

        // Showroom 2: Cầu Giấy Hà Nội (Quản lý: Đỗ Trọng Khôi)
        Store s2 = storeRepository.findBySlug("duy-truong-moto-showroom-cau-giay").orElseGet(() ->
            Store.builder().slug("duy-truong-moto-showroom-cau-giay").build()
        );
        s2.setName("Duy Truong Moto - Chi Nhánh & Kho Phụ Tùng Cầu Giấy Hà Nội");
        s2.setOwner(vendorKhoi);
        s2.setBio("Chi nhánh miền Bắc phân phối đồ bảo hộ và phụ tùng xe mô tô. Quản lý bởi Đỗ Trọng Khôi.");
        s2.setAddress("Số 168 Đường Trần Duy Hưng, Cầu Giấy, Hà Nội");
        s2.setPhone("024.3999.8888");
        s2.setAvatar("https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=300&q=80");
        s2.setCover("https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?auto=format&fit=crop&w=1200&q=80");
        s2.setIsActive(true);
        s2.setIsOpen(true);
        s2.setCommission(comm);
        s2 = storeRepository.save(s2);
        storeMap.put(2, s2);

        // Showroom 3: Hải Châu Đà Nẵng (Quản lý: Lê Nghĩa Tình)
        Store s3 = storeRepository.findBySlug("duy-truong-moto-showroom-da-nang").orElseGet(() ->
            Store.builder().slug("duy-truong-moto-showroom-da-nang").build()
        );
        s3.setName("Duy Truong Moto - Chi Nhánh & Kho Phụ Tùng Hải Châu Đà Nẵng");
        s3.setOwner(vendorTinh);
        s3.setBio("Chi nhánh miền Trung phân phối đồ bảo hộ và phụ tùng xe mô tô. Quản lý bởi Lê Nghĩa Tình.");
        s3.setAddress("Số 52 Đường Nguyễn Văn Linh, Hải Châu, Đà Nẵng");
        s3.setPhone("0236.3888.999");
        s3.setAvatar("https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?auto=format&fit=crop&w=300&q=80");
        s3.setCover("https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=1200&q=80");
        s3.setIsActive(true);
        s3.setIsOpen(true);
        s3.setCommission(comm);
        s3 = storeRepository.save(s3);
        storeMap.put(3, s3);

        log.info(">>> Đã chuẩn hóa và đồng bộ 3 Showroom (HCM: 0903.456.789, HN: 024.3999.8888, ĐN: 0236.3888.999)!");
        return storeMap;
    }

    
    private void purgeDeprecatedData() {
        // Xóa các sản phẩm đồ bảo hộ/pô độ bị seed trùng lặp slug dài
        List<String> duplicateSlugs = Arrays.asList(
            "mu-bao-hiem-cao-cao-ls2-mx703-x-force-carbon-full-face-8",
            "he-thong-po-akrapovic-full-titanium-racing-cho-moto-cao-cao-9",
            "mu-bao-hiem-agv-pista-gp-rr-carbon-italia-limited-edition-401",
            "bo-giap-da-lien-dainese-mugello-3-d-air-ua-chuyen-nghiep-402",
            "gang-tay-ua-alpinestars-supertech-gp-carbon-pro-403",
            "he-thong-po-sc-project-sc1-r-titanium-worldsbk-edition-404",
            "bo-phuoc-truoc-sau-ohlins-fgr-250-ttx-gp-thuy-ien-cao-cap-405"
        );
        for (String s : duplicateSlugs) {
            productRepository.findBySlug(s).ifPresent(p -> {
                try {
                    productRepository.delete(p);
                    log.info("Purged duplicate product slug: {}", s);
                } catch (Exception ex) {
                    log.warn("Could not delete duplicate product: {}", s);
                }
            });
        }

        // Xóa danh mục ô tô và các sản phẩm ô tô nếu còn tồn tại trong DB Render
        categoryRepository.findBySlug("oto").ifPresent(cat -> {
            try {
                List<Product> cars = productRepository.findByCategoryId(cat.getId());
                if (cars != null && !cars.isEmpty()) {
                    productRepository.deleteAll(cars);
                    log.info("Purged {} car products from oto category", cars.size());
                }
                categoryRepository.delete(cat);
                log.info("Purged deprecated category 'oto'");
            } catch (Exception ex) {
                log.warn("Could not delete oto category: {}", ex.getMessage());
            }
        });

        // Xóa các danh mục trùng lặp khác
        List<String> deprecatedCats = Arrays.asList("cruiser-touring", "scooter", "phu-tung-po-do");
        for (String cs : deprecatedCats) {
            categoryRepository.findBySlug(cs).ifPresent(c -> {
                try {
                    categoryRepository.delete(c);
                    log.info("Purged deprecated category: {}", cs);
                } catch (Exception ex) {
                    log.warn("Could not delete category {}: {}", cs, ex.getMessage());
                }
            });
        }
    }

    private void seedProduct(Store store, Category category, String name, String slug, String brand,
                             BigDecimal price, BigDecimal promoPrice, int qty, int sold, double rating,
                             String engine, String color, String image, String desc) {
        Product p = productRepository.findBySlug(slug).orElse(null);
        if (p == null) {
            p = Product.builder().slug(slug).build();
        }
        p.setStore(store);
        p.setCategory(category);
        p.setName(name);
        p.setBrand(brand);
        p.setPrice(price);
        p.setPromotionalPrice(promoPrice);
        p.setQuantity(qty);
        p.setSold(sold);
        p.setRating(rating);
        p.setEngineCapacity(engine);
        p.setColor(color);
        p.setMainImage(image);
        p.setDescription(desc);
        p.setIsActive(true);
        p.setIsSelling(true);
        productRepository.save(p);
    }

    private void initAll60Products(Map<String, Category> catMap, Map<Integer, Store> storeMap) {
        seedProduct(storeMap.get(1), catMap.get("dirtbike"),
            "KTM 300 EXC TPI Factory Edition 2024",
            "ktm-300-exc-tpi-factory-edition-2024-101",
            "KTM",
            new BigDecimal("375000000"),
            new BigDecimal("355000000"),
            4, 18, 5,
            "293.2 cc 2-Thì TPI Phun Xăng Điện Tử",
            "Cam Đen Factory",
            "/images/ktm_300_exc.jpg",
            "Vua cào cào Enduro 2 thì số 1 thế giới với công nghệ phun xăng điện tử TPI độc quyền, phuộc trước hành trình ngược WP XPLOR 48mm điều chỉnh độc lập, chảng ba CNC cam anodize, mâm nan hoa DID DirtStar gia cường vượt mọi địa hình bùn lầy, dốc đá hiểm trở.");

        seedProduct(storeMap.get(1), catMap.get("dirtbike"),
            "KTM 450 EXC-F Six Days Argentina Edition",
            "ktm-450-exc-f-six-days-argentina-edition-102",
            "KTM",
            new BigDecimal("445000000"),
            new BigDecimal("425000000"),
            3, 14, 4.95,
            "449.9 cc SOHC 4-Van Làm Mát Chất Lỏng",
            "Cam Trắng Six Days Argentina",
            "/images/ktm_450_exc.jpg",
            "Chiến mã cào cào Enduro thi đấu quốc tế phiên bản Six Days danh giá. Động cơ 450cc 4 thì cực kỳ bền bỉ và bốc ở dải tua đầu, trang bị bảo vệ gầm nhôm nguyên khối, quạt tản nhiệt thông minh và hệ thống kiểm soát lực kéo Traction Control đa cấp độ.");

        seedProduct(storeMap.get(2), catMap.get("dirtbike"),
            "Husqvarna FE 350 Pro Edition Thụy Điển",
            "husqvarna-fe-350-pro-edition-thuy-ien-103",
            "Husqvarna",
            new BigDecimal("450000000"),
            new BigDecimal("429000000"),
            3, 12, 4.9,
            "349.7 cc DOHC Piston Rèn Siêu Nhẹ",
            "Trắng Xanh Vàng Thụy Điển",
            "/images/husqvarna_fe350.jpg",
            "Tuyệt tác cào cào cao cấp đến từ Thụy Điển. Sự kết hợp hoàn hảo giữa trọng lượng nhẹ như xe 250cc và sức kéo mạnh mẽ của xe 450cc. Khung phụ bằng sợi carbon composite 2 mảnh, cùm phanh cùm côn thủy lực Brembo và mâm căm nhôm Excel cao cấp.");

        seedProduct(storeMap.get(1), catMap.get("dirtbike"),
            "Honda CRF450R Motocross AMA Champion",
            "honda-crf450r-motocross-ama-champion-104",
            "Honda",
            new BigDecimal("385000000"),
            new BigDecimal("365000000"),
            5, 16, 5,
            "449.8 cc Unicam 4 Van Làm Mát Kép",
            "Đỏ Trắng HRC Đua",
            "/images/honda_crf450r.jpg",
            "Chiến mã cào cào bay vô địch các giải đua cào cào địa hình danh giá AMA Motocross và Supercross của Mỹ. Khung nhôm đôi thế hệ thứ 7, phuộc trước Showa ti 49mm hành trình nhún 310mm nuốt trọn mọi cú nhảy tiếp đất từ độ cao 5 mét.");

        seedProduct(storeMap.get(3), catMap.get("dirtbike"),
            "Kawasaki KLX300 Dual-Sport Đa Địa Hình",
            "kawasaki-klx300-dual-sport-a-ia-hinh-105",
            "Kawasaki",
            new BigDecimal("159000000"),
            new BigDecimal("149000000"),
            6, 29, 4.88,
            "292 cc DOHC Phun Xăng Điện Tử",
            "Xanh Cốm Lime Green",
            "/images/kawasaki_klx300.jpg",
            "Dòng xe cào cào phố và phượt rừng bền bỉ được anh em biker săn đón nhiều nhất tại Duy Trương Motor. Yên xe êm ái, bảo dưỡng tiết kiệm, máy 300cc êm ái chạy phố hàng ngày nhẹ nhàng và leo đèo dốc off-road cực kỳ tự tin.");

        seedProduct(storeMap.get(1), catMap.get("dirtbike"),
            "Aprilia SX 125 Supermoto Bánh Trơn Chạy Phố",
            "aprilia-sx-125-supermoto-banh-tron-chay-pho-106",
            "Aprilia",
            new BigDecimal("89000000"),
            new BigDecimal("82000000"),
            7, 34, 4.85,
            "124.2 cc 4-Thì 4-Van DOHC Euro 5",
            "Đỏ Đen GP Black",
            "/images/aprilia_sx125.jpg",
            "Chiếc cào cào phố dáng Supermoto bánh trơn cực ngầu đến từ hãng xe đua nước Ý. Hệ thống phuộc up-side-down 41mm cứng cáp, phanh đĩa trước 300mm có ABS chống bó cứng an toàn, dáng ngồi cao ráo tôn dáng người lái.");

        seedProduct(storeMap.get(1), catMap.get("superbike"),
            "Ducati Panigale V4 S 2025",
            "ducati-panigale-v4-s-2025-1",
            "Ducati",
            new BigDecimal("1050000000"),
            new BigDecimal("990000000"),
            4, 22, 5,
            "1.103 cc Desmosedici Stradale V4",
            "Đỏ Ducati Red Truyền Thống",
            "/images/ducati_panigale_v4s.jpg",
            "Tuyệt tác tốc độ đỉnh cao của Ducati với khối động cơ MotoGP, phuộc điện tử Ohlins Smart EC 2.0 điều chỉnh theo thời gian thực, mâm hợp kim nhôm rèn Marchesini siêu nhẹ, cánh gió kép khí động học Biplane Wings.");

        seedProduct(storeMap.get(1), catMap.get("superbike"),
            "Kawasaki Ninja H2 Carbon Supercharged",
            "kawasaki-ninja-h2-carbon-supercharged-2",
            "Kawasaki",
            new BigDecimal("1299000000"),
            new BigDecimal("1249000000"),
            2, 11, 4.98,
            "998 cc Siêu Nạp Supercharged Độc Quyền",
            "Xám Gương Mirror Coated Spark Black",
            "/images/kawasaki_ninja_h2.jpg",
            "Quái vật siêu nạp sản sinh 231 mã lực, dàn áo sợi carbon CFRP cao cấp phủ sơn tự liền sẹo độc quyền của Kawasaki, hệ thống phanh Brembo Stylema đĩa kép 330mm.");

        seedProduct(storeMap.get(2), catMap.get("superbike"),
            "BMW S1000RR M Package 2025 (Cá Mập Lên Cánh)",
            "bmw-s1000rr-m-package-2025-ca-map-len-canh-3",
            "BMW",
            new BigDecimal("999000000"),
            new BigDecimal("949000000"),
            4, 27, 5,
            "999 cc 4-Xi Lanh Thẳng Hàng ShiftCam",
            "Trắng Xanh Đỏ M Motorsport",
            "/images/bmw_s1000rr.jpg",
            "Vua tốc độ 'Cá mập' phiên bản M Performance cao cấp nhất với mâm Carbon M nguyên khối, gắp sau M điều chỉnh độ cao, cánh gió M Winglets khí động học tạo lực ép 17.1kg ở 300 km/h.");

        seedProduct(storeMap.get(2), catMap.get("cruiser"),
            "Harley-Davidson Fat Boy 114 Vivid Black",
            "harley-davidson-fat-boy-114-vivid-black-4",
            "Harley-Davidson",
            new BigDecimal("869000000"),
            new BigDecimal("829000000"),
            5, 19, 4.88,
            "1.868 cc Milwaukee-Eight 114 ci",
            "Đen Bóng Vivid Black",
            "/images/harley_fatboy.jpg",
            "Biểu tượng cơ bắp phong trần Mỹ bất hủ. Mâm đúc nhôm nguyên khối Lakester bản to 240mm lốp sau, tiếng pô thì thầm uy lực rung chuyển lồng ngực mỗi cú vít ga.");

        seedProduct(storeMap.get(1), catMap.get("hyper-naked"),
            "Ducati Streetfighter V4 SP2 Special Edition",
            "ducati-streetfighter-v4-sp2-special-edition-5",
            "Ducati",
            new BigDecimal("1250000000"),
            new BigDecimal("1190000000"),
            2, 13, 4.97,
            "1.103 cc Desmosedici Stradale",
            "Đen Mờ Winter Test Kèm Nhôm Phay",
            "/images/ducati_streetfighter.jpg",
            "Mẫu Hyper Naked mạnh mẽ nhất hành tinh. Động cơ V4 kết hợp bộ nồi khô STM đua MotoGP âm thanh leng keng huyền thoại, mâm carbon 5 chấu siêu nhẹ và cánh gió đôi Biplane Wings.");

        seedProduct(storeMap.get(2), catMap.get("adventure"),
            "BMW R1250GS Adventure Triple Black (Vua Địa Hình)",
            "bmw-r1250gs-adventure-triple-black-vua-ia-hinh-6",
            "BMW",
            new BigDecimal("749000000"),
            new BigDecimal("719000000"),
            5, 25, 4.96,
            "1.254 cc Động Cơ Boxer Đối Xứng ShiftCam",
            "Đen Triple Black Nhám Sang",
            "/images/bmw_r1250gs.jpg",
            "Vua việt dã thám hiểm thế giới. Động cơ Boxer 1.254cc êm ái đầm chắc, phuộc điện tử Dynamic ESA cân bằng tải trọng tự động, hỗ trợ khởi hành ngang dốc Hill Start Control và full bộ 3 thùng nhôm chính hãng.");

        seedProduct(storeMap.get(1), catMap.get("scooter"),
            "Vespa 946 Christian Dior Limited Edition (Hàng Sưu Tầm)",
            "vespa-946-christian-dior-limited-edition-hang-suu-tam-7",
            "Vespa",
            new BigDecimal("790000000"),
            new BigDecimal("730000000"),
            2, 7, 4.99,
            "125 cc 3V i-Get Phun Xăng Điện Tử",
            "Trắng Kem Be Christian Dior",
            "/images/vespa_946.jpg",
            "Tuyệt phẩm thời trang giới hạn do Vespa bắt tay cùng nhà mốt Christian Dior Paris thiết kế. Họa tiết Dior Oblique đặc trưng dưới yên xe, đi kèm túi hành lý và mũ bảo hiểm đồng bộ chính hãng.");

        seedProduct(storeMap.get(1), catMap.get("xenho"),
            "Honda Monkey 125cc Limited Edition 2024",
            "honda-monkey-125cc-limited-edition-2024-301",
            "HONDA",
            new BigDecimal("115000000"),
            new BigDecimal("99000000"),
            6, 51, 4.95,
            "124.9 cc SOHC 4 Cấp Phun Xăng Điện Tử",
            "Vàng Chuối Banana Yellow Khung Bạc",
            "/images/honda_monkey_125.jpg",
            "Mẫu xe khỉ Honda Monkey 125cc biểu tượng phong cách đường phố thế giới, bình xăng xi mạ chrome bóng loáng, phuộc upside down thể thao, bánh béo địa hình cá tính.");

        seedProduct(storeMap.get(1), catMap.get("xenho"),
            "Piaggio Vespa 946 Christian Dior Limited Edition",
            "piaggio-vespa-946-christian-dior-limited-edition-302",
            "PIAGGIO VESPA",
            new BigDecimal("650000000"),
            new BigDecimal("590000000"),
            2, 8, 4.98,
            "155 cc 3-Van i-Get Tiết Kiệm Nhiên Liệu",
            "Trắng Be Christian Dior Độc Bản",
            "/images/vespa_946.jpg",
            "Kiệt tác tay ga thời trang cao cấp hợp tác cùng nhà mốt Christian Dior Paris danh giá, từng đường kim mũi chỉ và họa tiết Dior Oblique huyền thoại trên yên xe thủ công tại Ý.");

        seedProduct(storeMap.get(2), catMap.get("xenho"),
            "Honda Super Cub C125 Disney 100th Anniversary",
            "honda-super-cub-c125-disney-100th-anniversary-303",
            "HONDA",
            new BigDecimal("145000000"),
            new BigDecimal("135000000"),
            4, 26, 4.94,
            "124 cc SOHC PGM-FI Làm Mát Không Khí",
            "Đen Nhám Kèm Biểu Tượng Chuột Mickey",
            "/images/honda_monkey_125.jpg",
            "Phiên bản giới hạn kỷ niệm 100 năm Disney toàn cầu của dòng xe số huyền thoại Super Cub C125, tem kỷ niệm độc quyền đánh số thứ tự, chìa khóa thông minh Smartkey.");

        seedProduct(storeMap.get(1), catMap.get("xenho"),
            "BMW CE 04 Futuristic Electric Super Scooter 2024",
            "bmw-ce-04-futuristic-electric-super-scooter-2024-304",
            "BMW",
            new BigDecimal("549000000"),
            new BigDecimal("519000000"),
            3, 11, 4.9,
            "Electric Motor Permanent-Magnet Làm Mát Nước",
            "Xám Không Gian Magellan Grey Cắt Cam",
            "/images/bmw_ce04.jpg",
            "Siêu phi thuyền tay ga điện tương lai BMW CE 04 với thiết kế phá cách, pin lithium-ion cao áp 8.9 kWh cho tầm hoạt động 130km, màn hình TFT 10.25 inch tích hợp bản đồ dẫn đường.");

        seedProduct(storeMap.get(2), catMap.get("cruiser"),
            "Royal Enfield Continental GT 650 Cafe Racer 2024",
            "royal-enfield-continental-gt-650-cafe-racer-2024-111",
            "ROYAL ENFIELD",
            new BigDecimal("209000000"),
            new BigDecimal("195000000"),
            5, 30, 4.88,
            "648 cc 2 Xylanh Thẳng Hàng SOHC Làm Mát Gió",
            "Bình Xăng Mạ Chrome Mr Clean Soi Gương",
            "/images/royal_enfield_gt650.jpg",
            "Tuyệt tác Cafe Racer cổ điển từ thương hiệu xe Anh Quốc Royal Enfield với bình xăng mạ chrome bóng loáng, ghi-đông clip-on thể thao và tiếng nổ trầm ấm mê hoặc.");

        seedProduct(storeMap.get(1), catMap.get("superbike"),
            "Suzuki Hayabusa GSX-1300R Thần Gió 2024",
            "suzuki-hayabusa-gsx-1300r-than-gio-2024-112",
            "SUZUKI",
            new BigDecimal("799000000"),
            new BigDecimal("760000000"),
            3, 21, 4.96,
            "1.340 cc DOHC 4 Xylanh Thẳng Hàng",
            "Đen Xám Vân Cam Glass Sparkle Black",
            "/images/suzuki_hayabusa.jpg",
            "Huyền thoại Thần Gió Suzuki Hayabusa thế hệ thứ 3 với công nghệ điều khiển điện tử thông minh Suzuki Intelligent Ride System (S.I.R.S.), kiểm soát bốc đầu và sang số nhanh 2 chiều mượt mà.");

        seedProduct(storeMap.get(1), catMap.get("hyper-naked"),
            "MV Agusta Brutale 1000 RR Nurburgring Edition",
            "mv-agusta-brutale-1000-rr-nurburgring-edition-114",
            "MV AGUSTA",
            new BigDecimal("1190000000"),
            new BigDecimal("1140000000"),
            2, 6, 4.97,
            "998 cc 4 Xylanh Titan Đua WorldSBK",
            "Bạc Xám Đỏ Nurburgring Edition Kèm Mâm Carbon",
            "/images/mv_agusta_brutale.jpg",
            "Kiệt tác Hyper Naked siêu hiếm từ hãng xe nghệ thuật Ý MV Agusta, động cơ 4 xylanh van titan thừa hưởng từ xe đua WorldSBK, pô độ Arrow titan full system và mâm carbon siêu nhẹ.");

        // ================= NHÓM TRANG THIẾT BỊ BẢO HỘ CHO BIKER (RIDER GEAR) =================
        seedProduct(storeMap.get(1), catMap.get("do-bao-ho"),
            "Mũ Bảo Hiểm AGV Pista GP RR Carbon Italia Limited Edition",
            "agv-pista-gp-rr-carbon-italia-401",
            "AGV",
            new BigDecimal("39500000"),
            new BigDecimal("36800000"),
            12, 31, 5.0,
            "Vỏ 100% Sợi Carbon 3K Siêu Nhẹ 1.450g",
            "Carbon Italia 3 Màu Cờ Ý",
            "/images/agv_helmet.jpg",
            "Mũ bảo hiểm đường đua đỉnh cao nhất thế giới do AGV sản xuất cho các tay đua MotoGP huyền thoại như Valentino Rossi. Kính quang học Class 1 chống đọng sương Pinlock MaxVision 120, hệ thống thông gió kim loại và cánh gió Pro Spoiler tối ưu khí động học ở tốc độ trên 300 km/h.");

        seedProduct(storeMap.get(1), catMap.get("do-bao-ho"),
            "Mũ Bảo Hiểm Cào Cào LS2 MX703 X-Force Carbon Full Face",
            "ls2-mx703-x-force-carbon-8",
            "LS2",
            new BigDecimal("8900000"),
            new BigDecimal("8200000"),
            15, 58, 4.95,
            "Vỏ Sợi Carbon 100% Siêu Nhẹ 1.300g",
            "Vân Carbon Đỏ Racing",
            "/images/ls2_helmet.jpg",
            "Mũ bảo hiểm cào cào cao cấp bằng sợi carbon nguyên khối siêu nhẹ chuẩn thi đấu quốc tế, lót kháng khuẩn tháo giặt, khe hút gió làm mát tối đa khi bay nhảy trên sa mạc hoặc cung đường bùn đất.");

        seedProduct(storeMap.get(1), catMap.get("do-bao-ho"),
            "Bộ Giáp Da Liền Dainese Mugello 3 D-Air Đua Chuyên Nghiệp",
            "dainese-mugello-3-d-air-402",
            "DAINESE",
            new BigDecimal("105000000"),
            new BigDecimal("96000000"),
            6, 15, 4.98,
            "Da Bò Kangaroo Siêu Bền & Túi Khí Điện Tử D-Air",
            "Đen Trắng Đỏ Fluo Racing",
            "/images/dainese_suit.jpg",
            "Bộ áo giáp da đua cao cấp số 1 thế giới tích hợp công nghệ túi khí điện tử thông minh D-Air tự động bung trong 15 mili-giây khi phát hiện va chạm. Gia công hoàn toàn bằng da Kangaroo mềm nhẹ dẻo dai cùng giáp gối, chỏ titan chống mài mòn tuyệt đối.");

        seedProduct(storeMap.get(2), catMap.get("do-bao-ho"),
            "Găng Tay Đua Alpinestars Supertech GP Carbon Pro",
            "alpinestars-supertech-gp-carbon-403",
            "ALPINESTARS",
            new BigDecimal("10900000"),
            new BigDecimal("9800000"),
            20, 63, 4.96,
            "Da Dê Siêu Bền Kèm Gù Carbon Kép DFS",
            "Đen Trắng Gù Carbon",
            "/images/alpinestars_gloves.jpg",
            "Dòng găng tay đua đẳng cấp thế giới được các tay đua MotoGP Marc Marquez, Jorge Martin tin dùng. Lòng bàn tay bọc da dê tăng cảm giác tay ga, mu bàn tay gia cố gù carbon tản lực DFS và sợi chịu nhiệt Kevlar cao cấp.");

        // ================= NHÓM PHỤ TÙNG & ĐỒ CHƠI XE MÔ TÔ (BIKE PARTS) =================
        seedProduct(storeMap.get(1), catMap.get("gear-parts"),
            "Hệ Thống Pô Akrapovic Full Titanium Racing Cho Môtô & Cào Cào",
            "akrapovic-full-titanium-racing-9",
            "AKRAPOVIC",
            new BigDecimal("62000000"),
            new BigDecimal("56500000"),
            8, 42, 4.99,
            "Hợp Kim Titanium Hàng Không Siêu Bền",
            "Titanium Ánh Bạc Đầu Carbon",
            "/images/akrapovic_exhaust.jpg",
            "Hệ thống ống xả đua danh tiếng số 1 thế giới làm thủ công tại Slovenia. Giúp xe giải nhiệt tức thì, âm thanh uy lực xé gió và được dyno map xăng chuẩn chỉ tại xưởng Duy Trương Motor.");

        seedProduct(storeMap.get(1), catMap.get("gear-parts"),
            "Hệ Thống Pô SC-Project SC1-R Titanium WorldSBK Edition",
            "sc-project-sc1-r-titanium-404",
            "SC PROJECT",
            new BigDecimal("39500000"),
            new BigDecimal("36000000"),
            9, 47, 4.97,
            "Titanium Cao Cấp Cắt CNC Kèm Đuôi Carbon Chịu Nhiệt",
            "Titanium Mờ Lưới Tổ Ong",
            "/images/sc_project_exhaust.jpg",
            "Ống xả hiệu năng cao đỉnh cao từ SC-Project Italy thiết kế chuyên biệt cho các giải đua WorldSBK. Thân pô titan siêu nhẹ kết hợp nắp chụp carbon chống nhiệt độ cao, mang đến dải âm thanh gầm rú xé gió phấn khích tột độ.");

        seedProduct(storeMap.get(3), catMap.get("gear-parts"),
            "Bộ Phuộc Trước Sau Ohlins FGR 250 & TTX GP Thụy Điển Cao Cấp",
            "ohlins-fgr-250-ttx-gp-405",
            "OHLINS",
            new BigDecimal("245000000"),
            new BigDecimal("228000000"),
            3, 9, 5.0,
            "Công Nghệ Giảm Chấn Khí Nén TTX Twin Tube Thụy Điển",
            "Vàng Ohlins Gold Huyền Thoại Ty Mạ DLC",
            "/images/ohlins_suspension.jpg",
            "Đỉnh cao của hệ thống giảm xóc xe đua thế giới. Trang bị công nghệ van tiết lưu TTX độc quyền triệt tiêu hoàn toàn hiện tượng bọt khí dầu phuộc, đem lại độ êm ái, bám đường tuyệt đối khi vào cua tốc độ cao trên đường đua cũng như đường phố.");
    }

    private void initDemoOrders(Map<Integer, Store> storeMap) {
        if (orderRepository.count() > 0) {
            log.info(">>> Đã có {} đơn hàng trong CSDL, bỏ qua seed đơn hàng demo.", orderRepository.count());
            return;
        }

        User customerHung = userRepository.findByEmail("customer@duytruongmoto.com").orElse(null);
        User customerHoang = userRepository.findByEmail("customer@gmail.com").orElse(customerHung);
        User shipperTuan = userRepository.findByEmail("shipper@duytruongmoto.com").orElse(null);
        Delivery delivVip = deliveryRepository.findAll().stream().findFirst().orElse(null);

        Store s1 = storeMap.get(1); // Phu Nhuan HCM
        Store s2 = storeMap.get(2); // Cau Giay HN
        Store s3 = storeMap.get(3); // Hai Chau DN

        // Order 1: Delivered - KTM 300 EXC
        Product p1 = productRepository.findBySlug("ktm-300-exc-tpi-factory-edition-2024-101").orElse(null);
        if (p1 != null && s1 != null && customerHung != null) {
            BigDecimal total1 = p1.getEffectivePrice().add(BigDecimal.valueOf(1500000));
            Order o1 = orderRepository.save(Order.builder()
                .orderCode("DTM-2026-001")
                .user(customerHung).store(s1).delivery(delivVip).shipper(shipperTuan)
                .recipientName("Nguyễn Văn Hùng").phone("0918234567")
                .address("Số 45 Đường Lê Lợi, Phường Bến Nghé, Quận 1, TP. Hồ Chí Minh")
                .paymentMethod("VNPAY").isPaidBefore(true).status(OrderStatus.DELIVERED)
                .amountFromUser(total1)
                .amountToStore(total1.multiply(BigDecimal.valueOf(0.95)))
                .amountToGD(total1.multiply(BigDecimal.valueOf(0.05)))
                .note("Bàn giao xe sàn trượt VIP tận phòng khách, hướng dẫn bảo dưỡng 2 thì TPI.")
                .createdAt(LocalDateTime.now().minusDays(3))
                .updatedAt(LocalDateTime.now().minusDays(2))
                .build());
            orderItemRepository.save(OrderItem.builder().order(o1).product(p1).count(1).price(p1.getEffectivePrice()).selectedStyle("Cam Đen Factory").build());
        }

        // Order 2: Shipping - BMW S1000RR
        Product p2 = productRepository.findBySlug("bmw-s1000rr-m-package-2025-ca-map-len-canh-3").orElse(null);
        if (p2 != null && s2 != null && customerHoang != null) {
            BigDecimal total2 = p2.getEffectivePrice().add(BigDecimal.valueOf(1500000));
            Order o2 = orderRepository.save(Order.builder()
                .orderCode("DTM-2026-002")
                .user(customerHoang).store(s2).delivery(delivVip).shipper(shipperTuan)
                .recipientName("Trần Minh Trí").phone("0983888999")
                .address("Biệt thự Hoa Lan 02, Vinhomes Riverside, Quận Long Biên, Hà Nội")
                .paymentMethod("VNPAY").isPaidBefore(true).status(OrderStatus.SHIPPING)
                .amountFromUser(total2)
                .amountToStore(total2.multiply(BigDecimal.valueOf(0.95)))
                .amountToGD(total2.multiply(BigDecimal.valueOf(0.05)))
                .note("Đang trên xe sàn trượt di chuyển từ showroom Cầu Giấy giao cho khách VIP.")
                .createdAt(LocalDateTime.now().minusHours(18))
                .updatedAt(LocalDateTime.now().minusHours(2))
                .build());
            orderItemRepository.save(OrderItem.builder().order(o2).product(p2).count(1).price(p2.getEffectivePrice()).selectedStyle("Trắng Xanh Đỏ M Motorsport").build());
        }

        // Order 3: Confirmed - Kawasaki KLX300
        Product p3 = productRepository.findBySlug("kawasaki-klx300-dual-sport-a-ia-hinh-105").orElse(null);
        if (p3 != null && s3 != null && customerHung != null) {
            BigDecimal total3 = p3.getEffectivePrice();
            Order o3 = orderRepository.save(Order.builder()
                .orderCode("DTM-2026-003")
                .user(customerHung).store(s3).delivery(delivVip).shipper(shipperTuan)
                .recipientName("Lê Hoàng Nam").phone("0905123456")
                .address("Số 89 Đường Bạch Đằng, Phường Hải Châu 1, Quận Hải Châu, Đà Nẵng")
                .paymentMethod("COD").isPaidBefore(false).status(OrderStatus.CONFIRMED)
                .amountFromUser(total3)
                .amountToStore(total3.multiply(BigDecimal.valueOf(0.95)))
                .amountToGD(total3.multiply(BigDecimal.valueOf(0.05)))
                .note("Khách cọc giữ xe tại showroom, hẹn ngày đẹp lấy biển số HQCN.")
                .createdAt(LocalDateTime.now().minusHours(8))
                .updatedAt(LocalDateTime.now().minusHours(5))
                .build());
            orderItemRepository.save(OrderItem.builder().order(o3).product(p3).count(1).price(p3.getEffectivePrice()).selectedStyle("Xanh Cốm Lime Green").build());
        }

        // Order 4: Delivered - Đồ bảo hộ Dainese & AGV
        Product p4 = productRepository.findBySlug("dainese-mugello-3-d-air-402").orElse(null);
        Product p5 = productRepository.findBySlug("agv-pista-gp-rr-carbon-italia-401").orElse(null);
        if (p4 != null && s1 != null && customerHoang != null) {
            BigDecimal total4 = p4.getEffectivePrice();
            if (p5 != null) total4 = total4.add(p5.getEffectivePrice());
            Order o4 = orderRepository.save(Order.builder()
                .orderCode("DTM-2026-004")
                .user(customerHoang).store(s1).delivery(delivVip).shipper(shipperTuan)
                .recipientName("Vũ Thành Đạt").phone("0938555777")
                .address("Căn hộ Landmark 81, 720A Điện Biên Phủ, Phường 22, Quận Bình Thạnh, TP. Hồ Chí Minh")
                .paymentMethod("VNPAY").isPaidBefore(true).status(OrderStatus.DELIVERED)
                .amountFromUser(total4)
                .amountToStore(total4.multiply(BigDecimal.valueOf(0.95)))
                .amountToGD(total4.multiply(BigDecimal.valueOf(0.05)))
                .note("Giao giáp liền và mũ carbon nguyên seal, đã kiểm tra phụ kiện đầy đủ.")
                .createdAt(LocalDateTime.now().minusDays(5))
                .updatedAt(LocalDateTime.now().minusDays(4))
                .build());
            orderItemRepository.save(OrderItem.builder().order(o4).product(p4).count(1).price(p4.getEffectivePrice()).selectedStyle("Đen Trắng Đỏ Fluo Racing").build());
            if (p5 != null) {
                orderItemRepository.save(OrderItem.builder().order(o4).product(p5).count(1).price(p5.getEffectivePrice()).selectedStyle("Carbon Italia 3 Màu Cờ Ý").build());
            }
        }

        // Order 5: Picked Up - Pô SC Project
        Product p6 = productRepository.findBySlug("sc-project-sc1-r-titanium-404").orElse(null);
        if (p6 != null && s2 != null && customerHung != null) {
            BigDecimal total5 = p6.getEffectivePrice();
            Order o5 = orderRepository.save(Order.builder()
                .orderCode("DTM-2026-005")
                .user(customerHung).store(s2).delivery(delivVip).shipper(shipperTuan)
                .recipientName("Đặng Quốc Tuấn").phone("0912345678")
                .address("Số 12 Phố Tràng Tiền, Quận Hoàn Kiếm, Hà Nội")
                .paymentMethod("VNPAY").isPaidBefore(true).status(OrderStatus.PICKED_UP)
                .amountFromUser(total5)
                .amountToStore(total5.multiply(BigDecimal.valueOf(0.95)))
                .amountToGD(total5.multiply(BigDecimal.valueOf(0.05)))
                .note("Shipper đã nhận hàng từ kho Cầu Giấy, bàn giao trong ngày.")
                .createdAt(LocalDateTime.now().minusHours(4))
                .updatedAt(LocalDateTime.now().minusMinutes(45))
                .build());
            orderItemRepository.save(OrderItem.builder().order(o5).product(p6).count(1).price(p6.getEffectivePrice()).selectedStyle("Titanium Mờ Lưới Tổ Ong").build());
        }

        // Order 6: Not processed - Yamaha WR450F
        Product p7 = productRepository.findBySlug("yamaha-wr450f-rally-champion-2024-108").orElse(null);
        if (p7 != null && s3 != null && customerHoang != null) {
            BigDecimal total6 = p7.getEffectivePrice();
            Order o6 = orderRepository.save(Order.builder()
                .orderCode("DTM-2026-006")
                .user(customerHoang).store(s3).delivery(delivVip).shipper(shipperTuan)
                .recipientName("Phạm Quốc Bảo").phone("0905777888")
                .address("Số 268 Đường Trần Hưng Đạo, Phường An Hải Tây, Quận Sơn Trà, Đà Nẵng")
                .paymentMethod("COD").isPaidBefore(false).status(OrderStatus.NOT_PROCESSED)
                .amountFromUser(total6)
                .amountToStore(total6.multiply(BigDecimal.valueOf(0.95)))
                .amountToGD(total6.multiply(BigDecimal.valueOf(0.05)))
                .note("Đơn mới tạo qua website, chờ quản lý showroom Đà Nẵng duyệt xuất kho.")
                .createdAt(LocalDateTime.now().minusMinutes(25))
                .updatedAt(LocalDateTime.now().minusMinutes(25))
                .build());
            orderItemRepository.save(OrderItem.builder().order(o6).product(p7).count(1).price(p7.getEffectivePrice()).selectedStyle("Xanh Yamaha Racing Blue").build());
        }

        log.info(">>> Đã khởi tạo thành công 6 đơn hàng mẫu phủ khắp 3 Showroom Duy Trương Moto!");
    }

}
