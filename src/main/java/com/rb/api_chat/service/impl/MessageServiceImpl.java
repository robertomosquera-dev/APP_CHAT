package com.rb.api_chat.service.impl;

import com.rb.api_chat.dto.request.MessageRequest;
import com.rb.api_chat.dto.response.MessageResponse;
import com.rb.api_chat.model.ChatType;
import com.rb.api_chat.model.MessageType;
import com.rb.api_chat.repository.MessageRepository;
import com.rb.api_chat.service.IChatService;
import com.rb.api_chat.service.IMessageService;
import com.rb.api_chat.service.IStorageService;
import com.rb.api_chat.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements IMessageService {

    private final MessageRepository messageRepository;
    private final IStorageService storageService;
    private final IUserService userService;
    private final IChatService chatService;

    @Transactional
    @Override
    public MessageResponse sendMessage(FilePart file, ChatType chatType , MessageRequest messageRequest) {

        //Simula que extraemos desde el token por mientras
        UUID senderId = messageRequest.senderId();

        UUID receiverId = messageRequest.receiverId();
        UUID chatId = messageRequest.chatId();
        String content = messageRequest.content();
        MessageType type = messageRequest.type();

        return null;
    }
}
