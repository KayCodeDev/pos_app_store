package com.kaydev.appstore.models.dto.objects;

import com.kaydev.appstore.models.entities.Role;
import com.kaydev.appstore.models.enums.UserType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RoleObj {
    private Long id;
    private String name;
    private UserType roleType;

    public RoleObj(Role role) {
        this.id = role.getId();
        this.name = role.getName();
        this.roleType = role.getRoleType();
    }
}
