package com.kaydev.appstore.models.dto.objects;

import java.time.LocalDateTime;

import com.kaydev.appstore.models.entities.TerminalGeoFence;
import com.kaydev.appstore.models.enums.StatusType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TerminalGeoFenceObj {
    private Long id;
    private String uuid;
    private String terminalUuid;
    private String longitude;
    private String latitude;
    private String address;
    private double radius;
    private StatusType status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TerminalGeoFenceObj(TerminalGeoFence terminalGeoFence) {
        this.id = terminalGeoFence.getId();
        this.uuid = terminalGeoFence.getUuid();
        this.terminalUuid = terminalGeoFence.getTerminal().getUuid();
        this.longitude = terminalGeoFence.getLongitude();
        this.latitude = terminalGeoFence.getLatitude();
        this.address = terminalGeoFence.getAddress();
        this.radius = terminalGeoFence.getRadius();
        this.status = terminalGeoFence.getStatus();
        this.createdAt = terminalGeoFence.getCreatedAt();
        this.updatedAt = terminalGeoFence.getUpdatedAt();
    }

}
