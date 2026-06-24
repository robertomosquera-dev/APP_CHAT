package com.rb.api_chat.service.impl;

import com.rb.api_chat.service.IGenerateCodeService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;

@Service
public class GenerateCodeServiceImpl implements IGenerateCodeService {

    @Override
    public Mono<Integer> generateCode() {
        return Mono.just(new SecureRandom().nextInt(900000) + 100000);
    }

}
