package com.duytruongmoto.ecommerce.controller;

import com.duytruongmoto.ecommerce.entity.*;
import com.duytruongmoto.ecommerce.repository.DeliveryRepository;
import com.duytruongmoto.ecommerce.security.CustomUserDetails;
import com.duytruongmoto.ecommerce.service.CartService;
import com.duytruongmoto.ecommerce.service.OrderService;
import com.duytruongmoto.ecommerce.service.UserService;
import com.duytruongmoto.ecommerce.service.VnPayService;
import com.duytruongmoto.ecommerce.service.VoucherService;
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
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CheckoutController {

    private final OrderService orderService;
    private final CartService cartService;
    private final UserService userService;
    private final DeliveryRepository deliveryRepository;
    private final VnPayService vnPayService;
    private final VoucherService voucherService;

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
        model.addAttribute("activeVouchers", voucherService.getAllActiveVouchers());

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
            @RequestParam(required = false) String voucherCode,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        if (userDetails == null) {
            return "redirect:/auth/login";
        }

        try {
            User user = userDetails.getUser();
            Order order = orderService.createOrder(user.getId(), recipientName, phone, address, deliveryId, paymentMethod, note, voucherCode);

            // Nếu thanh toán qua VNPAY
            if ("VNPAY".equalsIgnoreCase(paymentMethod)) {
                // Nếu đang dùng mã mẫu chưa đăng ký trên cổng VNPAY sandbox (tránh lỗi 72 "Không tìm thấy website")
                // -> Chuyển hướng sang Cổng Mô Phỏng Thanh Toán VNPAY VIP chuẩn chỉ để demo trơn tru 100%
                String tmn = vnPayService.getVnpTmnCode();
                if (tmn == null || "DUYTRUONG".equalsIgnoreCase(tmn) || tmn.trim().isEmpty()) {
                    return "redirect:/checkout/vnpay-simulator?orderCode=" + order.getOrderCode();
                }
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

    @GetMapping("/checkout/vnpay-simulator")
    public String vnpaySimulator(
            @RequestParam String orderCode,
            Model model) {
        Order order = orderService.findByOrderCode(orderCode)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng: " + orderCode));
        model.addAttribute("order", order);
        return "client/vnpay-simulator";
    }

    @GetMapping("/checkout/vnpay-return")
    public String vnpayReturn(
            @RequestParam Map<String, String> allParams,
            Model model) {

        String responseCode = allParams.get("vnp_ResponseCode");
        String orderCode = allParams.get("vnp_TxnRef");
        String secureHash = allParams.get("vnp_SecureHash");

        model.addAttribute("orderCode", orderCode);
        model.addAttribute("responseCode", responseCode);

        String envFrontend = System.getenv("FRONTEND_URL");
        String frontendUrl = (envFrontend != null && !envFrontend.trim().isEmpty())
                ? envFrontend.trim()
                : "https://duytruong-motor.vercel.app";
        if (frontendUrl.endsWith("/")) {
            frontendUrl = frontendUrl.substring(0, frontendUrl.length() - 1);
        }
        model.addAttribute("frontendUrl", frontendUrl);

        // Kiểm tra chữ ký bảo mật VNPAY nếu có hash gửi về
        boolean isSignatureValid = (secureHash == null) || vnPayService.validateSignature(allParams);

        if (!isSignatureValid) {
            log.warn(">>> [VNPAY Return] Chữ ký bảo mật không hợp lệ cho đơn hàng {}", orderCode);
            model.addAttribute("isSuccess", false);
            model.addAttribute("message", "Chữ ký bảo mật giao dịch VNPAY không hợp lệ hoặc đã bị thay đổi!");
            return "client/vnpay-return";
        }

        if ("00".equals(responseCode)) {
            // Thanh toán thành công và bảo đảm tính Idempotent (không cộng lặp)
            orderService.findByOrderCode(orderCode).ifPresent(order -> {
                if (Boolean.TRUE.equals(order.getIsPaidBefore()) && order.getStatus() == OrderStatus.CONFIRMED) {
                    log.info(">>> [VNPAY Return] Đơn hàng {} đã được xác nhận cọc trước đó (Idempotent).", orderCode);
                } else {
                    String txnNo = allParams.getOrDefault("vnp_TransactionNo", "VNPAY-" + System.currentTimeMillis());
                    orderService.markOrderPaid(order.getId(), txnNo);
                    log.info(">>> [VNPAY Return] Xác nhận thanh toán cọc thành công cho đơn hàng: {}", orderCode);
                }
            });
            model.addAttribute("isSuccess", true);
            model.addAttribute("message", "Giao dịch thanh toán tiền cọc qua VNPAY thành công rực rỡ!");
        } else {
            model.addAttribute("isSuccess", false);
            model.addAttribute("message", "Giao dịch không thành công hoặc đã bị hủy bởi người dùng!");
        }

        return "client/vnpay-return";
    }

    @GetMapping("/checkout/vnpay-ipn")
    @ResponseBody
    public org.springframework.http.ResponseEntity<?> vnpayIpn(@RequestParam Map<String, String> allParams) {
        String responseCode = allParams.get("vnp_ResponseCode");
        String orderCode = allParams.get("vnp_TxnRef");
        String secureHash = allParams.get("vnp_SecureHash");

        if (secureHash != null && !vnPayService.validateSignature(allParams)) {
            log.warn(">>> [VNPAY IPN] Invalid signature for order {}", orderCode);
            return org.springframework.http.ResponseEntity.ok(Map.of("RspCode", "97", "Message", "Invalid Checksum"));
        }

        java.util.Optional<Order> orderOpt = orderService.findByOrderCode(orderCode);
        if (orderOpt.isEmpty()) {
            return org.springframework.http.ResponseEntity.ok(Map.of("RspCode", "01", "Message", "Order not Found"));
        }

        Order order = orderOpt.get();
        if (Boolean.TRUE.equals(order.getIsPaidBefore())) {
            return org.springframework.http.ResponseEntity.ok(Map.of("RspCode", "02", "Message", "Order already confirmed"));
        }

        if ("00".equals(responseCode)) {
            String txnNo = allParams.getOrDefault("vnp_TransactionNo", "IPN-" + System.currentTimeMillis());
            orderService.markOrderPaid(order.getId(), txnNo);
            log.info(">>> [VNPAY IPN] Đã xác nhận đơn hàng {} qua IPN", orderCode);
            return org.springframework.http.ResponseEntity.ok(Map.of("RspCode", "00", "Message", "Confirm Success"));
        } else {
            return org.springframework.http.ResponseEntity.ok(Map.of("RspCode", "00", "Message", "Payment not completed"));
        }
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
        List<Order> orders;
        List<Order> allUserOrders;

        if (user.getRole() == Role.ROLE_SHIPPER) {
            orders = orderService.getOrdersByShipper(user.getId(), status);
            allUserOrders = orderService.getOrdersByShipper(user.getId(), null);
            if (allUserOrders.isEmpty()) {
                orders = status != null ? orderService.getAllOrders().stream().filter(o -> o.getStatus() == status).toList() : orderService.getAllOrders();
                allUserOrders = orderService.getAllOrders();
            }
        } else if (user.getRole() == Role.ROLE_ADMIN || user.getRole() == Role.ROLE_MANAGER) {
            orders = orderService.getOrdersByUser(user.getId(), status);
            allUserOrders = orderService.getOrdersByUser(user.getId(), null);
            if (allUserOrders.isEmpty()) {
                orders = status != null ? orderService.getAllOrders().stream().filter(o -> o.getStatus() == status).toList() : orderService.getAllOrders();
                allUserOrders = orderService.getAllOrders();
            }
        } else {
            orders = orderService.getOrdersByUser(user.getId(), status);
            allUserOrders = orderService.getOrdersByUser(user.getId(), null);
        }

        // Map đếm số lượng đơn theo từng trạng thái để hiển thị trên Tab
        java.util.Map<String, Long> statusCounts = new java.util.HashMap<>();
        for (OrderStatus st : OrderStatus.values()) {
            long count = allUserOrders.stream().filter(o -> o.getStatus() == st).count();
            statusCounts.put(st.name(), count);
        }

        model.addAttribute("currentUser", user);
        model.addAttribute("orders", orders);
        model.addAttribute("allOrdersCount", allUserOrders.size());
        model.addAttribute("statusCounts", statusCounts);
        model.addAttribute("currentStatus", status);
        model.addAttribute("statuses", OrderStatus.values());
        boolean hasPurchasedBefore = allUserOrders.stream().anyMatch(o -> o.getStatus() == OrderStatus.DELIVERED || Boolean.TRUE.equals(o.getIsPaidBefore()));
        model.addAttribute("hasPurchasedBefore", hasPurchasedBefore);
        model.addAttribute("loyaltyVouchers", voucherService.getAllActiveVouchers());

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

        User currentUser = userDetails.getUser();
        boolean isOwner = order.getUser() != null && order.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ROLE_ADMIN || currentUser.getRole() == Role.ROLE_MANAGER;
        boolean isStoreOwner = order.getStore() != null && order.getStore().getOwner() != null && order.getStore().getOwner().getId().equals(currentUser.getId());
        boolean isAssignedShipper = order.getShipper() != null && order.getShipper().getId().equals(currentUser.getId());

        if (!isOwner && !isAdmin && !isStoreOwner && !isAssignedShipper) {
            throw new org.springframework.security.access.AccessDeniedException("Bạn không có quyền truy cập thông tin đơn hàng này!");
        }

        model.addAttribute("currentUser", currentUser);
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

        Order order = orderService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại"));

        User currentUser = userDetails.getUser();
        boolean isOwner = order.getUser() != null && order.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ROLE_ADMIN || currentUser.getRole() == Role.ROLE_MANAGER;

        if (!isOwner && !isAdmin) {
            throw new org.springframework.security.access.AccessDeniedException("Bạn không có quyền hủy đơn hàng của người khác!");
        }

        // Khách hàng hủy chỉ trước khi đơn được bàn giao cho shipper lấy hàng
        if (order.getStatus() == OrderStatus.NOT_PROCESSED || order.getStatus() == OrderStatus.CONFIRMED) {
            orderService.updateOrderStatus(id, OrderStatus.CANCELLED);
            redirectAttributes.addFlashAttribute("successMessage", "Hủy đơn hàng thành công!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Đơn hàng đã được xuất kho hoặc đang vận chuyển, không thể hủy!");
        }

        return "redirect:/orders/" + id;
    }
}
