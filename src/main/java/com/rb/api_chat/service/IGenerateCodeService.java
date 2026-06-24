package com.rb.api_chat.service;

import reactor.core.publisher.Mono;

public interface IGenerateCodeService {
    Mono<Integer> generateCode();
}
