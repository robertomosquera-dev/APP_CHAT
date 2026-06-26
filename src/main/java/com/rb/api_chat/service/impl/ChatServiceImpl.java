package com.rb.api_chat.service.impl;

import com.rb.api_chat.dto.response.ChatResponse;
import com.rb.api_chat.dto.response.GroupChatResponse;
import com.rb.api_chat.dto.response.PrivateChatResponse;
import com.rb.api_chat.model.ChatEntity;
import com.rb.api_chat.model.ChatType;
import com.rb.api_chat.repository.ChatRepository;
import com.rb.api_chat.repository.UserRepository;
import com.rb.api_chat.service.IChatService;
import com.rb.api_chat.service.IUserService;
import com.rb.api_chat.util.UtilsFunctional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements IChatService {

    private final ChatRepository chatRepository;
    private final IUserService userService;

    @Override
    public Mono<ChatResponse> createChat(String chatName, UUID senderId, List<UUID> receiverIds, ChatType type) {

        if (type.equals(ChatType.PRIVATE) && receiverIds.size() != 1) {
            return Mono.error(new RuntimeException("Solo se puede crear un chat privado con un solo receptor"));
        }

        if (type.equals(ChatType.GROUP) && receiverIds.size() < 2) {
            return Mono.error(new RuntimeException("Se necesita al menos dos personas para crear un chat de grupo"));
        }

        return switch (type) {
            case PRIVATE -> createPrivateChat(senderId, receiverIds.getFirst())
                    .map(privateChatResponse -> (ChatResponse) privateChatResponse);
            case GROUP -> createGroupChat(chatName, senderId, receiverIds)
                    .map(groupChatResponse -> (ChatResponse) groupChatResponse);
            default -> Mono.error(new RuntimeException("Tipo de chat no soportado"));
        };
    }

    private Mono<PrivateChatResponse> createPrivateChat(UUID senderId, UUID receiverId){
        return userService
                .getUserById(senderId)
                .zipWith(userService.getUserById(receiverId))
                .flatMap(tuple -> {
                    var sender = tuple.getT1();
                    var receiver = tuple.getT2();
                    String chatName = UtilsFunctional.getChatNameDefaultPrivate(sender);
                    return chatRepository.save(
                            ChatEntity.builder()
                                    .type(ChatType.PRIVATE)
                                    .name(chatName)
                                    .usersId(List.of(sender.id(), receiver.id()))
                                    .adminsId(List.of())
                                    .createdBy(sender.id())
                                    .build()
                    );
                })
                .map(chatEntity ->
                        PrivateChatResponse.builder()
                                .chatId(chatEntity.getId())
                                .senderId(senderId)
                                .receiverId(receiverId)
                                .createdAt(chatEntity.getCreatedAt())
                                .build()
                );
    }

    private Mono<GroupChatResponse> createGroupChat(String chatName, UUID senderId, List<UUID> receiverIds) {
        return userService.getUserById(senderId)
                .zipWith(userService.allByIds(receiverIds).collectList())
                .flatMap(tuple -> {
                    var sender = tuple.getT1();
                    var receivers = tuple.getT2();

                    List<UUID> allUsersId = new ArrayList<>();
                    allUsersId.add(sender.id());
                    receivers.forEach(r -> allUsersId.add(r.id()));

                    String name = chatName.isBlank() ? UtilsFunctional.getChatNameDefaultGroup(receivers) : chatName;

                    return chatRepository.save(
                            ChatEntity.builder()
                                    .type(ChatType.GROUP)
                                    .name(name)
                                    .usersId(allUsersId)
                                    .adminsId(List.of(sender.id()))
                                    .createdBy(sender.id())
                                    .build()
                    );
                })
                .map(chatEntity ->
                        GroupChatResponse.builder()
                                .chatId(chatEntity.getId())
                                .name(chatEntity.getName())
                                .usersId(chatEntity.getUsersId())
                                .adminsId(chatEntity.getAdminsId())
                                .createdAt(chatEntity.getCreatedAt())
                                .build()
                );
    }

    public Mono<ChatResponse> findByIdAndType(UUID chatId, ChatType type) {
        return switch (type){
            case PRIVATE -> findByPrivateId(chatId).map(privateChatResponse -> (PrivateChatResponse) privateChatResponse);
            case GROUP -> findByGroupId(chatId).map(groupChatResponse -> (GroupChatResponse) groupChatResponse);
            default -> Mono.error(new RuntimeException("Tipo de chat no soportado"));
        };
    }

    private Mono<PrivateChatResponse> findByPrivateId(UUID chatId) {
        return chatRepository
                .findById(chatId)
                .map(chat -> {
                    return PrivateChatResponse
                            .builder()
                            .chatId(chat.getCreatedBy())
                            .senderId(chat.getCreatedBy())
                            .receiverId(chat.getUsersId().getFirst())
                            .createdAt(chat.getCreatedAt())
                            .build();
                });
    }

    private Mono<GroupChatResponse> findByGroupId(UUID chatId) {
        return chatRepository
                .findById(chatId)
                .map(chat -> {
                    return GroupChatResponse
                            .builder()
                            .chatId(chat.getId())
                            .name(chat.getName())
                            .usersId(chat.getUsersId())
                            .adminsId(chat.getAdminsId())
                            .createdAt(chat.getCreatedAt())
                            .build();
                });
    }
}
