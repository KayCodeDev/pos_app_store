package com.kaydev.appstore.models.dto.request.is;

import java.io.Serializable;

import com.kaydev.appstore.models.enums.StatusType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UpdateDeveloperStatusRequest implements Serializable {

    @NotNull(message = "Status is required")
    @NotBlank(message = "Status is required")

    private StatusType status;
    private int expiryPeriod;
}
