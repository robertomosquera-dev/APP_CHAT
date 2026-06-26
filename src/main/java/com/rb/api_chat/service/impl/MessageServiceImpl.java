package com.rb.api_chat.service.impl;

import com.rb.api_chat.dto.request.MessageRequest;
import com.rb.api_chat.dto.response.GroupChatResponse;
import com.rb.api_chat.dto.response.MessageResponse;
import com.rb.api_chat.dto.response.PrivateChatResponse;
import com.rb.api_chat.mapper.MessageMapper;
import com.rb.api_chat.model.ChatType;
import com.rb.api_chat.model.MessageEntity;
import com.rb.api_chat.model.MessageStatus;
import com.rb.api_chat.model.MessageType;
import com.rb.api_chat.repository.MessageRepository;
import com.rb.api_chat.service.IChatService;
import com.rb.api_chat.service.IMessageService;
import com.rb.api_chat.service.IStorageService;
import com.rb.api_chat.service.IUserService;
import com.rb.api_chat.util.FileBuckets;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements IMessageService , FileBuckets {

    private final MessageRepository messageRepository;
    private final IStorageService storageService;
    private final IChatService chatService;
    private final MessageMapper mapper;

    @Transactional
    @Override
    public Mono<MessageResponse> sendMessage(FilePart file, ChatType chatType , MessageRequest messageRequest) {

        //Simula que extraemos desde el token por mientras
        UUID chatId = messageRequest.chatId();
        UUID senderId = messageRequest.senderId();
        String content = messageRequest.content();
        MessageType type = messageRequest.type();
        return chatService
                .findByIdAndType(chatId, chatType)
                .switchIfEmpty(
                        messageRequest.receiverId() != null
                                ? chatService.createChat(null, null, senderId, List.of(messageRequest.receiverId()), ChatType.PRIVATE)
                                : Mono.error(new RuntimeException("Debe proporcionar un receptor para crear el chat"))
                )
                .flatMap(chat -> switch (chat) {
                    case GroupChatResponse cg -> {
                        if (cg.usersId().stream().noneMatch(id -> id.equals(senderId))) {
                            yield Mono.error(new RuntimeException("No perteneces a este grupo"));
                        }
                        yield buildMessage(file, type, chat.chatId(), senderId, content).flatMap(messageRepository::save);
                    }
                    case PrivateChatResponse pc -> {
                        if (!pc.senderId().equals(senderId) && !pc.receiverId().equals(senderId)) {
                            yield Mono.error(new RuntimeException("No perteneces a este chat"));
                        }
                        yield buildMessage(file, type, chat.chatId(), senderId, content).flatMap(messageRepository::save);
                    }
                    default -> Mono.error(new IllegalStateException("Tipo de chat no soportado"));
                })
                .map(mapper::toMessageResponse);
    }

    //Se limitara el num de msj a 30
    //Sera desendente
    @Override
    public Flux<MessageResponse> findAllByChatId(UUID chatId, Integer page) {
        Pageable pageable = PageRequest.of(page, 30, Sort.by(Sort.Direction.DESC, "createdAt"));
        return messageRepository
                .findByChatId(chatId,pageable)
                .map(mapper::toMessageResponse);
    }


    private Mono<MessageEntity> buildMessage(FilePart file, MessageType messageType, UUID chatId, UUID senderId,String content){

        if(messageType.equals(MessageType.TEXT) || file == null){
            return Mono.just(
                    MessageEntity
                            .builder()
                            .chatId(chatId)
                            .senderId(senderId)
                            .type(messageType)
                            .content(content)
                            .build()
            );
        }

        return storageService.uploadFile(file,MESSAGES_PICTURES)
                .flatMap(storageService::getUrl)
                .map(url ->
                    MessageEntity
                            .builder()
                            .chatId(chatId)
                            .senderId(senderId)
                            .type(messageType)
                            .fileUrl(url)
                            .build()
                );

    }
}
