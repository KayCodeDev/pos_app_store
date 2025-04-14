package com.kaydev.appstore.models.dto.request.is;

import java.io.Serializable;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AssignTerminalsRequest implements Serializable {
    @NotBlank(message = "Group is required")
    @NotNull(message = "Group is required")
    private String groupUuid;
    @NotBlank(message = "Distributor is required")
    @NotNull(message = "Distributor is required")
    private String distributorUuid;
    @NotNull(message = "At least one terminal is required")
    List<Long> terminals;
}
