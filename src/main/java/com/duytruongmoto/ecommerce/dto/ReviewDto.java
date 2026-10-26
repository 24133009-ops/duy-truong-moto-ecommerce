package com.duytruongmoto.ecommerce.dto;

import com.duytruongmoto.ecommerce.entity.Review;
import lombok.*;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDto {
    private Long id;
    private Long productId;
    private String userName;
    private String userAvatar;
    private Integer stars;
    private String content;
    private String media;
    private String date;

    public static ReviewDto fromEntity(Review r) {
        if (r == null) return null;
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String formatted = r.getCreatedAt() != null ? r.getCreatedAt().format(fmt) : "";
        String uName = (r.getUser() != null) ? r.getUser().getFullName() : "Biker Khách Hàng";
        String uAvatar = (r.getUser() != null && r.getUser().getAvatar() != null) 
                ? r.getUser().getAvatar() 
                : "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=200&q=80";

        return ReviewDto.builder()
                .id(r.getId())
                .productId(r.getProduct() != null ? r.getProduct().getId() : null)
                .userName(uName)
                .userAvatar(uAvatar)
                .stars(r.getStars() != null ? r.getStars() : 5)
                .content(r.getContent())
                .media(r.getMediaUrls())
                .date(formatted)
                .build();
    }
}
