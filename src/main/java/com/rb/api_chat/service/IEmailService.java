package com.rb.api_chat.service;

import reactor.core.publisher.Mono;

public interface IEmailService {
    Mono<Void> sendVerificationEmail(String to, String code);
}
