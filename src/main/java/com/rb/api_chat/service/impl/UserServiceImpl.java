package com.rb.api_chat.service.impl;

import com.rb.api_chat.dto.request.UserRegisterRequest;
import com.rb.api_chat.dto.response.UserResponse;
import com.rb.api_chat.mapper.UserMapper;
import com.rb.api_chat.model.UserEntity;
import com.rb.api_chat.model.UserStatus;
import com.rb.api_chat.repository.UserRepository;
import com.rb.api_chat.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public Mono<UserResponse> createUser(UserRegisterRequest request) {

        UserEntity userEntity = userMapper.toEntity(request);

        userEntity.setStatus(
                UserStatus.builder()
                        .online(false)
                        .lastSeen(null)
                        .build()
        );

        return userRepository
                .save(userEntity)
                .map(userMapper::toResponse);
    }

    @Override
    public Mono<UserResponse> getUserById(UUID id) {

        return userRepository
                .findById(id)
                .switchIfEmpty(
                        Mono.error(
                                () -> new RuntimeException(
                                        "Usuario no encontrado"
                                )
                        )
                )
                .map(userMapper::toResponse);
    }

    @Override
    public Mono<UserResponse> getUserByUsername(String username) {

        return userRepository
                .findByUsername(username)
                .switchIfEmpty(
                        Mono.error(
                                () -> new RuntimeException(
                                        "Usuario no encontrado"
                                )
                        )
                )
                .map(userMapper::toResponse);
    }

    @Override
    public Mono<UserResponse> activateStatus(UUID id) {
        return updateStatus(id, true);
    }

    @Override
    public Mono<UserResponse> disableStatus(UUID id) {
        return updateStatus(id, false);
    }

    @Override
    public Flux<UserResponse> findAllUsers() {

        return userRepository
                .findAll()
                .map(userMapper::toResponse);
    }

    private Mono<UserResponse> updateStatus(
            UUID id,
            boolean online) {

        return userRepository
                .findById(id)
                .switchIfEmpty(
                        Mono.error(
                                () -> new RuntimeException(
                                        "Usuario no encontrado"
                                )
                        )
                )
                .flatMap(user -> {

                    user.setStatus(
                            UserStatus.builder()
                                    .online(online)
                                    .lastSeen(Instant.now())
                                    .build()
                    );

                    return userRepository.save(user);

                })
                .map(userMapper::toResponse);
    }
}