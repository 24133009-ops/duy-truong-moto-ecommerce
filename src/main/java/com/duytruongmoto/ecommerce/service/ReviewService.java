package com.duytruongmoto.ecommerce.service;

import com.duytruongmoto.ecommerce.entity.Order;
import com.duytruongmoto.ecommerce.entity.OrderStatus;
import com.duytruongmoto.ecommerce.entity.Product;
import com.duytruongmoto.ecommerce.entity.Review;
import com.duytruongmoto.ecommerce.entity.User;
import com.duytruongmoto.ecommerce.repository.OrderRepository;
import com.duytruongmoto.ecommerce.repository.ProductRepository;
import com.duytruongmoto.ecommerce.repository.ReviewRepository;
import com.duytruongmoto.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public List<Review> getReviewsByProduct(Long productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }

    @Transactional
    public Review addReview(Long userId, Long productId, Long orderId, int stars, String content, String mediaUrls) {
        if (content == null || content.trim().length() < 50) {
            throw new IllegalArgumentException("Nội dung đánh giá xe phải có tối thiểu 50 ký tự để cung cấp đánh giá chất lượng!");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại"));

        Order order = null;
        if (orderId != null) {
            order = orderRepository.findById(orderId).orElse(null);
            if (order != null && order.getStatus() != OrderStatus.DELIVERED) {
                throw new IllegalStateException("Bạn chỉ có thể đánh giá sản phẩm sau khi đơn hàng đã được giao thành công!");
            }
        }

        Review review = Review.builder()
                .user(user)
                .product(product)
                .order(order)
                .stars(stars)
                .content(content.trim())
                .mediaUrls(mediaUrls)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Review saved = reviewRepository.save(review);

        // Cập nhật rating trung bình cho sản phẩm
        List<Review> allReviews = reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
        double avg = allReviews.stream().mapToInt(Review::getStars).average().orElse(5.0);
        product.setRating(Math.round(avg * 10.0) / 10.0);
        productRepository.save(product);

        return saved;
    }
}
