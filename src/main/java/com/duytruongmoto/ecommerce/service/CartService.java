package com.duytruongmoto.ecommerce.service;

import com.duytruongmoto.ecommerce.entity.Cart;
import com.duytruongmoto.ecommerce.entity.CartItem;
import com.duytruongmoto.ecommerce.entity.Product;
import com.duytruongmoto.ecommerce.entity.User;
import com.duytruongmoto.ecommerce.repository.CartItemRepository;
import com.duytruongmoto.ecommerce.repository.CartRepository;
import com.duytruongmoto.ecommerce.repository.ProductRepository;
import com.duytruongmoto.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));
            Cart newCart = Cart.builder()
                    .user(user)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            return cartRepository.save(newCart);
        });
    }

    @Transactional
    public CartItem addToCart(Long userId, Long productId, int quantity) {
        Cart cart = getOrCreateCart(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại"));

        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setCount(item.getCount() + quantity);
            item.setPrice(product.getEffectivePrice());
            item.setUpdatedAt(LocalDateTime.now());
            return cartItemRepository.save(item);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .count(quantity)
                    .price(product.getEffectivePrice())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            return cartItemRepository.save(newItem);
        }
    }

    @Transactional
    public void updateQuantity(Long userId, Long cartItemId, int count) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Mục giỏ hàng không tồn tại"));
        if (item.getCart().getUser().getId().equals(userId)) {
            if (count <= 0) {
                cartItemRepository.delete(item);
            } else {
                item.setCount(count);
                item.setUpdatedAt(LocalDateTime.now());
                cartItemRepository.save(item);
            }
        }
    }

    @Transactional
    public void removeFromCart(Long userId, Long cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Mục giỏ hàng không tồn tại"));
        if (item.getCart().getUser().getId().equals(userId)) {
            cartItemRepository.delete(item);
        }
    }

    @Transactional
    public void clearCart(Long userId) {
        cartRepository.findByUserId(userId).ifPresent(cart -> cartItemRepository.deleteByCartId(cart.getId()));
    }

    public BigDecimal getCartTotal(Long userId) {
        return cartRepository.findByUserId(userId)
                .map(cart -> cart.getItems().stream()
                        .map(CartItem::getSubTotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .orElse(BigDecimal.ZERO);
    }
}
