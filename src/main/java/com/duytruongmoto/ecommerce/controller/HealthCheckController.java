package com.duytruongmoto.ecommerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Endpoint siêu nhẹ phục vụ Cron-Job / UptimeRobot giữ server Render luôn thức (Keep-Alive)
 * Hoàn toàn KHÔNG đụng đến Database, KHÔNG tốn RAM/CPU, phản hồi trong vài mili-giây.
 */
@RestController
public class HealthCheckController {

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "Duy Truong Motor Luxury E-Commerce",
            "timestamp", System.currentTimeMillis()
        ));
    }
}
