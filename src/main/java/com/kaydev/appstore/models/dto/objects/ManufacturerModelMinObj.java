package com.kaydev.appstore.models.dto.objects;

import com.kaydev.appstore.models.entities.ManufacturerModel;
import com.kaydev.appstore.models.enums.OsType;
import com.kaydev.appstore.models.enums.StatusType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@AllArgsConstructor
@NoArgsConstructor
public class ManufacturerModelMinObj {
    private Long id;
    private String modelName;
    private OsType osType;
    private Long manufacturerId;
    private StatusType status;
    // private LocalDateTime createdAt;
    // private LocalDateTime updatedAt;

    public ManufacturerModelMinObj(ManufacturerModel manufacturerModel) {
        this.id = manufacturerModel.getId();
        this.modelName = manufacturerModel.getModelName();
        this.osType = manufacturerModel.getOsType();
        this.manufacturerId = manufacturerModel.getManufacturer().getId();
        this.status = manufacturerModel.getStatus();
        // this.createdAt = manufacturerModel.getCreatedAt();
        // this.updatedAt = manufacturerModel.getUpdatedAt();
    }
}
