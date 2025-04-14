package com.kaydev.appstore.models.dto.objects.dashboard;

import java.sql.Date;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MonthlyDownload {
    private LocalDate date;
    private Long downloadCount;

    public MonthlyDownload(Date date, Long downloadCount) {
        this.date = LocalDate.parse(date.toString());
        this.downloadCount = downloadCount;
    }
}
