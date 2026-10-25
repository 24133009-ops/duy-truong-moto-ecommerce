package com.duytruongmoto.ecommerce.controller;

import com.duytruongmoto.ecommerce.entity.ChatMessage;
import com.duytruongmoto.ecommerce.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageRepository chatMessageRepository;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        chatMessage.setTimestamp(LocalDateTime.now());
        return chatMessageRepository.save(chatMessage);
    }

    @GetMapping("/api/chat/history/{storeId}")
    @ResponseBody
    public List<ChatMessage> getChatHistory(@PathVariable Long storeId) {
        return chatMessageRepository.findByStoreIdOrderByTimestampAsc(storeId);
    }
}
