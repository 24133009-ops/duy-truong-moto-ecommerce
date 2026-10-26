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
    @Builder.Default
    private List<String> colors = new ArrayList<>();
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
                .colors(generateColorsForProduct(p))
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

    private static List<String> generateColorsForProduct(Product p) {
        List<String> colors = new ArrayList<>();
        String primaryColor = (p.getColor() != null && !p.getColor().isBlank()) ? p.getColor() : "Nguyên Bản Showroom";
        colors.add(primaryColor);
        
        String brand = p.getBrand() != null ? p.getBrand().toUpperCase() : "";
        String cat = (p.getCategory() != null && p.getCategory().getSlug() != null) ? p.getCategory().getSlug() : "";
        
        if (brand.contains("KTM")) {
            if (!primaryColor.contains("Trắng Cam")) colors.add("Trắng Cam Ready To Race");
            if (!primaryColor.contains("Đen Mờ")) colors.add("Đen Mờ Six Days Phantom");
        } else if (brand.contains("DUCATI")) {
            if (!primaryColor.contains("Đỏ")) colors.add("Đỏ Ducati Red Truyền Thống");
            if (!primaryColor.contains("Dark Stealth")) colors.add("Đen Mờ Dark Stealth");
            if (!primaryColor.contains("Corse")) colors.add("Tem Đấu Corse Livery");
        } else if (brand.contains("VINFAST")) {
            if (!primaryColor.contains("Jet Black")) colors.add("Đen Jet Black Huyền Bí");
            if (!primaryColor.contains("Brahmaputra")) colors.add("Trắng Brahmaputra Tinh Khôi");
            if (!primaryColor.contains("Desat Silver")) colors.add("Bạc Desat Silver Lịch Lãm");
            if (!primaryColor.contains("Crimson")) colors.add("Đỏ Crimson Red Nổi Bật");
        } else if (brand.contains("ISUZU")) {
            if (!primaryColor.contains("Islay Grey")) colors.add("Xám Biển Islay Grey Khỏe Khoắn");
            if (!primaryColor.contains("Dolomite")) colors.add("Trắng Ngọc Trai Dolomite White");
            if (!primaryColor.contains("Onyx")) colors.add("Đen Huyền Bí Onyx Black");
        } else if (brand.contains("BMW")) {
            if (!primaryColor.contains("Motorsport")) colors.add("Trắng Xanh Đỏ M Motorsport");
            if (!primaryColor.contains("Black Storm")) colors.add("Đen Black Storm Metallic");
            if (!primaryColor.contains("San Marino")) colors.add("Xanh San Marino Blue");
        } else if (brand.contains("KAWASAKI")) {
            if (!primaryColor.contains("Lime Green")) colors.add("Xanh Cốm Lime Green Huyền Thoại");
            if (!primaryColor.contains("Spark Black")) colors.add("Đen Xám Metallic Spark Black");
        } else if (brand.contains("PORSCHE")) {
            if (!primaryColor.contains("Shark Blue")) colors.add("Xanh Shark Blue Thể Thao");
            if (!primaryColor.contains("Arctic Grey")) colors.add("Xám Xi Măng Arctic Grey");
            if (!primaryColor.contains("Racing Yellow")) colors.add("Vàng Racing Yellow");
        } else if (brand.contains("LAMBORGHINI")) {
            if (!primaryColor.contains("Arancio Borealis")) colors.add("Cam Arancio Borealis 4 Lớp");
            if (!primaryColor.contains("Giallo Inti")) colors.add("Vàng Giallo Inti");
            if (!primaryColor.contains("Nero Noctis")) colors.add("Đen Nero Noctis Nhám");
        } else if (brand.contains("FERRARI")) {
            if (!primaryColor.contains("Giallo Modena")) colors.add("Vàng Giallo Modena");
            if (!primaryColor.contains("Blu Tour")) colors.add("Xanh Biển Blu Tour de France");
            if (!primaryColor.contains("Nero Daytona")) colors.add("Đen Bóng Nero Daytona");
        } else if ("oto".equalsIgnoreCase(cat)) {
            colors.add("Đen Huyền Bí Ánh Kim");
            colors.add("Trắng Ngọc Trai Cao Cấp");
            colors.add("Xám Xi Măng Thể Thao");
        } else {
            colors.add("Đen Mờ Phantom");
            colors.add("Trắng Sứ Thể Thao");
            colors.add("Xám Xi Măng Racing");
        }
        
        List<String> distinct = new ArrayList<>();
        for (String c : colors) {
            if (!distinct.contains(c)) distinct.add(c);
        }
        return distinct;
    }

}
