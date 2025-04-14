package com.kaydev.appstore.models.dto.objects;

import java.util.Map;

import com.kaydev.appstore.models.entities.Task;
import com.kaydev.appstore.models.enums.PushPeriod;
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
public class TaskPushObj {
    private Long id;
    private String uuid;
    private String taskName;
    private String taskId;
    private DeveloperMinObj developer;
    private AppVersionObj appVersion;
    private String message;
    private StatusType status;
    private TaskType taskType;
    private PushPeriod pushPeriod;
    private int terminalCount = 0;
    private int completedCount = 0;
    private Map<String, Object> parameters;

    public TaskPushObj(Task task) {
        this.id = task.getId();
        this.uuid = task.getUuid();
        this.taskName = task.getTaskName();
        this.taskId = task.getTaskId();
        this.developer = task.getDeveloper() == null ? null : new DeveloperMinObj(task.getDeveloper(), false);
        this.appVersion = task.getAppVersion() == null ? null : new AppVersionObj(task.getAppVersion(), true);
        this.message = task.getMessage();
        this.status = task.getStatus();
        this.taskType = task.getTaskType();
        this.pushPeriod = task.getPushPeriod();
        this.terminalCount = task.getTerminalCount();
        this.completedCount = task.getCompletedCount();
        this.parameters = task.getParameters() == null ? null : task.getParameeterMap();
    }
}
