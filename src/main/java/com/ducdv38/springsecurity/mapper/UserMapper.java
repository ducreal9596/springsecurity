package com.ducdv38.springsecurity.mapper;

import com.ducdv38.springsecurity.dto.request.UserCreationRequest;
import com.ducdv38.springsecurity.dto.request.UserUpdateRequest;
import com.ducdv38.springsecurity.dto.response.UserResponse;
import com.ducdv38.springsecurity.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

// KHai báo để dùng trong framework spring => theo kiểu injection
@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    User toUser(UserCreationRequest userCreationRequest);
    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest userUpdateRequest);
    UserResponse toUserResponse(User user);
}
