package com.ducdv38.springsecurity.mapper;

import com.ducdv38.springsecurity.dto.request.PermissionRequest;
import com.ducdv38.springsecurity.dto.response.PermissionResponse;
import com.ducdv38.springsecurity.entity.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PermissionMapper {

    Permission toPermission(PermissionRequest permissionRequest);
    PermissionResponse toPermissionResponse(Permission permission);

}
