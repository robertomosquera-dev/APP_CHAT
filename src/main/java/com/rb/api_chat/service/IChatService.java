package com.rb.api_chat.service;


import com.rb.api_chat.dto.response.ChatResponse;
import com.rb.api_chat.model.ChatType;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface IChatService {
    Mono<ChatResponse> createChat(String chatName,UUID senderId, List<UUID> receiverId, ChatType type);
    Mono<ChatResponse> findByIdAndType(UUID chatId, ChatType type);

}
