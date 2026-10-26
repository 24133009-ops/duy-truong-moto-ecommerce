package com.duytruongmoto.ecommerce.controller.api;

import com.duytruongmoto.ecommerce.dto.StoreDto;
import com.duytruongmoto.ecommerce.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stores")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class StoreApiController {

    private final StoreRepository storeRepository;

    @GetMapping
    public ResponseEntity<List<StoreDto>> getAllStores() {
        List<StoreDto> dtos = storeRepository.findAll().stream()
                .map(StoreDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoreDto> getStoreById(@PathVariable Long id) {
        return storeRepository.findById(id)
                .map(s -> ResponseEntity.ok(StoreDto.fromEntity(s)))
                .orElse(ResponseEntity.notFound().build());
    }
}
