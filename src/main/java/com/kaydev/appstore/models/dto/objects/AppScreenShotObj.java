package com.kaydev.appstore.models.dto.objects;

import java.time.LocalDateTime;

import com.kaydev.appstore.models.entities.AppScreenShot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AppScreenShotObj {
    private Long id;
    private String uuid;
    private String imageUrl;
    private Long appId;
    // private String size;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AppScreenShotObj(AppScreenShot appScreenShot) {
        this.id = appScreenShot.getId();
        this.uuid = appScreenShot.getUuid();
        this.imageUrl = appScreenShot.getImageUrl();
        this.appId = appScreenShot.getApp().getId();
        // this.size = appScreenShot.getSize();
        this.createdAt = appScreenShot.getCreatedAt();
        this.updatedAt = appScreenShot.getUpdatedAt();
    }
}
