package com.kaydev.appstore.models.dto.objects.export;

import java.time.LocalDateTime;

import com.kaydev.appstore.models.entities.TaskTerminal;
import com.kaydev.appstore.models.enums.StatusType;

import lombok.Data;

@Data
public class TaskTerminalObjExp {
    private Long id;
    private String uuid;
    private String taskUuid;
    private String serialNumber;
    private String manufacturer;
    private String model;
    private StatusType status;
    private String response;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TaskTerminalObjExp(TaskTerminal t) {
        this.id = t.getId();
        this.uuid = t.getUuid();
        this.taskUuid = t.getTask().getUuid();
        this.serialNumber = t.getTerminal().getSerialNumber();
        this.manufacturer = t.getTerminal().getManufacturer().getManufacturerName();
        this.model = t.getTerminal().getModel().getModelName();
        this.status = t.getStatus();
        this.response = t.getResponse();
        this.createdAt = t.getCreatedAt();
        this.updatedAt = t.getUpdatedAt();
    }
}
