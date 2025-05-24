package com.ducdv38.springsecurity.service;

import com.ducdv38.springsecurity.dto.request.PermissionRequest;
import com.ducdv38.springsecurity.dto.response.PermissionResponse;

import java.util.List;

public interface IPermissionService {
    PermissionResponse create(PermissionRequest request);
    List<PermissionResponse> getAllPermissions();
    void deletePermission(String name);
}
