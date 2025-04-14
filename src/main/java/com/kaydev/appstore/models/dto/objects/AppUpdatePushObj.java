package com.kaydev.appstore.models.dto.objects;

import com.kaydev.appstore.models.entities.App;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AppUpdatePushObj {
    private Long id;
    private String uuid;
    private String name;
    private String packageName;

    public AppUpdatePushObj(App app) {
        this.id = app.getId();
        this.uuid = app.getUuid();
        this.name = app.getName();
        this.packageName = app.getPackageName().split("\\|\\|")[0];
    }
}
