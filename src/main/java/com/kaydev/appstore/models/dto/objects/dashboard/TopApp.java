package com.kaydev.appstore.models.dto.objects.dashboard;

import com.kaydev.appstore.models.entities.App;
import com.kaydev.appstore.models.enums.OsType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TopApp {
    private Long id;
    private String uuid;
    private OsType osType;
    private String name;
    private String icon;
    private String developer;
    private String distributor;
    private String version;
    private String category;
    private int downloadCount = 0;

    public TopApp(App app) {
        this.id = app.getId();
        this.uuid = app.getUuid();
        this.osType = app.getOsType();
        this.name = app.getName();
        this.icon = app.getIcon();
        this.developer = app.getDeveloper() == null ? null : app.getDeveloper().getOrganizationName();
        this.distributor = app.getDistributor() == null ? null : app.getDistributor().getDistributorName();
        this.version = app.getVersion() == null ? null : app.getVersion().getVersion();
        this.category = app.getCategory().getName();
        this.downloadCount = app.getDownloadCount();
    }
}
