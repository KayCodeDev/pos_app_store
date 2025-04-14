package com.kaydev.appstore.models.dto.objects;

import java.time.LocalDateTime;

import com.kaydev.appstore.models.entities.TerminalLog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@AllArgsConstructor
@NoArgsConstructor
public class TerminalLogObj {
    private Long id;
    private String uuid;
    private Long terminalId;
    private String longitude;
    private String latitude;
    private String batteryLevel;
    private String ram;
    private String rom;
    private String firmware;
    private String batteryTemp;
    private String batteryStatus;
    private String networkType;
    private String simOperator;
    private String simNumber;
    private String ipAddress;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TerminalLogObj(TerminalLog terminalLog) {
        this.id = terminalLog.getId();
        this.uuid = terminalLog.getUuid();
        this.terminalId = terminalLog.getTerminal().getId();
        this.longitude = terminalLog.getLongitude();
        this.latitude = terminalLog.getLatitude();
        this.batteryLevel = terminalLog.getBatteryLevel();
        this.ram = terminalLog.getRam();
        this.rom = terminalLog.getRom();
        this.firmware = terminalLog.getFirmware();
        this.batteryTemp = terminalLog.getBatteryTemp();
        this.batteryStatus = terminalLog.getBatteryStatus();
        if (terminalLog.getNetworkType().contains("mobile")) {
            String[] networkInfo = terminalLog.getNetworkType().split("\\|\\|");
            this.networkType = networkInfo[0];
            this.simOperator = networkInfo.length > 1 ? networkInfo[1] : null;
            this.simNumber = networkInfo.length > 2 ? networkInfo[2] : null;
        } else {
            this.networkType = terminalLog.getNetworkType();
        }
        this.ipAddress = terminalLog.getIpAddress();
        this.createdAt = terminalLog.getCreatedAt();
        this.updatedAt = terminalLog.getUpdatedAt();
    }
}
