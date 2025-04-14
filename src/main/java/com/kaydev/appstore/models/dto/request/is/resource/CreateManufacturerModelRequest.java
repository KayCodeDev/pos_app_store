package com.kaydev.appstore.models.dto.request.is.resource;

import java.util.List;

import com.kaydev.appstore.models.enums.OsType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CreateManufacturerModelRequest {
    @NotNull(message = "Manufacturer is required")
    @Positive(message = "Manufacturer is required")
    private Long manufacturerId;
    @NotNull(message = "Models are required")
    private List<String> models;
    @NotBlank(message = "OS type is required")
    @NotNull(message = "OS type is required")
    private OsType osType;
}
