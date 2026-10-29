package com.duytruongmoto.ecommerce.controller;

import com.duytruongmoto.ecommerce.entity.Category;
import com.duytruongmoto.ecommerce.entity.Order;
import com.duytruongmoto.ecommerce.repository.CategoryRepository;
import com.duytruongmoto.ecommerce.repository.CommissionRepository;
import com.duytruongmoto.ecommerce.security.CustomUserDetails;
import com.duytruongmoto.ecommerce.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final VoucherService voucherService;
    private final DeliveryService deliveryService;

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
    public String listUsers(@RequestParam(required = false) String keyword, Model model) {
        model.addAttribute("users", userService.searchUsers(keyword));
        model.addAttribute("keyword", keyword);
        return "admin/users";
    }

    @PostMapping("/users/{id}/toggle-lock")
    public String toggleLockUser(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        try {
            String adminEmail = (userDetails != null) ? userDetails.getUsername() : "";
            userService.toggleLock(id, adminEmail);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái khóa/mở tài khoản thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/activate")
    public String activateUser(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            var user = userService.activateUserDirectly(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã kích hoạt thành công tài khoản " + user.getEmail() + "! Người dùng có thể đăng nhập ngay.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/activate-all-pending")
    public String activateAllPendingUsers(RedirectAttributes redirectAttributes) {
        try {
            int count = userService.activateAllPendingUsers();
            redirectAttributes.addFlashAttribute("successMessage", "Đã kích hoạt thành công tất cả " + count + " tài khoản đang chờ OTP! Người dùng có thể đăng nhập ngay.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/stores")
    public String listStores(Model model) {
        model.addAttribute("stores", storeService.getAllStores());
        model.addAttribute("commissions", commissionRepository.findByIsDeletedFalse());
        return "admin/stores";
    }

    @PostMapping("/stores/{id}/toggle")
    public String toggleStore(@PathVariable Long id, @RequestParam boolean isActive, RedirectAttributes redirectAttributes) {
        storeService.toggleStoreStatus(id, isActive);
        redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật trạng thái hoạt động của Showroom!");
        return "redirect:/admin/stores";
    }

    @PostMapping("/stores/{id}/commission")
    public String updateStoreCommission(
            @PathVariable Long id,
            @RequestParam Long commissionId,
            RedirectAttributes redirectAttributes) {
        storeService.findById(id).ifPresent(store -> {
            commissionRepository.findById(commissionId).ifPresent(comm -> {
                store.setCommission(comm);
                // persist
            });
        });
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật tỉ lệ chiết khấu Showroom thành công!");
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

    @GetMapping("/vouchers")
    public String listVouchers(Model model) {
        model.addAttribute("vouchers", voucherService.getAllActiveVouchers());
        return "admin/vouchers";
    }

    @PostMapping("/vouchers/new")
    public String addVoucher(
            @RequestParam String code,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam int discountPercent,
            @RequestParam BigDecimal maxDiscountAmount,
            @RequestParam BigDecimal minOrderAmount,
            @RequestParam(required = false) Integer usageLimit,
            RedirectAttributes redirectAttributes) {
        try {
            voucherService.createVoucher(code, name, description, discountPercent, maxDiscountAmount, minOrderAmount, usageLimit, null);
            redirectAttributes.addFlashAttribute("successMessage", "Đã tạo mã khuyến mãi Voucher mới thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/vouchers";
    }

    @PostMapping("/vouchers/{id}/delete")
    public String deleteVoucher(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        voucherService.deleteVoucher(id);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa mã khuyến mãi thành công!");
        return "redirect:/admin/vouchers";
    }

    @GetMapping("/deliveries")
    public String listDeliveries(Model model) {
        model.addAttribute("deliveries", deliveryService.getAllActiveDeliveries());
        return "admin/deliveries";
    }

    @PostMapping("/deliveries/new")
    public String addDelivery(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam BigDecimal price,
            RedirectAttributes redirectAttributes) {
        try {
            deliveryService.createDelivery(name, description, price);
            redirectAttributes.addFlashAttribute("successMessage", "Đã thêm phương thức giao xe mới thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/deliveries";
    }

    @PostMapping("/deliveries/{id}/delete")
    public String deleteDelivery(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        deliveryService.deleteDelivery(id);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa phương thức giao xe thành công!");
        return "redirect:/admin/deliveries";
    }

    @GetMapping("/commissions")
    public String listCommissions(Model model) {
        model.addAttribute("commissions", commissionRepository.findByIsDeletedFalse());
        return "admin/commissions";
    }
}
