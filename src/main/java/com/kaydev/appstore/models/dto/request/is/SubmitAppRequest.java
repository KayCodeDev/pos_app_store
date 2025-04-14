package com.kaydev.appstore.models.dto.request.is;

import java.io.Serializable;
import java.util.List;

import com.kaydev.appstore.models.dto.utils.AppFileInfo;
import com.kaydev.appstore.models.enums.AppType;
import com.kaydev.appstore.models.enums.OsType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SubmitAppRequest implements Serializable {
    @NotNull(message = "App info is required")
    private AppFileInfo appInfo;
    @NotNull(message = "OS type is required")
    private OsType osType;
    private AppType appType = AppType.EXTERNAL;
    @NotNull(message = "Manufacturer is required")
    @Positive(message = "Manufacturer is required")
    private Long manufacturerId;
    @NotNull(message = "Category is required")
    @Positive(message = "Category is required")
    private Long categoryId;
    @NotNull(message = "Terminal model is required")
    private List<Long> modelIds;
    @NotNull(message = "At least one screen shoot is required")
    private List<String> screenShoots;
    private Long distributorId;
    @NotBlank(message = "Description is required")
    @NotNull(message = "Description is required")
    private String description;

}
