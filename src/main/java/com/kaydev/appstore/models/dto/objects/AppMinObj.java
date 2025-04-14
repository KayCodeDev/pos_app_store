package com.kaydev.appstore.models.dto.objects;

import org.joda.time.LocalDateTime;

import com.kaydev.appstore.models.entities.App;
import com.kaydev.appstore.models.enums.AppType;
import com.kaydev.appstore.models.enums.OsType;
import com.kaydev.appstore.models.enums.StatusType;

import java.util.List;
// import java.util.stream.Collectors;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AppMinObj {
    private Long id;
    private String uuid;
    private StatusType status;
    private OsType osType;
    private String name;
    private String packageName;
    private String icon;
    private DeveloperMinObj developer;
    private DistributorMinObj distributor;
    private String description;
    private List<AppScreenShotObj> screenShots;
    private List<AppVersionMinObj> versions;
    private AppVersionMinObj version;
    private CategoryMinObj category;
    private AppType appType;
    private int downloadCount = 0;
    private List<String> permissions;
    private String targetSdk;
    private String compileSdk;
    private String minSdk;
    private String maxSdk;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AppMinObj(App app) {
        this.id = app.getId();
        this.uuid = app.getUuid();
        this.status = app.getStatus();
        this.osType = app.getOsType();
        this.name = app.getName();
        this.packageName = app.getPackageName().split("\\|\\|")[0];
        this.icon = app.getIcon();
        this.developer = app.getDeveloper() == null ? null : new DeveloperMinObj(app.getDeveloper());
        this.distributor = app.getDistributor() == null ? null : new DistributorMinObj(app.getDistributor());
        this.description = app.getDescription();
        this.version = app.getVersion() == null ? null : new AppVersionMinObj(app.getVersion());
        this.screenShots = app.getScreenShots().stream()
                .map(data -> new AppScreenShotObj(data))
                .collect(Collectors.toList());
        this.versions = app.getVersions().stream()
                .map(data -> new AppVersionMinObj(data))
                .collect(Collectors.toList()).reversed();
        this.category = new CategoryMinObj(app.getCategory());
        this.appType = app.getAppType();
        this.downloadCount = app.getDownloadCount();
        this.permissions = app.getPermissions();
        this.targetSdk = app.getTargetSdk();
        this.compileSdk = app.getCompileSdk();
        this.minSdk = app.getMinSdk();
        this.maxSdk = app.getMaxSdk();

    }
}
