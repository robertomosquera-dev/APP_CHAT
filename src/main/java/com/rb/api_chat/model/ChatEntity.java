package com.rb.api_chat.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chat")
public class ChatEntity extends BaseDocument{

    @Builder.Default
    private ChatType type = ChatType.PRIVATE;

    private String name;

    @Builder.Default
    private List<UUID> usersId = new ArrayList<>();

    @Builder.Default
    private List<UUID> adminsId = new ArrayList<>();

    private UUID createdBy;


}