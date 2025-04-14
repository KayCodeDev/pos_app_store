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
public class TaskTerminalObj {
    private Long id;
    private String uuid;
    private String taskUuid;
    private TerminalMinObj terminal;
    private StatusType status;
    private String response;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TaskTerminalObj(TaskTerminal taskTerminal) {
        this.id = taskTerminal.getId();
        this.uuid = taskTerminal.getUuid();
        this.taskUuid = taskTerminal.getTask().getUuid();
        this.terminal = new TerminalMinObj(taskTerminal.getTerminal());
        this.status = taskTerminal.getStatus();
        this.response = taskTerminal.getResponse();
        this.createdAt = taskTerminal.getCreatedAt();
        this.updatedAt = taskTerminal.getUpdatedAt();
    }
}
