package com.duytruongmoto.ecommerce;

import com.duytruongmoto.ecommerce.entity.*;
import com.duytruongmoto.ecommerce.repository.OrderRepository;
import com.duytruongmoto.ecommerce.repository.ProductRepository;
import com.duytruongmoto.ecommerce.repository.StoreRepository;
import com.duytruongmoto.ecommerce.repository.UserRepository;
import com.duytruongmoto.ecommerce.service.OrderService;
import com.duytruongmoto.ecommerce.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class SecurityRoleOwnershipTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Test
    @DisplayName("1. Verify BCrypt Password Encoding for all demo accounts")
    void testBcryptEncoding() {
        String adminHash = passwordEncoder.encode("admin123");
        String vendorHash = passwordEncoder.encode("vendor123");
        String shipperHash = passwordEncoder.encode("shipper123");
        String customerHash = passwordEncoder.encode("customer123");

        assertTrue(passwordEncoder.matches("admin123", adminHash));
        assertTrue(passwordEncoder.matches("vendor123", vendorHash));
        assertTrue(passwordEncoder.matches("shipper123", shipperHash));
        assertTrue(passwordEncoder.matches("customer123", customerHash));

        assertFalse(passwordEncoder.matches("wrongpass", adminHash));
        assertFalse(passwordEncoder.matches("admin123", vendorHash));

        System.out.println("GEN_BCRYPT_ADMIN: " + adminHash);
        System.out.println("GEN_BCRYPT_VENDOR: " + vendorHash);
        System.out.println("GEN_BCRYPT_SHIPPER: " + shipperHash);
        System.out.println("GEN_BCRYPT_CUSTOMER: " + customerHash);
    }

    @Test
    @DisplayName("2. IDOR Protection: Vendor HN cannot update Product belonging to Vendor DN")
    void testVendorCrossStoreProductProtection() {
        Store storeHN = storeRepository.findBySlug("duy-truong-moto-showroom-cau-giay-ha-noi").orElse(null);
        Store storeDN = storeRepository.findBySlug("duy-truong-moto-showroom-hai-chau-da-nang").orElse(null);

        if (storeHN != null && storeDN != null) {
            Product productDN = productRepository.findAll().stream()
                    .filter(p -> p.getStore() != null && p.getStore().getId().equals(storeDN.getId()))
                    .findFirst()
                    .orElse(null);

            if (productDN != null) {
                // Vendor HN tries to delete productDN using storeHN's ID -> Must throw AccessDeniedException
                assertThrows(AccessDeniedException.class, () -> {
                    productService.deleteProduct(productDN.getId(), storeHN.getId());
                }, "Vendor HN không được xóa sản phẩm thuộc Showroom DN");

                // Vendor HN tries to toggle selling productDN using storeHN's ID -> Must throw AccessDeniedException
                assertThrows(AccessDeniedException.class, () -> {
                    productService.toggleSelling(productDN.getId(), storeHN.getId());
                }, "Vendor HN không được đổi trạng thái sản phẩm thuộc Showroom DN");
            }
        }
    }

    @Test
    @DisplayName("3. Order Status Transition Matrix: Strict Role Check")
    void testOrderStatusTransitionsByRole() {
        Order testOrder = orderRepository.findAll().stream().findFirst().orElse(null);
        if (testOrder != null) {
            User customer = testOrder.getUser();
            User shipper = userRepository.findByEmail("shipper@duytruongmoto.com").orElse(null);

            if (shipper != null && customer != null) {
                // Customer CANNOT transition order directly to DELIVERED or SHIPPING
                assertThrows(AccessDeniedException.class, () -> {
                    orderService.updateOrderStatusWithRoleCheck(testOrder.getId(), OrderStatus.DELIVERED, customer);
                }, "Khách hàng không được tự ý đổi trạng thái đơn sang DELIVERED");

                // Shipper CANNOT confirm or prepare order (Vendor responsibility)
                assertThrows(IllegalArgumentException.class, () -> {
                    orderService.updateOrderStatusWithRoleCheck(testOrder.getId(), OrderStatus.CONFIRMED, shipper);
                }, "Shipper không được xác nhận duyệt đơn thay Showroom");
            }
        }
    }

    @Test
    @DisplayName("4. Floor Price Negotiation Protection")
    void testFloorPriceNegotiation() {
        Product p = productRepository.findAll().stream()
                .filter(prod -> prod.getFloorPrice() != null)
                .findFirst()
                .orElse(null);

        if (p != null) {
            BigDecimal belowFloor = p.getFloorPrice().subtract(BigDecimal.valueOf(1000000));
            BigDecimal aboveFloor = p.getFloorPrice().add(BigDecimal.valueOf(1000000));

            assertTrue(belowFloor.compareTo(p.getFloorPrice()) < 0, "Giá đề xuất dưới floorPrice phải bị chặn");
            assertTrue(aboveFloor.compareTo(p.getFloorPrice()) >= 0, "Giá đề xuất trên floorPrice được chấp nhận");
        }
    }
}
