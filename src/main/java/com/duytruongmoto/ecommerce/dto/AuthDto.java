package com.duytruongmoto.ecommerce.dto;

import com.duytruongmoto.ecommerce.entity.User;
import lombok.*;

import java.math.BigDecimal;

public class AuthDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RegisterRequest {
        private String email;
        private String password;
        private String firstname;
        private String lastname;
        private String phone;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserProfileDto {
        private Long id;
        private String email;
        private String fullName;
        private String phone;
        private String role;
        private String avatar;
        private BigDecimal eWallet;
        private Long point;
        private String levelName;
        private String token;

        public static UserProfileDto fromEntity(User u, String token) {
            if (u == null) return null;
            String level = (u.getUserLevel() != null) ? u.getUserLevel().getName() : "Thành viên Bạc";
            return UserProfileDto.builder()
                    .id(u.getId())
                    .email(u.getEmail())
                    .fullName(u.getFullName())
                    .phone(u.getPhone())
                    .role(u.getRole() != null ? u.getRole().name() : "ROLE_USER")
                    .avatar(u.getAvatar())
                    .eWallet(u.getEWallet())
                    .point(u.getPoint())
                    .levelName(level)
                    .token(token != null ? token : "session-active")
                    .build();
        }
    }
}
