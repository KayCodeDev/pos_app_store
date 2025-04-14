package com.kaydev.appstore.models.dto.objects;

import java.time.LocalDateTime;

import com.kaydev.appstore.models.entities.TerminalInfo;
import com.kaydev.appstore.models.enums.OsType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TerminalInfoObj {
    private Long id;
    private String uuid;
    private Long terminalId;
    private String longitude;
    private String latitude;
    private String serialNumber;
    private String deviceId;
    private String batteryLevel;
    private String manufacturer;
    private String model;
    private OsType osType;
    private String osVersion;
    private String sdkVersion;
    private String ram;
    private String rom;
    private String firmware;
    private String printer;
    private String batteryTemp;
    private String batteryStatus;
    private String networkType;
    private String simOperator;
    private String simNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TerminalInfoObj(TerminalInfo terminalInfo) {
        this.id = terminalInfo.getId();
        this.uuid = terminalInfo.getUuid();
        this.terminalId = terminalInfo.getTerminal().getId();
        this.longitude = terminalInfo.getLongitude();
        this.latitude = terminalInfo.getLatitude();
        this.serialNumber = terminalInfo.getSerialNumber();
        this.deviceId = terminalInfo.getDeviceId();
        this.batteryLevel = terminalInfo.getBatteryLevel();
        this.manufacturer = terminalInfo.getManufacturer();
        this.model = terminalInfo.getModel();
        this.osType = terminalInfo.getOsType();
        this.osVersion = terminalInfo.getOsVersion();
        this.sdkVersion = terminalInfo.getSdkVersion();
        this.ram = terminalInfo.getRam();
        this.rom = terminalInfo.getRom();
        this.firmware = terminalInfo.getFirmware();
        this.printer = terminalInfo.getPrinter();
        this.batteryTemp = terminalInfo.getBatteryTemp();
        this.batteryStatus = terminalInfo.getBatteryStatus();
        if (terminalInfo.getNetworkType() != null) {
            if (terminalInfo.getNetworkType().contains("mobile")) {
                String[] networkInfo = terminalInfo.getNetworkType().split("\\|\\|");
                this.networkType = networkInfo[0];
                this.simOperator = networkInfo.length > 1 ? networkInfo[1] : null;
                this.simNumber = networkInfo.length > 2 ? networkInfo[2] : null;
            } else {
                this.networkType = terminalInfo.getNetworkType();
            }
        } else {
            this.networkType = "N/A";
        }
        this.createdAt = terminalInfo.getCreatedAt();
        this.updatedAt = terminalInfo.getUpdatedAt();
    }
}
