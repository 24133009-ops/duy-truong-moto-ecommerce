package com.duytruongmoto.ecommerce.controller;

import com.duytruongmoto.ecommerce.entity.Cart;
import com.duytruongmoto.ecommerce.entity.User;
import com.duytruongmoto.ecommerce.security.CustomUserDetails;
import com.duytruongmoto.ecommerce.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public String viewCart(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }
        User user = userDetails.getUser();
        Cart cart = cartService.getOrCreateCart(user.getId());

        model.addAttribute("currentUser", user);
        model.addAttribute("cart", cart);
        model.addAttribute("cartTotal", cartService.getCartTotal(user.getId()));

        return "client/cart";
    }

    @PostMapping("/add")
    public String addToCart(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") int quantity,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        if (userDetails == null) {
            return "redirect:/auth/login";
        }

        try {
            cartService.addToCart(userDetails.getUser().getId(), productId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Đã thêm sản phẩm vào giỏ hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateQuantity(
            @RequestParam Long cartItemId,
            @RequestParam int count,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails != null) {
            cartService.updateQuantity(userDetails.getUser().getId(), cartItemId, count);
        }
        return "redirect:/cart";
    }

    @GetMapping("/remove/{id}")
    public String removeItem(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        if (userDetails != null) {
            cartService.removeFromCart(userDetails.getUser().getId(), id);
            redirectAttributes.addFlashAttribute("infoMessage", "Đã xóa mục khỏi giỏ hàng.");
        }
        return "redirect:/cart";
    }
}
