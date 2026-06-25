package com.rb.api_chat.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
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
@Document(collection = "users")
public class UserEntity extends BaseDocument{

    private String username;

    private String firstName;

    private String lastName;

    private String photoUrl;

    @Indexed(unique = true)
    private String email;

    private UserStatus status;

    @Indexed(unique = true)
    private String phoneNumber;

    @Builder.Default
    private List<Contact> contacts = new ArrayList<>();

}
