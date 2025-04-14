package com.kaydev.appstore.models.dto.request.is;

import java.time.LocalDate;

import com.kaydev.appstore.models.enums.AppType;
import com.kaydev.appstore.models.enums.OsType;
import com.kaydev.appstore.models.enums.StatusType;
import com.kaydev.appstore.models.enums.SubServiceType;
import com.kaydev.appstore.models.enums.UserType;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SearchParams {
    private int page = 0;
    private int pageSize = 20;
    private String search;
    private LocalDate from;
    private LocalDate to;
    private StatusType status;
    private Long countryId;
    private OsType osType;
    private Long categoryId;
    private String connectionId;
    private UserType userType;
    private String role;
    private Long developerId;
    private String developerUuid;
    private Long distributorId;
    private String distributorUuid;
    private Long manufacturerId;
    private Long modelId;
    private Long userId;
    private Long terminalId;
    private Long groupId;
    private String groupUuid;
    private AppType appType;
    private SubServiceType subServiceType;
    private LocalDate createdDate;
    private boolean lowBattery;
    private boolean noPaper;
    private boolean notCharging;
}
