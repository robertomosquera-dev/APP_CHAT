package com.rb.api_chat.service.impl;

import com.rb.api_chat.repository.ChatRepository;
import com.rb.api_chat.repository.UserRepository;
import com.rb.api_chat.service.IChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements IChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;



}
