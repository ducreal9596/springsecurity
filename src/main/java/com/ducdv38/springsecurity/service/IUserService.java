package com.ducdv38.springsecurity.service;

import com.ducdv38.springsecurity.dto.request.UserCreationRequest;
import com.ducdv38.springsecurity.dto.request.UserUpdateRequest;
import com.ducdv38.springsecurity.dto.response.UserResponse;

import java.util.List;

public interface IUserService {

    UserResponse creeateUser(UserCreationRequest userCreationRequest);

    List<UserResponse> getAllUsers();

    UserResponse getUserByUserName(String userName);

    void deleteUser(String id);

    UserResponse updateUser(String userName, UserUpdateRequest request);
}
