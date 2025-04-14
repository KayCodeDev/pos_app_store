package com.kaydev.appstore.models.dto.objects;

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
public class CategoryMinObj {
    private Long id;
    private String uuid;
    private String name;
    private StatusType status;

    public CategoryMinObj(Category category) {
        this.id = category.getId();
        this.uuid = category.getUuid();
        this.name = category.getName();
        this.status = category.getStatus();
    }
}
