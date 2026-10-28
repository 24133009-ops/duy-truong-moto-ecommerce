package com.duytruongmoto.ecommerce.service;

import com.duytruongmoto.ecommerce.entity.Category;
import com.duytruongmoto.ecommerce.entity.Product;
import com.duytruongmoto.ecommerce.entity.Store;
import com.duytruongmoto.ecommerce.repository.CategoryRepository;
import com.duytruongmoto.ecommerce.repository.ProductRepository;
import com.duytruongmoto.ecommerce.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final StoreRepository storeRepository;

    public long countActiveSellingProducts() {
        return productRepository.countByIsActiveTrueAndIsSellingTrue();
    }

    public List<Product> getTopOver10Sold() {
        return productRepository.findTopOver10Sold();
    }

    public List<Product> getTop20Newest() {
        return productRepository.findTop20ByIsActiveTrueAndIsSellingTrueOrderByCreatedAtDesc();
    }

    public List<Product> getTop20BestSellers() {
        return productRepository.findTop20ByIsActiveTrueAndIsSellingTrueOrderBySoldDesc();
    }

    public List<Product> getTop20Rated() {
        return productRepository.findTop20ByIsActiveTrueAndIsSellingTrueOrderByRatingDesc();
    }

    public List<String> getAllDistinctBrands() {
        return productRepository.findDistinctBrands();
    }

    public Page<Product> filterProducts(String keyword, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, String brand, int page, int size) {
        return filterProducts(keyword, categoryId, null, minPrice, maxPrice, brand, page, size);
    }

    public Page<Product> filterProducts(String keyword, Long categoryId, Long storeId, BigDecimal minPrice, BigDecimal maxPrice, String brand, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return productRepository.filterProducts(keyword, categoryId, storeId, minPrice, maxPrice, brand, pageable);
    }

    public Page<Product> getProductsByCategory(Long categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return productRepository.findByCategoryIdAndIsActiveTrueAndIsSellingTrue(categoryId, pageable);
    }

    public List<Product> getProductsByStore(Long storeId) {
        return productRepository.findByStoreId(storeId);
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public Optional<Product> findBySlug(String slug) {
        return productRepository.findBySlug(slug);
    }

    @Transactional
    public Product saveProduct(Long storeId, Long categoryId, String name, String slug, String description,
                               BigDecimal price, BigDecimal promotionalPrice, Integer quantity,
                               String mainImage, String engineCapacity, String color, String brand) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Showroom không tồn tại!"));
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Danh mục không tồn tại!"));

        Product product = Product.builder()
                .store(store)
                .category(category)
                .name(name)
                .slug(slug)
                .description(description)
                .price(price)
                .promotionalPrice(promotionalPrice)
                .quantity(quantity)
                .sold(0)
                .isActive(true)
                .isSelling(true)
                .mainImage(mainImage)
                .engineCapacity(engineCapacity)
                .color(color)
                .brand(brand)
                .rating(5.0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long productId, Long storeId, Long categoryId, String name, String slug,
                                 String description, BigDecimal price, BigDecimal promotionalPrice,
                                 Integer quantity, String mainImage, String engineCapacity, String color, String brand) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại!"));
        if (!product.getStore().getId().equals(storeId)) {
            throw new AccessDeniedException("Bạn không có quyền sửa sản phẩm của showroom khác!");
        }
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Danh mục không tồn tại!"));

        product.setCategory(category);
        product.setName(name);
        product.setSlug(slug);
        product.setDescription(description);
        product.setPrice(price);
        product.setPromotionalPrice(promotionalPrice);
        product.setQuantity(quantity);
        if (mainImage != null && !mainImage.isBlank()) {
            product.setMainImage(mainImage);
        }
        product.setEngineCapacity(engineCapacity);
        product.setColor(color);
        product.setBrand(brand);
        product.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(product);
    }

    @Transactional
    public void toggleSelling(Long productId, Long storeId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại!"));
        if (!product.getStore().getId().equals(storeId)) {
            throw new AccessDeniedException("Bạn không có quyền thay đổi trạng thái sản phẩm của showroom khác!");
        }
        product.setIsSelling(!product.getIsSelling());
        productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long productId, Long storeId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại!"));
        if (!product.getStore().getId().equals(storeId)) {
            throw new AccessDeniedException("Bạn không có quyền xóa sản phẩm của showroom khác!");
        }
        productRepository.delete(product);
    }
}
