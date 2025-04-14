package com.kaydev.appstore.models.dto.objects.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ManufacturerModelSum {
    private String modelName;
    private Long terminalCount;
}
