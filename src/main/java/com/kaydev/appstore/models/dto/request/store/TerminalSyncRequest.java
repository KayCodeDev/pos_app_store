package com.kaydev.appstore.models.dto.request.store;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kaydev.appstore.models.dto.utils.DeviceInfo;
import com.kaydev.appstore.models.dto.utils.InstalledApp;

import java.util.List;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class TerminalSyncRequest implements Serializable {

    @NotNull(message = "Terminal Device Info is required")
    private DeviceInfo deviceInfo;
    private List<InstalledApp> installedApp;

}
