package com.kaydev.appstore.models.dto.objects;

import java.time.LocalDateTime;

import com.kaydev.appstore.models.entities.ManufacturerModel;
import com.kaydev.appstore.models.enums.OsType;
import com.kaydev.appstore.models.enums.StatusType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@AllArgsConstructor
@NoArgsConstructor
public class ManufacturerModelObj {
    private Long id;
    private String modelName;
    private OsType osType;
    private Long manufacturerId;
    private UserMinObj user;
    private StatusType status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ManufacturerModelObj(ManufacturerModel manufacturerModel) {
        this.id = manufacturerModel.getId();
        this.modelName = manufacturerModel.getModelName();
        this.osType = manufacturerModel.getOsType();
        this.manufacturerId = manufacturerModel.getManufacturer().getId();
        this.user = new UserMinObj(manufacturerModel.getUser());
        this.status = manufacturerModel.getStatus();
        this.createdAt = manufacturerModel.getCreatedAt();
        this.updatedAt = manufacturerModel.getUpdatedAt();
    }

}
