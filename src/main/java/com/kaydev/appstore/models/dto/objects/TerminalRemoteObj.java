package com.kaydev.appstore.models.dto.objects;

import com.kaydev.appstore.models.entities.Terminal;
import com.kaydev.appstore.models.enums.StatusType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@AllArgsConstructor
@NoArgsConstructor
public class TerminalRemoteObj {
    private Long id;
    private String uuid;
    private String serialNumber;
    private String deviceId;
    private StatusType status;
    private ManufacturerMinObj manufacturer;
    private ManufacturerModelMinObj model;

    public TerminalRemoteObj(Terminal terminal) {
        this.id = terminal.getId();
        this.uuid = terminal.getUuid();
        this.serialNumber = terminal.getSerialNumber();
        this.deviceId = terminal.getDeviceId();
        this.status = terminal.getStatus();
        this.manufacturer = terminal.getManufacturer() == null ? null
                : new ManufacturerMinObj(terminal.getManufacturer(), true);
        this.model = terminal.getModel() == null ? null : new ManufacturerModelMinObj(terminal.getModel());
    }
}
