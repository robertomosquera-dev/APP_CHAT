package com.rb.api_chat.service;

import com.rb.api_chat.dto.request.ChangeAliasContactRequest;
import com.rb.api_chat.dto.request.ContactToUserRequest;
import com.rb.api_chat.dto.request.UserRegisterRequest;
import com.rb.api_chat.dto.response.ContactResponse;
import com.rb.api_chat.dto.response.UserResponse;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface IUserService {

    Mono<UserResponse> createUser(UserRegisterRequest userRegisterRequest,FilePart img);

    Mono<UserResponse> getUserById(UUID id);

    Mono<UserResponse> getUserByUsername(String username);

    Mono<UserResponse> activateStatus(UUID id);

    Mono<UserResponse> disableStatus(UUID id);

    Flux<UserResponse> findAllUsers();

    Mono<Void> addContact(UUID id, ContactToUserRequest contactToUserRequest);

    Mono<Void> blockContact(UUID id, UUID contactId);

    Mono<Void> removeContact(UUID id, UUID contactId);

    Mono<Void> changeAliasContact(UUID id, ChangeAliasContactRequest changeAliasContactRequest);

    Mono<UserResponse> changeProfilePicture(UUID id, FilePart img);

    Flux<ContactResponse> allContact(UUID id);

}
