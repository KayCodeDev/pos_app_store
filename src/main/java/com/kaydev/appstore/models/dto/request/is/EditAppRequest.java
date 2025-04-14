package com.kaydev.appstore.models.dto.request.is;

import java.io.Serializable;
import java.util.List;

import com.kaydev.appstore.models.enums.OsType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class EditAppRequest implements Serializable {
    @NotBlank(message = "App identifier is required")
    @NotNull(message = "App identifier is required")
    private String appUuid;
    private OsType osType;
    private Long categoryId;
    private List<Long> modelIds;
    private List<String> screenShoots;
    private Long distributorId;
    private String description;
}