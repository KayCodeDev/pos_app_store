package com.kaydev.appstore.models.dto.objects;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
public class GroupMinObj {
    private Long id;
    private String uuid;
    private String groupName;
    private OsType osType;
    private StatusType status;
    private Long terminalCount;
    private ManufacturerMinObj manufacturer;
    private List<ManufacturerModelMinObj> models;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public GroupMinObj(Group group) {
        this.id = group.getId();
        this.uuid = group.getUuid();
        this.groupName = group.getGroupName();
        this.osType = group.getOsType();
        this.status = group.getStatus();
        this.createdAt = group.getCreatedAt();
        this.updatedAt = group.getUpdatedAt();
    }

    public GroupMinObj(Group group, Long terminalCount) {
        this.id = group.getId();
        this.uuid = group.getUuid();
        this.groupName = group.getGroupName();
        this.osType = group.getOsType();
        this.status = group.getStatus();
        this.terminalCount = terminalCount;
        this.manufacturer = new ManufacturerMinObj(group.getManufacturer(), true);
        this.models = group.getModels().stream().map(ManufacturerModelMinObj::new).collect(Collectors.toList());
        this.createdAt = group.getCreatedAt();
        this.updatedAt = group.getUpdatedAt();
    }
}
