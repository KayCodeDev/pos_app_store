package com.kaydev.appstore.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kaydev.appstore.models.dto.objects.TaskPushMinObj;
import com.kaydev.appstore.models.entities.Developer;
import com.kaydev.appstore.models.entities.Distributor;
import com.kaydev.appstore.models.entities.Manufacturer;
import com.kaydev.appstore.models.entities.ManufacturerModel;
import com.kaydev.appstore.models.entities.Task;
import com.kaydev.appstore.models.entities.TaskTerminal;
import com.kaydev.appstore.models.entities.Terminal;
import com.kaydev.appstore.models.entities.User;
import com.kaydev.appstore.models.enums.OsType;
import com.kaydev.appstore.models.enums.PushPeriod;
import com.kaydev.appstore.models.enums.StatusType;
import com.kaydev.appstore.models.enums.TaskType;
import com.kaydev.appstore.services.data.TaskService;
import com.kaydev.appstore.services.data.TerminalService;
import com.kaydev.appstore.utils.GenericUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class HelperService {

    @Autowired
    private TerminalService terminalService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private NotificationService notificationService;

    @Async("taskExecutor")
    public void savedImportedTerminal(Developer developer, Distributor distributor, Manufacturer manufacturer,
            ManufacturerModel model, List<Map<String, String>> data, OsType terminalOS,
            User user) {

        for (Map<String, String> row : data) {
            savedSingleImportedTerminal(developer, distributor, manufacturer, model, row, terminalOS, user);
        }
    }

    @Async("taskExecutor")
    public void savedSingleImportedTerminal(Developer developer, Distributor distributor, Manufacturer manufacturer,
            ManufacturerModel model, Map<String, String> row, OsType terminalOS,
            User user) {
        try {
            if (row.get("serial_no") != null && !row.get("serial_no").isEmpty() && !GenericUtil.hasSpecialCharacters(
                    row.get("serial_no"))) {
                Terminal terminal = terminalService.getTerminalRepository().findBySerialNumber(
                        row.get("serial_no")).orElse(null);
                if (terminal == null) {
                    terminal = new Terminal();
                    terminal.setSerialNumber(row.get("serial_no"));
                    terminal.setDeviceId(row.get("device_id"));
                    terminal.setDeveloper(developer);
                    terminal.setManufacturer(manufacturer);
                    terminal.setUser(user);
                    terminal.setModel(model);
                    terminal.setDistributor(distributor);

                    terminalService.getTerminalRepository().save(terminal);
                }
            }
        } catch (Exception e) {
            log.error("Error saving imported terminal: " + e.getMessage());
        }
    }

    // @Async("taskExecutor")
    @Transactional
    public void pushTasKToTerminals(Task task, List<Terminal> terminals) {
        Map<String, Object> socketData = new HashMap<>();
        List<Map<String, Object>> tasks = new ArrayList<>();

        Map<String, Object> data = GenericUtil.convertObjectToMap(new TaskPushMinObj(task));
        tasks.add(data);

        socketData.put("tasks", tasks);

        for (Terminal terminal : terminals) {

            TaskTerminal taskTerminal = new TaskTerminal();
            taskTerminal.setTask(task);
            taskTerminal.setTerminal(terminal);
            if (Arrays.asList(TaskType.REBOOT_DEVICE, TaskType.SHUTDOWN_DEVICE).contains(task.getTaskType())) {
                taskTerminal.setStatus(StatusType.DONE);
                taskTerminal.setResponse("Task Initiated");
                taskTerminal.setUpdatedAt(LocalDateTime.now());

            } else if (task.getTaskType().equals(TaskType.PUSH_APP)
                    && terminal.getManufacturer() != task.getAppVersion().getApp().getManufacturer()
                    && !terminal.getModel().getOsType().equals(task.getAppVersion().getApp().getOsType())) {
                taskTerminal.setStatus(StatusType.FAILED);
                taskTerminal.setResponse("Invalid app version model or OS type");
                taskTerminal.setUpdatedAt(LocalDateTime.now());

            } else {
                taskTerminal.setStatus(StatusType.NOT_STARTED);
            }
            taskService.getTaskTerminalRepository().save(taskTerminal);

            if (task.getPushPeriod().equals(PushPeriod.IMMEDIATE)) {
                // notificationService.sendTCPSocket(socketData, terminal.getSerialNumber());
                // notificationService.sendNettySocket(socketData, terminal.getSerialNumber());
                notificationService.sendMQTT(socketData, terminal.getSerialNumber());
            }
        }

    }

    // @Async("taskExecutor")
    public void pushTasKToTerminal(TaskPushMinObj taskMin, Task task, Terminal terminal, TaskTerminal taskTerminal) {
        Map<String, Object> socketData = new HashMap<>();
        List<Map<String, Object>> tasks = new ArrayList<>();

        Map<String, Object> data = GenericUtil.convertObjectToMap(taskMin);

        tasks.add(data);

        socketData.put("tasks", tasks);

        if (taskTerminal == null) {
            taskTerminal = new TaskTerminal();
            taskTerminal.setTask(task);
            taskTerminal.setTerminal(terminal);
        }

        taskTerminal.setStatus(StatusType.NOT_STARTED);
        taskService.getTaskTerminalRepository().save(taskTerminal);

        if (task.getPushPeriod().equals(PushPeriod.IMMEDIATE)) {
            // notificationService.sendTCPSocket(socketData, terminal.getSerialNumber());
            // notificationService.sendNettySocket(socketData, terminal.getSerialNumber());
            notificationService.sendMQTT(socketData, terminal.getSerialNumber());
        }
    }

}
