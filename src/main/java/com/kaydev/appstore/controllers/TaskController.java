package com.kaydev.appstore.controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kaydev.appstore.handlers.TaskHandler;
import com.kaydev.appstore.models.dto.request.is.CreateTaskRequest;
import com.kaydev.appstore.models.dto.request.is.SearchParams;
import com.kaydev.appstore.models.dto.response.BaseResponse;
import com.kaydev.appstore.security.implementation.UserDetailsImpl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v1/is/task")
public class TaskController {
    @Autowired
    private TaskHandler taskHandler;

    @PostMapping("/list")
    @PreAuthorize("hasAuthority('TASK_LIST')")
    public ResponseEntity<BaseResponse> taskList(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody SearchParams searchParams) {
        return taskHandler.taskList(userDetails, searchParams);
    }

    @GetMapping("/{taskUuid}")
    @PreAuthorize("hasAuthority('VIEW_TASK')")
    public ResponseEntity<BaseResponse> getTask(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String taskUuid) {
        return taskHandler.getTask(userDetails, taskUuid);
    }

    @PostMapping("/{taskUuid}/terminals")
    @PreAuthorize("hasAuthority('VIEW_TASK')")
    public ResponseEntity<BaseResponse> getTaskTerminals(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String taskUuid, @RequestBody SearchParams searchParams) {
        return taskHandler.getTaskTerminals(userDetails, taskUuid, searchParams);
    }

    @PostMapping("/{taskUuid}/terminals/export")
    @PreAuthorize("hasAuthority('TASK_LIST')")
    public void exportTaskList(HttpServletResponse response, @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String taskUuid, @RequestBody SearchParams searchParams) {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=task_report.xlsx");

        try (ByteArrayOutputStream outputStream = taskHandler.exportTaskTerminalList(userDetails, taskUuid,
                searchParams)) {
            if (outputStream != null) {
                response.getOutputStream().write(outputStream.toByteArray());
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @PatchMapping("/{taskUuid}/cancel")
    @PreAuthorize("hasAuthority('CREATE_TASK')")
    public ResponseEntity<BaseResponse> cancelTask(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String taskUuid) {
        return taskHandler.cancelTask(userDetails, taskUuid);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('CREATE_TASK')")
    public ResponseEntity<BaseResponse> createTask(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody CreateTaskRequest request) {
        return taskHandler.createTask(userDetails, request);
    }

}
