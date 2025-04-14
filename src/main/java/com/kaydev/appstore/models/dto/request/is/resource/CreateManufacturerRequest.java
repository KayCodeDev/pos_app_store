package com.kaydev.appstore.models.dto.request.is.resource;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CreateManufacturerRequest {
    @NotBlank(message = "Manufactturer name is required")
    @NotNull(message = "Manufactturer name is required")
    private String manufacturerName;
}
