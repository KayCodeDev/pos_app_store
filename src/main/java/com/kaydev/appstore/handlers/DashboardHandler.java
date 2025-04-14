package com.kaydev.appstore.handlers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.kaydev.appstore.models.dto.objects.dashboard.AppList;
import com.kaydev.appstore.models.dto.objects.dashboard.AppVersionDownload;
import com.kaydev.appstore.models.dto.objects.dashboard.CountSummary;
import com.kaydev.appstore.models.dto.objects.dashboard.ManufacturerList;
import com.kaydev.appstore.models.dto.objects.dashboard.ManufacturerModelSum;
import com.kaydev.appstore.models.dto.objects.dashboard.ManufacturerTerminalSum;
import com.kaydev.appstore.models.dto.objects.dashboard.MonthlyDownload;
import com.kaydev.appstore.models.dto.objects.dashboard.TopApp;
import com.kaydev.appstore.models.dto.response.BaseResponse;
import com.kaydev.appstore.models.dto.response.GenericResponse;
import com.kaydev.appstore.models.entities.App;
import com.kaydev.appstore.models.entities.Developer;
import com.kaydev.appstore.models.entities.User;
import com.kaydev.appstore.models.enums.StatusType;
import com.kaydev.appstore.security.implementation.UserDetailsImpl;
import com.kaydev.appstore.services.data.AppService;
import com.kaydev.appstore.services.data.ResourceService;
import com.kaydev.appstore.services.data.TaskService;
import com.kaydev.appstore.services.data.TerminalService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DashboardHandler {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private AppService appService;

    @Autowired
    private TerminalService terminalService;

    public ResponseEntity<BaseResponse> storeSummary(UserDetailsImpl userDetails) {
        GenericResponse response = new GenericResponse();
        try {
            User user = userDetails.getUser();
            Developer developer = user.getDeveloper();

            Long devId = null;
            if (developer != null) {
                devId = developer.getId();
            }

            CountSummary countSummary = new CountSummary();

            CompletableFuture<Integer> totalTerminalCount = terminalService.getTotalTerminalCount(devId);
            CompletableFuture<Integer> activeTerminalCount = terminalService.getTerminalCountByStatus(devId,
                    StatusType.ACTIVE);
            CompletableFuture<Integer> offlineTerminalCount = terminalService.getTerminalCountByStatus(devId,
                    StatusType.OFFLINE);
            CompletableFuture<Integer> unsyncedTerminalCount = terminalService.getTerminalCountByStatus(devId,
                    StatusType.UNSYNCED);

            CompletableFuture<Integer> geoOfflineTerminalCount = terminalService.getTerminalCountByGeoStatus(devId,
                    StatusType.OFFLINE);

            CompletableFuture<Integer> totalAppCount = appService.getTotalAppCount(devId);

            CompletableFuture.allOf(totalTerminalCount, activeTerminalCount, offlineTerminalCount,
                    unsyncedTerminalCount, geoOfflineTerminalCount, totalAppCount).join();

            countSummary.setTotalTerminals(totalTerminalCount.get());
            countSummary.setActiveTerminals(activeTerminalCount.get());
            countSummary.setOfflineTerminals(offlineTerminalCount.get());
            countSummary.setUnsyncedTerminals(unsyncedTerminalCount.get());
            countSummary.setTotalApps(totalAppCount.get());
            countSummary.setGeoOfflineTerminals(geoOfflineTerminalCount.get());

            response.setMessage("Store summary retrieved successfully");
            response.getData().put("summary", countSummary);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("storeSummary", e);
            response.setStatus("error");
            response.setMessage("Exception while getting store summary");
            return ResponseEntity.ok(response);
        }
    }

    public ResponseEntity<BaseResponse> getTopApps(UserDetailsImpl userDetails) {
        GenericResponse response = new GenericResponse();
        try {
            User user = userDetails.getUser();
            Developer developer = user.getDeveloper();

            Long devId = null;
            if (developer != null) {
                devId = developer.getId();
            }

            Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "downloadCount"));

            Page<TopApp> apps = appService.getTopAppByFilter(pageable, StatusType.ACTIVE, devId);

            response.setMessage("Top apps retrieved successfully");
            response.getData().put("topApps", apps.getContent());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("getTopApps", e);
            response.setStatus("error");
            response.setMessage("Exception while getting top apps");
            return ResponseEntity.ok(response);
        }

    }

    public ResponseEntity<BaseResponse> getNewApps(UserDetailsImpl userDetails) {
        GenericResponse response = new GenericResponse();
        try {
            User user = userDetails.getUser();
            Developer developer = user.getDeveloper();

            Long devId = null;
            if (developer != null) {
                devId = developer.getId();
            }
            LocalDateTime twoWeeksAgo = LocalDateTime.now().minusWeeks(3);

            List<TopApp> apps = appService.getNewApps(devId, 3, twoWeeksAgo);

            // List<TopApp> topApps =
            // apps.stream().map(TopApp::new).collect(Collectors.toList());

            response.setMessage("New apps retrieved successfully");
            response.getData().put("newApps", apps.stream());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("getNewApps", e);
            response.setStatus("error");
            response.setMessage("Exception while getting new apps");
            return ResponseEntity.ok(response);
        }

    }

    public ResponseEntity<BaseResponse> getMonthlyDownloads(UserDetailsImpl userDetails) {
        GenericResponse response = new GenericResponse();
        try {
            User user = userDetails.getUser();
            Developer developer = user.getDeveloper();

            LocalDateTime startOfMonth = LocalDate.now().minusDays(30).atStartOfDay();
            LocalDateTime now = LocalDate.now().atTime(LocalTime.MAX);

            Long devId = null;
            if (developer != null) {
                devId = developer.getId();
            }

            List<MonthlyDownload> monthlyDownloads = taskService.getMonthlyDownloads(startOfMonth,
                    now, StatusType.DONE, devId);

            response.setMessage("Monthly downloads retrieved successfully");
            response.getData().put("downloads", monthlyDownloads);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("getMonthlyDownloads", e);
            response.setStatus("error");
            response.setMessage("Exception while getting monthly downloads");
            return ResponseEntity.ok(response);
        }
    }

    public ResponseEntity<BaseResponse> getAppVersionDownloadSummary(UserDetailsImpl userDetails, String appUuid) {
        GenericResponse response = new GenericResponse();
        try {
            User user = userDetails.getUser();
            Developer developer = user.getDeveloper();

            Long devId = null;
            if (developer != null) {
                devId = developer.getId();
            }

            List<AppList> appList = appService.getMinAppsByDeveloperId(devId);
            response.getData().put("apps", appList);
            if (appUuid != null) {
                App app = appService.getAppByUuid(appUuid);
                if (app == null) {
                    response.setStatus("error");
                    response.setMessage("App not found");
                    return ResponseEntity.ok(response);
                }

                if (developer != null && app.getDeveloper().getId() != developer.getId()) {
                    response.setStatus("error");
                    response.setMessage("App not found");
                    return ResponseEntity.ok(response);
                }

                List<AppVersionDownload> appVersionDownloads = appService.getAppVersionDownloadByAppId(app.getId());

                response.getData().put("versions", appVersionDownloads);
            } else {
                response.getData().put("versions", null);
            }
            response.setMessage("Success");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("getAppVersionDownloadSummary", e);
            response.setStatus("error");
            response.setMessage("Exception while getting app version download summary");
            return ResponseEntity.ok(response);
        }
    }

    public ResponseEntity<BaseResponse> getTerminalManufacturerSummary(UserDetailsImpl userDetails) {
        GenericResponse response = new GenericResponse();
        try {
            User user = userDetails.getUser();
            Developer developer = user.getDeveloper();

            Long devId = null;
            if (developer != null) {
                devId = developer.getId();
            }

            List<ManufacturerTerminalSum> manufacturers = terminalService.getManufacturerTerminalSum(devId);

            response.setMessage("Manufacturer terminals retrieved successfully");
            response.getData().put("manufacturers", manufacturers);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("getTerminalManufacturerSummary", e);
            response.setStatus("error");
            response.setMessage("Exception while getting terminal manufacturer summary");
            return ResponseEntity.ok(response);
        }

    }

    public ResponseEntity<BaseResponse> getTerminalManufacturerModelSummary(UserDetailsImpl userDetails,
            String manufacturerUuid) {

        GenericResponse response = new GenericResponse();
        try {
            User user = userDetails.getUser();
            Developer developer = user.getDeveloper();

            Long devId = null;
            if (developer != null) {
                devId = developer.getId();
            }

            List<ManufacturerList> manufacturerLists = resourceService.getMinManufacturerList();
            response.getData().put("manufacturers", manufacturerLists);
            if (manufacturerUuid != null) {

                List<ManufacturerModelSum> modelTerminalSums = terminalService.getManufacturerModelSum(manufacturerUuid,
                        devId);

                response.getData().put("models", modelTerminalSums);
            } else {
                response.getData().put("models", null);
            }

            response.setMessage("Success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("getTerminalManufacturerModelSummary", e);
            response.setStatus("error");
            response.setMessage("Exception while getting terminal manufacturer model summary");
            return ResponseEntity.ok(response);
        }

    }

}
