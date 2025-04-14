package com.kaydev.appstore.models.dto.objects;

import com.kaydev.appstore.models.entities.App;
import com.kaydev.appstore.models.enums.AppType;
import com.kaydev.appstore.models.enums.OsType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AppUpdateObj {
    private Long id;
    private String uuid;
    private OsType osType;
    private String name;
    private String packageName;
    private AppType appType;
    private String icon;

    public AppUpdateObj(App app) {
        this.id = app.getId();
        this.uuid = app.getUuid();
        this.osType = app.getOsType();
        this.name = app.getName();
        this.packageName = app.getPackageName().split("\\|\\|")[0];
        this.appType = app.getAppType();
        this.icon = app.getIcon();
    }
}
