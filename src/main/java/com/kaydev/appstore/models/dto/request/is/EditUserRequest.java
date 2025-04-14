package com.kaydev.appstore.models.dto.request.is;

import java.io.Serializable;

import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class EditUserRequest implements Serializable {
    private String fullName;
    @Email(message = "Invalid email address")
    private String email;
    private String role;
}
