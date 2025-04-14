package com.kaydev.appstore.handlers;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kaydev.appstore.models.dto.objects.TaskPushMinObj;
import com.kaydev.appstore.models.dto.objects.TerminalInstalledAppObj;
import com.kaydev.appstore.models.dto.objects.TerminalLogObj;
import com.kaydev.appstore.models.dto.objects.TerminalObj;
import com.kaydev.appstore.models.dto.objects.TerminalTaskObj;
import com.kaydev.appstore.models.dto.objects.export.TerminalObjExp;
import com.kaydev.appstore.models.dto.request.is.AssignTerminalsRequest;
import com.kaydev.appstore.models.dto.request.is.CreateTerminalRequest;
import com.kaydev.appstore.models.dto.request.is.DeleteBulkTerminalRequest;
import com.kaydev.appstore.models.dto.request.is.EditTerminalRequest;
import com.kaydev.appstore.models.dto.request.is.SearchParams;
import com.kaydev.appstore.models.dto.request.is.TerminalGeoFenceRequest;
import com.kaydev.appstore.models.dto.response.BaseResponse;
import com.kaydev.appstore.models.dto.response.GenericResponse;
import com.kaydev.appstore.models.entities.Developer;
import com.kaydev.appstore.models.entities.Distributor;
import com.kaydev.appstore.models.entities.Group;
import com.kaydev.appstore.models.entities.Manufacturer;
import com.kaydev.appstore.models.entities.ManufacturerModel;
import com.kaydev.appstore.models.entities.Task;
import com.kaydev.appstore.models.entities.TaskTerminal;
import com.kaydev.appstore.models.entities.Terminal;
import com.kaydev.appstore.models.entities.TerminalGeoFence;
import com.kaydev.appstore.models.entities.User;
import com.kaydev.appstore.models.enums.AppType;
import com.kaydev.appstore.models.enums.OsType;
import com.kaydev.appstore.models.enums.StatusType;
import com.kaydev.appstore.security.implementation.UserDetailsImpl;
import com.kaydev.appstore.services.GeoFencer;
import com.kaydev.appstore.services.HelperService;
import com.kaydev.appstore.services.NotificationService;
import com.kaydev.appstore.services.data.DeveloperService;
import com.kaydev.appstore.services.data.ResourceService;
import com.kaydev.appstore.services.data.TaskService;
import com.kaydev.appstore.services.data.TerminalService;
import com.kaydev.appstore.utils.ExcelUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TerminalHandler {
    @Autowired
    private TerminalService terminalService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private DeveloperService developerService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private GeoFencer geoFencer;

    @Autowired
    private HelperService helperService;

    // @Autowired
    // private MqttPahoMessageDrivenChannelAdapter mqttMessageDrivenChannelAdapter;

    public ResponseEntity<BaseResponse> getTerminals(UserDetailsImpl userDetails, SearchParams searchParams) {
        GenericResponse response = new GenericResponse();
        User user = userDetails.getUser();
        Developer developer = user.getDeveloper();
        Long devId = searchParams.getDeveloperId();
        if (developer != null) {
            devId = developer.getId();
        }

        PageRequest pageable = PageRequest.of(searchParams.getPage(), searchParams.getPageSize(),
                Sort.by(Sort.Direction.DESC, "id"));

        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        if (searchParams.getCreatedDate() != null) {
            startDate = searchParams.getCreatedDate().atStartOfDay();
            endDate = searchParams.getCreatedDate().atTime(LocalTime.MAX);
        }

        Page<TerminalObj> terminals = terminalService.getAllTerminalByFilter(pageable, searchParams.getSearch(),
                searchParams.getStatus(), searchParams.getOsType(), devId, searchParams.getDeveloperUuid(),
                searchParams.getDistributorId(),
                searchParams.getDistributorUuid(),
                searchParams.getGroupId(), searchParams.getGroupUuid(), searchParams.getManufacturerId(),
                searchParams.getModelId(), startDate, endDate, searchParams.isLowBattery(), searchParams.isNoPaper(),
                searchParams.isNotCharging());

        response.setMessage("Terminals retrieved successfully");
        response.getData().put("terminals", terminals.getContent());
        response.getData().put("currentPageNumber", terminals.getNumber());
        response.getData().put("totalPages", terminals.getTotalPages());
        response.getData().put("totalItems", terminals.getTotalElements());
        response.getData().put("hasNext", terminals.hasNext());

        return ResponseEntity.ok(response);
    }

    public ByteArrayOutputStream exportTerminal(UserDetailsImpl userDetails,
            SearchParams searchParams) {

        try {
            User user = userDetails.getUser();
            Developer developer = user.getDeveloper();
            Long devId = searchParams.getDeveloperId();
            if (developer != null) {
                devId = developer.getId();
            }

            int page = 0;
            int size = 1000;
            List<TerminalObjExp> data = new ArrayList<>();
            Page<TerminalObjExp> terminals;

            LocalDateTime startDate = null;
            LocalDateTime endDate = null;
            if (searchParams.getCreatedDate() != null) {
                startDate = searchParams.getCreatedDate().atStartOfDay();
                endDate = searchParams.getCreatedDate().atTime(LocalTime.MAX);
            }

            do {
                PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

                terminals = terminalService.getAllTerminalByFilterForExport(pageable, searchParams.getSearch(),
                        searchParams.getStatus(), searchParams.getOsType(), devId, searchParams.getDeveloperUuid(),
                        searchParams.getDistributorId(),
                        searchParams.getDistributorUuid(),
                        searchParams.getGroupId(), searchParams.getGroupUuid(), searchParams.getManufacturerId(),
                        searchParams.getModelId(), startDate, endDate, searchParams.isLowBattery(),
                        searchParams.isNoPaper(),
                        searchParams.isNotCharging());

                data.addAll(terminals.getContent());
                page++;
            } while (terminals.hasNext());

            log.info("Exported {} terminals", data.size());

            ExcelUtil<TerminalObjExp> excelUtil = new ExcelUtil<>();

            return excelUtil.generateExcel(data, TerminalObjExp.class);
        } catch (Exception e) {
            log.error("Error exporting terminals: {}", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public ResponseEntity<BaseResponse> getTerminal(UserDetailsImpl userDetails, String terminalUuid) {
        GenericResponse response = new GenericResponse();

        User user = userDetails.getUser();
        Developer developer = user.getDeveloper();

        Terminal terminal = terminalService.getTerminalByUuid(terminalUuid);

        if (terminal == null) {
            response.setStatus("error");
            response.setMessage("Terminal not found");
            return ResponseEntity.ok(response);
        }

        if (developer != null && developer.getId() != terminal.getDeveloper().getId()) {
            response.setStatus("error");
            response.setMessage("Terminal not found");
            return ResponseEntity.ok(response);
        }

        response.setMessage("Terminal retrieved successfully");
        response.getData().put("terminal", new TerminalObj(terminal));

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<BaseResponse> getTerminalApps(UserDetailsImpl userDetails, String terminalUuid) {
        GenericResponse response = new GenericResponse();

        User user = userDetails.getUser();
        Developer developer = user.getDeveloper();

        Terminal terminal = terminalService.getTerminalByUuid(terminalUuid);

        if (terminal == null) {
            response.setStatus("error");
            response.setMessage("Terminal not found");
            return ResponseEntity.ok(response);
        }

        if (developer != null && developer.getId() != terminal.getDeveloper().getId()) {
            response.setStatus("error");
            response.setMessage("Terminal not found");
            return ResponseEntity.ok(response);
        }

        List<TerminalInstalledAppObj> apps = terminalService.getTerminalApps(terminal.getId());

        List<TerminalInstalledAppObj> systemApps = apps.stream().filter(app -> app.getAppType().equals(AppType.SYSTEM))
                .collect(Collectors.toList());
        apps.removeAll(systemApps);

        response.setMessage("Terminal apps retrieved successfully");
        response.getData().put("installedApps", apps);
        response.getData().put("systemApps", systemApps);

        return ResponseEntity.ok(response);

    }

    public ResponseEntity<BaseResponse> getTerminalLogs(UserDetailsImpl userDetails, String terminalUuid, int page,
            int size) {
        GenericResponse response = new GenericResponse();

        User user = userDetails.getUser();
        Developer developer = user.getDeveloper();

        Terminal terminal = terminalService.getTerminalByUuid(terminalUuid);

        if (terminal == null) {
            response.setStatus("error");
            response.setMessage("Terminal not found");
            return ResponseEntity.ok(response);
        }

        if (developer != null && developer.getId() != terminal.getDeveloper().getId()) {
            response.setStatus("error");
            response.setMessage("Invalid Terminal identifier");
            return ResponseEntity.ok(response);
        }

        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        Page<TerminalLogObj> logs = terminalService.getTerminalLogs(terminal.getId(), pageable);

        response.setMessage("Terminal logs retrieved successfully");
        response.getData().put("logs", logs.getContent());
        response.getData().put("currentPageNumber", logs.getNumber());
        response.getData().put("totalPages", logs.getTotalPages());
        response.getData().put("totalItems", logs.getTotalElements());
        response.getData().put("hasNext", logs.hasNext());

        return ResponseEntity.ok(response);
    }

    @Transactional
    public ResponseEntity<BaseResponse> syncTerminal(UserDetailsImpl userDetails, String terminalUuid) {
        GenericResponse response = new GenericResponse();
        User user = userDetails.getUser();
        Developer developer = user.getDeveloper();
        Terminal terminal = terminalService.getTerminalByUuid(terminalUuid);
        if (terminal == null) {
            response.setStatus("error");
            response.setMessage("Terminal not found");
            return ResponseEntity.ok(response);
        }

        if (developer != null && developer.getId() != terminal.getDeveloper().getId()) {
            response.setStatus("error");
            response.setMessage("Invalid Terminal identifier");
            return ResponseEntity.ok(response);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("action", "sync_terminal");
        notificationService.sendWebSocket(data, "sync_terminal_" + terminal.getSerialNumber());

        response.setMessage(
                "Sync Command Sent Successfully. If terminal is turned on and connected, the sync should start shortly");

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<BaseResponse> getTerminalTasks(UserDetailsImpl userDetails, String terminalUuid, int page,
            int size, String date) {
        GenericResponse response = new GenericResponse();
        User user = userDetails.getUser();
        Developer developer = user.getDeveloper();
        Terminal terminal = terminalService.getTerminalByUuid(terminalUuid);
        if (terminal == null) {
            response.setStatus("error");
            response.setMessage("Terminal not found");
            return ResponseEntity.ok(response);
        }

        if (developer != null && developer.getId() != terminal.getDeveloper().getId()) {
            response.setStatus("error");
            response.setMessage("Invalid Terminal identifier");
            return ResponseEntity.ok(response);
        }
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        LocalDateTime from = null;
        LocalDateTime to = null;
        if (date != null) {
            from = LocalDate.parse(date).atStartOfDay();
            to = LocalDate.parse(date).atTime(LocalTime.MAX);
        }

        Page<TerminalTaskObj> tasks = taskService.getTaskTerminalByTerminal(pageable, terminal.getId(), from, to);

        // List<TerminalTaskObj> taskObjs =
        // tasks.getContent().stream().map(TerminalTaskObj::new)
        // .collect(Collectors.toList());

        response.setMessage("Terminal tasks retrieved successfully");
        response.getData().put("tasks", tasks.getContent());
        response.getData().put("currentPageNumber", tasks.getNumber());
        response.getData().put("totalPages", tasks.getTotalPages());
        response.getData().put("totalItems", tasks.getTotalElements());
        response.getData().put("hasNext", tasks.hasNext());
        return ResponseEntity.ok(response);
    }

    @Transactional
    public ResponseEntity<BaseResponse> cancelTerminalTask(UserDetailsImpl userDetails, String terminalUuid,
            String taskUuid) {
        GenericResponse response = new GenericResponse();
        try {
            User user = userDetails.getUser();
            Developer developer = user.getDeveloper();

            Terminal terminal = terminalService.getTerminalByUuid(terminalUuid);
            if (terminal == null) {
                response.setStatus("error");
                response.setMessage("Terminal not found");
                return ResponseEntity.ok(response);
            }

            if (developer != null && developer.getId() != terminal.getDeveloper().getId()) {
                response.setStatus("error");
                response.setMessage("Terminal not found");
                return ResponseEntity.ok(response);
            }

            Task task = taskService.getTaskByUuid(taskUuid);
            if (task == null) {
                response.setStatus("error");
                response.setMessage("Task not found");
                return ResponseEntity.ok(response);
            }

            TaskTerminal taskTerminal = taskService.getTaskTerminalByTerminalAndTask(terminal, task);

            if (taskTerminal == null) {
                response.setStatus("error");
                response.setMessage("Terminal task not found");
                return ResponseEntity.ok(response);
            }

            if (taskTerminal.getStatus() != StatusType.NOT_STARTED) {
                response.setStatus("error");
                response.setMessage("Terminal task already " + taskTerminal.getStatus().name());
                return ResponseEntity.ok(response);
            }

            taskTerminal.setStatus(StatusType.CANCELLED);
            taskService.getTaskTerminalRepository().save(taskTerminal);

            response.setMessage("Terminal task canceled successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("cancelTerminalTask", e);
            response.setStatus("error");
            response.setMessage("Exception occurred while canceling terminal task");
            return ResponseEntity.ok(response);
        }

    }

    @Transactional
    public ResponseEntity<BaseResponse> pushTerminalTask(UserDetailsImpl userDetails, String terminalUuid,
            String taskUuid) {
        GenericResponse response = new GenericResponse();
        try {
            User user = userDetails.getUser();
            Developer developer = user.getDeveloper();

            Terminal terminal = terminalService.getTerminalByUuid(terminalUuid);
            if (terminal == null) {
                response.setStatus("error");
                response.setMessage("Terminal not found");
                return ResponseEntity.ok(response);
            }

            if (developer != null && developer.getId() != terminal.getDeveloper().getId()) {
                response.setStatus("error");
                response.setMessage("Terminal not found");
                return ResponseEntity.ok(response);
            }

            Task task = taskService.getTaskByUuid(taskUuid);
            if (task == null) {
                response.setStatus("error");
                response.setMessage("Task not found");
                return ResponseEntity.ok(response);
            }

            TaskTerminal taskTerminal = taskService.getTaskTerminalByTerminalAndTask(terminal, task);

            if (taskTerminal == null) {
                response.setStatus("error");
                response.setMessage("Terminal task not found");
                return ResponseEntity.ok(response);
            }

            StatusType[] needRepush = new StatusType[] { StatusType.NOT_STARTED, StatusType.FAILED };
            if (!Arrays.asList(needRepush).contains(taskTerminal.getStatus())) {
                response.setStatus("error");
                response.setMessage(
                        "Terminal task cannot be re-pushed. Current status is " + taskTerminal.getStatus().name());
                return ResponseEntity.ok(response);
            }

            TaskPushMinObj taskMin = new TaskPushMinObj(task);

            helperService.pushTasKToTerminal(taskMin, task, terminal, taskTerminal);

            response.setMessage("Terminal task re-pushed successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("pushTerminalTask", e);
            response.setStatus("error");
            response.setMessage("Exception occurred while re-pushing terminal task");
            return ResponseEntity.ok(response);
        }

    }

    @Transactional
    public ResponseEntity<BaseResponse> createTerminal(UserDetailsImpl userDetails, CreateTerminalRequest request) {
        GenericResponse response = new GenericResponse();
        try {
            Terminal terminal = terminalService.getTerminalBySerialNumber(request.getSerialNumber());

            if (terminal != null) {
                response.setStatus("error");
                response.setMessage("Terminal with serial number " + request.getSerialNumber() + " already exists");
                return ResponseEntity.ok(response);
            }

            User user = userDetails.getUser();

            Developer developer = user.getDeveloper();

            if (developer == null) {
                developer = developerService.getDeveloperRepository().findById(request.getDeveloperId())
                        .orElseThrow(() -> new RuntimeException("Developer not found"));
            }

            Distributor distributor = developerService.getDistributorByIDAndDeveloperId(request.getDistributorId(),
                    developer.getId());

            if (distributor == null) {
                response.setStatus("error");
                response.setMessage("Distributor not found");
                return ResponseEntity.ok(response);
            }

            Manufacturer manufacturer = resourceService.getManufacturerRepository()
                    .findById(request.getManufacturerId())
                    .orElseThrow(() -> new RuntimeException("Manufacturer not found"));

            ManufacturerModel model = resourceService.getManufacturerModelRespoitory().findById(request.getModelId())
                    .orElseThrow(() -> new RuntimeException("Model not found"));

            if (model.getManufacturer().getId() != manufacturer.getId()) {
                response.setStatus("error");
                response.setMessage("Model not found");
                return ResponseEntity.ok(response);
            }

            terminal = new Terminal();

            terminal.setSerialNumber(request.getSerialNumber());
            terminal.setDeveloper(developer);
            terminal.setDeviceId(request.getDeviceId());
            terminal.setDistributor(distributor);
            terminal.setManufacturer(manufacturer);
            terminal.setModel(model);
            terminal.setUser(user);

            terminalService.getTerminalRepository().save(terminal);

            response.setMessage("Terminal created successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("createTerminal", e);
            response.setStatus("error");
            response.setMessage(e.getMessage());
            return ResponseEntity.ok(response);
        }

    }

    public ResponseEntity<BaseResponse> editTerminal(UserDetailsImpl userDetails, EditTerminalRequest request) {
        GenericResponse response = new GenericResponse();
        try {
            Terminal terminal = terminalService.getTerminalByUuid(request.getTerminalUuid());

            if (terminal == null) {
                response.setStatus("error");
                response.setMessage("Terminal not found");
                return ResponseEntity.ok(response);
            }

            User user = userDetails.getUser();
            Developer developer = user.getDeveloper();

            if (developer != null && developer.getId() != terminal.getDeveloper().getId()) {
                response.setStatus("error");
                response.setMessage("Invalid Terminal identifier");
                return ResponseEntity.ok(response);
            }

            Manufacturer manufacturer = null;
            if (request.getManufacturerId() != null) {
                manufacturer = resourceService.getManufacturerRepository()
                        .findById(request.getManufacturerId())
                        .orElseThrow(() -> new RuntimeException("Manufacturer not found"));
            }

            ManufacturerModel model = null;
            if (request.getModelId() != null) {
                model = resourceService.getManufacturerModelRespoitory().findById(request.getModelId())
                        .orElseThrow(() -> new RuntimeException("Model not found"));

                if (manufacturer != null && model.getManufacturer().getId() != manufacturer.getId()) {
                    response.setStatus("error");
                    response.setMessage("Model not found");
                    return ResponseEntity.ok(response);
                }
            }

            if (request.getSerialNumber() != null) {
                terminal.setSerialNumber(request.getSerialNumber());
            }

            if (request.getDeviceId() != null) {
                terminal.setDeviceId(request.getDeviceId());
            }

            if (manufacturer != null) {
                terminal.setManufacturer(manufacturer);
            }

            if (model != null) {
                terminal.setModel(model);
            }

            terminalService.getTerminalRepository().save(terminal);

            response.setMessage("Terminal updated successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.setStatus("error");
            response.setMessage(e.getMessage());
            return ResponseEntity.ok(response);
        }

    }

    public ResponseEntity<BaseResponse> assignTerminal(UserDetailsImpl userDetails, AssignTerminalsRequest request) {
        GenericResponse response = new GenericResponse();
        try {
            User user = userDetails.getUser();
            Developer developer = user.getDeveloper();

            Distributor distributor = developerService.getDistributorByUuid(request.getDistributorUuid());

            if (distributor == null) {
                response.setStatus("error");
                response.setMessage("Distributor not found");
                return ResponseEntity.ok(response);
            }

            if (developer != null && developer.getId() != distributor.getDeveloper().getId()) {
                response.setStatus("error");
                response.setMessage("Invalid distributor identifier");
                return ResponseEntity.ok(response);
            }

            Group group = developerService.getGroupByUuid(request.getGroupUuid());

            if (group == null) {
                response.setStatus("error");
                response.setMessage("Group not found");
                return ResponseEntity.ok(response);
            }

            if ((developer != null && developer.getId() != group.getDeveloper().getId())
                    || distributor.getId() != group.getDistributor().getId()) {
                response.setStatus("error");
                response.setMessage("Group not found");
                return ResponseEntity.ok(response);
            }

            Long devId = group.getDeveloper().getId();
            if (developer != null) {
                devId = developer.getId();
            }

            terminalService.reeassignTerminals(request.getTerminals(), devId, group.getId(), distributor.getId());

            response.setMessage("Terminals assigned to group successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("assignTerminal", e);
            response.setStatus("error");
            response.setMessage("Exceeption occurred aasigning terminal to group");
            return ResponseEntity.ok(response);
        }

    }

    public ResponseEntity<BaseResponse> geoFenceTerminal(UserDetailsImpl userDetails, String terminalUuid,
            TerminalGeoFenceRequest request) {
        GenericResponse response = new GenericResponse();
        try {
            User user = userDetails.getUser();
            Developer developer = user.getDeveloper();

            Terminal terminal = terminalService.getTerminalByUuid(terminalUuid);

            if (terminal == null) {
                response.setStatus("error");
                response.setMessage("Terminal not found");
                return ResponseEntity.ok(response);
            }

            if (developer != null && developer.getId() != terminal.getDeveloper().getId()) {
                response.setStatus("error");
                response.setMessage("Invalid Terminal identifier");
                return ResponseEntity.ok(response);
            }

            TerminalGeoFence terminalGeoFence = terminal.getTerminalGeoFence();

            if (request.getEnable()) {
                if (terminalGeoFence == null && request.getLatitude() == 0 || request.getLongitude() == 0
                        || request.getRadius() == 0) {
                    response.setStatus("error");
                    response.setMessage("Geofencing coordinates is required");
                    return ResponseEntity.ok(response);
                }
                if (terminalGeoFence == null) {
                    terminalGeoFence = new TerminalGeoFence();
                    terminalGeoFence.setTerminal(terminal);
                }
                if (request.getLatitude() > 0 || request.getLatitude() < 0) {
                    terminalGeoFence.setLatitude(request.getLatitude() + "");
                } else {
                    if (terminalGeoFence.getLatitude() == null) {
                        response.setStatus("error");
                        response.setMessage("Latitude is required");
                        return ResponseEntity.ok(response);
                    }
                }

                if (request.getLongitude() > 0 || request.getLongitude() < 0) {
                    terminalGeoFence.setLongitude(request.getLongitude() + "");
                } else {
                    if (terminalGeoFence.getLongitude() == null) {
                        response.setStatus("error");
                        response.setMessage("Longitude is required");
                        return ResponseEntity.ok(response);
                    }
                }
                if (request.getRadius() > 0) {
                    terminalGeoFence.setRadius(request.getRadius());
                } else {
                    response.setStatus("error");
                    response.setMessage("Radius is required");
                    return ResponseEntity.ok(response);
                }

                terminalGeoFence.setAddress(request.getAddress());

                terminalService.getTerminalGeoFenceRepository().save(terminalGeoFence);
                terminal.setTerminalGeoFence(terminalGeoFence);

                double centerLatitude = Double.valueOf(terminalGeoFence.getLatitude());
                double centerLongitude = Double.valueOf(terminalGeoFence.getLongitude());
                double terminalLatitude = Double.valueOf(terminal.getTerminalInfo().getLatitude());
                double terminalLongitude = Double.valueOf(terminal.getTerminalInfo().getLongitude());
                double radius = terminalGeoFence.getRadius();

                boolean isOnline = geoFencer.isOnline(centerLatitude, centerLongitude, terminalLatitude,
                        terminalLongitude, radius);

                terminalGeoFence.setStatus(isOnline ? StatusType.ONLINE : StatusType.OFFLINE);
                terminalService.getTerminalGeoFenceRepository().save(terminalGeoFence);

                if (!isOnline) {
                    try {
                        geoFencer.pushGeoShutdown(terminal.getUuid());
                    } catch (Exception e) {
                        log.error("Exception from pushGeoShutdown", e.getMessage());
                    }
                }
            } else {
                terminal.setTerminalGeoFence(null);
            }

            terminal.setGeofencingEnabled(request.getEnable());
            terminalService.getTerminalRepository().save(terminal);

            response.setMessage("Terminal geofence updated successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("geoFenceTerminal", e);
            response.setStatus("error");
            response.setMessage("Exception occurred while geofencing terminal");
            return ResponseEntity.ok(response);
        }

    }

    public ResponseEntity<BaseResponse> deleteTerminal(UserDetailsImpl userDetails, String terminalUuid) {
        GenericResponse response = new GenericResponse();
        try {
            User user = userDetails.getUser();
            Developer developer = user.getDeveloper();

            Terminal terminal = terminalService.getTerminalByUuid(terminalUuid);

            if (terminal == null) {
                response.setStatus("error");
                response.setMessage("Terminal not found");
                return ResponseEntity.ok(response);
            }

            if (developer != null && developer.getId() != terminal.getDeveloper().getId()) {
                response.setStatus("error");
                response.setMessage("Invalid Terminal identifier");
                return ResponseEntity.ok(response);
            }

            handleDeleteTerminal(terminal);

            response.setMessage("Terminal deleted successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.setStatus("error");
            response.setMessage(e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    private void handleDeleteTerminal(Terminal terminal) {
        if (terminal.getStatus().equals(StatusType.UNSYNCED)) {
            terminalService.deleteTerminal(terminal.getId());
        } else {
            terminal.setDeleted(true);
            terminal.setStatus(StatusType.DELETED);
            terminal.setSerialNumber(terminal.getSerialNumber() + ".DELETED");
            terminal.setDeviceId(terminal.getDeviceId() + ".DELETED");

            terminalService.getTerminalRepository().save(terminal);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("action", "update_terminallist");
        notificationService.sendWebSocket(data, "terminallist_update_" + terminal.getDeveloper().getUuid());
        notificationService.sendWebSocket(data, "terminallist_update_admin");

    }

    public ResponseEntity<BaseResponse> deleteBulkTerminal(UserDetailsImpl userDetails,
            DeleteBulkTerminalRequest request) {
        GenericResponse response = new GenericResponse();
        try {
            User user = userDetails.getUser();
            Developer developer = user.getDeveloper();
            Long devId = null;
            if (developer != null) {
                devId = developer.getId();
            }

            List<Terminal> terminalList = terminalService.getTerminalsByIds(request.getTerminals(), devId);

            for (Terminal terminal : terminalList) {
                executorService.execute(() -> {
                    handleDeleteTerminal(terminal);
                });

            }

            response.setMessage("Terminals deleted successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.setStatus("error");
            response.setMessage(e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    public ResponseEntity<BaseResponse> createTerminalBulk(UserDetailsImpl userDetails, Long developerId,
            Long distributorId, Long manufacturerId, Long modelId, String osType,
            MultipartFile file) {
        GenericResponse response = new GenericResponse();
        try {
            User user = userDetails.getUser();
            Developer developer = user.getDeveloper();
            if (developer == null) {
                developer = developerService.getDeveloperRepository().findById(developerId)
                        .orElseThrow(() -> new RuntimeException("Developer not found"));
            }

            Distributor distributor = developerService.getDistributorRepository().findById(distributorId)
                    .orElseThrow(() -> new RuntimeException("Distributor not found"));

            if (developer.getId() != distributor.getDeveloper().getId()) {
                response.setStatus("error");
                response.setMessage("Distributor not found");
                return ResponseEntity.ok(response);
            }

            Manufacturer manufacturer = resourceService.getManufacturerRepository().findById(manufacturerId)
                    .orElseThrow(() -> new RuntimeException("Manufacturer not found"));

            ManufacturerModel model = resourceService.getManufacturerModelRespoitory().findById(modelId)
                    .orElseThrow(() -> new RuntimeException("Model not found"));

            if (manufacturer.getId() != model.getManufacturer().getId()) {
                response.setStatus("error");
                response.setMessage("Invalid model identifier");
                return ResponseEntity.ok(response);
            }

            OsType terminalOs = OsType.valueOf(osType.toUpperCase());

            if (model.getOsType() != terminalOs) {
                response.setStatus("error");
                response.setMessage("Invalid model OS type selected");
                return ResponseEntity.ok(response);
            }

            List<Map<String, String>> data = ExcelUtil.readFile(file);

            helperService.savedImportedTerminal(developer, distributor, manufacturer, model, data, terminalOs, user);

            response.setMessage("Terminal upload in progress");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.setStatus("error");
            response.setMessage(e.getMessage());
            return ResponseEntity.ok(response);
        }

    }

    // public void addSubscription(String topic) {
    // mqttMessageDrivenChannelAdapter.addTopic(topic);
    // System.out.println("Subscribed to topic: " + topic);
    // }

    // public void removeSubscription(String topic) {
    // mqttMessageDrivenChannelAdapter.removeTopic(topic);
    // System.out.println("Unsubscribed from topic: " + topic);
    // }

    // public void handleMqttInbound() {

    // }
}
