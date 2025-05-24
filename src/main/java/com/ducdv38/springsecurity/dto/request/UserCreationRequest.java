package com.ducdv38.springsecurity.dto.request;

import com.ducdv38.springsecurity.validator.BirthDayConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @NotBlank(message = "USER_NAME_INVALID")
    String username;

    @Size(min = 6, max = 12, message = "PASSWORD_INVALID")
    String password;
    String fistName;
    String lastName;

    @BirthDayConstraint(min = 20,message="DOB_INVALID")
    LocalDate birthDate;
}
