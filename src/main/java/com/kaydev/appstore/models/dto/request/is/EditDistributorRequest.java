package com.kaydev.appstore.models.dto.request.is;

import java.io.Serializable;

import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class EditDistributorRequest implements Serializable {
    private String distributorName;
    private String contactName;
    @Email(message = "Invalid email address")
    private String contactEmail;
    private Long countryId;
}
