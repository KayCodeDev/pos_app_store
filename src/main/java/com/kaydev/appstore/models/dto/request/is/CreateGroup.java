package com.kaydev.appstore.models.dto.request.is;

import java.util.List;

import com.kaydev.appstore.models.enums.OsType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CreateGroup {
    @NotBlank(message = "Group name is required")
    @NotNull(message = "Group name is required")
    private String groupName;

    @NotNull(message = "OS type is required")
    private OsType osType;

    @NotNull(message = "Manufacturer is required")
    @Positive(message = "Manufcturer is required")
    private Long manufacturerId;

    @NotNull(message = "At least one model is required")
    private List<Long> modelIds;

    @NotNull(message = "Distributor is required")
    @Positive(message = "Disttributor is required")
    private Long distributorId;
}
