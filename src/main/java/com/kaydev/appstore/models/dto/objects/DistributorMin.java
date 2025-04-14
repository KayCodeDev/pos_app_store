package com.kaydev.appstore.models.dto.objects;

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
public class DistributorMin {
    private Long id;
    private String uuid;
    private String distributorName;

    private StatusType status;
    private Long developerId;

    public DistributorMin(Distributor distributor) {
        this.id = distributor.getId();
        this.uuid = distributor.getUuid();
        this.distributorName = distributor.getDistributorName();
        this.status = distributor.getStatus();
        this.developerId = distributor.getDeveloper().getId();
    }

}
