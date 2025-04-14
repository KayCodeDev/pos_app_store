package com.kaydev.appstore.models.dto.objects.export;

import java.time.LocalDateTime;

import com.kaydev.appstore.models.entities.Terminal;
import com.kaydev.appstore.models.enums.OsType;
import com.kaydev.appstore.models.enums.StatusType;

import lombok.Data;

@Data
public class TerminalObjExp {
    private String serialNumber;
    private String deviceId;
    private OsType osType;
    private String manufacturer;
    private String model;
    private String developer;
    private String distributor;
    private String group;
    private StatusType status;
    private LocalDateTime lastHeartbeat;
    private String batteryLevel;
    private String batteryStatus;
    private String printer;
    private String firmware;
    private String networkType;
    private LocalDateTime createdAt;

    public TerminalObjExp(Terminal t) {
        this.serialNumber = t.getSerialNumber();
        this.deviceId = t.getDeviceId();
        this.manufacturer = t.getManufacturer().getManufacturerName();
        this.model = t.getModel().getModelName();
        this.developer = t.getDeveloper().getOrganizationName();
        this.distributor = t.getDistributor() == null ? null : t.getDistributor().getDistributorName();
        this.group = t.getGroup() == null ? null : t.getGroup().getGroupName();
        this.status = t.getStatus();
        this.lastHeartbeat = t.getLastHeartbeat();
        this.batteryLevel = t.getTerminalInfo() == null ? null : t.getTerminalInfo().getBatteryLevel();
        this.osType = t.getTerminalInfo() == null ? null : t.getTerminalInfo().getOsType();
        this.batteryStatus = t.getTerminalInfo() == null ? null : t.getTerminalInfo().getBatteryLevel();
        this.printer = t.getTerminalInfo() == null ? null : t.getTerminalInfo().getPrinter();
        this.firmware = t.getTerminalInfo() == null ? null : t.getTerminalInfo().getFirmware();
        this.networkType = t.getTerminalInfo() == null ? null : t.getTerminalInfo().getNetworkType();
        this.createdAt = t.getCreatedAt();
    }
}
