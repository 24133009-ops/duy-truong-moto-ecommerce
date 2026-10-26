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
            .csrf(csrf -> csrf.ignoringRequestMatchers("/ws/**", "/api/**"))
            .authorizeHttpRequests(auth -> auth
                // Tài nguyên công khai
                .requestMatchers(
                    "/", "/home", "/products/**", "/stores/**", "/auth/**",
                    "/css/**", "/js/**", "/images/**", "/fonts/**", "/webjars/**",
                    "/ws/**", "/api/chat/**"
                ).permitAll()

                // Phân quyền theo vai trò
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
}
