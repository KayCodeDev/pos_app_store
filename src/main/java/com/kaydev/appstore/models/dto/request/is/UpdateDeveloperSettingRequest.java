package com.kaydev.appstore.models.dto.request.is;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UpdateDeveloperSettingRequest implements Serializable {
    private int maxDistributors;
    private int maxApps;
    private boolean canPush;
    private boolean canAddApp;
    private boolean canRemote;
    private boolean canAddDistributor;
}
