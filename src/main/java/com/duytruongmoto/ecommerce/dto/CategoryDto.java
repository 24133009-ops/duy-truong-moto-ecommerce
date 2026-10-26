package com.duytruongmoto.ecommerce.dto;

import com.duytruongmoto.ecommerce.entity.Category;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {
    private Long id;
    private String name;
    private String slug;
    private String image;
    private String icon;

    public static CategoryDto fromEntity(Category c) {
        if (c == null) return null;
        String icon = resolveIcon(c.getSlug());
        return CategoryDto.builder()
                .id(c.getId())
                .name(c.getName())
                .slug(c.getSlug())
                .image(c.getImage())
                .icon(icon)
                .build();
    }

    private static String resolveIcon(String slug) {
        if (slug == null) return "fa-motorcycle";
        switch (slug.toLowerCase()) {
            case "oto": return "fa-car-side";
            case "xenho": return "fa-motorcycle";
            case "dirtbike": return "fa-mountain";
            case "superbike": return "fa-gauge-high";
            case "hyper-naked": return "fa-bolt";
            case "cruiser": 
            case "cruiser-touring": return "fa-road";
            case "adventure": return "fa-compass";
            case "scooter": return "fa-gem";
            case "gear-parts":
            case "phu-tung-po-do":
            case "do-bao-ho": return "fa-helmet-safety";
            default: return "fa-motorcycle";
        }
    }
}
