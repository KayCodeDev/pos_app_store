package com.kaydev.appstore.models.dto.request.is.resource;

import com.kaydev.appstore.models.enums.CrudAction;
import com.kaydev.appstore.models.enums.ResourceEntity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ManageResourceRequest<T> {
    private Long resourceId;
    private CrudAction action;
    private ResourceEntity entity;
    private T data;

}
