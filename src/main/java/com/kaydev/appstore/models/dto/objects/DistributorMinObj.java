package com.kaydev.appstore.models.dto.objects;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kaydev.appstore.models.entities.Distributor;
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
public class DistributorMinObj {
    private Long id;
    private String uuid;
    private String distributorName;
    private String contactName;
    private String contactEmail;
    private CountryObj country;
    private StatusType status;
    private Long developerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public DistributorMinObj(Distributor distributor) {
        this.id = distributor.getId();
        this.uuid = distributor.getUuid();
        this.distributorName = distributor.getDistributorName();
        this.contactName = distributor.getContactName();
        this.contactEmail = distributor.getContactEmail();
        this.country = new CountryObj(distributor.getCountry());
        this.status = distributor.getStatus();
        this.developerId = distributor.getDeveloper().getId();
        this.createdAt = distributor.getCreatedAt();
        this.updatedAt = distributor.getUpdatedAt();
    }

    public DistributorMinObj(Distributor distributor, boolean min) {
        this.id = distributor.getId();
        this.uuid = distributor.getUuid();
        this.distributorName = distributor.getDistributorName();
        this.status = distributor.getStatus();
    }

}
