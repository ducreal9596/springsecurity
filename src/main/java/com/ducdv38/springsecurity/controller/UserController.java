package com.ducdv38.springsecurity.controller;

import com.ducdv38.springsecurity.dto.request.UserCreationRequest;
import com.ducdv38.springsecurity.dto.request.UserUpdateRequest;
import com.ducdv38.springsecurity.dto.response.ApiResponse;
import com.ducdv38.springsecurity.dto.response.UserResponse;
import com.ducdv38.springsecurity.entity.User;
import com.ducdv38.springsecurity.mapper.UserMapper;
import com.ducdv38.springsecurity.service.impl.UserServiceIpml;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/user")
public class UserController {
    
    UserServiceIpml userService;

    @PostMapping("/post-user")
    public ApiResponse<User> createUser(@RequestBody @Valid UserCreationRequest request) {
        log.info("Creating user concat: {}", request);
        ApiResponse resp = new ApiResponse();
        resp.setResult(userService.creeateUser(request));
        return resp;
    }

    @GetMapping("/get-users")
    public ApiResponse<List<UserResponse>> getAllUsers() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("Username: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority ->
                log.info("GrantedAuthority: {}", grantedAuthority.getAuthority()));

        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getAllUsers())
                .build();
    }

    @GetMapping("/get-user/{username}")
    public ApiResponse<UserResponse> getUser(@PathVariable String username) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUserByUserName(username))
                .build();
    }

    @GetMapping("/get-user/myInfo")
    public ApiResponse<UserResponse> getMyinfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @PatchMapping("/update/{userName}")
    public ApiResponse<UserResponse> updateUser(@PathVariable String userName, @RequestBody UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(userName, request))
                .build();
    }

    @DeleteMapping("/delete/{userName}")
    public ApiResponse<String> deleteUser(@PathVariable String userName) {
        userService.deleteUser(userName);
        return ApiResponse.<String>builder()
                .result("User deleted: " + userName)
                .build();
    }

}
