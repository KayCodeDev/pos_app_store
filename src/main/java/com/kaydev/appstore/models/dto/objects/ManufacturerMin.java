package com.kaydev.appstore.models.dto.objects;

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
public class ManufacturerMin {
    private Long id;
    private String manufacturerName;
    private StatusType status;

    public ManufacturerMin(Manufacturer manufacturer) {
        this.id = manufacturer.getId();
        this.manufacturerName = manufacturer.getManufacturerName();
        this.status = manufacturer.getStatus();

    }
}
