package com.duytruongmoto.ecommerce.service;

import com.duytruongmoto.ecommerce.entity.Commission;
import com.duytruongmoto.ecommerce.entity.Role;
import com.duytruongmoto.ecommerce.entity.Store;
import com.duytruongmoto.ecommerce.entity.User;
import com.duytruongmoto.ecommerce.repository.CommissionRepository;
import com.duytruongmoto.ecommerce.repository.StoreRepository;
import com.duytruongmoto.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final CommissionRepository commissionRepository;

    public Optional<Store> findById(Long id) {
        return storeRepository.findById(id);
    }

    public Optional<Store> findBySlug(String slug) {
        return storeRepository.findBySlug(slug);
    }

    public Optional<Store> findByOwnerId(Long ownerId) {
        if (ownerId == null) return Optional.empty();
        return storeRepository.findFirstByOwnerId(ownerId);
    }

    public List<Store> getAllActiveStores() {
        return storeRepository.findByIsActiveTrue();
    }

    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }

    @Transactional
    public Store getOrCreateVendorStore(User user) {
        if (user != null) {
            Optional<Store> storeOpt = storeRepository.findFirstByOwnerId(user.getId());
            if (storeOpt.isPresent()) {
                return storeOpt.get();
            }
        }
        List<Store> allStores = storeRepository.findAll();
        if (!allStores.isEmpty()) {
            return allStores.get(0);
        }
        Commission comm = commissionRepository.findAll().stream().findFirst().orElse(null);
        User fallbackOwner = (user != null) ? user : userRepository.findByEmail("vendor@duytruongmoto.com").orElse(null);
        return storeRepository.save(Store.builder()
                .name("Duy Truong Motor - Chi nhánh Phú Nhuận TP.HCM (Trụ Sở Chính)")
                .slug("duy-truong-motor-phu-nhuan-" + System.currentTimeMillis())
                .owner(fallbackOwner)
                .bio("Showroom trưng bày siêu môtô PKL & xe cào cào địa hình lớn nhất miền Nam tại 123 Hoàng Văn Thụ")
                .address("Số 123 Đường Hoàng Văn Thụ, Phường 8, Quận Phú Nhuận, TP. HCM")
                .phone("0903.456.789 - 0933.333.382")
                .avatar("https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?auto=format&fit=crop&w=300&q=80")
                .cover("https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=1200&q=80")
                .isActive(true).isOpen(true).commission(comm).point(1200L).rating(4.9)
                .eWallet(BigDecimal.valueOf(120000000)).build());
    }

    @Transactional
    public Store registerStore(Long userId, String name, String slug, String bio, String address, String phone, Long commissionId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));

        Commission commission = null;
        if (commissionId != null) {
            commission = commissionRepository.findById(commissionId).orElse(null);
        }

        Store store = Store.builder()
                .name(name)
                .slug(slug)
                .owner(owner)
                .bio(bio)
                .address(address)
                .phone(phone)
                .avatar("https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?auto=format&fit=crop&w=300&q=80")
                .cover("https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=1200&q=80")
                .isActive(true)
                .isOpen(true)
                .commission(commission)
                .point(0L)
                .rating(5.0)
                .eWallet(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Nâng quyền User lên Vendor nếu chưa có
        if (owner.getRole() == Role.ROLE_USER) {
            owner.setRole(Role.ROLE_VENDOR);
            userRepository.save(owner);
        }

        return storeRepository.save(store);
    }

    @Transactional
    public void toggleStoreStatus(Long storeId, boolean isActive) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Showroom không tồn tại"));
        store.setIsActive(isActive);
        store.setUpdatedAt(LocalDateTime.now());
        storeRepository.save(store);
    }
}
