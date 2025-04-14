package com.kaydev.appstore.models.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ResetPasswordRequest {
    @NotNull(message = "User ID is required")
    private Long userId;
    @NotBlank(message = "Verification code is required")
    @NotNull(message = "Verification code is required")
    private String otp;
    @NotBlank(message = "Password is required")
    @NotNull(message = "Password is required")
    private String password;
}
