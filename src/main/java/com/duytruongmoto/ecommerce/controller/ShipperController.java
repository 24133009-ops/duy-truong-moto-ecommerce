package com.duytruongmoto.ecommerce.controller;

import com.duytruongmoto.ecommerce.entity.Order;
import com.duytruongmoto.ecommerce.entity.OrderStatus;
import com.duytruongmoto.ecommerce.entity.User;
import com.duytruongmoto.ecommerce.security.CustomUserDetails;
import com.duytruongmoto.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/shipper")
@RequiredArgsConstructor
public class ShipperController {

    private final OrderService orderService;

    @GetMapping("/dashboard")
    public String dashboard(
            @RequestParam(required = false) OrderStatus status,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {

        if (userDetails == null) return "redirect:/auth/login";

        User shipper = userDetails.getUser();
        List<Order> assignedOrders = orderService.getOrdersByShipper(shipper.getId(), status);

        // Nếu chưa được gán đơn cụ thể, hiển thị thêm các đơn đang chờ giao (PICKED_UP hoặc CONFIRMED)
        if (assignedOrders.isEmpty()) {
            assignedOrders = orderService.getAllOrders().stream()
                    .filter(o -> o.getStatus() == OrderStatus.PICKED_UP || o.getStatus() == OrderStatus.SHIPPING)
                    .toList();
        }

        long inTransitCount = assignedOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.SHIPPING || o.getStatus() == OrderStatus.PICKED_UP)
                .count();
        long deliveredCount = assignedOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .count();
        BigDecimal totalCod = assignedOrders.stream()
                .filter(o -> "COD".equalsIgnoreCase(o.getPaymentMethod()) && !Boolean.TRUE.equals(o.getIsPaidBefore()) && o.getStatus() != OrderStatus.CANCELLED)
                .map(Order::getAmountFromUser)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("currentUser", shipper);
        model.addAttribute("orders", assignedOrders);
        model.addAttribute("statuses", OrderStatus.values());
        model.addAttribute("currentStatus", status);
        model.addAttribute("totalAssigned", assignedOrders.size());
        model.addAttribute("inTransitCount", inTransitCount);
        model.addAttribute("deliveredCount", deliveredCount);
        model.addAttribute("totalCod", totalCod);

        return "shipper/dashboard";
    }

    @PostMapping("/orders/{id}/deliver")
    public String markDelivered(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        if (userDetails == null) return "redirect:/auth/login";

        try {
            orderService.updateOrderStatusWithRoleCheck(id, OrderStatus.DELIVERED, userDetails.getUser());
            redirectAttributes.addFlashAttribute("successMessage", "Xác nhận bàn giao xe và giấy tờ cho khách hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/shipper/dashboard";
    }
}
