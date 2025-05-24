package com.ducdv38.springsecurity.service.impl;

import com.ducdv38.springsecurity.dto.request.PermissionRequest;
import com.ducdv38.springsecurity.dto.response.PermissionResponse;
import com.ducdv38.springsecurity.entity.Permission;
import com.ducdv38.springsecurity.exceptionhandle.AppException;
import com.ducdv38.springsecurity.exceptionhandle.ErrorCode;
import com.ducdv38.springsecurity.mapper.PermissionMapper;
import com.ducdv38.springsecurity.repository.PermissionRepository;
import com.ducdv38.springsecurity.service.IPermissionService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PermissionService implements IPermissionService {

    PermissionRepository permissionRepository;
    PermissionMapper mapper;

    @Override
//    @PreAuthorize("hasRole('ADMIN')")
    public PermissionResponse create(PermissionRequest request) {
        Permission permission = mapper.toPermission(request);
        permission = permissionRepository.save(permission);
        return mapper.toPermissionResponse(permission);
    }

    @Override
    public List<PermissionResponse> getAllPermissions() {
        return permissionRepository.findAll().stream().map(mapper::toPermissionResponse).toList();
    }

    @Override
    public void deletePermission(String name) {
        permissionRepository.deleteById(name);
    }

}
