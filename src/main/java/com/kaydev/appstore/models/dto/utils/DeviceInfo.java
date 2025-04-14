package com.kaydev.appstore.models.dto.utils;

import com.kaydev.appstore.models.enums.OsType;

import lombok.Data;

@Data

public class DeviceInfo {
    String serialNumber;
    String batteryLevel;
    String imei;
    String manufacturer;
    String model;
    String sdkVersion;
    String networkType;
    OsType osType = OsType.ANDROID;
    String osVersion;
    String ram;
    String rom;
    String firmware;
    String printer;
    String ipAddress;
    String batteryTemp;
    String batteryStatus;
    String longitude;
    String latitude;
}
