package com.kaydev.appstore.models.dto.objects;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.kaydev.appstore.models.entities.App;
import com.kaydev.appstore.models.enums.AppType;
import com.kaydev.appstore.models.enums.OsType;
import com.kaydev.appstore.models.enums.StatusType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Data

@AllArgsConstructor
@NoArgsConstructor
public class AppObj {
    private Long id;
    private String uuid;
    private StatusType status;
    private OsType osType;
    private String name;
    private String packageName;
    private String icon;
    private DeveloperMinObj developer;
    private DistributorMinObj distributor;
    private UserMinObj user;
    private String description;
    private List<AppScreenShotObj> screenShots;
    private AppVersionMinObj version;
    private ManufacturerMinObj manufacturer;
    private List<ManufacturerModelMinObj> models;
    private CategoryObj category;
    private AppType appType;
    private int downloadCount = 0;
    private List<String> permissions;
    private String targetSdk;
    private String compileSdk;
    private String minSdk;
    private String maxSdk;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AppObj(App app) {
        this.id = app.getId();
        this.uuid = app.getUuid();
        this.status = app.getStatus();
        this.osType = app.getOsType();
        this.name = app.getName();
        this.packageName = app.getPackageName().split("\\|\\|")[0];
        this.icon = app.getIcon();
        this.developer = app.getDeveloper() == null ? null : new DeveloperMinObj(app.getDeveloper());
        this.distributor = app.getDistributor() == null ? null : new DistributorMinObj(app.getDistributor());
        this.user = new UserMinObj(app.getUser());
        this.description = app.getDescription();
        this.screenShots = app.getScreenShots().stream()
                .map(data -> new AppScreenShotObj(data))
                .collect(Collectors.toList());
        this.version = new AppVersionMinObj(app.getVersion(), true);
        this.manufacturer = new ManufacturerMinObj(app.getManufacturer(), true);
        this.models = app.getModels().stream()
                .map(data -> new ManufacturerModelMinObj(data))
                .collect(Collectors.toList());
        this.category = new CategoryObj(app.getCategory(), true);
        this.appType = app.getAppType();
        this.downloadCount = app.getDownloadCount();
        this.permissions = app.getPermissions();
        this.targetSdk = app.getTargetSdk();
        this.createdAt = app.getCreatedAt();
        this.updatedAt = app.getUpdatedAt();
    }
}
