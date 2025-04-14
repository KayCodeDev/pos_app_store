package com.kaydev.appstore.models.dto.request.is;

import java.util.List;
import java.util.Map;

import com.kaydev.appstore.models.enums.PushPeriod;
import com.kaydev.appstore.models.enums.PushTo;
import com.kaydev.appstore.models.enums.TaskType;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class CreateTaskRequest {

    @NotNull(message = "Task Type is required")
    private TaskType taskType;
    @NotNull(message = "Push period is required")
    private PushPeriod pushPeriod;
    @NotNull(message = "Push reecipient is required")
    private PushTo pushTo;

    private Long groupId;
    private Long distributorId;

    private Long developerId;

    private List<Long> terminaList;

    private Long appVersionId;
    private String message;
    private Map<String, Object> parameters;

}
