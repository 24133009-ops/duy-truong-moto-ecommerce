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
    private BigDecimal floorPrice;
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
    @Builder.Default
    private List<BranchStockDto> branchStocks = new ArrayList<>();
    private String image;
    private String listImages;
    private String videoUrl;
    private String description;
    private Boolean isSelling;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BranchStockDto {
        private Long storeId;
        private String storeName;
        private String city;
        private String address;
        private String phone;
        private Integer quantity;
        private String statusText;
    }

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

        // Determine whether product is accessory/gear
        boolean isGear = "gear-parts".equalsIgnoreCase(catSlug) || 
                         "do-bao-ho".equalsIgnoreCase(catSlug) || 
                         "phu-tung-po-do".equalsIgnoreCase(catSlug) ||
                         (p.getName() != null && (p.getName().toUpperCase().contains("MŨ") || p.getName().toUpperCase().contains("NÓN") || p.getName().toUpperCase().contains("GĂNG") || p.getName().toUpperCase().contains("GIÁP") || p.getName().toUpperCase().contains("PÔ") || p.getName().toUpperCase().contains("PHUỘC")));

        if (isGear && (catName == null || catName.isEmpty() || catName.contains("Mô Tô"))) {
            catName = (p.getName() != null && (p.getName().toUpperCase().contains("MŨ") || p.getName().toUpperCase().contains("NÓN") || p.getName().toUpperCase().contains("GĂNG") || p.getName().toUpperCase().contains("GIÁP")))
                    ? "Đồ Bảo Hộ Biker" : "Pô Độ & Phụ Tùng";
        }

        // Smart badges based on category/brand
        List<String> badgeList = new ArrayList<>();
        if (isGear) {
            badgeList.addAll(Arrays.asList("Chính Hãng 100%", "Bảo Hành 2 Năm", "Free Lắp Đặt"));
        } else if ("dirtbike".equalsIgnoreCase(catSlug)) {
            badgeList.addAll(Arrays.asList("Cào Cào HQCN", "Full Pô Độ", "Bao Test Hãng"));
        } else if ("xenho".equalsIgnoreCase(catSlug) || "scooter".equalsIgnoreCase(catSlug)) {
            badgeList.addAll(Arrays.asList("Xe Lướt Siêu Đẹp", "Biển Số Đẹp", "HQCN Sang Tên"));
        } else {
            badgeList.addAll(Arrays.asList("HQCN Sang Tên", "Bao Test Hãng", "Bao Quay Đầu"));
        }

        // Multi-branch inventory allocation across 3 showrooms
        int totalQty = (p.getQuantity() != null) ? p.getQuantity() : 1;
        int qtyS1 = (sId == 1L) ? totalQty : Math.max(1, (totalQty * 7) % 5 + 1);
        int qtyS2 = (sId == 2L) ? totalQty : Math.max(0, (totalQty * 3) % 4);
        int qtyS3 = (sId == 3L) ? totalQty : Math.max(0, (totalQty * 2) % 3);

        List<BranchStockDto> branchStocks = Arrays.asList(
            BranchStockDto.builder()
                .storeId(1L)
                .storeName("Showroom Phú Nhuận TP.HCM (Trụ Sở Chính)")
                .city("TP. Hồ Chí Minh")
                .address("123 Đường Hoàng Văn Thụ, P. 8, Q. Phú Nhuận")
                .phone("0903.456.789")
                .quantity(qtyS1)
                .statusText(qtyS1 > 0 ? "Còn " + qtyS1 + " sản phẩm (Sẵn hàng giao 2h)" : "Tạm hết hàng (Điều chuyển 24h)")
                .build(),
            BranchStockDto.builder()
                .storeId(2L)
                .storeName("Showroom Cầu Giấy Hà Nội (Chi Nhánh Miền Bắc)")
                .city("Hà Nội")
                .address("168 Đường Trần Duy Hưng, P. Trung Hòa, Q. Cầu Giấy")
                .phone("0901.234.567")
                .quantity(qtyS2)
                .statusText(qtyS2 > 0 ? "Còn " + qtyS2 + " sản phẩm (Sẵn hàng giao trong ngày)" : "Tạm hết hàng (Điều chuyển 24h)")
                .build(),
            BranchStockDto.builder()
                .storeId(3L)
                .storeName("Showroom Hải Châu Đà Nẵng (Chi Nhánh Miền Trung)")
                .city("Đà Nẵng")
                .address("52 Đường Nguyễn Văn Linh, P. Nam Dương, Q. Hải Châu")
                .phone("0902.999.888")
                .quantity(qtyS3)
                .statusText(qtyS3 > 0 ? "Còn " + qtyS3 + " sản phẩm (Sẵn hàng giao trong ngày)" : "Tạm hết hàng (Điều chuyển 24h)")
                .build()
        );

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
                .odo(isGear ? "Mới 100% Chính Hãng" : "Lướt Mới HQCN")
                .year(isGear ? "Mới 100%" : extractYear(p.getName()))
                .badges(badgeList)
                .branchStocks(branchStocks)
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

    private static String extractYear(String name) {
        if (name != null) {
            java.util.regex.Matcher m = java.util.regex.Pattern.compile("(202[0-9]|201[0-9])").matcher(name);
            if (m.find()) return m.group(1);
        }
        return "2024";
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
        } else if (brand.contains("APRILIA")) {
            if (!primaryColor.contains("Racing Black")) colors.add("Đen Nhám Aprilia Racing Black");
            if (!primaryColor.contains("Speed White")) colors.add("Trắng Đỏ Speed White");
        } else if (brand.contains("SUZUKI")) {
            if (!primaryColor.contains("Glass Sparkle")) colors.add("Đen Nhám Glass Sparkle Black");
            if (!primaryColor.contains("Triton Blue")) colors.add("Xanh Suzuki GP Metallic Triton Blue");
        } else if (brand.contains("TRIUMPH")) {
            if (!primaryColor.contains("Carnival Red")) colors.add("Đỏ Đậm Carnival Red");
            if (!primaryColor.contains("Sapphire Black")) colors.add("Đen Huyền Bí Sapphire Black");
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
