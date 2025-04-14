package com.kaydev.appstore.models.dto.request.is;

import java.io.Serializable;

import com.kaydev.appstore.models.enums.UserType;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CreateUserRequest implements Serializable {
    @NotNull(message = "Full Name is required")
    @NotBlank(message = "Full Name is required")
    private String fullName;
    @NotNull(message = "Email is required")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email address")
    private String email;
    private Long developerId;
    @NotNull(message = "User type is required")
    @NotBlank(message = "User type is required")
    private UserType userType;
    @NotNull(message = "Role is required")
    @NotBlank(message = "Role is required")
    private String role;
}
