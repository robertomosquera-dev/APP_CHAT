package com.rb.api_chat.util;

import com.rb.api_chat.dto.response.UserResponse;

import java.util.List;
import java.util.stream.Collectors;

public class UtilsFunctional {
    private UtilsFunctional() {}

    public static String getChatNameDefaultGroup(List<UserResponse> users) {
        return users.stream()
                .map(UserResponse::getFullName)
                .collect(Collectors.joining(", "));
    }

    public static String getChatNameDefaultPrivate(UserResponse user) {
        return user.username().isBlank()
                ? user.phoneNumber()
                : user.username();
    }
}
