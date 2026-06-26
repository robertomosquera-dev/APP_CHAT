package com.rb.api_chat.service.impl;

import com.rb.api_chat.dto.request.ChangeAliasContactRequest;
import com.rb.api_chat.dto.request.ContactToUserRequest;
import com.rb.api_chat.dto.request.UserRegisterRequest;
import com.rb.api_chat.dto.response.ContactResponse;
import com.rb.api_chat.dto.response.UserResponse;
import com.rb.api_chat.mapper.UserMapper;
import com.rb.api_chat.model.Contact;
import com.rb.api_chat.model.UserEntity;
import com.rb.api_chat.model.UserStatus;
import com.rb.api_chat.repository.UserRepository;
import com.rb.api_chat.service.IStorageService;
import com.rb.api_chat.service.IUserService;
import com.rb.api_chat.util.FileBuckets;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService , FileBuckets {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final IStorageService storageService;

    @Override
    public Mono<UserResponse> createUser(UserRegisterRequest request,FilePart img) {

        UserEntity userEntity = userMapper.toEntity(request);

        userEntity.setStatus(
                UserStatus.builder()
                        .online(false)
                        .lastSeen(null)
                        .build()
        );

        return storageService
                .uploadFile(img,PROFILE_PICTURES)
                .flatMap(s -> {
                    userEntity.setPhotoUrl(s);
                    return userRepository.save(userEntity);
                })
                .map(userMapper::toResponse)
                .flatMap(this::toResponseWithPhoto);
    }

    private Mono<UserResponse> toResponseWithPhoto(UserResponse userResponse){
        if(userResponse.photoUrl() == null){
            return Mono.just(userResponse);
        }
        return storageService
                .getUrl(userResponse.photoUrl())
                .map(s -> userResponse.toBuilder().photoUrl(s).build());
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
                .map(userMapper::toResponse)
                .flatMap(this::toResponseWithPhoto);
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
                .map(userMapper::toResponse)
                .flatMap(this::toResponseWithPhoto);
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
                .map(userMapper::toResponse)
                .flatMap(this::toResponseWithPhoto);
    }

    //En un futuro esto lo debe hacer el user nomas, osea ese userId lo sacaremos del token
    @Override
    public Mono<Void> addContact(UUID id, ContactToUserRequest contactToUserRequest) {
        if (contactToUserRequest == null) {
            return Mono.error(() -> new RuntimeException("Los datos del contacto no pueden ser nulos"));
        }
        return userRepository.findById(id)
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
    public Mono<Void> blockContact(UUID id, UUID contactId) {
        return userRepository.findById(id)
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
    public Mono<Void> removeContact(UUID id, UUID contactId) {
        return userRepository.findById(id)
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
    public Mono<Void> changeAliasContact(UUID id, ChangeAliasContactRequest changeAliasContactRequest) {
        return userRepository.findById(id)
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

    @Override
    public Mono<UserResponse> changeProfilePicture(UUID id, FilePart img) {
        return userRepository
                .findById(id)
                .switchIfEmpty(Mono.error(() -> new RuntimeException("Usuario no encontrado")))
                .flatMap(user -> storageService
                        .updateFile(img, PROFILE_PICTURES, user.getPhotoUrl())
                        .flatMap(newUrl -> {
                            user.setPhotoUrl(newUrl);
                            return userRepository.save(user);
                        })
                )
                .map(userMapper::toResponse)
                .flatMap(this::toResponseWithPhoto);
    }


    @Override
    public Flux<ContactResponse> allContact(UUID id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(() -> new RuntimeException("Usuario logueado no encontrado")))
                .flatMapMany(userEntity -> {

                    Map<UUID,Contact> contactMap = userEntity
                            .getContacts()
                            .stream()
                            .collect(Collectors.toMap(Contact::getUserId, Function.identity()));

                    List<UUID> ContanctIds = contactMap.keySet().stream().toList();

                    return userRepository
                            .findAllById(ContanctIds)
                            .flatMap(friendEntity -> {
                                Contact localContact = contactMap.get(friendEntity.getId());
                                return storageService
                                        .getUrl(friendEntity.getPhotoUrl())
                                        .map(photoUrl -> {
                                            return ContactResponse.builder()
                                                    .id(friendEntity.getId())
                                                    .alias(localContact != null ? localContact.getAlias() : friendEntity.getUsername())
                                                    .username(friendEntity.getUsername())
                                                    .numberPhone(friendEntity.getPhoneNumber())
                                                    .photoUrl(photoUrl)
                                                    .online(friendEntity.getStatus() != null && friendEntity.getStatus().isOnline())
                                                    .lastSeen(friendEntity.getStatus() != null ? friendEntity.getStatus().getLastSeen() : null)
                                                    .blocked(localContact != null && localContact.getBlocked())
                                                    .build();
                                        });

                            });
                });
    }

    @Override
    public Flux<UserResponse> allByIds(List<UUID> ids) {
        return userRepository.findByIdIn(ids).map(userMapper::toResponse);
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
                .map(userMapper::toResponse)
                .flatMap(this::toResponseWithPhoto);
    }
}