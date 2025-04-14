package com.kaydev.appstore.models.dto.objects;

import java.time.LocalDateTime;

import com.kaydev.appstore.models.entities.Task;
import com.kaydev.appstore.models.enums.PushPeriod;
import com.kaydev.appstore.models.enums.PushTo;
import com.kaydev.appstore.models.enums.StatusType;
import com.kaydev.appstore.models.enums.TaskType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TaskMinObj {
    private Long id;
    private String uuid;
    private String taskName;
    private String taskId;
    private DeveloperMin developer;
    private String message;
    private StatusType status;
    private TaskType taskType;
    private PushTo pushTo;
    private PushPeriod pushPeriod;
    private int terminalCount = 0;
    private int completedCount = 0;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TaskMinObj(Task task) {
        this.id = task.getId();
        this.uuid = task.getUuid();
        this.taskName = task.getTaskName();
        this.taskId = task.getTaskId();
        this.developer = task.getDeveloper() == null ? null : new DeveloperMin(task.getDeveloper());
        this.message = task.getMessage();
        this.status = task.getStatus();
        this.taskType = task.getTaskType();
        this.pushTo = task.getPushTo();
        this.pushPeriod = task.getPushPeriod();
        this.terminalCount = task.getTerminalCount();
        this.completedCount = task.getCompletedCount();
        this.createdAt = task.getCreatedAt();
        this.updatedAt = task.getUpdatedAt();
    }
}
