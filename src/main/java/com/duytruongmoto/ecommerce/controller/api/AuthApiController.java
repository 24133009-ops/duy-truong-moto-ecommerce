package com.duytruongmoto.ecommerce.controller.api;

import com.duytruongmoto.ecommerce.dto.AuthDto;
import com.duytruongmoto.ecommerce.entity.Role;
import com.duytruongmoto.ecommerce.entity.User;
import com.duytruongmoto.ecommerce.entity.UserLevel;
import com.duytruongmoto.ecommerce.repository.UserLevelRepository;
import com.duytruongmoto.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@Slf4j
public class AuthApiController {

    private final UserRepository userRepository;
    private final UserLevelRepository userLevelRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthDto.LoginRequest request) {
        if (request.getEmail() == null || request.getPassword() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email và mật khẩu không được để trống!"));
        }

        Optional<User> userOpt = userRepository.findByEmail(request.getEmail().trim());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Email hoặc mật khẩu không chính xác!"));
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Email hoặc mật khẩu không chính xác!"));
        }

        String token = "jwt-dtm-" + UUID.randomUUID().toString();
        log.info(">>> [API Auth] User logged in: {} (Role: {})", user.getEmail(), user.getRole());
        return ResponseEntity.ok(AuthDto.UserProfileDto.fromEntity(user, token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthDto.RegisterRequest request) {
        if (request.getEmail() == null || request.getPassword() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email và mật khẩu là bắt buộc!"));
        }

        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email này đã được đăng ký trong hệ thống!"));
        }

        UserLevel silver = userLevelRepository.findAll().stream().findFirst().orElse(null);
        String fname = request.getFirstname() != null ? request.getFirstname() : "Khách";
        String lname = request.getLastname() != null ? request.getLastname() : "Hàng";
        String slug = (fname + "-" + lname).toLowerCase().replaceAll("[^a-z0-9]+", "-") + "-" + System.currentTimeMillis();

        String phone = (request.getPhone() != null && !request.getPhone().isBlank()) ? request.getPhone().trim() : null;
        if (phone != null && userRepository.existsByPhone(phone)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Số điện thoại " + phone + " đã được đăng ký trong hệ thống!"));
        }
        if (phone == null) {
            phone = "09" + String.valueOf(System.currentTimeMillis()).substring(5);
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .firstname(fname)
                .lastname(lname)
                .slug(slug)
                .phone(phone)
                .role(Role.ROLE_USER)
                .isEmailActive(true)
                .isPhoneActive(true)
                .userLevel(silver)
                .eWallet(BigDecimal.valueOf(10000000))
                .point(100L)
                .avatar("https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=200&q=80")
                .build();

        User saved = userRepository.save(user);
        String token = "jwt-dtm-" + UUID.randomUUID().toString();
        log.info(">>> [API Auth] Registered new user: {}", saved.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(AuthDto.UserProfileDto.fromEntity(saved, token));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestParam(defaultValue = "customer@duytruongmoto.com") String email) {
        return userRepository.findByEmail(email)
                .map(u -> ResponseEntity.ok(AuthDto.UserProfileDto.fromEntity(u, "session-valid")))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/demo-users")
    public ResponseEntity<?> getDemoUsers() {
        Map<String, Object> map = new HashMap<>();
        userRepository.findByEmail("customer@duytruongmoto.com").ifPresent(u -> map.put("user", AuthDto.UserProfileDto.fromEntity(u, "demo-user")));
        userRepository.findByEmail("vendor@duytruongmoto.com").ifPresent(u -> map.put("vendor", AuthDto.UserProfileDto.fromEntity(u, "demo-vendor")));
        userRepository.findByEmail("admin@duytruongmoto.com").ifPresent(u -> map.put("admin", AuthDto.UserProfileDto.fromEntity(u, "demo-admin")));
        userRepository.findByEmail("shipper@duytruongmoto.com").ifPresent(u -> map.put("shipper", AuthDto.UserProfileDto.fromEntity(u, "demo-shipper")));
        return ResponseEntity.ok(map);
    }
}
