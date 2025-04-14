package com.kaydev.appstore.models.dto.objects.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ManufacturerTerminalSum {
    private String manufacturer;
    private Long terminalCount;
}
