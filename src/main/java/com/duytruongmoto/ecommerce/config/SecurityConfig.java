package com.duytruongmoto.ecommerce.config;

import com.duytruongmoto.ecommerce.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.ignoringRequestMatchers("/ws/**", "/api/**", "/ping", "/health"))
            .authorizeHttpRequests(auth -> auth
                // Tài nguyên công khai & REST API
                .requestMatchers(
                    "/", "/home", "/products/**", "/stores/**", "/auth/**",
                    "/css/**", "/js/**", "/images/**", "/fonts/**", "/webjars/**",
                    "/ws/**", "/api/**", "/ping", "/health"
                ).permitAll()

                // Phân quyền theo vai trò cho SSR Web
                .requestMatchers("/admin/**").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers("/vendor/**").hasAnyRole("VENDOR", "ADMIN")
                .requestMatchers("/shipper/**").hasAnyRole("SHIPPER", "ADMIN")
                .requestMatchers("/cart/**", "/checkout/**", "/user/**", "/orders/**", "/reviews/add").authenticated()

                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login-process")
                .usernameParameter("email")
                .passwordParameter("password")
                .successHandler((request, response, authentication) -> {
                    for (org.springframework.security.core.GrantedAuthority auth : authentication.getAuthorities()) {
                        String role = auth.getAuthority();
                        if ("ROLE_ADMIN".equals(role) || "ROLE_MANAGER".equals(role)) {
                            response.sendRedirect("/admin/dashboard");
                            return;
                        }
                        if ("ROLE_VENDOR".equals(role)) {
                            response.sendRedirect("/vendor/dashboard");
                            return;
                        }
                        if ("ROLE_SHIPPER".equals(role)) {
                            response.sendRedirect("/shipper/dashboard");
                            return;
                        }
                    }
                    response.sendRedirect("/home");
                })
                .failureUrl("/auth/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/auth/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/auth/access-denied")
            );

        http.authenticationProvider(authenticationProvider());
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        List<String> origins = new ArrayList<>(Arrays.asList(
            "https://duytruong-motor.vercel.app",
            "https://duy-truong-moto-ecommerce.onrender.com",
            "http://localhost:5173",
            "http://localhost:3000"
        ));

        // 1. Đọc biến môi trường FRONTEND_URL từ Render (tự động loại bỏ dấu / ở cuối)
        String envFrontend = System.getenv("FRONTEND_URL");
        if (envFrontend != null && !envFrontend.trim().isEmpty()) {
            String clean = envFrontend.trim();
            if (clean.endsWith("/")) {
                clean = clean.substring(0, clean.length() - 1);
            }
            if (!origins.contains(clean)) {
                origins.add(clean);
            }
        }

        configuration.setAllowedOrigins(origins);

        // 2. Cho phép cả các bản Vercel Preview (duytruong-motor-git-*.vercel.app) và localhost mọi port
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "https://*.vercel.app",
            "http://localhost:*"
        ));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        configuration.setExposedHeaders(Arrays.asList("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
