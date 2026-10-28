package com.duytruongmoto.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String firstname;

    @Column(nullable = false, length = 50)
    private String lastname;

    @Column(unique = true, length = 100)
    private String slug;

    @Column(unique = true, length = 20)
    private String idCard; // CMND / CCCD

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(unique = true, length = 20)
    private String phone;

    @Column(nullable = false)
    private String password;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.ROLE_USER;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isEmailActive = false;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isPhoneActive = false;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isBlocked = false;

    private String otpCode;
    private LocalDateTime otpExpiry;

    @Column(length = 500)
    private String avatar;

    @Column(length = 500)
    private String cover;

    @Builder.Default
    @Column(nullable = false)
    private Long point = 0L;

    @Builder.Default
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal eWallet = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_level_id")
    private UserLevel userLevel;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses = new ArrayList<>();

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    public String getFullName() {
        return (firstname != null ? firstname : "") + " " + (lastname != null ? lastname : "");
    }
}
