package com.duytruongmoto.ecommerce.service;

import com.duytruongmoto.ecommerce.entity.Store;
import com.duytruongmoto.ecommerce.entity.Voucher;
import com.duytruongmoto.ecommerce.repository.StoreRepository;
import com.duytruongmoto.ecommerce.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VoucherService {

    private final VoucherRepository voucherRepository;
    private final StoreRepository storeRepository;

    public List<Voucher> getAllActiveVouchers() {
        return voucherRepository.findByIsDeletedFalse();
    }

    public Optional<Voucher> findByCode(String code) {
        if (code == null) return Optional.empty();
        return voucherRepository.findByCodeIgnoreCaseAndIsDeletedFalse(code.trim());
    }

    @Transactional
    public Voucher createVoucher(String code, String name, String description, int discountPercent,
                                 BigDecimal maxDiscountAmount, BigDecimal minOrderAmount,
                                 Integer usageLimit, Long storeId) {
        Store store = (storeId != null) ? storeRepository.findById(storeId).orElse(null) : null;

        Voucher voucher = Voucher.builder()
                .code(code.trim().toUpperCase())
                .name(name)
                .description(description)
                .discountPercent(discountPercent)
                .maxDiscountAmount(maxDiscountAmount)
                .minOrderAmount(minOrderAmount)
                .usageLimit(usageLimit != null ? usageLimit : 100)
                .usedCount(0)
                .store(store)
                .isDeleted(false)
                .build();

        return voucherRepository.save(voucher);
    }

    @Transactional
    public void deleteVoucher(Long id) {
        voucherRepository.findById(id).ifPresent(v -> {
            v.setIsDeleted(true);
            voucherRepository.save(v);
        });
    }

    public BigDecimal calculateDiscount(Voucher voucher, BigDecimal orderTotal) {
        if (voucher == null || !voucher.isValid() || orderTotal == null) {
            return BigDecimal.ZERO;
        }

        if (voucher.getMinOrderAmount() != null && orderTotal.compareTo(voucher.getMinOrderAmount()) < 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount = orderTotal.multiply(BigDecimal.valueOf(voucher.getDiscountPercent())).divide(BigDecimal.valueOf(100));
        if (voucher.getMaxDiscountAmount() != null && discount.compareTo(voucher.getMaxDiscountAmount()) > 0) {
            discount = voucher.getMaxDiscountAmount();
        }

        return discount;
    }
}
