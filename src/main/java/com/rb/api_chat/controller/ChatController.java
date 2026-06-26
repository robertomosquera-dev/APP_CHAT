package com.rb.api_chat.controller;

import com.rb.api_chat.dto.response.ChatResponse;
import com.rb.api_chat.dto.response.PrivateChatResponse;
import com.rb.api_chat.model.ChatType;
import com.rb.api_chat.service.IChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final IChatService chatService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ChatResponse> createGroupChat(
            @RequestPart(required = false) FilePart img,
            @RequestPart String chatName,
            @RequestPart UUID senderId,
            @RequestPart List<UUID> receiverIds) {
        return chatService.createChat(img, chatName, senderId, receiverIds, ChatType.GROUP);
    }

    @GetMapping("/user/{userId}")
    public Flux<ChatResponse> findAllByUserId(
            @PathVariable UUID userId) {
        return chatService.findAllByUserId(userId);
    }

    @GetMapping("/{chatId}/private/{userId}")
    public Mono<PrivateChatResponse> findPrivateChat(
            @PathVariable UUID chatId,
            @PathVariable UUID userId) {
        return chatService.findByPrivateId(userId, chatId);
    }
}
