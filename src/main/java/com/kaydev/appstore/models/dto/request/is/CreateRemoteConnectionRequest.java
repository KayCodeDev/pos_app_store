package com.kaydev.appstore.models.dto.request.is;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CreateRemoteConnectionRequest {
    @NotNull(message = "Terminal is required")
    @Positive(message = "Terminal is required")
    private Long terminalId;
}
