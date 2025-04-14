package com.kaydev.appstore.models.dto.utils;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppFileInfo {
    private String appName;
    private String packageName;
    private String versionName;
    private Long versionCode;
    private String size;
    private String file;
    private String icon;
    private List<String> permissions;
    private String targetSdk;
    private String compileSdk;
    private String minSdk;
    private String maxSdk;

}
