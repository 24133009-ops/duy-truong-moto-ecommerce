package com.duytruongmoto.ecommerce.controller;

import com.duytruongmoto.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Email hoặc mật khẩu không chính xác!");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "Bạn đã đăng xuất an toàn khỏi hệ thống Duy Truong Moto.");
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        return "auth/register";
    }

    @PostMapping("/register")
    public String handleRegister(@RequestParam String firstname,
                                 @RequestParam String lastname,
                                 @RequestParam String email,
                                 @RequestParam String phone,
                                 @RequestParam String password,
                                 RedirectAttributes redirectAttributes) {
        try {
            userService.registerUser(firstname, lastname, email, phone, password);
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("infoMessage", "Mã xác thực OTP đã được gửi về email của bạn. Vui lòng nhập mã để kích hoạt tài khoản!");
            return "redirect:/auth/verify-otp?email=" + email;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/auth/register";
        }
    }

    @GetMapping("/verify-otp")
    public String verifyOtpPage(@RequestParam(required = false) String email, Model model) {
        model.addAttribute("email", email);
        return "auth/verify-otp";
    }

    @PostMapping("/verify-otp")
    public String handleVerifyOtp(@RequestParam String email,
                                  @RequestParam String otp,
                                  RedirectAttributes redirectAttributes) {
        boolean verified = userService.verifyOtp(email, otp);
        if (verified) {
            redirectAttributes.addFlashAttribute("successMessage", "Kích hoạt tài khoản thành công! Bạn có thể đăng nhập ngay bây giờ.");
            return "redirect:/auth/login";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Mã OTP không đúng hoặc đã hết hạn! Vui lòng thử lại.");
            return "redirect:/auth/verify-otp?email=" + email;
        }
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword(@RequestParam String email, RedirectAttributes redirectAttributes) {
        try {
            userService.sendForgotPasswordOtp(email);
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("infoMessage", "Mã OTP đặt lại mật khẩu đã được gửi đến email của bạn!");
            return "redirect:/auth/reset-password?email=" + email;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/auth/forgot-password";
        }
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam(required = false) String email, Model model) {
        model.addAttribute("email", email);
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String handleResetPassword(@RequestParam String email,
                                      @RequestParam String otp,
                                      @RequestParam String newPassword,
                                      RedirectAttributes redirectAttributes) {
        boolean success = userService.resetPasswordWithOtp(email, otp, newPassword);
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Đổi mật khẩu thành công! Vui lòng đăng nhập với mật khẩu mới.");
            return "redirect:/auth/login";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Mã OTP không đúng hoặc đã hết hạn!");
            return "redirect:/auth/reset-password?email=" + email;
        }
    }

    @GetMapping("/access-denied")
    public String accessDenied(Model model) {
        model.addAttribute("errorMessage", "Bạn không có quyền truy cập vào phân hệ này!");
        return "auth/login";
    }
}
