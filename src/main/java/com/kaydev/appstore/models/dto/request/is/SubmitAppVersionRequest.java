package com.kaydev.appstore.models.dto.request.is;

import java.io.Serializable;

import com.kaydev.appstore.models.dto.utils.AppFileInfo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SubmitAppVersionRequest implements Serializable {
    @NotNull(message = "App info is required")
    private AppFileInfo appInfo;
    @NotNull(message = "App identifier is required")
    @NotBlank(message = "App identifier is required")
    private String appUuid;
    @NotBlank(message = "Description is required")
    @NotNull(message = "Description is required")
    private String description;
}
