package com.duytruongmoto.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long senderId;

    @Column(nullable = false, length = 100)
    private String senderName;

    private Long recipientId;

    private Long storeId;

    @Lob
    @Column(nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String message;

    private LocalDateTime timestamp = LocalDateTime.now();
}
