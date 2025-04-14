package com.kaydev.appstore.models.dto.request.is;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CreateDistributorRequest {
    @NotBlank(message = "Distributor name is required")
    @NotNull(message = "Distributor name is required")
    private String distributorName;
    private Long developerId;
    @NotBlank(message = "Contact name is required")
    @NotNull(message = "Contact name is required")
    private String contactName;
    @NotBlank(message = "Contact email is required")
    @NotNull(message = "Contact email is required")
    @Email(message = "Invalid email address")
    private String contactEmail;

    @NotNull(message = "Country is required")
    @Positive(message = "Country is required")
    private Long countryId;
}
