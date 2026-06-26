package com.rb.api_chat.service;

import com.rb.api_chat.dto.request.MessageRequest;
import com.rb.api_chat.dto.response.MessageResponse;
import com.rb.api_chat.model.ChatType;
import org.springframework.http.codec.multipart.FilePart;

public interface IMessageService {
    MessageResponse sendMessage(FilePart file, ChatType chatType, MessageRequest messageRequest);
}
