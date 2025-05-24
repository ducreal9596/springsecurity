package com.ducdv38.springsecurity.service.impl;

import com.ducdv38.springsecurity.dto.request.UserCreationRequest;
import com.ducdv38.springsecurity.dto.request.UserUpdateRequest;
import com.ducdv38.springsecurity.dto.response.UserResponse;
import com.ducdv38.springsecurity.entity.Role;
import com.ducdv38.springsecurity.entity.User;
import com.ducdv38.springsecurity.exceptionhandle.AppException;
import com.ducdv38.springsecurity.exceptionhandle.ErrorCode;
import com.ducdv38.springsecurity.mapper.UserMapper;
import com.ducdv38.springsecurity.repository.RoleRepository;
import com.ducdv38.springsecurity.repository.UserRepository;
import com.ducdv38.springsecurity.service.IUserService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class UserServiceIpml implements IUserService {

    UserMapper userMapper;
    UserRepository userRepository;
    RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse creeateUser(UserCreationRequest userCreationRequest) {
        if (userRepository.existsByUsername(userCreationRequest.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        User user = userMapper.toUser(userCreationRequest);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        user.setRoles(Set.of(role));
        return userMapper.toUserResponse(userRepository.save(user));
    }

    public UserResponse getMyInfo(){
        var context = SecurityContextHolder.getContext();
        var name = context.getAuthentication().getName();
        User user = userRepository.findByUsername(name).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXIST));
        return userMapper.toUserResponse(user);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
//    @PreAuthorize("hasAuthority('CREATE_DATA')")
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toUserResponse).collect(Collectors.toList());
    }

    @Override
    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse getUserByUserName(String userName) {
        return userMapper.toUserResponse(userRepository.findByUsername(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST)));
    }


    @Override
    @PreAuthorize("hasRole('CREATE_DATA')")
    public void deleteUser(String userName) {
        if (userRepository.existsByUsername(userName)) {
            userRepository.deleteByUsername(userName);
        } else {
            throw new RuntimeException("User not found");
        }
    }


    @Override
//    @PreAuthorize("#username == authentication.name")
    public UserResponse updateUser(String userName, UserUpdateRequest request) {
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
        userMapper.updateUser(user, request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        var roles = roleRepository.findAllById(request.getRoles());
        user.setRoles(new HashSet<>(roles));
        return userMapper.toUserResponse(userRepository.save(user));
    }

}
