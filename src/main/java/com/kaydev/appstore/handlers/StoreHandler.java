package com.kaydev.appstore.handlers;

import java.io.File;
// import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
// import java.util.Random;
// import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
// import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kaydev.appstore.models.dto.objects.AppMinObj;
import com.kaydev.appstore.models.dto.objects.DeveloperMinObj;
import com.kaydev.appstore.models.dto.objects.TaskPushObj;
import com.kaydev.appstore.models.dto.request.store.TerminalSyncRequest;
import com.kaydev.appstore.models.dto.response.BaseResponse;
import com.kaydev.appstore.models.dto.response.GenericResponse;
import com.kaydev.appstore.models.dto.utils.InstalledApp;
import com.kaydev.appstore.models.entities.App;
import com.kaydev.appstore.models.entities.AppVersion;
import com.kaydev.appstore.models.entities.Manufacturer;
import com.kaydev.appstore.models.entities.ManufacturerModel;
import com.kaydev.appstore.models.entities.Task;
import com.kaydev.appstore.models.entities.TaskTerminal;
import com.kaydev.appstore.models.entities.Terminal;
import com.kaydev.appstore.models.entities.TerminalGeoFence;
import com.kaydev.appstore.models.entities.TerminalInfo;
import com.kaydev.appstore.models.entities.TerminalInstalledApp;
import com.kaydev.appstore.models.entities.TerminalLog;
import com.kaydev.appstore.models.enums.AppType;
import com.kaydev.appstore.models.enums.StatusType;
import com.kaydev.appstore.services.AppParser;
import com.kaydev.appstore.services.GeoFencer;
import com.kaydev.appstore.services.NotificationService;
import com.kaydev.appstore.services.data.AppService;
import com.kaydev.appstore.services.data.TaskService;
import com.kaydev.appstore.services.data.TerminalService;
import com.kaydev.appstore.utils.GenericUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class StoreHandler {
    @Autowired
    private AppService appService;

    @Autowired
    private GeoFencer geoFencer;
    // @Autowired
    // private TaskScheduler taskScheduler;

    // @Autowired
    // private ExecutorService executorService;

    // @Autowired
    // private AwsService awsService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TerminalService terminalService;

    @Autowired
    private AppParser appParser;

    @Transactional
    public ResponseEntity<BaseResponse> getApps(Terminal term, String serialNumber) {
        GenericResponse response = new GenericResponse();
        Terminal terminal = terminalService.getTerminalBySerialNumber(term.getSerialNumber());

        if (terminal.getStatus() == StatusType.UNSYNCED) {
            response.setStatus("unsynced");
            response.setMessage("Terminal is not synced");
            return ResponseEntity.ok(response);
        }

        List<AppMinObj> apps = appService.getAllMinByFilter(StatusType.ACTIVE, terminal.getModel().getOsType(), null,
                terminal.getDeveloper().getId(), terminal.getDistributor().getId(), null,
                terminal.getManufacturer().getId(), null, AppType.EXTERNAL);

        response.getData().put("apps", apps);
        response.getData().put("developer", new DeveloperMinObj(terminal.getDeveloper()));
        response.setMessage("Apps Retrieved Successfully");

        return ResponseEntity.ok(response);
    }

    // @Transactional
    // public ResponseEntity<BaseResponse> syncTerminal(Terminal term,
    // TerminalSyncRequest request) {
    // TerminalSyncResponse response = new TerminalSyncResponse();

    // try {
    // Terminal terminal =
    // terminalService.getTerminalBySerialNumber(term.getSerialNumber());
    // String devUuid = terminal.getDeveloper().getUuid();

    // // Update terminal sync status
    // updateSyncedTerminal(terminal, terminal.getTerminalInfo(),
    // terminal.getTerminalGeoFence(), request);

    // // Check and update installed apps if necessary
    // updateInstalledAppsIfNeeded(terminal, request);

    // // Send notifications
    // notifyTerminalUpdate(terminal, devUuid);

    // // Get pending tasks
    // List<TaskPushObj> pendingTasks = getPendingTasks(terminal);

    // response.setTasks(pendingTasks);
    // response.setMessage("Terminal Synced Successfully");
    // return ResponseEntity.ok(response);

    // } catch (Exception e) {
    // log.error("Failed to sync terminal: {}", e.getMessage(), e);
    // response.setStatus("error");
    // response.setMessage("Failed to sync terminal: " + e.getMessage());
    // return ResponseEntity.ok(response);
    // }
    // }

    @Transactional
    public void syncTerminalMqtt(Terminal term, TerminalSyncRequest request) {

        try {
            Terminal terminal = terminalService.getTerminalBySerialNumber(term.getSerialNumber());
            String devUuid = terminal.getDeveloper().getUuid();

            // Update terminal sync status
            updateSyncedTerminal(terminal, terminal.getTerminalInfo(),
                    terminal.getTerminalGeoFence(), request);

            // Check and update installed apps if necessary
            updateInstalledAppsIfNeeded(terminal, request);

            // Send notifications
            notifyTerminalUpdate(terminal, devUuid);

            // Get pending tasks
            List<Map<String, Object>> pendingTasks = getPendingTasksMap(terminal);

            if (!pendingTasks.isEmpty()) {
                Map<String, Object> socketData = new HashMap<>();

                socketData.put("tasks", pendingTasks);

                notificationService.sendMQTT(socketData, terminal.getSerialNumber());
            }

        } catch (Exception e) {
            log.error("Failed to sync terminal MQTT: {}", e.getMessage(), e);
        }
    }

    private void updateInstalledAppsIfNeeded(Terminal terminal, TerminalSyncRequest request) {
        if (request.getInstalledApp() != null && !request.getInstalledApp().isEmpty()) {
            Long count = terminalService.countTerminalApp(terminal);
            if (Long.valueOf(request.getInstalledApp().size()) != count) {
                terminalService.deleteAllByTerminalId(terminal.getId());
                saveTerminalInstalledApps(terminal, terminal.getManufacturer(),
                        terminal.getModel(), request.getInstalledApp());
            }
        }
    }

    private void notifyTerminalUpdate(Terminal terminal, String devUuid) {
        // Terminal update notification
        notificationService.sendWebSocket(
                Map.of("action", "update_terminal"),
                "terminal_update_" + terminal.getUuid());

        // Dashboard update notifications
        notificationService.sendWebSocket(
                Map.of("action", "update_summary"),
                "dashboard_update_" + devUuid);
        notificationService.sendWebSocket(
                Map.of("action", "update_summary"),
                "dashboard_update_admin");

        // Terminal list update notifications
        notificationService.sendWebSocket(
                Map.of("action", "update_terminallist"),
                "terminallist_update_" + devUuid);
        notificationService.sendWebSocket(
                Map.of("action", "update_terminallist"),
                "terminallist_update_admin");
    }

    // private List<TaskPushObj> getPendingTasks(Terminal terminal) {
    // List<TaskTerminal> tasks = taskService.getTaskTerminalByTerminalAndStatus(
    // terminal, StatusType.NOT_STARTED);

    // return tasks.stream()
    // .map(task -> new TaskPushObj(task.getTask()))
    // .collect(Collectors.toList());
    // }

    private List<Map<String, Object>> getPendingTasksMap(Terminal terminal) {
        List<TaskTerminal> tasks = taskService.getTaskTerminalByTerminalAndStatus(
                terminal, StatusType.NOT_STARTED);

        return tasks.stream()
                .map(task -> GenericUtil.convertObjectToMap(new TaskPushObj(task.getTask())))
                .collect(Collectors.toList());
    }

    public ResponseEntity<BaseResponse> taskUpload(Terminal terminal,
            String serialNumber, String taskId,
            MultipartFile capture) {
        GenericResponse response = new GenericResponse();
        try {
            String ext = appParser.getFileExtension(capture);

            String fileName = "uploadedAppCapture_app_file_" + System.currentTimeMillis();
            File tempFile = File.createTempFile(fileName, "." + ext);
            capture.transferTo(tempFile);

            String fileUrl = appParser.getAwsService().uploadFile("app-capture/" + tempFile.getName(), tempFile);
            tempFile.delete();

            response.setMessage("File uploaded successfully");
            response.getData().put("uploadUrl", fileUrl);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("screenShotUpload", e.getMessage());
            response.setStatus("error");
            response.setMessage("Exception occurred while uploading screenshoot");
            return ResponseEntity.ok(response);
        }

    }

    // public ResponseEntity<BaseResponse> updateTask(Terminal terminal,
    // String serialNumber, String taskId, String status, String message,
    // MultipartFile capture) {
    // GenericResponse response = new GenericResponse();

    // Task task = taskService.getTaskByTaskId(taskId);

    // if (task == null) {
    // response.setStatus("error");
    // response.setMessage("Task not found");

    // return ResponseEntity.ok(response);
    // }

    // TaskTerminal taskTerminal =
    // taskService.getTaskTerminalByTerminalAndTask(terminal, task);

    // if (taskTerminal == null) {
    // response.setStatus("error");
    // response.setMessage("Task Terminal not found");

    // return ResponseEntity.ok(response);
    // }

    // taskTerminal.setStatus(StatusType.valueOf(status));
    // if (capture != null) {
    // File tempFile = GenericUtil.convertMultiPartFileToFile(capture, ".png");
    // String awsUrl = awsService.uploadFile("app-capture/" + tempFile.getName(),
    // tempFile);
    // taskTerminal.setResponse(awsUrl);
    // tempFile.delete();
    // } else {
    // taskTerminal.setResponse(message);
    // }

    // task.setCompletedCount(task.getCompletedCount() + 1);

    // if (task.getCompletedCount() == task.getTerminalCount()) {
    // task.setStatus(StatusType.COMPLETED);
    // } else {
    // task.setStatus(StatusType.IN_PROGRESS);
    // }

    // taskService.getTaskRepository().save(task);
    // taskService.getTaskTerminalRepository().save(taskTerminal);

    // Map<String, Object> data = new HashMap<>();
    // data.put("action", "update_task");
    // notificationService.sendWebSocket(data, "task_update_" + task.getUuid());

    // Map<String, Object> dataTerminal = new HashMap<>();
    // dataTerminal.put("action", "update_terminal");
    // notificationService.sendWebSocket(dataTerminal, "terminal_update_" +
    // taskTerminal.getTerminal().getUuid());

    // Map<String, Object> dataList = new HashMap<>();
    // dataList.put("action", "update_task_list");
    // notificationService.sendWebSocket(dataList, "task_update_list");

    // response.setMessage("Task Updated Successfully");

    // return ResponseEntity.ok(response);
    // }

    public void updateTaskMqtt(Terminal terminal,
            String serialNumber, String taskId, String status, String message,
            String capture) {

        Task task = taskService.getTaskByTaskId(taskId);

        if (task != null) {

            TaskTerminal taskTerminal = taskService.getTaskTerminalByTerminalAndTask(terminal, task);

            if (taskTerminal == null) {
                return;
            }

            taskTerminal.setStatus(StatusType.valueOf(status));
            if (capture != null) {
                // File tempFile = GenericUtil.convertBase64ToFile(capture);
                // String awsUrl = awsService.uploadFile("app-capture/" + tempFile.getName(),
                // tempFile);
                taskTerminal.setResponse(capture);
                // tempFile.delete();
            } else {
                taskTerminal.setResponse(message);
            }

            task.setCompletedCount(task.getCompletedCount() + 1);

            log.info("completedd vs count {}", task.getCompletedCount(), task.getTerminalCount());

            if (task.getCompletedCount() == task.getTerminalCount()) {
                task.setStatus(StatusType.COMPLETED);
            } else {
                task.setStatus(StatusType.IN_PROGRESS);
            }

            taskService.getTaskRepository().save(task);
            taskService.getTaskTerminalRepository().save(taskTerminal);

            Map<String, Object> data = new HashMap<>();
            data.put("action", "update_task");
            notificationService.sendWebSocket(data, "task_update_" + task.getUuid());

            Map<String, Object> dataTerminal = new HashMap<>();
            dataTerminal.put("action", "update_terminal");
            notificationService.sendWebSocket(dataTerminal, "terminal_update_" + terminal.getUuid());

            Map<String, Object> dataList = new HashMap<>();
            dataList.put("action", "update_task_list");
            notificationService.sendWebSocket(dataList, "task_update_list");

        }
    }

    // public ResponseEntity<BaseResponse> notifyDownload(Terminal terminal, String
    // appUuid, String versionUuid) {
    // GenericResponse response = new GenericResponse();

    // executorService.execute(() -> {
    // updateAppDownloadCount(appUuid, versionUuid);
    // });

    // response.setMessage("Notification Received");
    // return ResponseEntity.ok(response);
    // }

    public void notifyDownloadMqtt(Terminal terminal, String appUuid, String versionUuid) {
        updateAppDownloadCount(appUuid, versionUuid);
    }

    public void updateAppDownloadCount(String appUuid, String versionUuid) {
        App app = appService.getAppByUuid(appUuid);
        if (app != null) {
            app.setDownloadCount(app.getDownloadCount() + 1);
            appService.getAppRepository().save(app);

            AppVersion appVersion = appService.getAppVersionByUuid(versionUuid);
            if (appVersion != null) {
                appVersion.setDownloadCount(appVersion.getDownloadCount() + 1);
                appService.getAppVersionRepository().save(appVersion);
            }
            Map<String, Object> data = new HashMap<>();
            data.put("action", "update_app");
            notificationService.sendWebSocket(data, "app_update_" + appUuid);
        }
    }

    @Transactional
    public void saveTerminalInstalledApps(Terminal term, Manufacturer manufacturer, ManufacturerModel model,
            List<InstalledApp> apps) {
        try {

            Terminal terminal = terminalService.getTerminalByUuid(term.getUuid());

            List<String> systemApps = Arrays.asList("com.iisysgroup.itexstore", "com.iisysgroup.itex_desk");
            List<TerminalInstalledApp> terminalInstalledApps = new ArrayList<>();
            for (InstalledApp app : apps) {

                System.out.println(app);
                TerminalInstalledApp terminalInstalledApp = new TerminalInstalledApp();

                String packageName = app.getPackageName() + "||"
                        + manufacturer.getManufacturerName()
                        + "_" + model.getOsType();

                App storeApp = appService.getAppByPackageName(packageName);

                if (storeApp != null) {
                    terminalInstalledApp.setApp(storeApp);
                    terminalInstalledApp.setIcon(storeApp.getIcon());
                }

                terminalInstalledApp.setTerminal(terminal);
                terminalInstalledApp.setAppName(app.getName());
                terminalInstalledApp.setPackageName(app.getPackageName());
                terminalInstalledApp.setVersion(app.getVersionName());
                terminalInstalledApp.setVersionCode(app.getVersionCode() + "");
                if (systemApps.contains(app.getPackageName()) && storeApp != null) {
                    terminalInstalledApp.setAppType(AppType.SYSTEM);
                } else {
                    terminalInstalledApp.setAppType(AppType.EXTERNAL);
                }
                terminalInstalledApp.setBuiltWith(app.getBuiltWith());
                terminalInstalledApp.setOsType(model.getOsType());

                terminalInstalledApps.add(terminalInstalledApp);

            }

            terminalService.getTerminalInstalledAppRepository().saveAllAndFlush(terminalInstalledApps);
        } catch (Exception e) {
            // e.printStackTrace();
            log.error("Error from saveTerminalInstalledApps:" + e.getMessage());
        }
    }

    @Transactional
    public void updateSyncedTerminal(Terminal term, TerminalInfo termInfo, TerminalGeoFence terminalGeoFence,
            TerminalSyncRequest request) {
        try {

            Terminal terminal = terminalService.getTerminalByUuid(term.getUuid());

            TerminalInfo terminalInfo = null;
            if (termInfo == null) {
                terminalInfo = new TerminalInfo();
                terminalInfo.setTerminal(terminal);
            } else {
                terminalInfo = terminalService.getTerminalInfoRepository().findById(termInfo.getId()).get();
            }
            terminalInfo.setBatteryLevel(request.getDeviceInfo().getBatteryLevel());
            terminalInfo.setBatteryTemp(request.getDeviceInfo().getBatteryTemp());
            terminalInfo.setLongitude(request.getDeviceInfo().getLongitude());
            terminalInfo.setLatitude(request.getDeviceInfo().getLatitude());
            terminalInfo.setSerialNumber(request.getDeviceInfo().getSerialNumber());
            terminalInfo.setFirmware(request.getDeviceInfo().getFirmware());
            terminalInfo.setManufacturer(request.getDeviceInfo().getManufacturer());
            terminalInfo.setModel(request.getDeviceInfo().getModel());
            terminalInfo.setNetworkType(request.getDeviceInfo().getNetworkType());
            terminalInfo.setPrinter(request.getDeviceInfo().getPrinter());
            terminalInfo.setOsType(request.getDeviceInfo().getOsType());
            terminalInfo.setOsVersion(request.getDeviceInfo().getOsVersion());
            terminalInfo.setRam(request.getDeviceInfo().getRam());
            terminalInfo.setRom(request.getDeviceInfo().getRom());
            terminalInfo.setSdkVersion(request.getDeviceInfo().getSdkVersion());
            terminalInfo.setDeviceId(terminal.getDeviceId());
            terminalInfo.setBatteryStatus(request.getDeviceInfo().getBatteryStatus());
            terminalService.getTerminalInfoRepository().save(terminalInfo);

            terminal.setStatus(StatusType.ACTIVE);
            terminal.setLastHeartbeat(LocalDateTime.now());

            terminal.setTerminalInfo(terminalInfo);
            terminalService.getTerminalRepository().save(terminal);

            // terminal = terminalService.getTerminalByUuid(term.getUuid());

            TerminalLog terminalLog = new TerminalLog();
            terminalLog.setTerminal(terminal);
            terminalLog.setBatteryLevel(request.getDeviceInfo().getBatteryLevel());
            terminalLog.setBatteryTemp(request.getDeviceInfo().getBatteryTemp());
            terminalLog.setBatteryStatus(request.getDeviceInfo().getBatteryStatus());
            terminalLog.setFirmware(request.getDeviceInfo().getFirmware());
            terminalLog.setLatitude(request.getDeviceInfo().getLatitude());
            terminalLog.setLongitude(request.getDeviceInfo().getLongitude());
            terminalLog.setNetworkType(request.getDeviceInfo().getNetworkType());
            terminalLog.setIpAddress(request.getDeviceInfo().getIpAddress());
            terminalLog.setRam(request.getDeviceInfo().getRam());
            terminalLog.setRom(request.getDeviceInfo().getRom());
            terminalService.getTerminalLogRepository().save(terminalLog);

            if (terminal.isGeofencingEnabled() && terminalGeoFence != null && terminalInfo
                    .getLatitude() != null && terminalInfo.getLongitude() != null) {

                double centerLatitude = Double.valueOf(terminalGeoFence.getLatitude());
                double centerLongitude = Double.valueOf(terminalGeoFence.getLongitude());
                double terminalLatitude = Double.valueOf(terminalInfo.getLatitude());
                double terminalLongitude = Double.valueOf(terminalInfo.getLongitude());
                double radius = terminalGeoFence.getRadius();

                boolean isOnline = geoFencer.isOnline(centerLatitude, centerLongitude, terminalLatitude,
                        terminalLongitude, radius);

                terminalGeoFence.setStatus(isOnline ? StatusType.ONLINE : StatusType.OFFLINE);
                terminalService.getTerminalGeoFenceRepository().save(terminalGeoFence);

                if (!isOnline) {
                    geoFencer.pushGeoShutdown(terminal.getUuid());
                }
            }
        } catch (Exception e) {
            // e.printStackTrace();
            log.error("Error from updateSyncedTerminal:" + e.getMessage());
        }
    }

}
