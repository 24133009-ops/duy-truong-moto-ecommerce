package com.duytruongmoto.ecommerce.controller.api;

import com.duytruongmoto.ecommerce.dto.ProductDto;
import com.duytruongmoto.ecommerce.entity.Category;
import com.duytruongmoto.ecommerce.entity.Product;
import com.duytruongmoto.ecommerce.entity.Store;
import com.duytruongmoto.ecommerce.repository.CategoryRepository;
import com.duytruongmoto.ecommerce.repository.ProductRepository;
import com.duytruongmoto.ecommerce.repository.StoreRepository;
import com.duytruongmoto.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@Slf4j
public class ProductApiController {

    private final ProductRepository productRepository;
    private final ProductService productService;
    private final CategoryRepository categoryRepository;
    private final StoreRepository storeRepository;

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Long storeId,
            @RequestParam(defaultValue = "100") int limit) {

        // Resolve category slug to categoryId if provided
        if (categoryId == null && category != null && !category.trim().isEmpty() && !"all".equalsIgnoreCase(category)) {
            categoryId = categoryRepository.findBySlug(category.trim())
                    .map(Category::getId)
                    .orElse(null);
        }

        List<Product> products;
        if (keyword != null || categoryId != null || brand != null || minPrice != null || maxPrice != null) {
            String cleanBrand = ("all".equalsIgnoreCase(brand) || brand == null || brand.trim().isEmpty()) ? null : brand.trim();
            Page<Product> p = productService.filterProducts(keyword, categoryId, minPrice, maxPrice, cleanBrand, 0, limit);
            products = p.getContent();
        } else if (storeId != null) {
            products = productService.getProductsByStore(storeId);
        } else {
            products = productRepository.findAll();
        }

        List<ProductDto> dtos = products.stream()
                .map(ProductDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(p -> ResponseEntity.ok(ProductDto.fromEntity(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ProductDto> getProductBySlug(@PathVariable String slug) {
        return productRepository.findBySlug(slug)
                .map(p -> ResponseEntity.ok(ProductDto.fromEntity(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/top-sold")
    public ResponseEntity<List<ProductDto>> getTopSoldProducts() {
        List<ProductDto> dtos = productService.getTopOver10Sold().stream()
                .map(ProductDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/top-rated")
    public ResponseEntity<List<ProductDto>> getTopRatedProducts() {
        List<ProductDto> dtos = productService.getTop20Rated().stream()
                .map(ProductDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/brands")
    public ResponseEntity<List<String>> getAllBrands() {
        return ResponseEntity.ok(productService.getAllDistinctBrands());
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Map<String, Object> body) {
        try {
            String name = (String) body.get("name");
            String brand = (String) body.getOrDefault("brand", "KTM");
            String catSlug = (String) body.getOrDefault("category", "dirtbike");
            String engine = (String) body.getOrDefault("engine", "Phân Khối Lớn");
            String color = (String) body.getOrDefault("color", "Cam Đen");
            String image = (String) body.getOrDefault("image", "/images/ktm_300_exc.jpg");
            String desc = (String) body.getOrDefault("description", "Chiến mã PKL Duy Trương Motor");

            BigDecimal price = new BigDecimal(body.getOrDefault("price", "300000000").toString());
            BigDecimal promoPrice = body.containsKey("promotionalPrice") 
                    ? new BigDecimal(body.get("promotionalPrice").toString()) : price;

            Category category = categoryRepository.findBySlug(catSlug)
                    .orElseGet(() -> categoryRepository.findAll().stream().findFirst().orElse(null));

            Store store = storeRepository.findById(1L)
                    .orElseGet(() -> storeRepository.findAll().stream().findFirst().orElse(null));

            String baseSlug = name.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-+|-+$", "");
            String slug = baseSlug + "-" + System.currentTimeMillis();

            Product product = Product.builder()
                    .name(name)
                    .slug(slug)
                    .brand(brand)
                    .category(category)
                    .store(store)
                    .price(price)
                    .promotionalPrice(promoPrice)
                    .quantity(4)
                    .sold(0)
                    .rating(5.0)
                    .engineCapacity(engine)
                    .color(color)
                    .mainImage(image)
                    .description(desc)
                    .isActive(true)
                    .isSelling(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            Product saved = productRepository.save(product);
            log.info(">>> [API] Created new product: {} (ID: {})", saved.getName(), saved.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(ProductDto.fromEntity(saved));
        } catch (Exception e) {
            log.error("Error creating product via API: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }


    @PostMapping("/{id}/negotiate")
    public ResponseEntity<?> negotiatePrice(
            @PathVariable Long id,
            @RequestBody Map<String, Object> req) {

        Optional<Product> prodOpt = productRepository.findById(id);
        if (prodOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Product product = prodOpt.get();
        if (!req.containsKey("proposedPrice")) {
            return ResponseEntity.badRequest().body(Map.of("accepted", false, "message", "Vui lòng nhập mức giá đề xuất bớt lộc!"));
        }

        BigDecimal proposedPrice = new BigDecimal(req.get("proposedPrice").toString());
        BigDecimal floorPrice = product.getFloorPrice() != null ? product.getFloorPrice() : product.getPrice().multiply(BigDecimal.valueOf(0.90));

        if (proposedPrice.compareTo(floorPrice) < 0) {
            return ResponseEntity.ok(Map.of(
                    "accepted", false,
                    "floorPrice", floorPrice,
                    "message", "Mức giá đề xuất thấp hơn giá sàn tối thiểu của Showroom. Showroom chưa thể chốt giá này!"
            ));
        }

        return ResponseEntity.ok(Map.of(
                "accepted", true,
                "negotiatedPrice", proposedPrice,
                "message", "Showroom chấp thuận mức giá bớt lộc! Quý khách có thể tiến hành đặt cọc ngay."
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        if (!productRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        productRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Sản phẩm đã được xóa thành công"));
    }
}
