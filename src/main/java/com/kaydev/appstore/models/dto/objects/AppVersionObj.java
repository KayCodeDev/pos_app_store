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
public class AppVersionObj {
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
    private UserMinObj user;
    private String iconUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AppVersionObj(AppVersion appVersion) {
        this.id = appVersion.getId();
        this.uuid = appVersion.getUuid();
        this.status = appVersion.getStatus();
        this.version = appVersion.getVersion();
        this.versionCode = appVersion.getVersionCode();
        this.size = appVersion.getSize();
        this.app = new AppUpdateObj(appVersion.getApp());
        this.updateDescription = appVersion.getUpdateDescription();
        this.downloadCount = appVersion.getDownloadCount();
        this.user = appVersion.getUser() == null ? null : new UserMinObj(appVersion.getUser());
        this.iconUrl = appVersion.getApp().getIcon();
        this.downloadUrl = appVersion.getDownloadUrl();
        this.createdAt = appVersion.getCreatedAt();
        this.updatedAt = appVersion.getUpdatedAt();
    }

    public AppVersionObj(AppVersion appVersion, boolean withApp) {
        this.id = appVersion.getId();
        this.uuid = appVersion.getUuid();
        this.status = appVersion.getStatus();
        this.version = appVersion.getVersion();
        this.versionCode = appVersion.getVersionCode();
        this.app = new AppUpdateObj(appVersion.getApp());
        this.size = appVersion.getSize();
        this.updateDescription = appVersion.getUpdateDescription();
        this.downloadCount = appVersion.getDownloadCount();
        this.downloadUrl = appVersion.getDownloadUrl();
        // this.createdAt = appVersion.getCreatedAt();
        // this.updatedAt = appVersion.getUpdatedAt();
    }

    public AppVersionObj(AppVersion appVersion, String action) {
        this.id = appVersion.getId();
        this.uuid = appVersion.getUuid();
        this.status = appVersion.getStatus();
        this.version = appVersion.getVersion();
        this.versionCode = appVersion.getVersionCode();
        this.size = appVersion.getSize();
        this.app = new AppUpdateObj(appVersion.getApp());
        this.updateDescription = appVersion.getUpdateDescription();
        this.downloadCount = appVersion.getDownloadCount();
        this.iconUrl = appVersion.getApp().getIcon();
        this.createdAt = appVersion.getCreatedAt();
        this.updatedAt = appVersion.getUpdatedAt();
    }
}
