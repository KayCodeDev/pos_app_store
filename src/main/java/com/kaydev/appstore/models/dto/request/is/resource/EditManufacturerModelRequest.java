package com.kaydev.appstore.models.dto.request.is.resource;

import com.kaydev.appstore.models.enums.OsType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class EditManufacturerModelRequest {
    @NotBlank(message = "Model name is required")
    @NotNull(message = "Model name is required")
    private String model;
    @NotBlank(message = "OS type is required")
    @NotNull(message = "OS type is required")
    private OsType osType;
}
