package com.kaydev.appstore.models.dto.request.is;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ChangePasswordRequest implements Serializable {
    @NotBlank(message = "Current password is required")
    @NotNull(message = "Current password is required")
    private String currentPassword;
    @NotBlank(message = "New password is required")
    @NotNull(message = "New password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String newPassword;
}
