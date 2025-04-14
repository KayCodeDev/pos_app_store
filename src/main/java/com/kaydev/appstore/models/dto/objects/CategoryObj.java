package com.kaydev.appstore.models.dto.objects;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kaydev.appstore.models.entities.Category;
import com.kaydev.appstore.models.enums.StatusType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryObj {
    private Long id;
    private String uuid;
    private String name;
    private StatusType status;
    private UserMinObj user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CategoryObj(Category category) {
        this.id = category.getId();
        this.uuid = category.getUuid();
        this.name = category.getName();
        this.status = category.getStatus();
        this.user = new UserMinObj(category.getUser());
        this.createdAt = category.getCreatedAt();
        this.updatedAt = category.getUpdatedAt();
    }

    public CategoryObj(Category category, boolean noUser) {
        this.id = category.getId();
        this.uuid = category.getUuid();
        this.name = category.getName();
    }
}
