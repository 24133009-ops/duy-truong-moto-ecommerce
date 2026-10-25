package com.duytruongmoto.ecommerce.controller;

import com.duytruongmoto.ecommerce.entity.Category;
import com.duytruongmoto.ecommerce.entity.Order;
import com.duytruongmoto.ecommerce.repository.CategoryRepository;
import com.duytruongmoto.ecommerce.repository.CommissionRepository;
import com.duytruongmoto.ecommerce.service.OrderService;
import com.duytruongmoto.ecommerce.service.StoreService;
import com.duytruongmoto.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final StoreService storeService;
    private final OrderService orderService;
    private final CategoryRepository categoryRepository;
    private final CommissionRepository commissionRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Order> allOrders = orderService.getAllOrders();

        BigDecimal totalPlatformRevenue = allOrders.stream()
                .map(Order::getAmountToGD)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalGrossMerchandiseValue = allOrders.stream()
                .map(Order::getAmountFromUser)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("totalUsers", userService.getAllUsers().size());
        model.addAttribute("totalStores", storeService.getAllStores().size());
        model.addAttribute("totalOrders", allOrders.size());
        model.addAttribute("totalPlatformRevenue", totalPlatformRevenue);
        model.addAttribute("totalGmv", totalGrossMerchandiseValue);
        model.addAttribute("recentOrders", allOrders.stream().limit(8).toList());

        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }

    @GetMapping("/stores")
    public String listStores(Model model) {
        model.addAttribute("stores", storeService.getAllStores());
        return "admin/stores";
    }

    @PostMapping("/stores/{id}/toggle")
    public String toggleStore(@PathVariable Long id, @RequestParam boolean isActive, RedirectAttributes redirectAttributes) {
        storeService.toggleStoreStatus(id, isActive);
        redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật trạng thái hoạt động của Showroom!");
        return "redirect:/admin/stores";
    }

    @GetMapping("/categories")
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryRepository.findByIsDeletedFalse());
        return "admin/categories";
    }

    @PostMapping("/categories/new")
    public String addCategory(@RequestParam String name, @RequestParam String slug, RedirectAttributes redirectAttributes) {
        Category cat = Category.builder()
                .name(name)
                .slug(slug)
                .isDeleted(false)
                .build();
        categoryRepository.save(cat);
        redirectAttributes.addFlashAttribute("successMessage", "Đã thêm danh mục mới thành công!");
        return "redirect:/admin/categories";
    }

    @GetMapping("/commissions")
    public String listCommissions(Model model) {
        model.addAttribute("commissions", commissionRepository.findByIsDeletedFalse());
        return "admin/commissions";
    }
}
