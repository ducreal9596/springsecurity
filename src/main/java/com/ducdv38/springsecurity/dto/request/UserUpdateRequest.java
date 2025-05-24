package com.ducdv38.springsecurity.dto.request;

import com.ducdv38.springsecurity.validator.BirthDayConstraint;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    String password;
    String fistName;
    String lastName;
    @BirthDayConstraint(min = 18,message="DOB_INVALID")
    LocalDate birthDate;
    List<String> roles;
}
