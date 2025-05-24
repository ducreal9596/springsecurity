package com.ducdv38.springsecurity.mapper;

import com.ducdv38.springsecurity.dto.request.RoleRequest;
import com.ducdv38.springsecurity.dto.response.RoleResponse;
import com.ducdv38.springsecurity.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoleMapper {

    @Mapping(target = "permissions" , ignore = true)
    Role toRole(RoleRequest roleRequest);

    RoleResponse toRoleResponse(Role role);
}
