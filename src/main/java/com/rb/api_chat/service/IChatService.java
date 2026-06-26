package com.rb.api_chat.service;


import com.rb.api_chat.dto.response.ChatResponse;
import com.rb.api_chat.dto.response.PrivateChatResponse;
import com.rb.api_chat.model.ChatType;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface IChatService {
    Mono<ChatResponse> createChat(FilePart filePart, String chatName, UUID senderId, List<UUID> receiverId, ChatType type);
    Mono<ChatResponse> findByIdAndType(UUID chatId, ChatType type);
    Flux<ChatResponse> findAllByUserId(UUID userId);
    Mono<PrivateChatResponse> findByPrivateId(UUID userId, UUID chatId);
}
