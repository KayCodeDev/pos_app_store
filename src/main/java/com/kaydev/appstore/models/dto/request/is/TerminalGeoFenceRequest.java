package com.kaydev.appstore.models.dto.request.is;

import java.io.Serializable;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TerminalGeoFenceRequest implements Serializable {
    @NotNull(message = "Geo fence status is required")
    private Boolean enable;
    private double latitude = 0;
    private double longitude = 0;
    private double radius = 0;
    private String address;
}
