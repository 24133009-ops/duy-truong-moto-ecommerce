package com.duytruongmoto.ecommerce.repository;

import com.duytruongmoto.ecommerce.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByStoreIdOrderByTimestampAsc(Long storeId);
}
