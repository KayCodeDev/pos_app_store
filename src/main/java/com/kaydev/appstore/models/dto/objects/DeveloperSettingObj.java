package com.kaydev.appstore.models.dto.objects;

import java.time.LocalDateTime;

import com.kaydev.appstore.models.entities.DeveloperSetting;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DeveloperSettingObj {
    private Long id;
    private Long developerId;
    private int maxDistributors;
    private int maxApps;
    private boolean canPush;
    private boolean canAddApp;
    private boolean canRemote;
    private boolean canAddDistributor;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public DeveloperSettingObj(DeveloperSetting developerSetting) {
        this.id = developerSetting.getId();
        this.developerId = developerSetting.getDeveloper().getId();
        this.maxDistributors = developerSetting.getMaxDistributors();
        this.maxApps = developerSetting.getMaxApps();
        this.canPush = developerSetting.isCanPush();
        this.canAddApp = developerSetting.isCanAddApp();
        this.canRemote = developerSetting.isCanRemote();
        this.canAddDistributor = developerSetting.isCanAddDistributor();
        this.createdAt = developerSetting.getCreatedAt();
        this.updatedAt = developerSetting.getUpdatedAt();
    }
}
