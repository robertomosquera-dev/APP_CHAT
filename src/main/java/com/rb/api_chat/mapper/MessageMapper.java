package com.rb.api_chat.mapper;

import com.rb.api_chat.dto.response.MessageResponse;
import com.rb.api_chat.model.MessageEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    MessageResponse toMessageResponse(MessageEntity messageEntity);
}
