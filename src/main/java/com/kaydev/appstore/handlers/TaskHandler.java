package com.kaydev.appstore.handlers;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.kaydev.appstore.models.dto.objects.TaskMinObj;
import com.kaydev.appstore.models.dto.objects.TaskObj;
import com.kaydev.appstore.models.dto.objects.TaskTerminalObj;
import com.kaydev.appstore.models.dto.objects.export.TaskTerminalObjExp;
import com.kaydev.appstore.models.dto.request.is.CreateTaskRequest;
import com.kaydev.appstore.models.dto.request.is.SearchParams;
import com.kaydev.appstore.models.dto.response.BaseResponse;
import com.kaydev.appstore.models.dto.response.GenericResponse;
import com.kaydev.appstore.models.entities.AppVersion;
import com.kaydev.appstore.models.entities.Developer;
import com.kaydev.appstore.models.entities.Distributor;
import com.kaydev.appstore.models.entities.Group;
import com.kaydev.appstore.models.entities.Task;
import com.kaydev.appstore.models.entities.Terminal;
import com.kaydev.appstore.models.entities.User;
import com.kaydev.appstore.models.enums.PushTo;
import com.kaydev.appstore.models.enums.StatusType;
import com.kaydev.appstore.models.enums.TaskType;
import com.kaydev.appstore.models.enums.UserType;
import com.kaydev.appstore.security.implementation.UserDetailsImpl;
import com.kaydev.appstore.services.HelperService;
import com.kaydev.appstore.services.NotificationService;
import com.kaydev.appstore.services.data.AppService;
import com.kaydev.appstore.services.data.DeveloperService;
import com.kaydev.appstore.services.data.TaskService;
import com.kaydev.appstore.services.data.TerminalService;
import com.kaydev.appstore.utils.ExcelUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TaskHandler {
    @Autowired
    private TaskService taskService;

    @Autowired
    private TerminalService terminalService;

    @Autowired
    private AppService appService;

    @Autowired
    private DeveloperService developerService;

    @Autowired
    private HelperService helperService;

    @Autowired
    NotificationService notificationService;

    public ResponseEntity<BaseResponse> createTask(UserDetailsImpl userDetails, CreateTaskRequest request) {
        GenericResponse response = new GenericResponse();
        try {
            User user = userDetails.getUser();
            Developer developer = user.getDeveloper();
            Distributor distributor = null;
            Group group = null;

            if (developer != null) {
                if (!developer.getSetting().isCanPush()) {
                    response.setStatus("error");
                    response.setMessage("You are not allowed to push tasks");
                    return ResponseEntity.ok(response);
                }
            }

            List<Terminal> terminals = new ArrayList<Terminal>();

            if (user.getUserType().equals(UserType.DEVELOPER)) {
                if (developer != null) {
                    if (request.getPushTo().equals(PushTo.TERMINAL)) {
                        terminals = terminalService.getTerminalsByIds(request.getTerminaList(), developer.getId());
                    } else if (request.getPushTo().equals(PushTo.DISTRIBUTOR) && request.getDistributorId() > 0) {
                        distributor = developerService.getDistributorRepository().findById(request.getDistributorId())
                                .orElseThrow(() -> new RuntimeException("Distributor not found"));

                        terminals = terminalService.getTerminalsByDistributorIdAndDeveloperId(
                                request.getDistributorId(),
                                developer.getId());
                    } else if (request.getPushTo().equals(PushTo.GROUP) && request.getGroupId() > 0) {
                        group = developerService.getGroupRepository().findById(request.getGroupId())
                                .orElseThrow(() -> new RuntimeException("Group not found"));

                        terminals = terminalService.getTerminalsByDistributorIdAAndGroupId(request.getDistributorId(),
                                request.getGroupId(), developer.getId());
                    }
                }
            } else {
                if (request.getTerminaList().size() == 0) {
                    terminals = terminalService.getTerminalRepository().findAll();
                } else {
                    terminals = terminalService.getTerminalsByIds(request.getTerminaList(), null);
                }
            }

            if (terminals.isEmpty()) {
                response.setMessage("No terminals found");
                response.setStatus("error");
                return ResponseEntity.ok(response);
            }

            AppVersion version = null;
            if (request.getTaskType().equals(TaskType.PUSH_APP)) {
                if (request.getAppVersionId() == null || request.getAppVersionId() == 0) {
                    response.setMessage("App version is required to push app");
                    response.setStatus("error");

                    return ResponseEntity.ok(response);

                } else {
                    version = appService.getAppVersionRepository().findById(request.getAppVersionId())
                            .orElseThrow(() -> new RuntimeException("App version not found"));
                }
            }

            if (Arrays.asList(TaskType.PUSH_MESSAGE, TaskType.SET_TIME_ZONE,
                    TaskType.UNINSTALL_APP).contains(request.getTaskType())
                    && request.getMessage() == null) {
                response.setMessage("Push command message is required");
                response.setStatus("error");

                return ResponseEntity.ok(response);
            }

            if (TaskType.UNINSTALL_APP.equals(request.getTaskType()) && Arrays.asList("com.iisysgroup.itexstore")
                    .contains(request.getMessage())) {
                response.setMessage("You can't uninstall system apps");
                response.setStatus("error");

                return ResponseEntity.ok(response);
            }

            if (TaskType.PUSH_PARAMETERS.equals(request.getTaskType())
                    && (request.getParameters() == null || request.getParameters() instanceof Map == false)) {
                response.setMessage("Parameters to push are required");
                response.setStatus("error");

                return ResponseEntity.ok(response);
            }

            Task task = new Task();
            task.setTaskType(request.getTaskType());
            task.setPushPeriod(request.getPushPeriod());
            task.setDeveloper(developer);
            task.setDistributor(distributor);
            task.setGroup(group);
            task.setPushTo(request.getPushTo());
            task.setUser(user);

            task.setTaskId(UUID.randomUUID().toString());
            task.setTaskName(request.getTaskType().name().toLowerCase() + "_" + request.getPushTo() + "_"
                    + terminals.size() + "_" + new Date().getTime());

            if (Arrays.asList(TaskType.PUSH_APP).contains(request.getTaskType())) {
                task.setAppVersion(version);
            }
            if (Arrays.asList(TaskType.PUSH_MESSAGE, TaskType.SET_TIME_ZONE,
                    TaskType.UNINSTALL_APP).contains(request.getTaskType())) {
                task.setMessage(request.getMessage());
            }
            if (TaskType.PUSH_PARAMETERS.equals(request.getTaskType())) {
                task.setParameeterMap(request.getParameters());
            }
            task.setTerminalCount(terminals.size());
            if (Arrays.asList(TaskType.REBOOT_DEVICE, TaskType.SHUTDOWN_DEVICE).contains(task.getTaskType())) {
                task.setStatus(StatusType.COMPLETED);
                task.setCompletedCount(task.getCompletedCount() + 1);
            }

            taskService.getTaskRepository().save(task);

            helperService.pushTasKToTerminals(task, terminals);

            response.setMessage("Task created successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus("error");
            response.setMessage(e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    public void performTask(List<Terminal> terminals, CreateTaskRequest request, User user, Developer developer) {
        Task task = new Task();
        task.setTaskType(request.getTaskType());
        task.setPushPeriod(request.getPushPeriod());
        task.setDeveloper(developer);
        task.setPushTo(request.getPushTo());
        task.setUser(user);
        task.setTaskId(UUID.randomUUID().toString());
        task.setTaskName(request.getTaskType().name().toLowerCase() + "_" + request.getPushTo() + "_"
                + terminals.size() + "_" + new Date().getTime());

        if (Arrays.asList(TaskType.PUSH_MESSAGE, TaskType.SET_TIME_ZONE,
                TaskType.UNINSTALL_APP).contains(request.getTaskType())) {
            task.setMessage(request.getMessage());
        }
        task.setTerminalCount(terminals.size());
        if (Arrays.asList(TaskType.REBOOT_DEVICE, TaskType.SHUTDOWN_DEVICE).contains(task.getTaskType())) {
            task.setStatus(StatusType.COMPLETED);
        }

        taskService.getTaskRepository().save(task);

        helperService.pushTasKToTerminals(task, terminals);
    }

    public ResponseEntity<BaseResponse> taskList(UserDetailsImpl userDetails, SearchParams searchParams) {
        GenericResponse response = new GenericResponse();
        try {
            User user = userDetails.getUser();
            Developer developer = user.getDeveloper();

            Long devId = searchParams.getDeveloperId();
            if (developer != null) {
                devId = developer.getId();
            }

            Pageable pageable = PageRequest.of(searchParams.getPage(), searchParams.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "id"));

            Page<TaskMinObj> tasks = taskService.getAllTaskByFilter(pageable, devId, searchParams.getDistributorId(),
                    searchParams.getGroupId(), searchParams.getUserId(),
                    searchParams.getStatus(), searchParams.getSearch());

            // List<TaskMinObj> taskObjs =
            // tasks.getContent().stream().map(TaskMinObj::new).collect(Collectors.toList());

            response.setMessage("Tasks retrieved successfully");
            response.getData().put("tasks", tasks.getContent());
            response.getData().put("currentPageNumber", tasks.getNumber());
            response.getData().put("totalPages", tasks.getTotalPages());
            response.getData().put("totalItems", tasks.getTotalElements());
            response.getData().put("hasNext", tasks.hasNext());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatus("error");
            response.setMessage(e.getMessage());
            return ResponseEntity.ok(response);
        }

    }

    public ResponseEntity<BaseResponse> getTask(UserDetailsImpl userDetails, String taskUuid) {
        GenericResponse response = new GenericResponse();
        try {
            User user = userDetails.getUser();
            Developer developer = user.getDeveloper();

            Task task = taskService.getTaskByUuid(taskUuid);
            if (task == null) {
                response.setStatus("error");
                response.setMessage("Task not found");
                return ResponseEntity.ok(response);
            }
            if (developer != null && task.getDeveloper().getId() != developer.getId()) {
                response.setStatus("error");
                response.setMessage("Task not found");
                return ResponseEntity.ok(response);
            }

            TaskObj taskObj = new TaskObj(task, true);
            response.setMessage("Task retrieved successfully");
            response.getData().put("task", taskObj);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Exception occurred in getTask", e);
            response.setStatus("error");
            response.setMessage("Exception occurred while getting task");
            return ResponseEntity.ok(response);
        }

    }

    public ResponseEntity<BaseResponse> getTaskTerminals(UserDetailsImpl userDetails, String taskUuid,
            SearchParams searchParams) {
        GenericResponse response = new GenericResponse();
        try {
            User user = userDetails.getUser();
            Developer developer = user.getDeveloper();

            Task task = taskService.getTaskByUuid(taskUuid);
            if (task == null) {
                response.setStatus("error");
                response.setMessage("Task not found");
                return ResponseEntity.ok(response);
            }
            if (developer != null && task.getDeveloper().getId() != developer.getId()) {
                response.setStatus("error");
                response.setMessage("Task not found");
                return ResponseEntity.ok(response);
            }

            Pageable pageable = PageRequest.of(searchParams.getPage(), searchParams.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "id"));
            Page<TaskTerminalObj> terminals = taskService.getAllTaskTerminalByFilter(pageable, task.getId(),
                    searchParams.getStatus(), searchParams.getSearch());

            // List<TaskTerminalObj> terminalObjs =
            // terminals.getContent().stream().map(TaskTerminalObj::new)
            // .collect(Collectors.toList());

            response.setMessage("Task terminals retrieved successfully");
            response.getData().put("taskTerminals", terminals.getContent());
            response.getData().put("currentPageNumber", terminals.getNumber());
            response.getData().put("totalPages", terminals.getTotalPages());
            response.getData().put("totalItems", terminals.getTotalElements());
            response.getData().put("hasNext", terminals.hasNext());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Exception occurred in getTaskTerminals", e);
            response.setStatus("error");
            response.setMessage("Exception occurred while getting task terminals");
            return ResponseEntity.ok(response);
        }
    }

    public ByteArrayOutputStream exportTaskTerminalList(UserDetailsImpl userDetails, String taskUuid,
            SearchParams searchParams) {

        try {
            User user = userDetails.getUser();
            Developer developer = user.getDeveloper();

            Task task = taskService.getTaskByUuid(taskUuid);
            if (task == null) {
                throw new RuntimeException("Task not found");
            }
            if (developer != null && task.getDeveloper().getId() != developer.getId()) {
                throw new RuntimeException("Task not found");
            }

            int page = 0;
            int size = 1000;
            List<TaskTerminalObjExp> data = new ArrayList<>();
            Page<TaskTerminalObjExp> tasks;

            do {
                PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

                tasks = taskService.getAllTaskTerminalByFilterForExport(pageable, task.getId());

                data.addAll(tasks.getContent());
                page++;
            } while (tasks.hasNext());

            ExcelUtil<TaskTerminalObjExp> excelUtil = new ExcelUtil<>();

            return excelUtil.generateExcel(data, TaskTerminalObjExp.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public ResponseEntity<BaseResponse> cancelTask(UserDetailsImpl userDetails, String taskUuid) {
        GenericResponse response = new GenericResponse();
        try {
            User user = userDetails.getUser();
            Developer developer = user.getDeveloper();

            Task task = taskService.getTaskByUuid(taskUuid);
            if (task == null) {
                response.setStatus("error");
                response.setMessage("Task not found");
                return ResponseEntity.ok(response);
            }
            if (developer != null && task.getDeveloper().getId() != developer.getId()) {
                response.setStatus("error");
                response.setMessage("Task not found");
                return ResponseEntity.ok(response);
            }

            if (task.getStatus() == StatusType.COMPLETED) {
                response.setStatus("error");
                response.setMessage("Task already completed");
                return ResponseEntity.ok(response);
            }

            task.setStatus(StatusType.CANCELLED);
            taskService.getTaskRepository().save(task);

            taskService.updateAllPendingTaskStatus(task.getId(), StatusType.CANCELLED, StatusType.NOT_STARTED);

            response.setMessage("Task cancelled successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Exception Cancelling Tassk", e);
            response.setStatus("error");
            response.setMessage("Exception occurred cancelling task");
            return ResponseEntity.ok(response);
        }

    }

}
