package com.rb.api_chat.mapper;

import com.rb.api_chat.dto.request.UserRegisterRequest;
import com.rb.api_chat.dto.response.UserResponse;
import com.rb.api_chat.model.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping( target = "status", ignore = true)
    @Mapping( target = "contacts", ignore = true)
    UserEntity toEntity(UserRegisterRequest userRegisterRequest);

    @Mapping( target = "rolesName", ignore = true)
    UserResponse toResponse(UserEntity userEntity);
}
