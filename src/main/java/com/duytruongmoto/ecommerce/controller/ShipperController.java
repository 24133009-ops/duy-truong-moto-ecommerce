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
        List<Order> allOrders = orderService.getAllOrders();

        List<Order> assignedOrders;
        if (status != null) {
            assignedOrders = allOrders.stream()
                    .filter(o -> o.getStatus() == status && (o.getShipper() == null || o.getShipper().getId().equals(shipper.getId())))
                    .toList();
        } else {
            // Mặc định hiển thị toàn bộ các đơn của tài xế HOẶC đơn đang trong quy trình vận chuyển (CONFIRMED, PICKED_UP, SHIPPING, HANDOVER, DELIVERED)
            assignedOrders = allOrders.stream()
                    .filter(o -> (o.getShipper() != null && o.getShipper().getId().equals(shipper.getId()))
                            || o.getStatus() == OrderStatus.PICKED_UP
                            || o.getStatus() == OrderStatus.SHIPPING
                            || o.getStatus() == OrderStatus.HANDOVER
                            || o.getStatus() == OrderStatus.CONFIRMED
                            || o.getStatus() == OrderStatus.DELIVERED)
                    .toList();
        }

        long inTransitCount = assignedOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.SHIPPING || o.getStatus() == OrderStatus.PICKED_UP || o.getStatus() == OrderStatus.HANDOVER)
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

    @PostMapping("/orders/{id}/start-shipping")
    public String startShipping(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        if (userDetails == null) return "redirect:/auth/login";

        try {
            orderService.updateOrderStatusWithRoleCheck(id, OrderStatus.SHIPPING, userDetails.getUser());
            redirectAttributes.addFlashAttribute("successMessage", "Xe cứu hộ sàn trượt VIP đã xuất phát vận chuyển siêu môtô đến địa chỉ khách hàng (Chặng 4/6)!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/shipper/dashboard";
    }

    @PostMapping("/orders/{id}/handover")
    public String handover(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        if (userDetails == null) return "redirect:/auth/login";

        try {
            orderService.updateOrderStatusWithRoleCheck(id, OrderStatus.HANDOVER, userDetails.getUser());
            redirectAttributes.addFlashAttribute("successMessage", "Đã hạ sàn trượt thủy lực! Tiến hành cùng khách hàng kiểm tra xe, đối chiếu số khung và nổ máy test pô (Chặng 5/6)!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/shipper/dashboard";
    }

    @PostMapping("/orders/{id}/deliver")
    public String markDelivered(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        if (userDetails == null) return "redirect:/auth/login";

        try {
            orderService.updateOrderStatusWithRoleCheck(id, OrderStatus.DELIVERED, userDetails.getUser());
            redirectAttributes.addFlashAttribute("successMessage", "Khách hàng đã ký biên bản nghiệm thu & nhận Smartkey! Kích hoạt thành công gói bảo hành điện tử chính hãng 3 năm (Chặng 6/6)!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/shipper/dashboard";
    }
}
