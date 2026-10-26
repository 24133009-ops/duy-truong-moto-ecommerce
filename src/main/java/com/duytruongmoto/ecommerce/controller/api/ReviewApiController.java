package com.duytruongmoto.ecommerce.controller.api;

import com.duytruongmoto.ecommerce.dto.ReviewDto;
import com.duytruongmoto.ecommerce.entity.Product;
import com.duytruongmoto.ecommerce.entity.Review;
import com.duytruongmoto.ecommerce.entity.User;
import com.duytruongmoto.ecommerce.repository.ProductRepository;
import com.duytruongmoto.ecommerce.repository.ReviewRepository;
import com.duytruongmoto.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@Slf4j
public class ReviewApiController {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewDto>> getReviewsForProduct(@PathVariable Long productId) {
        List<ReviewDto> dtos = reviewRepository.findByProductIdOrderByCreatedAtDesc(productId).stream()
                .map(ReviewDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<?> addReview(@RequestBody Map<String, Object> req) {
        try {
            Long productId = Long.parseLong(req.get("productId").toString());
            String content = (String) req.get("content");
            int stars = req.containsKey("stars") ? Integer.parseInt(req.get("stars").toString()) : 5;
            String media = (String) req.getOrDefault("media", "");

            if (content == null || content.trim().length() < 50) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Nội dung đánh giá xe phải có tối thiểu 50 ký tự để chia sẻ trải nghiệm thực tế!"
                ));
            }

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm xe này!"));

            User user = userRepository.findByEmail("customer@duytruongmoto.com")
                    .orElseGet(() -> userRepository.findAll().stream().findFirst().orElse(null));

            Review review = Review.builder()
                    .product(product)
                    .user(user)
                    .stars(stars)
                    .content(content)
                    .mediaUrls(media)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            Review saved = reviewRepository.save(review);
            log.info(">>> [API] Review added for product {}: {} stars", productId, stars);
            return ResponseEntity.status(HttpStatus.CREATED).body(ReviewDto.fromEntity(saved));
        } catch (Exception e) {
            log.error("Error adding review: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
}
