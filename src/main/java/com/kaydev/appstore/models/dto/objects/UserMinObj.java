package com.kaydev.appstore.models.dto.objects;

import com.kaydev.appstore.models.entities.User;
import com.kaydev.appstore.models.enums.UserType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@AllArgsConstructor
@NoArgsConstructor
public class UserMinObj {
    private Long id;
    private String uuid;
    private String fullName;
    private boolean active;
    private UserType userType;
    private String roleName;

    public UserMinObj(User user) {
        this.id = user.getId();
        this.uuid = user.getUuid();
        this.fullName = user.getFullName();
        this.active = user.isActive();
        this.userType = user.getUserType();
        this.roleName = user.getRole().getName();
    }
}
