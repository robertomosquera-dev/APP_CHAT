package com.rb.api_chat.service;

import com.rb.api_chat.dto.request.MessageRequest;
import com.rb.api_chat.dto.response.MessageResponse;
import com.rb.api_chat.model.ChatType;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface IMessageService {
    Mono<MessageResponse> sendMessage(FilePart file, ChatType chatType, MessageRequest messageRequest);
    Flux<MessageResponse> findAllByChatId(UUID chatId, Integer page);
}
