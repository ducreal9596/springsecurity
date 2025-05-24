package com.ducdv38.springsecurity.controller;

import com.ducdv38.springsecurity.dto.request.PermissionRequest;
import com.ducdv38.springsecurity.dto.request.RoleRequest;
import com.ducdv38.springsecurity.dto.response.ApiResponse;
import com.ducdv38.springsecurity.dto.response.RoleResponse;
import com.ducdv38.springsecurity.service.impl.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/role")
public class RoleController {

    RoleService roleService;

    @PostMapping("/create")
    public ApiResponse<RoleResponse> createPermission(@RequestBody RoleRequest roleRequest) {
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.createRole(roleRequest))
                .build();
    }

    @GetMapping("/get-roles")
    public ApiResponse<List<RoleResponse>> getRoles() {
        return ApiResponse.<List<RoleResponse>>builder()
                .result(roleService.getAllRoles())
                .build();
    }

    @DeleteMapping("/delete/{name}")
    public ApiResponse<String> deletePermission(@PathVariable String name) {
        roleService.deleteRole(name);
        return ApiResponse.<String>builder()
                .result("Role deleted: " + name)
                .build();
    }
}
