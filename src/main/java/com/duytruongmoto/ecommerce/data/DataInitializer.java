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
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0 || userLevelRepository.count() > 0) {
            log.info("Dữ liệu hệ thống Duy Truong Motor đã tồn tại. Bỏ qua DataInitializer.");
            return;
        }

        log.info(">>> Khởi tạo dữ liệu mẫu cho Chuỗi Showroom Xe Luxury Duy Truong Motor...");

        // 1. User Levels
        UserLevel silver = userLevelRepository.save(UserLevel.builder().name("Thành viên Bạc").minPoint(0L).discount(BigDecimal.ZERO).isDeleted(false).build());
        UserLevel gold = userLevelRepository.save(UserLevel.builder().name("Khách hàng Vàng").minPoint(1000L).discount(BigDecimal.valueOf(3.0)).isDeleted(false).build());
        UserLevel diamond = userLevelRepository.save(UserLevel.builder().name("Khách hàng Kim Cương VIP").minPoint(5000L).discount(BigDecimal.valueOf(7.0)).isDeleted(false).build());

        // 2. Commissions
        Commission commStandard = commissionRepository.save(Commission.builder().name("Chiết khấu Tiêu chuẩn").cost(BigDecimal.valueOf(5.0)).description("Chiết khấu 5% cho showroom đối tác").isDeleted(false).build());
        Commission commVip = commissionRepository.save(Commission.builder().name("Chiết khấu Đại lý VIP").cost(BigDecimal.valueOf(3.0)).description("Chiết khấu ưu đãi 3%").isDeleted(false).build());

        // 3. Deliveries
        Delivery delivFlatbed = deliveryRepository.save(Delivery.builder().name("Giao xe sàn trượt thủy lực VIP (Chống trầy xước 100%)").description("Xe chuyên dụng sàn trượt hạ đất, bàn giao tận phòng khách khách hàng").price(BigDecimal.valueOf(1500000)).isDeleted(false).build());
        Delivery delivVan = deliveryRepository.save(Delivery.builder().name("Xe tải thùng kín chuyên dụng chuyển môtô").description("Xe tải thùng bạt chuyên chở xe PKL đi tỉnh").price(BigDecimal.valueOf(800000)).isDeleted(false).build());
        Delivery delivStandard = deliveryRepository.save(Delivery.builder().name("Giao hàng tiêu chuẩn (Phụ tùng, đồ bảo hộ)").description("Giao nhanh trong 2-3 ngày").price(BigDecimal.valueOf(50000)).isDeleted(false).build());

        // 4. Users
        User admin = userRepository.save(User.builder()
                .firstname("Duy")
                .lastname("Trường")
                .slug("duy-truong-admin")
                .email("admin@duytruongmoto.com")
                .phone("0901234567")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ROLE_ADMIN)
                .isEmailActive(true)
                .isPhoneActive(true)
                .avatar("https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=200&q=80")
                .point(9999L)
                .eWallet(BigDecimal.valueOf(500000000))
                .userLevel(diamond)
                .build());

        User manager = userRepository.save(User.builder()
                .firstname("Quản lý")
                .lastname("Chi nhánh")
                .slug("manager-duytruong")
                .email("manager@duytruongmoto.com")
                .phone("0902345678")
                .password(passwordEncoder.encode("manager123"))
                .role(Role.ROLE_MANAGER)
                .isEmailActive(true)
                .isPhoneActive(true)
                .point(500L)
                .userLevel(gold)
                .build());

        User vendor = userRepository.save(User.builder()
                .firstname("Minh")
                .lastname("Showroom Q1")
                .slug("minh-showroom-q1")
                .email("vendor@duytruongmoto.com")
                .phone("0903456789")
                .password(passwordEncoder.encode("vendor123"))
                .role(Role.ROLE_VENDOR)
                .isEmailActive(true)
                .isPhoneActive(true)
                .point(2000L)
                .eWallet(BigDecimal.valueOf(150000000))
                .userLevel(gold)
                .build());

        User shipper = userRepository.save(User.builder()
                .firstname("Tuấn")
                .lastname("Vận Chuyển")
                .slug("tuan-shipper")
                .email("shipper@duytruongmoto.com")
                .phone("0904567890")
                .password(passwordEncoder.encode("shipper123"))
                .role(Role.ROLE_SHIPPER)
                .isEmailActive(true)
                .isPhoneActive(true)
                .point(100L)
                .build());

        User customer = userRepository.save(User.builder()
                .firstname("Hoàng")
                .lastname("Biker")
                .slug("hoang-biker")
                .email("customer@gmail.com")
                .phone("0905678901")
                .password(passwordEncoder.encode("user123"))
                .role(Role.ROLE_USER)
                .isEmailActive(true)
                .isPhoneActive(true)
                .point(350L)
                .eWallet(BigDecimal.valueOf(50000000))
                .userLevel(silver)
                .build());

        // 5. Stores (Showroom Chuỗi Duy Truong Motor)
        Store showroomQ1 = storeRepository.save(Store.builder()
                .name("Duy Truong Motor - Flagship Showroom Quận 1")
                .slug("duy-truong-moto-showroom-quan-1")
                .owner(vendor)
                .bio("Showroom trưng bày các dòng siêu môtô đỉnh cao hàng đầu Việt Nam: Ducati, BMW, Kawasaki...")
                .address("Số 88 Đường Nguyễn Hữu Cảnh, Phường 22, Quận 1, TP. Hồ Chí Minh")
                .phone("0903456789")
                .avatar("https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?auto=format&fit=crop&w=300&q=80")
                .cover("https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=1200&q=80")
                .isActive(true)
                .isOpen(true)
                .commission(commStandard)
                .point(1200L)
                .rating(4.9)
                .eWallet(BigDecimal.valueOf(120000000))
                .build());

        Store showroomHN = storeRepository.save(Store.builder()
                .name("Duy Truong Motor - Showroom Cầu Giấy Hà Nội")
                .slug("duy-truong-moto-showroom-cau-giay")
                .owner(admin)
                .bio("Chi nhánh miền Bắc chính hãng của hệ thống chuỗi Duy Truong Motor.")
                .address("Số 168 Đường Trần Duy Hưng, Cầu Giấy, Hà Nội")
                .phone("0901234567")
                .avatar("https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=300&q=80")
                .cover("https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?auto=format&fit=crop&w=1200&q=80")
                .isActive(true)
                .isOpen(true)
                .commission(commVip)
                .point(980L)
                .rating(5.0)
                .eWallet(BigDecimal.valueOf(85000000))
                .build());

        // 6. Categories
        Category catSuperbike = categoryRepository.save(Category.builder().name("Superbike / Sportbike").slug("superbike").image("https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?auto=format&fit=crop&w=500&q=80").isDeleted(false).build());
        Category catNaked = categoryRepository.save(Category.builder().name("Hyper Naked").slug("hyper-naked").image("https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=500&q=80").isDeleted(false).build());
        Category catCruiser = categoryRepository.save(Category.builder().name("Cruiser & Touring").slug("cruiser-touring").image("https://images.unsplash.com/photo-1558981285-6f0c94958bb6?auto=format&fit=crop&w=500&q=80").isDeleted(false).build());
        Category catExhaust = categoryRepository.save(Category.builder().name("Pô Độ & Phụ Tùng Cao Cấp").slug("phu-tung-po-do").image("https://images.unsplash.com/photo-1558981408-db0ecd8a1ee4?auto=format&fit=crop&w=500&q=80").isDeleted(false).build());
        Category catGear = categoryRepository.save(Category.builder().name("Đồ Bảo Hộ & Nón AGV").slug("do-bao-ho").image("https://images.unsplash.com/photo-1558980664-769d59546b3d?auto=format&fit=crop&w=500&q=80").isDeleted(false).build());

        // 7. Products (Xe Luxury bán chạy > 10 xe đáp ứng yêu cầu Guest)
        productRepository.save(Product.builder()
                .store(showroomQ1)
                .category(catSuperbike)
                .name("Ducati Panigale V4 S 2024")
                .slug("ducati-panigale-v4-s-2024")
                .description("Siêu phẩm Superbike đỉnh cao trang bị động cơ Desmosedici Stradale V4 1.103cc, công suất 215,5 mã lực. Phuộc Ohlins Smart EC 2.0 điện tử, mâm nhôm rèn Marchesini.")
                .price(BigDecimal.valueOf(950000000))
                .promotionalPrice(BigDecimal.valueOf(920000000))
                .quantity(5)
                .sold(18) // > 10 cho Guest
                .isActive(true)
                .isSelling(true)
                .mainImage("/images/ducati_panigale_v4s.jpg")
                .engineCapacity("1103 cc")
                .color("Đỏ Rosso Corsa")
                .brand("Ducati")
                .rating(5.0)
                .build());

        productRepository.save(Product.builder()
                .store(showroomQ1)
                .category(catSuperbike)
                .name("Kawasaki Ninja H2 Carbon")
                .slug("kawasaki-ninja-h2-carbon")
                .description("Tuyệt tác siêu nạp Supercharged 998cc độc quyền Kawasaki, công suất cực đại 231 mã lực, dàn áo carbon siêu nhẹ.")
                .price(BigDecimal.valueOf(1350000000))
                .promotionalPrice(BigDecimal.valueOf(1290000000))
                .quantity(3)
                .sold(14) // > 10 cho Guest
                .isActive(true)
                .isSelling(true)
                .mainImage("/images/kawasaki_ninja_h2.jpg")
                .engineCapacity("998 cc")
                .color("Đen Nhám Carbon")
                .brand("Kawasaki")
                .rating(4.9)
                .build());

        productRepository.save(Product.builder()
                .store(showroomHN)
                .category(catSuperbike)
                .name("BMW S1000RR M Package 2024")
                .slug("bmw-s1000rr-m-package")
                .description("Vua tốc độ 'Cá mập' BMW S1000RR gói M cao cấp: Mâm Carbon, gắp M, ống xả Akrapovic titanium, công suất 210 mã lực ShiftCam.")
                .price(BigDecimal.valueOf(1099000000))
                .promotionalPrice(BigDecimal.valueOf(1050000000))
                .quantity(4)
                .sold(19) // > 10 cho Guest
                .isActive(true)
                .isSelling(true)
                .mainImage("/images/bmw_s1000rr.jpg")
                .engineCapacity("999 cc")
                .color("Trắng Đỏ Xanh M Motorsport")
                .brand("BMW")
                .rating(5.0)
                .build());

        productRepository.save(Product.builder()
                .store(showroomHN)
                .category(catCruiser)
                .name("Harley-Davidson Fat Boy 114")
                .slug("harley-davidson-fat-boy-114")
                .description("Biểu tượng cơ bắp Mỹ với khối động cơ Milwaukee-Eight 114 V-Twin 1.868cc, mâm đúc nhôm nguyên khối Lakester.")
                .price(BigDecimal.valueOf(899000000))
                .promotionalPrice(BigDecimal.valueOf(870000000))
                .quantity(6)
                .sold(12) // > 10 cho Guest
                .isActive(true)
                .isSelling(true)
                .mainImage("/images/harley_fatboy.jpg")
                .engineCapacity("1868 cc")
                .color("Vàng Đồng Vivid Black")
                .brand("Harley-Davidson")
                .rating(4.8)
                .build());

        productRepository.save(Product.builder()
                .store(showroomQ1)
                .category(catNaked)
                .name("Ducati Streetfighter V4 S")
                .slug("ducati-streetfighter-v4-s")
                .description("Mẫu Hyper Naked mạnh mẽ nhất hành tinh, cánh gió khí động học Biplane Wings, ghi-đông cao thể thao, 208 mã lực.")
                .price(BigDecimal.valueOf(890000000))
                .promotionalPrice(BigDecimal.valueOf(860000000))
                .quantity(7)
                .sold(15) // > 10 cho Guest
                .isActive(true)
                .isSelling(true)
                .mainImage("/images/ducati_streetfighter.jpg")
                .engineCapacity("1103 cc")
                .color("Xám Nhám Dark Stealth")
                .brand("Ducati")
                .rating(4.9)
                .build());

        productRepository.save(Product.builder()
                .store(showroomQ1)
                .category(catExhaust)
                .name("Pô Akrapovic Full System Titanium Panigale V4")
                .slug("po-akrapovic-titanium-panigale-v4")
                .description("Hệ thống ống xả Akrapovic chính hãng full titan, tăng 12 mã lực, giảm 6kg trọng lượng xe, âm thanh nổ uy lực.")
                .price(BigDecimal.valueOf(145000000))
                .promotionalPrice(BigDecimal.valueOf(138000000))
                .quantity(15)
                .sold(25) // > 10 cho Guest
                .isActive(true)
                .isSelling(true)
                .mainImage("/images/akrapovic_exhaust.jpg")
                .color("Titanium Mờ")
                .brand("Akrapovic")
                .rating(5.0)
                .build());

        productRepository.save(Product.builder()
                .store(showroomHN)
                .category(catGear)
                .name("Mũ Bảo Hiểm Cào Cào LS2 MX703 X-Force Carbon")
                .slug("mu-bao-hiem-ls2-mx703-carbon")
                .description("Nón bảo hiểm cào cào 100% sợi carbon siêu nhẹ ECE 22.06, khe hút gió làm mát tối đa.")
                .price(BigDecimal.valueOf(12500000))
                .promotionalPrice(BigDecimal.valueOf(11200000))
                .quantity(20)
                .sold(42) // > 10 cho Guest
                .isActive(true)
                .isSelling(true)
                .mainImage("/images/ls2_helmet.jpg")
                .color("Carbon Bóng")
                .brand("LS2")
                .rating(5.0)
                .build());

        log.info(">>> Khởi tạo dữ liệu mẫu thành công cho Hệ thống Duy Truong Motor!");
    }
}
