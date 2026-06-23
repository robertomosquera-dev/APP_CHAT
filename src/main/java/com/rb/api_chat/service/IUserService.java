package com.rb.api_chat.service;

import com.rb.api_chat.dto.request.UserRegisterRequest;
import com.rb.api_chat.dto.response.UserResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface IUserService {

    Mono<UserResponse> createUser(UserRegisterRequest userRegisterRequest);

    Mono<UserResponse> getUserById(UUID id);

    Mono<UserResponse> getUserByUsername(String username);

    Mono<UserResponse> activateStatus(UUID id);

    Mono<UserResponse> disableStatus(UUID id);

    Flux<UserResponse> findAllUsers();
}
