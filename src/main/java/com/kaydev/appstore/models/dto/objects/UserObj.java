package com.kaydev.appstore.models.dto.objects;

import java.time.LocalDateTime;

import com.kaydev.appstore.models.entities.User;
import com.kaydev.appstore.models.enums.UserType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@AllArgsConstructor
@NoArgsConstructor
public class UserObj {
    private Long id;
    private String uuid;
    private String fullName;
    private String email;
    private boolean active;
    private boolean verified;
    private UserType userType;
    private DeveloperMinObj developer;
    private String roleName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserObj(User user) {
        this.id = user.getId();
        this.uuid = user.getUuid();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.active = user.isActive();
        this.verified = user.isVerified();
        this.userType = user.getUserType();
        this.developer = user.getDeveloper() == null ? null : new DeveloperMinObj(user.getDeveloper());
        this.roleName = user.getRole().getName();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }

    public UserObj(User user, boolean showDevSetting) {
        this.id = user.getId();
        this.uuid = user.getUuid();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.active = user.isActive();
        this.verified = user.isVerified();
        this.userType = user.getUserType();
        this.developer = user.getDeveloper() == null ? null : new DeveloperMinObj(user.getDeveloper(), true);
        this.roleName = user.getRole().getName();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }
}
