package com.kaydev.appstore.models.dto.objects;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kaydev.appstore.models.entities.Terminal;
import com.kaydev.appstore.models.enums.StatusType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@AllArgsConstructor
@NoArgsConstructor
public class TerminalMinObj {
    private Long id;
    private String uuid;
    private String serialNumber;
    private String deviceId;
    private ManufacturerMinObj manufacturer;
    private ManufacturerModelMinObj model;
    private Long developerId;
    private Long distributorId;
    private Long groupId;
    private boolean geofencingEnabled;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private TerminalInfoObj terminalInfo;
    private StatusType status;
    private StatusType geoStatus;
    private LocalDateTime lastHeartbeat;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TerminalMinObj(Terminal terminal) {
        this.id = terminal.getId();
        this.uuid = terminal.getUuid();
        this.serialNumber = terminal.getSerialNumber();
        this.deviceId = terminal.getDeviceId();
        this.manufacturer = new ManufacturerMinObj(terminal.getManufacturer(), true);
        this.model = new ManufacturerModelMinObj(terminal.getModel());
        this.developerId = terminal.getDeveloper().getId();
        this.distributorId = terminal.getDistributor().getId();
        this.groupId = terminal.getGroup() == null ? null : terminal.getGroup().getId();
        // this.terminalInfo = terminal.getTerminalInfo() == null ? null : new
        // TerminalInfoObj(terminal.getTerminalInfo());
        this.geofencingEnabled = terminal.isGeofencingEnabled();
        this.geoStatus = terminal.getTerminalGeoFence() == null ? null : terminal.getTerminalGeoFence().getStatus();
        this.status = terminal.getStatus();
        this.lastHeartbeat = terminal.getLastHeartbeat();
        this.createdAt = terminal.getCreatedAt();
        this.updatedAt = terminal.getUpdatedAt();
    }

    public TerminalMinObj(Terminal terminal, boolean withInfo) {
        this.id = terminal.getId();
        this.uuid = terminal.getUuid();
        this.serialNumber = terminal.getSerialNumber();
        this.deviceId = terminal.getDeviceId();
        this.manufacturer = new ManufacturerMinObj(terminal.getManufacturer(), true);
        this.model = new ManufacturerModelMinObj(terminal.getModel());
        this.developerId = terminal.getDeveloper().getId();
        this.distributorId = terminal.getDistributor().getId();
        this.groupId = terminal.getGroup() == null ? null : terminal.getGroup().getId();
        this.terminalInfo = terminal.getTerminalInfo() == null ? null : new TerminalInfoObj(terminal.getTerminalInfo());
        this.geofencingEnabled = terminal.isGeofencingEnabled();
        this.status = terminal.getStatus();
        this.lastHeartbeat = terminal.getLastHeartbeat();
        this.createdAt = terminal.getCreatedAt();
        this.updatedAt = terminal.getUpdatedAt();
    }
}
