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
    Page<Product> findByCategoryIdAndIsActiveTrueAndIsSellingTrue(Long categoryId, Pageable pageable);

    // Lọc theo store của vendor
    List<Product> findByStoreId(Long storeId);

    // Tìm kiếm và lọc nâng cao
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.isSelling = true " +
           "AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
           "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
           "AND (:brand IS NULL OR p.brand = :brand)")
    Page<Product> filterProducts(@Param("keyword") String keyword,
                                @Param("categoryId") Long categoryId,
                                @Param("minPrice") BigDecimal minPrice,
                                @Param("maxPrice") BigDecimal maxPrice,
                                @Param("brand") String brand,
                                Pageable pageable);
}
