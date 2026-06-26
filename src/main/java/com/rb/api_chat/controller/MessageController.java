package com.rb.api_chat.controller;

import com.rb.api_chat.dto.request.MessageRequest;
import com.rb.api_chat.dto.response.MessageResponse;
import com.rb.api_chat.model.ChatType;
import com.rb.api_chat.model.MessageStatus;
import com.rb.api_chat.service.IMessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {

    private final IMessageService messageService;

    @PostMapping(value = "/{chatType}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MessageResponse> sendMessage(
            @PathVariable ChatType chatType,
            @RequestPart(required = false) FilePart file,
            @RequestPart("data") @Valid MessageRequest messageRequest) {
        return messageService.sendMessage(file, chatType, messageRequest);
    }

    @GetMapping("/{chatId}")
    public Flux<MessageResponse> findAllByChatId(
            @PathVariable UUID chatId,
            @RequestParam(defaultValue = "0") Integer page) {
        return messageService.findAllByChatId(chatId, page);
    }

    @PatchMapping("/{messageId}/status/{status}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<MessageResponse> changeStatus(
            @PathVariable UUID messageId,
            @PathVariable MessageStatus status) {
        return messageService.changeStatus(messageId, status);
    }

}