package com.duytruongmoto.ecommerce.repository;

import com.duytruongmoto.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    Optional<Product> findBySlug(String slug);
    long countByIsActiveTrueAndIsSellingTrue();

    // Guest: Hiển thị sản phẩm bán trên 10 sản phẩm của các shop (sắp xếp theo thứ tự từ lớn đến nhỏ)
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.isSelling = true AND p.sold > 10 ORDER BY p.sold DESC")
    List<Product> findTopOver10Sold();

    // 20 sản phẩm mới nhất
    List<Product> findTop20ByIsActiveTrueAndIsSellingTrueOrderByCreatedAtDesc();

    // 20 sản phẩm bán chạy nhất
    List<Product> findTop20ByIsActiveTrueAndIsSellingTrueOrderBySoldDesc();

    // 20 sản phẩm đánh giá cao nhất
    List<Product> findTop20ByIsActiveTrueAndIsSellingTrueOrderByRatingDesc();

    // Phân trang theo Category
    List<Product> findByCategoryId(Long categoryId);
    Page<Product> findByCategoryIdAndIsActiveTrueAndIsSellingTrue(Long categoryId, Pageable pageable);

    // Lọc theo store của vendor
    List<Product> findByStoreId(Long storeId);

    // Danh sách tất cả thương hiệu xe hiện có
    @Query("SELECT DISTINCT p.brand FROM Product p WHERE p.isActive = true AND p.isSelling = true AND p.brand IS NOT NULL ORDER BY p.brand ASC")
    List<String> findDistinctBrands();

    // Tìm kiếm và lọc nâng cao thông minh (Hỗ trợ lọc theo Showroom / Store)
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.isSelling = true " +
           "AND (:keyword IS NULL OR :keyword = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
           "AND (:storeId IS NULL OR p.store.id = :storeId) " +
           "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
           "AND (:brand IS NULL OR :brand = '' OR LOWER(p.brand) = LOWER(:brand) OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :brand, '%')) OR LOWER(:brand) LIKE LOWER(CONCAT('%', p.brand, '%')) OR LOWER(p.category.slug) = LOWER(:brand) OR LOWER(p.category.name) LIKE LOWER(CONCAT('%', :brand, '%')))")
    Page<Product> filterProducts(@Param("keyword") String keyword,
                                 @Param("categoryId") Long categoryId,
                                 @Param("storeId") Long storeId,
                                 @Param("minPrice") BigDecimal minPrice,
                                 @Param("maxPrice") BigDecimal maxPrice,
                                 @Param("brand") String brand,
                                 Pageable pageable);
}
