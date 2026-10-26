package com.duytruongmoto.ecommerce.controller;

import com.duytruongmoto.ecommerce.entity.*;
import com.duytruongmoto.ecommerce.repository.CategoryRepository;
import com.duytruongmoto.ecommerce.repository.CommissionRepository;
import com.duytruongmoto.ecommerce.security.CustomUserDetails;
import com.duytruongmoto.ecommerce.service.CloudinaryService;
import com.duytruongmoto.ecommerce.service.OrderService;
import com.duytruongmoto.ecommerce.service.ProductService;
import com.duytruongmoto.ecommerce.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/vendor")
@RequiredArgsConstructor
public class VendorController {

    private final StoreService storeService;
    private final ProductService productService;
    private final OrderService orderService;
    private final CategoryRepository categoryRepository;
    private final CommissionRepository commissionRepository;
    private final CloudinaryService cloudinaryService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null) return "redirect:/auth/login";

        Store store = storeService.getOrCreateVendorStore(userDetails.getUser());
        List<Product> products = productService.getProductsByStore(store.getId());
        List<Order> orders = orderService.getOrdersByStore(store.getId(), null);

        BigDecimal totalRevenue = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED && o.getAmountToStore() != null)
                .map(Order::getAmountToStore)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("store", store);
        model.addAttribute("totalProducts", products.size());
        model.addAttribute("totalOrders", orders.size());
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("recentOrders", orders.stream().limit(5).toList());

        return "vendor/dashboard";
    }

    @GetMapping("/products")
    public String listProducts(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null) return "redirect:/auth/login";
        Store store = storeService.getOrCreateVendorStore(userDetails.getUser());
        model.addAttribute("store", store);
        model.addAttribute("products", productService.getProductsByStore(store.getId()));
        return "vendor/products";
    }

    @GetMapping("/products/new")
    public String newProductForm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null) return "redirect:/auth/login";
        Store store = storeService.getOrCreateVendorStore(userDetails.getUser());
        model.addAttribute("store", store);
        model.addAttribute("categories", categoryRepository.findByIsDeletedFalse());
        return "vendor/product-form";
    }

    @PostMapping("/products/new")
    public String createProduct(
            @RequestParam Long categoryId,
            @RequestParam String name,
            @RequestParam String slug,
            @RequestParam String description,
            @RequestParam BigDecimal price,
            @RequestParam(required = false) BigDecimal promotionalPrice,
            @RequestParam Integer quantity,
            @RequestParam String engineCapacity,
            @RequestParam String color,
            @RequestParam String brand,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        if (userDetails == null) return "redirect:/auth/login";
        Store store = storeService.getOrCreateVendorStore(userDetails.getUser());

        try {
            String imageUrl = "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=800&q=80";
            if (imageFile != null && !imageFile.isEmpty()) {
                imageUrl = cloudinaryService.uploadFile(imageFile, "products");
            }

            productService.saveProduct(store.getId(), categoryId, name, slug, description,
                    price, promotionalPrice, quantity, imageUrl, engineCapacity, color, brand);

            redirectAttributes.addFlashAttribute("successMessage", "Đã thêm dòng xe / phụ tùng vào Showroom thành công!");
            return "redirect:/vendor/products";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/vendor/products/new";
        }
    }

    @PostMapping("/products/{id}/toggle")
    public String toggleSelling(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null) {
            Store store = storeService.getOrCreateVendorStore(userDetails.getUser());
            if (store != null) {
                productService.toggleSelling(id, store.getId());
            }
        }
        return "redirect:/vendor/products";
    }

    @GetMapping("/orders")
    public String listOrders(
            @RequestParam(required = false) OrderStatus status,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {
        if (userDetails == null) return "redirect:/auth/login";
        Store store = storeService.getOrCreateVendorStore(userDetails.getUser());
        model.addAttribute("store", store);
        model.addAttribute("orders", orderService.getOrdersByStore(store.getId(), status));
        model.addAttribute("statuses", OrderStatus.values());
        model.addAttribute("currentStatus", status);

        return "vendor/orders";
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status,
            RedirectAttributes redirectAttributes) {
        try {
            orderService.updateOrderStatus(id, status);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái đơn hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/vendor/orders";
    }

    @GetMapping("/register")
    public String registerStorePage(Model model) {
        model.addAttribute("commissions", commissionRepository.findByIsDeletedFalse());
        return "vendor/register";
    }

    @PostMapping("/register")
    public String handleRegisterStore(
            @RequestParam String name,
            @RequestParam String slug,
            @RequestParam String bio,
            @RequestParam String address,
            @RequestParam String phone,
            @RequestParam Long commissionId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        if (userDetails == null) return "redirect:/auth/login";

        try {
            storeService.registerStore(userDetails.getUser().getId(), name, slug, bio, address, phone, commissionId);
            redirectAttributes.addFlashAttribute("successMessage", "Đăng ký mở Showroom Duy Truong Motor thành công!");
            return "redirect:/vendor/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/vendor/register";
        }
    }
}
