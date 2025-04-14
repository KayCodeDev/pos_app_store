package com.kaydev.appstore.models.dto.objects;

import java.util.Map;

import com.kaydev.appstore.models.entities.Task;
import com.kaydev.appstore.models.enums.TaskType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TaskPushMinObj {
    private Long id;
    private String uuid;
    private String taskName;
    private String taskId;
    private AppVersionPushMinObj appVersion;
    private String message;
    private TaskType taskType;
    private Map<String, Object> parameters;

    public TaskPushMinObj(Task task) {
        this.id = task.getId();
        this.uuid = task.getUuid();
        this.taskName = task.getTaskName();
        this.taskId = task.getTaskId();
        this.appVersion = task.getAppVersion() == null ? null : new AppVersionPushMinObj(task.getAppVersion());
        this.message = task.getMessage();
        this.taskType = task.getTaskType();
        this.parameters = task.getParameters() == null ? null : task.getParameeterMap();
    }
}
