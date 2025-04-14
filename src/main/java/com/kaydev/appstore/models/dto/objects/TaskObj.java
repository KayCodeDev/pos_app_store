package com.kaydev.appstore.models.dto.objects;

import java.time.LocalDateTime;
import java.util.Map;

import com.kaydev.appstore.models.entities.Task;
import com.kaydev.appstore.models.enums.PushPeriod;
import com.kaydev.appstore.models.enums.PushTo;
import com.kaydev.appstore.models.enums.StatusType;
import com.kaydev.appstore.models.enums.TaskType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@AllArgsConstructor
@NoArgsConstructor
public class TaskObj {
    private Long id;
    private String uuid;
    private String taskName;
    private String taskId;
    private DeveloperMinObj developer;
    private DistributorMinObj distributor;
    private GroupMinObj group;
    private AppVersionObj appVersion;
    private String message;
    private StatusType status;
    private TaskType taskType;
    private PushTo pushTo;
    private PushPeriod pushPeriod;
    private int terminalCount = 0;
    private int completedCount = 0;
    private UserMinObj user;
    private Map<String, Object> parameters;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TaskObj(Task task) {
        this.id = task.getId();
        this.uuid = task.getUuid();
        this.taskName = task.getTaskName();
        this.taskId = task.getTaskId();
        this.developer = task.getDeveloper() == null ? null : new DeveloperMinObj(task.getDeveloper());
        this.distributor = task.getDistributor() == null ? null : new DistributorMinObj(task.getDistributor());
        this.group = task.getGroup() == null ? null : new GroupMinObj(task.getGroup());
        this.appVersion = task.getAppVersion() == null ? null : new AppVersionObj(task.getAppVersion());
        this.message = task.getMessage();
        this.status = task.getStatus();
        this.taskType = task.getTaskType();
        this.pushTo = task.getPushTo();
        this.pushPeriod = task.getPushPeriod();
        this.user = new UserMinObj(task.getUser());
        this.parameters = task.getParameters() == null ? null : task.getParameeterMap();
        this.terminalCount = task.getTerminalCount();
        this.completedCount = task.getCompletedCount();
        this.createdAt = task.getCreatedAt();
        this.updatedAt = task.getUpdatedAt();
    }

    public TaskObj(Task task, boolean taskView) {
        this.id = task.getId();
        this.uuid = task.getUuid();
        this.taskName = task.getTaskName();
        this.taskId = task.getTaskId();
        this.developer = task.getDeveloper() == null ? null : new DeveloperMinObj(task.getDeveloper());
        this.distributor = task.getDistributor() == null ? null : new DistributorMinObj(task.getDistributor());
        this.group = task.getGroup() == null ? null : new GroupMinObj(task.getGroup());
        this.appVersion = task.getAppVersion() == null ? null : new AppVersionObj(task.getAppVersion(), "taskView");
        this.message = task.getMessage();
        this.status = task.getStatus();
        this.taskType = task.getTaskType();
        this.pushTo = task.getPushTo();
        this.pushPeriod = task.getPushPeriod();
        this.user = new UserMinObj(task.getUser());
        this.parameters = task.getParameters() == null ? null : task.getParameeterMap();
        this.terminalCount = task.getTerminalCount();
        this.completedCount = task.getCompletedCount();
        this.createdAt = task.getCreatedAt();
        this.updatedAt = task.getUpdatedAt();
    }
}
