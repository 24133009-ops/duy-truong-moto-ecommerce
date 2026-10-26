package com.duytruongmoto.ecommerce.controller.api;

import com.duytruongmoto.ecommerce.repository.CategoryRepository;
import com.duytruongmoto.ecommerce.repository.OrderRepository;
import com.duytruongmoto.ecommerce.repository.ProductRepository;
import com.duytruongmoto.ecommerce.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/status")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class StatusApiController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final StoreRepository storeRepository;
    private final OrderRepository orderRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getSystemStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("appName", "Duy Trương Motor - Chuỗi Showroom Môtô PKL & Siêu Xe");
        status.put("status", "UP");
        status.put("backendEngine", "Spring Boot 3.2.3 / Java 17");
        status.put("database", "MySQL / H2 Connected");
        status.put("productCount", productRepository.count());
        status.put("categoryCount", categoryRepository.count());
        status.put("storeCount", storeRepository.count());
        status.put("orderCount", orderRepository.count());
        status.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(status);
    }
}
