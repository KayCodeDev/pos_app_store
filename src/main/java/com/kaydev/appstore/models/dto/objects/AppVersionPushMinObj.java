package com.kaydev.appstore.models.dto.objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kaydev.appstore.models.entities.AppVersion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class AppVersionPushMinObj {
    private Long id;
    private String uuid;
    private AppUpdatePushObj app;
    private String downloadUrl;
    private String version;
    private String versionCode;
    private String size;

    public AppVersionPushMinObj(AppVersion appVersion) {
        this.id = appVersion.getId();
        this.uuid = appVersion.getUuid();
        this.app = new AppUpdatePushObj(appVersion.getApp());
        this.downloadUrl = appVersion.getDownloadUrl();
        this.version = appVersion.getVersion();
        this.versionCode = appVersion.getVersionCode();
        this.size = appVersion.getSize();
    }
}
