
package com.kaydev.appstore.models.dto.objects;

import java.time.LocalDateTime;

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
public class AppListObj {
    private Long id;
    private String uuid;
    private StatusType status;
    private OsType osType;
    private String name;
    private String packageName;
    private String icon;
    private DeveloperMin developer;
    private DistributorMin distributor;
    private AppVersionMinObj version;
    private ManufacturerMin manufacturer;
    private CategoryObj category;
    private AppType appType;
    private int downloadCount = 0;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AppListObj(App app) {
        this.id = app.getId();
        this.uuid = app.getUuid();
        this.status = app.getStatus();
        this.osType = app.getOsType();
        this.name = app.getName();
        this.packageName = app.getPackageName().split("\\|\\|")[0];
        this.icon = app.getIcon();
        this.developer = app.getDeveloper() == null ? null : new DeveloperMin(app.getDeveloper());
        this.distributor = app.getDistributor() == null ? null : new DistributorMin(app.getDistributor());
        this.version = app.getVersion() == null ? null : new AppVersionMinObj(app.getVersion(), true);
        this.manufacturer = new ManufacturerMin(app.getManufacturer());
        this.category = new CategoryObj(app.getCategory(), true);
        this.appType = app.getAppType();
        this.downloadCount = app.getDownloadCount();
        this.createdAt = app.getCreatedAt();
        this.updatedAt = app.getUpdatedAt();
    }
}
