package com.kaydev.appstore.models.dto.objects.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AppVersionDownload {

    private String version;
    private int downloadCount;
}
