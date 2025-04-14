package com.kaydev.appstore.models.dto.request.is;

import java.io.Serializable;

import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class EditDeveloperRequest implements Serializable {
    private String organizationName;
    private Long countryId;
    private String websiteUrl;
    @Email(message = "Invalid email address")
    private String supportEmail;
}
