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
public class DeveloperMin {
    private Long id;
    private String uuid;
    private String organizationName;
    private StatusType status;

    public DeveloperMin(Developer developer) {
        this.id = developer.getId();
        this.uuid = developer.getUuid();
        this.organizationName = developer.getOrganizationName();
        this.status = developer.getStatus();
    }

}
