package com.kaydev.appstore.models.dto.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data

public class InstalledApp {
    String name;
    @JsonProperty("package_name")
    String packageName;
    @JsonProperty("version_name")
    String versionName;
    @JsonProperty("version_code")
    int versionCode;
    @JsonProperty("built_with")
    String builtWith;
    Boolean isSystemApp;
    @JsonProperty("installed_timestamp")
    Long installedTimestamp;
}
