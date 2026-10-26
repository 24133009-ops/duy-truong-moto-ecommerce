package com.duytruongmoto.ecommerce.dto;

import com.duytruongmoto.ecommerce.entity.Store;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreDto {
    private Long id;
    private String name;
    private String slug;
    private String city;
    private String address;
    private String phone;
    private String avatar;
    private String cover;
    private Double rating;
    private Boolean active;
    private String bio;

    public static StoreDto fromEntity(Store s) {
        if (s == null) return null;
        String city = "TP. Hồ Chí Minh";
        if (s.getAddress() != null) {
            if (s.getAddress().contains("Hà Nội")) city = "Hà Nội";
            else if (s.getAddress().contains("Đà Nẵng")) city = "Đà Nẵng";
        }
        return StoreDto.builder()
                .id(s.getId())
                .name(s.getName())
                .slug(s.getSlug())
                .city(city)
                .address(s.getAddress())
                .phone(s.getPhone())
                .avatar(s.getAvatar())
                .cover(s.getCover())
                .rating(s.getRating() != null ? s.getRating() : 5.0)
                .active(s.getIsActive() != null ? s.getIsActive() : true)
                .bio(s.getBio())
                .build();
    }
}
