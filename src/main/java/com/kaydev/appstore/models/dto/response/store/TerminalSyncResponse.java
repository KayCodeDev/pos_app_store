package com.kaydev.appstore.models.dto.response.store;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kaydev.appstore.models.dto.objects.TaskPushObj;
import com.kaydev.appstore.models.dto.response.BaseResponse;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TerminalSyncResponse extends BaseResponse {
    List<TaskPushObj> tasks;
}
