package com.kaydev.appstore.models.dto.objects;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kaydev.appstore.models.entities.Developer;
import com.kaydev.appstore.models.enums.StatusType;

import lombok.Data;
import lombok.ToString;

@Data
@ToString

public class DeveloperObj {
    private Long id;
    private String uuid;
    private String organizationName;
    private CountryObj country;
    private StatusType status;
    private String websiteUrl;
    private String supportEmail;
    private double remoteHours;
    private double exhaustedRemoteHours;
    private LocalDateTime expiryDate;
    private Long terminalCount;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private DeveloperSettingObj setting;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserMinObj user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public DeveloperObj(Developer developer) {
        this.id = developer.getId();
        this.uuid = developer.getUuid();
        this.organizationName = developer.getOrganizationName();
        this.country = new CountryObj(developer.getCountry());
        this.status = developer.getStatus();
        this.remoteHours = developer.getRemoteHours();
        this.exhaustedRemoteHours = developer.getExhaustedRemoteHours();
        this.websiteUrl = developer.getWebsiteUrl();
        this.supportEmail = developer.getSupportEmail();
        this.expiryDate = developer.getExpiryDate();
        this.setting = new DeveloperSettingObj(developer.getSetting());
        this.user = new UserMinObj(developer.getUser());
        this.createdAt = developer.getCreatedAt();
        this.updatedAt = developer.getUpdatedAt();
    }

    public DeveloperObj(Developer developer, boolean noUser) {
        this.id = developer.getId();
        this.uuid = developer.getUuid();
        this.organizationName = developer.getOrganizationName();
        this.country = new CountryObj(developer.getCountry());
        this.status = developer.getStatus();
        this.remoteHours = developer.getRemoteHours();
        this.exhaustedRemoteHours = developer.getExhaustedRemoteHours();
        this.websiteUrl = developer.getWebsiteUrl();
        this.supportEmail = developer.getSupportEmail();
        this.expiryDate = developer.getExpiryDate();
        this.createdAt = developer.getCreatedAt();
        this.updatedAt = developer.getUpdatedAt();
    }

    public DeveloperObj(Developer developer, Long terminalCount) {
        this.id = developer.getId();
        this.uuid = developer.getUuid();
        this.organizationName = developer.getOrganizationName();
        this.country = new CountryObj(developer.getCountry());
        this.status = developer.getStatus();
        this.remoteHours = developer.getRemoteHours();
        this.exhaustedRemoteHours = developer.getExhaustedRemoteHours();
        this.websiteUrl = developer.getWebsiteUrl();
        this.supportEmail = developer.getSupportEmail();
        this.terminalCount = terminalCount;
        this.expiryDate = developer.getExpiryDate();
        this.setting = new DeveloperSettingObj(developer.getSetting());
        this.user = new UserMinObj(developer.getUser());
        this.createdAt = developer.getCreatedAt();
        this.updatedAt = developer.getUpdatedAt();
    }
}
