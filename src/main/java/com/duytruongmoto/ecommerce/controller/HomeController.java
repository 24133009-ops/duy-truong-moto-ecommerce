package com.duytruongmoto.ecommerce.controller;

import com.duytruongmoto.ecommerce.entity.User;
import com.duytruongmoto.ecommerce.repository.CategoryRepository;
import com.duytruongmoto.ecommerce.security.CustomUserDetails;
import com.duytruongmoto.ecommerce.service.ProductService;
import com.duytruongmoto.ecommerce.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductService productService;
    private final StoreService storeService;
    private final CategoryRepository categoryRepository;

    @GetMapping({"/", "/home"})
    public String index(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails != null) {
            User currentUser = userDetails.getUser();
            model.addAttribute("currentUser", currentUser);
        }

        // 1. Guest: Top sản phẩm bán trên 10 sản phẩm (sắp xếp giảm dần)
        model.addAttribute("topOver10Sold", productService.getTopOver10Sold());

        // 2. 20 sản phẩm mới nhất
        model.addAttribute("newest20", productService.getTop20Newest());

        // 3. 20 sản phẩm bán chạy nhất
        model.addAttribute("bestSeller20", productService.getTop20BestSellers());

        // 4. 20 sản phẩm đánh giá cao nhất
        model.addAttribute("topRated20", productService.getTop20Rated());

        // 5. Danh mục môtô & phụ tùng
        model.addAttribute("categories", categoryRepository.findByIsDeletedFalse());

        // 6. Danh sách chuỗi Showroom Duy Truong Motor
        model.addAttribute("stores", storeService.getAllActiveStores());

        model.addAttribute("totalProducts", productService.countActiveSellingProducts());

        return "client/home";
    }

    @GetMapping("/policy")
    public String policyPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails != null) {
            model.addAttribute("currentUser", userDetails.getUser());
        }
        return "client/policy";
    }
}
