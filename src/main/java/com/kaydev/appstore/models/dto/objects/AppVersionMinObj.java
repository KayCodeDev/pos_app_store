package com.kaydev.appstore.models.dto.objects;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kaydev.appstore.models.entities.AppVersion;
import com.kaydev.appstore.models.enums.StatusType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class AppVersionMinObj {
    private Long id;
    private String uuid;
    private StatusType status;
    private AppUpdateObj app;
    private String version;
    private String versionCode;
    private String size;
    private String updateDescription;
    private int downloadCount;
    private String downloadUrl;
    private LocalDateTime createdAt;

    public AppVersionMinObj(AppVersion appVersion) {
        this.id = appVersion.getId();
        this.uuid = appVersion.getUuid();
        this.status = appVersion.getStatus();
        this.app = new AppUpdateObj(appVersion.getApp());
        this.version = appVersion.getVersion();
        this.versionCode = appVersion.getVersionCode();
        this.size = appVersion.getSize();
        this.updateDescription = appVersion.getUpdateDescription();
        this.downloadCount = appVersion.getDownloadCount();
        this.downloadUrl = appVersion.getDownloadUrl();
        this.createdAt = appVersion.getCreatedAt();
    }

    public AppVersionMinObj(AppVersion appVersion, boolean noApp) {
        this.id = appVersion.getId();
        this.uuid = appVersion.getUuid();
        this.status = appVersion.getStatus();
        this.version = appVersion.getVersion();
        this.versionCode = appVersion.getVersionCode();
        this.downloadCount = appVersion.getDownloadCount();
        this.size = appVersion.getSize();
        this.createdAt = appVersion.getCreatedAt();
    }
}
