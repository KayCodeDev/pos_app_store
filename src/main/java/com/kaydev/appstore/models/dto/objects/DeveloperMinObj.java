package com.kaydev.appstore.models.dto.objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kaydev.appstore.models.entities.Developer;
import com.kaydev.appstore.models.enums.StatusType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeveloperMinObj {
    private Long id;
    private String uuid;
    private String organizationName;
    private double remoteHours;
    private double exhaustedRemoteHours;
    private DeveloperSettingObj setting;
    private CountryObj country;
    private StatusType status;
    private String websiteUrl;
    private String supportEmail;

    public DeveloperMinObj(Developer developer) {
        this.id = developer.getId();
        this.uuid = developer.getUuid();
        this.remoteHours = developer.getRemoteHours();
        this.exhaustedRemoteHours = developer.getExhaustedRemoteHours();
        this.organizationName = developer.getOrganizationName();
        this.country = new CountryObj(developer.getCountry());
        this.status = developer.getStatus();
        this.websiteUrl = developer.getWebsiteUrl();
        this.supportEmail = developer.getSupportEmail();
    }

    public DeveloperMinObj(Developer developer, boolean showSettings) {
        this.id = developer.getId();
        this.uuid = developer.getUuid();
        this.organizationName = developer.getOrganizationName();
        if (showSettings) {
            this.remoteHours = developer.getRemoteHours();
            this.exhaustedRemoteHours = developer.getExhaustedRemoteHours();
            this.setting = new DeveloperSettingObj(developer.getSetting());
            this.country = new CountryObj(developer.getCountry());
            this.status = developer.getStatus();
            this.websiteUrl = developer.getWebsiteUrl();
            this.supportEmail = developer.getSupportEmail();
        }
    }
}
