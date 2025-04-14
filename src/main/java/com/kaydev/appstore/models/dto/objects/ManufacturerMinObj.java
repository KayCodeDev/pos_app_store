package com.kaydev.appstore.models.dto.objects;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kaydev.appstore.models.entities.Manufacturer;
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
public class ManufacturerMinObj {
    private Long id;
    private String manufacturerName;
    private StatusType status;
    private List<ManufacturerModelMinObj> manufacturerModels;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ManufacturerMinObj(Manufacturer manufacturer) {
        this.id = manufacturer.getId();
        this.manufacturerName = manufacturer.getManufacturerName();
        this.status = manufacturer.getStatus();
        this.manufacturerModels = manufacturer.getManufacturerModels().stream()
                .map(data -> new ManufacturerModelMinObj(data))
                .collect(Collectors.toList());
        this.createdAt = manufacturer.getCreatedAt();
        this.updatedAt = manufacturer.getUpdatedAt();
    }

    public ManufacturerMinObj(Manufacturer manufacturer, boolean noModel) {
        this.id = manufacturer.getId();
        this.manufacturerName = manufacturer.getManufacturerName();
        this.status = manufacturer.getStatus();

    }
}
