package com.rb.api_chat.service.impl;

import com.rb.api_chat.dto.request.ChangeAliasContactRequest;
import com.rb.api_chat.dto.request.ContactToUserRequest;
import com.rb.api_chat.dto.request.UserRegisterRequest;
import com.rb.api_chat.dto.response.UserResponse;
import com.rb.api_chat.mapper.UserMapper;
import com.rb.api_chat.model.Contact;
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

    //En un futuro esto lo debe hacer el user nomas, osea ese userId lo sacaremos del token
    @Override
    public Mono<Void> addContact(UUID userId, ContactToUserRequest contactToUserRequest) {
        if (contactToUserRequest == null) {
            return Mono.error(() -> new RuntimeException("Los datos del contacto no pueden ser nulos"));
        }
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(() -> new RuntimeException("Usuario logueado no encontrado")))
                .flatMap(currentUser -> {
                    return userRepository.findByPhoneNumber(contactToUserRequest.number())
                            .switchIfEmpty(Mono.error(() -> new RuntimeException("El usuario con el número " + contactToUserRequest.number() + " no está registrado en la app")))
                            .flatMap(contactUser -> {

                                if (currentUser.getId().equals(contactUser.getId())) {
                                    return Mono.error(() -> new RuntimeException("No puedes agregarte a ti mismo como contacto"));
                                }

                                boolean alreadyExists = currentUser.getContacts().stream()
                                        .anyMatch(c -> c.getAlias().equalsIgnoreCase(contactToUserRequest.alias())
                                                || c.getUserId().equals(contactUser.getId()));

                                if (alreadyExists) {
                                    return Mono.error(() -> new RuntimeException("El contacto ya está registrado en tu lista"));
                                }

                                currentUser.getContacts().add(
                                        Contact.builder()
                                                .userId(contactUser.getId())
                                                .alias(contactToUserRequest.alias())
                                                .build()
                                );

                                return userRepository.save(currentUser);
                            });
                })
                .then();
    }

    @Override
    public Mono<Void> blockContact(UUID userId, UUID contactId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(() -> new RuntimeException("Usuario logueado no encontrado")))
                .flatMap(userEntity -> {
                    return userEntity.getContacts().stream()
                            .filter(contact -> contact.getUserId().equals(contactId))
                            .findFirst()
                            .map(contact -> {
                                contact.setBlocked(true);
                                return userRepository.save(userEntity);
                            })
                            .orElseGet(() -> Mono.error(() -> new RuntimeException("No se encontró el contacto con el ID: " + contactId + " en tu lista")));
                })
                .then();
    }

    @Override
    public Mono<Void> removeContact(UUID userId, UUID contactId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(() -> new RuntimeException("Usuario logueado no encontrado")))
                .flatMap(userEntity -> {
                    return userEntity.getContacts().stream()
                            .filter(contact -> contact.getUserId().equals(contactId))
                            .findFirst()
                            .map(contact -> {
                                userEntity.getContacts().remove(contact);
                                return userRepository.save(userEntity);
                            })
                            .orElseGet(() -> Mono.error(() -> new RuntimeException("No se pudo eliminar el contacto por que no se encontró en tu lista")));
                })
                .then();
    }

    @Override
    public Mono<Void> changeAliasContact(UUID userId, ChangeAliasContactRequest changeAliasContactRequest) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(() -> new RuntimeException("Usuario logueado no encontrado")))
                .flatMap(userEntity -> {
                    return userEntity.getContacts().stream()
                            .filter(contact -> contact.getUserId().equals(changeAliasContactRequest.contactId()))
                            .findFirst()
                            .map(contact -> {
                                contact.setAlias(changeAliasContactRequest.alias());
                                return userRepository.save(userEntity);
                            })
                            .orElseGet(() -> Mono.error(() -> new RuntimeException("No se pudo cambiar el alias por que el contacto no existe en tu lista")));
                })
                .then();
    }

    //En un futuro esto lo debe hacer el user nomas, osea ese userId lo sacaremos del token
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