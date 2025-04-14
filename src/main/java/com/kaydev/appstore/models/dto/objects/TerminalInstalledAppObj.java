package com.kaydev.appstore.models.dto.objects;

import java.time.LocalDateTime;

import com.kaydev.appstore.models.entities.TerminalInstalledApp;
import com.kaydev.appstore.models.enums.AppType;
import com.kaydev.appstore.models.enums.OsType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@AllArgsConstructor
@NoArgsConstructor
public class TerminalInstalledAppObj {
    private Long id;
    private String uuid;
    private Long terminalId;
    private String appName;
    private String packageName;
    private String version;
    private String versionCode;
    private OsType osType;
    private String icon;
    private AppType appType;
    private LocalDateTime installedAt;
    private String builtWith;
    private AppListObj app;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TerminalInstalledAppObj(TerminalInstalledApp terminalInstalledApp) {
        this.id = terminalInstalledApp.getId();
        this.uuid = terminalInstalledApp.getUuid();
        this.terminalId = terminalInstalledApp.getTerminal().getId();
        this.appName = terminalInstalledApp.getAppName();
        this.packageName = terminalInstalledApp.getPackageName();
        this.version = terminalInstalledApp.getVersion();
        this.versionCode = terminalInstalledApp.getVersionCode();
        this.osType = terminalInstalledApp.getOsType();
        this.icon = terminalInstalledApp.getIcon();
        this.appType = terminalInstalledApp.getAppType();
        this.builtWith = terminalInstalledApp.getBuiltWith();
        this.app = terminalInstalledApp.getApp() == null ? null : new AppListObj(terminalInstalledApp.getApp());
        this.createdAt = terminalInstalledApp.getCreatedAt();
        this.updatedAt = terminalInstalledApp.getUpdatedAt();
    }

}
