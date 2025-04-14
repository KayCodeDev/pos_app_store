package com.kaydev.appstore.models.dto.objects.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountSummary {
    private int totalTerminals = 0;
    private int activeTerminals = 0;
    private int offlineTerminals = 0;
    private int unsyncedTerminals = 0;
    private int totalApps = 0;
    private int geoOfflineTerminals = 0;
}
