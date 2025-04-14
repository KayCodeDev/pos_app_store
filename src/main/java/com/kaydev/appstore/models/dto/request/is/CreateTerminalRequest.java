package com.kaydev.appstore.models.dto.request.is;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CreateTerminalRequest {
    @NotBlank(message = "Serial number is required")
    @NotNull(message = "Serial number is required")
    private String serialNumber;
    @NotBlank(message = "Device ID is required")
    @NotNull(message = "Device ID is required")
    private String deviceId;
    @NotNull(message = "Manufacturer is required")
    private Long manufacturerId;
    @NotNull(message = "Model is required")
    @Positive(message = "Model is required")
    private Long modelId;
    @NotNull(message = "Developer is required")
    @Positive(message = "Developer is required")
    private Long developerId;
    @NotNull(message = "Distributor is required")
    @Positive(message = "Distributor is required")
    private Long distributorId;

}
