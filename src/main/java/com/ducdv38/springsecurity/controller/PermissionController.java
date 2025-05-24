package com.ducdv38.springsecurity.controller;

import com.ducdv38.springsecurity.dto.request.PermissionRequest;
import com.ducdv38.springsecurity.dto.response.ApiResponse;
import com.ducdv38.springsecurity.dto.response.PermissionResponse;
import com.ducdv38.springsecurity.service.impl.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/permission")
public class PermissionController {

    PermissionService permissionService;

    @PostMapping("/create")
    public ApiResponse<PermissionResponse> createPermission(@RequestBody PermissionRequest permissionRequest) {
        return ApiResponse.<PermissionResponse>builder()
                .result(permissionService.create(permissionRequest))
                .build();
    }

    @GetMapping("/get-permission")
    public ApiResponse<List<PermissionResponse>> getPermission() {
        return ApiResponse.<List<PermissionResponse>>builder()
                .result(permissionService.getAllPermissions())
                .build();
    }

    @DeleteMapping("/delete/{name}")
    public ApiResponse<String> deletePermission(@PathVariable String name) {
        permissionService.deletePermission(name);
        return ApiResponse.<String>builder()
                .result("Permission deleted: " + name )
                .build();
    }
}
