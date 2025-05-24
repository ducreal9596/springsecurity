package com.ducdv38.springsecurity.dto.response;

import com.ducdv38.springsecurity.entity.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String id;
    String username;
    String fistName;
    String lastName;
    LocalDate birthDate;
    Set<Role> roles;
}
