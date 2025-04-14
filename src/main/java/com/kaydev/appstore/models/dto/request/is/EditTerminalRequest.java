package com.kaydev.appstore.models.dto.request.is;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class EditTerminalRequest {
    @NotBlank(message = "Terminal identifier is required")
    @NotNull(message = "Terminal identifier is required")
    private String terminalUuid;
    private String serialNumber;
    private String deviceId;
    private Long manufacturerId;
    private Long modelId;
}
