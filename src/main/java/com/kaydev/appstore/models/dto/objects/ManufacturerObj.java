package com.kaydev.appstore.models.dto.objects;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
public class ManufacturerObj {
    private Long id;
    private String manufacturerName;
    private StatusType status;
    private List<ManufacturerModelMinObj> manufacturerModels;
    private UserObj user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ManufacturerObj(Manufacturer manufacturer) {
        this.id = manufacturer.getId();
        this.manufacturerName = manufacturer.getManufacturerName();
        this.status = manufacturer.getStatus();
        this.manufacturerModels = manufacturer.getManufacturerModels().stream()
                .map(data -> new ManufacturerModelMinObj(data))
                .collect(Collectors.toList());
        this.user = new UserObj(manufacturer.getUser());
        this.createdAt = manufacturer.getCreatedAt();
        this.updatedAt = manufacturer.getUpdatedAt();
    }
}
