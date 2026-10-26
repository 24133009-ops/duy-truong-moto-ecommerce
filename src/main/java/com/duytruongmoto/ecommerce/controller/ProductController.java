package com.duytruongmoto.ecommerce.controller;

import com.duytruongmoto.ecommerce.entity.Product;
import com.duytruongmoto.ecommerce.entity.User;
import com.duytruongmoto.ecommerce.repository.CategoryRepository;
import com.duytruongmoto.ecommerce.security.CustomUserDetails;
import com.duytruongmoto.ecommerce.service.CloudinaryService;
import com.duytruongmoto.ecommerce.service.ProductService;
import com.duytruongmoto.ecommerce.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CategoryRepository categoryRepository;
    private final ReviewService reviewService;
    private final CloudinaryService cloudinaryService;

    @GetMapping
    public String listProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String brand,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "12") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {

        if (userDetails != null) {
            model.addAttribute("currentUser", userDetails.getUser());
        }

        // Clean up empty string query parameters
        String cleanKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        String cleanBrand = (brand != null && !brand.trim().isEmpty()) ? brand.trim() : null;
        
        // Prevent negative page numbers
        int pageIndex = (page == null || page < 0) ? 0 : page;

        Page<Product> productPage = productService.filterProducts(cleanKeyword, categoryId, minPrice, maxPrice, cleanBrand, pageIndex, size);

        // Fallback: If requested page is out of bounds (e.g. user requested page=1 when only page 0 has items)
        if (productPage.getContent().isEmpty() && productPage.getTotalElements() > 0 && pageIndex > 0) {
            pageIndex = 0;
            productPage = productService.filterProducts(cleanKeyword, categoryId, minPrice, maxPrice, cleanBrand, pageIndex, size);
        }

        model.addAttribute("productPage", productPage);
        model.addAttribute("categories", categoryRepository.findByIsDeletedFalse());
        model.addAttribute("brands", productService.getAllDistinctBrands());
        model.addAttribute("keyword", cleanKeyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("brand", cleanBrand);
        model.addAttribute("currentPage", pageIndex);

        return "client/products";
    }

    @GetMapping("/{slug}")
    public String productDetail(
            @PathVariable String slug,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {

        Product product = productService.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy dòng xe hoặc phụ tùng này!"));

        if (userDetails != null) {
            model.addAttribute("currentUser", userDetails.getUser());
        }

        model.addAttribute("product", product);
        model.addAttribute("reviews", reviewService.getReviewsByProduct(product.getId()));
        model.addAttribute("relatedProducts", productService.getProductsByCategory(product.getCategory().getId(), 0, 4).getContent());

        return "client/product-detail";
    }

    @PostMapping("/reviews/add")
    public String addReview(
            @RequestParam Long productId,
            @RequestParam(required = false) Long orderId,
            @RequestParam int stars,
            @RequestParam String content,
            @RequestParam(value = "mediaFile", required = false) MultipartFile mediaFile,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        if (userDetails == null) {
            return "redirect:/auth/login";
        }

        User user = userDetails.getUser();
        Product product = productService.findById(productId).orElse(null);
        if (product == null) {
            return "redirect:/home";
        }

        try {
            String mediaUrl = null;
            if (mediaFile != null && !mediaFile.isEmpty()) {
                mediaUrl = cloudinaryService.uploadFile(mediaFile, "reviews");
            }

            reviewService.addReview(user.getId(), productId, orderId, stars, content, mediaUrl);
            redirectAttributes.addFlashAttribute("successMessage", "Cảm ơn bạn đã chia sẻ đánh giá trải nghiệm thực tế về xe!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/products/" + product.getSlug();
    }
}
