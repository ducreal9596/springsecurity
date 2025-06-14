package com.ducdv38.springsecurity.service.impl;

import com.ducdv38.springsecurity.dto.request.RoleRequest;
import com.ducdv38.springsecurity.dto.response.ApiResponse;
import com.ducdv38.springsecurity.dto.response.RoleResponse;
import com.ducdv38.springsecurity.entity.Role;
import com.ducdv38.springsecurity.mapper.RoleMapper;
import com.ducdv38.springsecurity.repository.PermissionRepository;
import com.ducdv38.springsecurity.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {

    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RoleMapper roleMapper;

    public RoleResponse createRole(RoleRequest roleRequest) {
        var role = roleMapper.toRole(roleRequest);
        var permission = permissionRepository.findAllById(roleRequest.getPermissions());
        role.setPermissions(new HashSet<>(permission));
        role = roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }

    public List<RoleResponse> getAllRoles() {
        // (STR) 2025-06-14 K23-840 DEV DucDV38 DEL Federation
// log.info("Token already expired or invalid");
// (END) 2025-06-14 K23-840 DEV DucDV38 DEL Federation
        return roleRepository.findAll().stream().map(roleMapper::toRoleResponse).toList();
    }

    public void deleteRole(String roleName) {
        // (STR) 2025-06-14 K23-840 DEV DucDV38 MOD PCML
        // roleRepository.deleteById(roleName);
        roleRepository.deleteById(roleName);
        // (END) 2025-06-14 K23-840 DEV DucDV38 MOD PCML
    }
}
