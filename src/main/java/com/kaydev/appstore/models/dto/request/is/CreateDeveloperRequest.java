package com.kaydev.appstore.models.dto.request.is;

import java.io.Serializable;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CreateDeveloperRequest implements Serializable {
    @NotBlank(message = "Organization name is required")
    @NotNull(message = "Organization name is required")
    private String organizationName;
    @NotNull(message = "Country is required")
    @Positive(message = "Country is required")
    private Long countryId;
    @NotBlank(message = "Website is required")
    @NotNull(message = "Website is required")
    private String websiteUrl;
    @NotBlank(message = "Support email is required")
    @NotNull(message = "Support email is required")
    @Email(message = "Invalid email address")
    private String supportEmail;
    @NotNull(message = "Expiry period is required")
    private Long expiryPeriod;

    @NotBlank(message = "Contact person is required")
    @NotNull(message = "Contact person is required")
    private String contactPerson;

    @NotBlank(message = "Contact email is required")
    @NotNull(message = "Contact email is required")
    @Email(message = "Invalid email address")
    private String contactEmail;

}
