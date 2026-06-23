package com.rb.api_chat.controller;

import com.rb.api_chat.dto.request.UserRegisterRequest;
import com.rb.api_chat.dto.response.UserResponse;
import com.rb.api_chat.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @PostMapping
    public Mono<UserResponse> createUser(
            @Valid @RequestBody UserRegisterRequest request) {

        return userService.createUser(request);
    }

    @GetMapping("/{id}")
    public Mono<UserResponse> getUserById(
            @PathVariable UUID id) {

        return userService.getUserById(id);
    }

    @GetMapping("/username/{username}")
    public Mono<UserResponse> getUserByUsername(
            @PathVariable String username) {

        return userService.getUserByUsername(username);
    }

    @PatchMapping("/{id}/online")
    public Mono<UserResponse> activateStatus(
            @PathVariable UUID id) {

        return userService.activateStatus(id);
    }

    @PatchMapping("/{id}/offline")
    public Mono<UserResponse> disableStatus(
            @PathVariable UUID id) {

        return userService.disableStatus(id);
    }

    @GetMapping
    public Flux<UserResponse> findAllUsers() {
        return userService.findAllUsers();
    }

}