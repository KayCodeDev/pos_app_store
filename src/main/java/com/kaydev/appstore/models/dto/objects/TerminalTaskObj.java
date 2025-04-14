package com.kaydev.appstore.models.dto.objects;

import java.time.LocalDateTime;

import com.kaydev.appstore.models.entities.TaskTerminal;
import com.kaydev.appstore.models.enums.StatusType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TerminalTaskObj {
    private Long id;
    private String uuid;
    private TaskMinObj task;
    private String terminalUuid;
    private StatusType status;
    private String response;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TerminalTaskObj(TaskTerminal taskTerminal) {
        this.id = taskTerminal.getId();
        this.uuid = taskTerminal.getUuid();
        this.task = new TaskMinObj(taskTerminal.getTask());
        this.terminalUuid = taskTerminal.getTerminal().getUuid();
        this.status = taskTerminal.getStatus();
        this.response = taskTerminal.getResponse();
        this.createdAt = taskTerminal.getCreatedAt();
        this.updatedAt = taskTerminal.getUpdatedAt();
    }
}
