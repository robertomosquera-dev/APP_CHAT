package com.rb.api_chat.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "messages")
public class MessageEntity extends BaseDocument{

    private UUID chatId;

    private UUID senderId;

    private MessageType type;

    private MessageStatus status;

    private String content;

    private String fileUrl;

    @CreatedDate
    private Instant sentAt;

}