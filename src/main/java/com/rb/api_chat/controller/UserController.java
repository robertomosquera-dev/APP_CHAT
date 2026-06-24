package com.rb.api_chat.controller;

import com.rb.api_chat.dto.request.ChangeAliasContactRequest;
import com.rb.api_chat.dto.request.ContactToUserRequest;
import com.rb.api_chat.dto.request.UserRegisterRequest;
import com.rb.api_chat.dto.response.UserResponse;
import com.rb.api_chat.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    /* ==========================================
       🆕 NUEVOS ENDPOINTS PARA CONTACTOS
       ========================================== */

    @PostMapping("/{userId}/contacts")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> addContact(
            @PathVariable UUID userId,
            @Valid @RequestBody ContactToUserRequest contactToUserRequest) {
        return userService.addContact(userId, contactToUserRequest);
    }

    @PatchMapping("/{userId}/contacts/{contactId}/block")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> blockContact(
            @PathVariable UUID userId,
            @PathVariable UUID contactId) {
        return userService.blockContact(userId, contactId);
    }

    @DeleteMapping("/{userId}/contacts/{contactId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> removeContact(
            @PathVariable UUID userId,
            @PathVariable UUID contactId) {
        return userService.removeContact(userId, contactId);
    }

    @PutMapping("/{userId}/contacts/alias")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> changeAliasContact(
            @PathVariable UUID userId,
            @Valid @RequestBody ChangeAliasContactRequest changeAliasContactRequest) {
        return userService.changeAliasContact(userId, changeAliasContactRequest);
    }
}