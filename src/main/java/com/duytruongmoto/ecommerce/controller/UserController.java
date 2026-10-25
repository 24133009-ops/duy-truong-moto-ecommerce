package com.duytruongmoto.ecommerce.controller;

import com.duytruongmoto.ecommerce.entity.User;
import com.duytruongmoto.ecommerce.security.CustomUserDetails;
import com.duytruongmoto.ecommerce.service.CloudinaryService;
import com.duytruongmoto.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CloudinaryService cloudinaryService;

    @GetMapping("/profile")
    public String profilePage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }
        User user = userService.findById(userDetails.getUser().getId()).orElse(userDetails.getUser());
        model.addAttribute("currentUser", user);
        model.addAttribute("addresses", userService.getUserAddresses(user.getId()));
        return "client/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(
            @RequestParam String firstname,
            @RequestParam String lastname,
            @RequestParam String phone,
            @RequestParam(required = false) String idCard,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        if (userDetails == null) {
            return "redirect:/auth/login";
        }

        try {
            String avatarUrl = null;
            if (avatarFile != null && !avatarFile.isEmpty()) {
                avatarUrl = cloudinaryService.uploadFile(avatarFile, "avatars");
            }
            userService.updateProfile(userDetails.getUser().getId(), firstname, lastname, phone, idCard, avatarUrl);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật hồ sơ cá nhân thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/user/profile";
    }

    @PostMapping("/addresses/add")
    public String addAddress(
            @RequestParam String recipientName,
            @RequestParam String phone,
            @RequestParam String streetAddress,
            @RequestParam String city,
            @RequestParam(defaultValue = "false") boolean isDefault,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        if (userDetails == null) {
            return "redirect:/auth/login";
        }

        try {
            userService.addAddress(userDetails.getUser().getId(), recipientName, phone, streetAddress, city, isDefault);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm địa chỉ nhận hàng mới thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/user/profile";
    }

    @GetMapping("/addresses/delete/{id}")
    public String deleteAddress(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        if (userDetails != null) {
            userService.deleteAddress(id, userDetails.getUser().getId());
            redirectAttributes.addFlashAttribute("infoMessage", "Đã xóa địa chỉ.");
        }
        return "redirect:/user/profile";
    }
}
