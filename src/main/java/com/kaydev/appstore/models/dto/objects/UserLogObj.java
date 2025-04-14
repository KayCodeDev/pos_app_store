package com.kaydev.appstore.models.dto.objects;

import java.time.LocalDateTime;

import com.kaydev.appstore.models.entities.UserLog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@AllArgsConstructor
@NoArgsConstructor
public class UserLogObj {
    private Long id;
    private String uuid;
    private Long userId;
    private String userUuid;
    private String activity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserLogObj(UserLog userLog) {
        this.id = userLog.getId();
        this.uuid = userLog.getUuid();
        this.userId = userLog.getUser().getId();
        this.userUuid = userLog.getUser().getUuid();
        this.activity = userLog.getActivity();
        this.createdAt = userLog.getCreatedAt();
        this.updatedAt = userLog.getUpdatedAt();

    }
}
