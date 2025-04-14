package com.kaydev.appstore.models.dto.objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kaydev.appstore.models.entities.Group;
import com.kaydev.appstore.models.enums.OsType;
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
public class GroupMin {
    private Long id;
    private String uuid;
    private String groupName;
    private OsType osType;
    private StatusType status;

    public GroupMin(Group group) {
        this.id = group.getId();
        this.uuid = group.getUuid();
        this.groupName = group.getGroupName();
        this.osType = group.getOsType();
        this.status = group.getStatus();

    }
}
