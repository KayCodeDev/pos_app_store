package com.kaydev.appstore.models.dto.objects.dashboard;

import com.kaydev.appstore.models.enums.OsType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AppList {
    private Long id;
    private String uuid;
    private OsType osType;
    private String name;
}
