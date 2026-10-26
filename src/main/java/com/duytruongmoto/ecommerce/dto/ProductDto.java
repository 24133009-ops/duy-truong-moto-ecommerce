package com.duytruongmoto.ecommerce.dto;

import com.duytruongmoto.ecommerce.entity.Product;
import lombok.*;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    private Long id;
    private String name;
    private String slug;
    private String brand;
    private String category;
    private String categoryName;
    private Long storeId;
    private String storeName;
    private BigDecimal price;
    private BigDecimal promotionalPrice;
    private String priceDisplay;
    private Integer quantity;
    private Integer sold;
    private Double rating;
    private String engine;
    private String power;
    private String color;
    private String odo;
    private String year;
    @Builder.Default
    private List<String> badges = new ArrayList<>();
    private String image;
    private String listImages;
    private String videoUrl;
    private String description;
    private Boolean isSelling;

    public static ProductDto fromEntity(Product p) {
        if (p == null) return null;

        String catSlug = (p.getCategory() != null && p.getCategory().getSlug() != null) 
                ? p.getCategory().getSlug() : "dirtbike";
        String catName = (p.getCategory() != null && p.getCategory().getName() != null) 
                ? p.getCategory().getName() : "Mô Tô Phân Khối Lớn";

        Long sId = (p.getStore() != null && p.getStore().getId() != null) ? p.getStore().getId() : 1L;
        String sName = (p.getStore() != null && p.getStore().getName() != null) 
                ? p.getStore().getName() : "Duy Trương Motor - Chi nhánh Phú Nhuận TP.HCM";

        // Format price display
        BigDecimal eff = p.getEffectivePrice();
        String display = formatPriceDisplay(eff);

        // Smart badges based on category/brand
        List<String> badgeList = new ArrayList<>();
        if ("oto".equalsIgnoreCase(catSlug)) {
            badgeList.addAll(Arrays.asList("Nhập Khẩu Nguyên Chiếc", "Full Lịch Sử Hãng", "Bao Test Hãng"));
        } else if ("gear-parts".equalsIgnoreCase(catSlug) || "do-bao-ho".equalsIgnoreCase(catSlug) || "phu-tung-po-do".equalsIgnoreCase(catSlug)) {
            badgeList.addAll(Arrays.asList("Chính Hãng 100%", "Bảo Hành 2 Năm", "Free Lắp Đặt"));
        } else if ("xenho".equalsIgnoreCase(catSlug) || "scooter".equalsIgnoreCase(catSlug)) {
            badgeList.addAll(Arrays.asList("Xe Lướt Siêu Đẹp", "Biển Số Đẹp", "HQCN Sang Tên"));
        } else {
            badgeList.addAll(Arrays.asList("HQCN Sang Tên", "Bao Test Hãng", "Bao Quay Đầu"));
        }

        return ProductDto.builder()
                .id(p.getId())
                .name(p.getName())
                .slug(p.getSlug())
                .brand(p.getBrand() != null ? p.getBrand() : "Duy Trương Motor")
                .category(catSlug)
                .categoryName(catName)
                .storeId(sId)
                .storeName(sName)
                .price(p.getPrice())
                .promotionalPrice(p.getPromotionalPrice())
                .priceDisplay(display)
                .quantity(p.getQuantity() != null ? p.getQuantity() : 1)
                .sold(p.getSold() != null ? p.getSold() : 0)
                .rating(p.getRating() != null ? p.getRating() : 5.0)
                .engine(p.getEngineCapacity() != null ? p.getEngineCapacity() : "Tiêu Chuẩn Showroom")
                .power(extractPower(p.getDescription()))
                .color(p.getColor() != null ? p.getColor() : "Nguyên Bản")
                .odo("Lướt Mới HQCN")
                .year("2024")
                .badges(badgeList)
                .image(p.getMainImage() != null ? p.getMainImage() : "/images/ktm_300_exc.jpg")
                .listImages(p.getListImages())
                .videoUrl(p.getVideoUrl())
                .description(p.getDescription() != null ? p.getDescription() : "")
                .isSelling(p.getIsSelling() != null ? p.getIsSelling() : true)
                .build();
    }

    private static String formatPriceDisplay(BigDecimal amount) {
        if (amount == null) return "Liên hệ";
        long val = amount.longValue();
        if (val >= 1_000_000_000L) {
            double ty = (double) val / 1_000_000_000.0;
            return new DecimalFormat("#,##0.#").format(ty) + " Tỷ";
        } else if (val >= 1_000_000L) {
            double tr = (double) val / 1_000_000.0;
            return new DecimalFormat("#,##0.#").format(tr) + " Triệu";
        }
        return new DecimalFormat("#,###").format(val) + " đ";
    }

    private static String extractPower(String desc) {
        if (desc == null) return "Công suất đỉnh cao";
        if (desc.contains("HP")) {
            int idx = desc.indexOf("HP");
            int start = Math.max(0, idx - 15);
            return desc.substring(start, Math.min(desc.length(), idx + 2)).trim();
        }
        return "Hiệu năng thể thao";
    }
}
