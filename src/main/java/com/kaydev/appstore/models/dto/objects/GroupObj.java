package com.kaydev.appstore.models.dto.objects;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
public class GroupObj {
    private Long id;
    private String uuid;
    private String groupName;
    private OsType osType;
    private DeveloperMinObj developer;
    private DistributorMinObj distributor;
    private ManufacturerMinObj manufacturer;
    private List<ManufacturerModelMinObj> models;
    private UserMinObj user;
    private StatusType status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public GroupObj(Group group) {
        this.id = group.getId();
        this.uuid = group.getUuid();
        this.groupName = group.getGroupName();
        this.osType = group.getOsType();
        this.developer = new DeveloperMinObj(group.getDeveloper());
        this.manufacturer = new ManufacturerMinObj(group.getManufacturer(), true);
        this.models = group.getModels().stream().map(ManufacturerModelMinObj::new).collect(Collectors.toList());
        this.distributor = new DistributorMinObj(group.getDistributor(), true);
        this.user = group.getUser() == null ? null : new UserMinObj(group.getUser());
        this.status = group.getStatus();
        this.createdAt = group.getCreatedAt();
        this.updatedAt = group.getUpdatedAt();
    }
}
