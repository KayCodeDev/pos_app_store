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
public class TerminalObj {
    private Long id;
    private String uuid;
    private String serialNumber;
    private String deviceId;
    private ManufacturerMin manufacturer;
    private ManufacturerModelMinObj model;
    private DeveloperMin developer;
    private DistributorMin distributor;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private TerminalInfoObj terminalInfo;
    private GroupMin group;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private boolean geofencingEnabled;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private TerminalGeoFenceObj terminalGeoFence;
    private StatusType status;
    private LocalDateTime lastHeartbeat;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserMinObj user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TerminalObj(Terminal terminal) {
        this.id = terminal.getId();
        this.uuid = terminal.getUuid();
        this.serialNumber = terminal.getSerialNumber();
        this.deviceId = terminal.getDeviceId();
        this.manufacturer = terminal.getManufacturer() == null ? null : new ManufacturerMin(terminal.getManufacturer());
        this.model = terminal.getModel() == null ? null : new ManufacturerModelMinObj(terminal.getModel());
        this.developer = terminal.getDeveloper() == null ? null : new DeveloperMin(terminal.getDeveloper());
        this.distributor = terminal.getDistributor() == null ? null : new DistributorMin(terminal.getDistributor());
        this.terminalInfo = terminal.getTerminalInfo() == null ? null : new TerminalInfoObj(terminal.getTerminalInfo());
        this.group = terminal.getGroup() == null ? null : new GroupMin(terminal.getGroup());
        this.geofencingEnabled = terminal.isGeofencingEnabled();
        this.terminalGeoFence = terminal.getTerminalGeoFence() == null ? null
                : new TerminalGeoFenceObj(terminal.getTerminalGeoFence());
        this.status = terminal.getStatus();
        this.lastHeartbeat = terminal.getLastHeartbeat();
        this.user = terminal.getUser() == null ? null : new UserMinObj(terminal.getUser());
        this.createdAt = terminal.getCreatedAt();
        this.updatedAt = terminal.getUpdatedAt();
    }

    public TerminalObj(Terminal terminal, boolean withInfo) {
        this.id = terminal.getId();
        this.uuid = terminal.getUuid();
        this.serialNumber = terminal.getSerialNumber();
        this.deviceId = terminal.getDeviceId();
        this.manufacturer = new ManufacturerMin(terminal.getManufacturer());
        this.model = new ManufacturerModelMinObj(terminal.getModel());
        this.developer = new DeveloperMin(terminal.getDeveloper());
        this.distributor = terminal.getDistributor() == null ? null
                : new DistributorMin(terminal.getDistributor());
        this.group = terminal.getGroup() == null ? null : new GroupMin(terminal.getGroup());
        if (withInfo) {
            this.geofencingEnabled = terminal.isGeofencingEnabled();
            this.terminalGeoFence = terminal.getTerminalGeoFence() == null ? null
                    : new TerminalGeoFenceObj(terminal.getTerminalGeoFence());
            this.terminalInfo = terminal.getTerminalInfo() == null ? null
                    : new TerminalInfoObj(terminal.getTerminalInfo());
            this.user = new UserMinObj(terminal.getUser());
        }
        this.status = terminal.getStatus();
        this.lastHeartbeat = terminal.getLastHeartbeat();

        this.createdAt = terminal.getCreatedAt();
        this.updatedAt = terminal.getUpdatedAt();
    }
}
