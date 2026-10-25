package com.duytruongmoto.ecommerce.controller;

import com.duytruongmoto.ecommerce.entity.*;
import com.duytruongmoto.ecommerce.repository.DeliveryRepository;
import com.duytruongmoto.ecommerce.security.CustomUserDetails;
import com.duytruongmoto.ecommerce.service.CartService;
import com.duytruongmoto.ecommerce.service.OrderService;
import com.duytruongmoto.ecommerce.service.UserService;
import com.duytruongmoto.ecommerce.service.VnPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CheckoutController {

    private final OrderService orderService;
    private final CartService cartService;
    private final UserService userService;
    private final DeliveryRepository deliveryRepository;
    private final VnPayService vnPayService;

    @GetMapping("/checkout")
    public String checkoutPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }
        User user = userDetails.getUser();
        Cart cart = cartService.getOrCreateCart(user.getId());

        if (cart.getItems().isEmpty()) {
            return "redirect:/cart";
        }

        model.addAttribute("currentUser", user);
        model.addAttribute("cart", cart);
        model.addAttribute("cartTotal", cartService.getCartTotal(user.getId()));
        model.addAttribute("addresses", userService.getUserAddresses(user.getId()));
        model.addAttribute("deliveries", deliveryRepository.findByIsDeletedFalse());

        return "client/checkout";
    }

    @PostMapping("/checkout/process")
    public String processCheckout(
            @RequestParam String recipientName,
            @RequestParam String phone,
            @RequestParam String address,
            @RequestParam Long deliveryId,
            @RequestParam String paymentMethod,
            @RequestParam(required = false) String note,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        if (userDetails == null) {
            return "redirect:/auth/login";
        }

        try {
            User user = userDetails.getUser();
            Order order = orderService.createOrder(user.getId(), recipientName, phone, address, deliveryId, paymentMethod, note);

            // Nếu thanh toán qua VNPAY -> Chuyển hướng sang cổng thanh toán VNPAY Sandbox
            if ("VNPAY".equalsIgnoreCase(paymentMethod)) {
                String paymentUrl = vnPayService.createPaymentUrl(order.getOrderCode(), order.getAmountFromUser(), "Thanh toan don dat xe " + order.getOrderCode(), request);
                return "redirect:" + paymentUrl;
            }

            redirectAttributes.addFlashAttribute("successMessage", "Đặt hàng thành công! Mã đơn của bạn là: " + order.getOrderCode());
            return "redirect:/orders/" + order.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/checkout";
        }
    }

    @GetMapping("/checkout/vnpay-return")
    public String vnpayReturn(
            @RequestParam(value = "vnp_ResponseCode", required = false) String responseCode,
            @RequestParam(value = "vnp_TxnRef", required = false) String orderCode,
            @RequestParam(value = "vnp_Amount", required = false) String amount,
            Model model) {

        model.addAttribute("orderCode", orderCode);
        model.addAttribute("responseCode", responseCode);

        if ("00".equals(responseCode)) {
            // Thanh toán thành công
            orderService.findByOrderCode(orderCode).ifPresent(order -> {
                order.setIsPaidBefore(true);
                orderService.updateOrderStatus(order.getId(), OrderStatus.CONFIRMED);
            });
            model.addAttribute("isSuccess", true);
            model.addAttribute("message", "Giao dịch thanh toán tiền cọc qua VNPAY thành công rực rỡ!");
        } else {
            model.addAttribute("isSuccess", false);
            model.addAttribute("message", "Giao dịch không thành công hoặc đã bị hủy bởi người dùng!");
        }

        return "client/vnpay-return";
    }

    @GetMapping("/orders")
    public String orderHistory(
            @RequestParam(required = false) OrderStatus status,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {

        if (userDetails == null) {
            return "redirect:/auth/login";
        }

        User user = userDetails.getUser();
        List<Order> orders = orderService.getOrdersByUser(user.getId(), status);

        model.addAttribute("currentUser", user);
        model.addAttribute("orders", orders);
        model.addAttribute("currentStatus", status);
        model.addAttribute("statuses", OrderStatus.values());

        return "client/order-history";
    }

    @GetMapping("/orders/{id}")
    public String orderDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {

        if (userDetails == null) {
            return "redirect:/auth/login";
        }

        Order order = orderService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại"));

        model.addAttribute("currentUser", userDetails.getUser());
        model.addAttribute("order", order);

        return "client/order-detail";
    }

    @PostMapping("/orders/{id}/cancel")
    public String cancelOrder(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        if (userDetails == null) {
            return "redirect:/auth/login";
        }

        Order order = orderService.findById(id).orElse(null);
        if (order != null && order.getUser().getId().equals(userDetails.getUser().getId())) {
            if (order.getStatus() == OrderStatus.NOT_PROCESSED) {
                orderService.updateOrderStatus(id, OrderStatus.CANCELLED);
                redirectAttributes.addFlashAttribute("successMessage", "Hủy đơn hàng thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Đơn hàng đã được Showroom xử lý, không thể hủy!");
            }
        }
        return "redirect:/orders/" + id;
    }
}
