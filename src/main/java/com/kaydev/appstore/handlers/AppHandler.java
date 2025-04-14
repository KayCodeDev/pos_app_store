package com.kaydev.appstore.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kaydev.appstore.models.dto.objects.AppListObj;
import com.kaydev.appstore.models.dto.objects.AppObj;
import com.kaydev.appstore.models.dto.objects.AppVersionObj;
import com.kaydev.appstore.models.dto.request.is.EditAppRequest;
import com.kaydev.appstore.models.dto.request.is.SearchParams;
import com.kaydev.appstore.models.dto.request.is.SubmitAppRequest;
import com.kaydev.appstore.models.dto.request.is.SubmitAppVersionRequest;
import com.kaydev.appstore.models.dto.response.BaseResponse;
import com.kaydev.appstore.models.dto.response.GenericResponse;
import com.kaydev.appstore.models.dto.utils.AppFileInfo;
import com.kaydev.appstore.models.entities.App;
import com.kaydev.appstore.models.entities.AppScreenShot;
import com.kaydev.appstore.models.entities.AppVersion;
import com.kaydev.appstore.models.entities.Category;
import com.kaydev.appstore.models.entities.Developer;
import com.kaydev.appstore.models.entities.Distributor;
import com.kaydev.appstore.models.entities.Manufacturer;
import com.kaydev.appstore.models.entities.ManufacturerModel;
import com.kaydev.appstore.models.entities.User;
import com.kaydev.appstore.models.enums.AppType;
import com.kaydev.appstore.models.enums.StatusType;
import com.kaydev.appstore.security.implementation.UserDetailsImpl;
import com.kaydev.appstore.services.AppParser;
import com.kaydev.appstore.services.NotificationService;
import com.kaydev.appstore.services.data.AppService;
import com.kaydev.appstore.services.data.DeveloperService;
import com.kaydev.appstore.services.data.ResourceService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AppHandler {
    @Autowired
    private AppService appService;

    @Autowired
    private AppParser appParser;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private DeveloperService developerService;

    @Autowired
    private NotificationService notificationService;

    public ResponseEntity<BaseResponse> storeList(UserDetailsImpl userDetails, SearchParams searchParams) {
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

            Page<AppListObj> apps = appService.getAllByFilter(pageable, searchParams.getStatus(),
                    searchParams.getOsType(),
                    searchParams.getSearch(),
                    devId, searchParams.getDistributorId(),
                    searchParams.getUserId(), searchParams.getManufacturerId(), searchParams.getCategoryId(),
                    searchParams.getAppType());

            // List<AppListObj> appObjs =
            // apps.getContent().stream().map(AppListObj::new).collect(Collectors.toList());

            response.setMessage("Apps retrieved successfully");
            response.getData().put("apps", apps.getContent());
            response.getData().put("currentPageNumber", apps.getNumber());
            response.getData().put("totalPages", apps.getTotalPages());
            response.getData().put("totalItems", apps.getTotalElements());
            response.getData().put("hasNext", apps.hasNext());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("storeList", e);
            response.setStatus("error");
            response.setMessage("Exception occurred while getting apps");
            return ResponseEntity.ok(response);
        }
    }

    public ResponseEntity<BaseResponse> getApp(UserDetailsImpl userDetails, String appUuid) {
        GenericResponse response = new GenericResponse();

        User user = userDetails.getUser();
        Developer developer = user.getDeveloper();

        App app = appService.getAppByUuid(appUuid);
        if (app == null) {
            response.setStatus("error");
            response.setMessage("App not found");
            return ResponseEntity.ok(response);
        }

        if (developer != null && app.getDeveloper() != null && app.getDeveloper().getId() != developer.getId()) {
            response.setStatus("error");
            response.setMessage("App not found");
            return ResponseEntity.ok(response);
        }

        response.setMessage("App retrieved successfully");
        response.getData().put("app", new AppObj(app));

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<BaseResponse> getAppVersions(UserDetailsImpl userDetails, String appUuid) {
        GenericResponse response = new GenericResponse();

        User user = userDetails.getUser();
        Developer developer = user.getDeveloper();

        App app = appService.getAppByUuid(appUuid);
        if (app == null) {
            response.setStatus("error");
            response.setMessage("App not found");
            return ResponseEntity.ok(response);
        }

        if (app.getAppType() == AppType.EXTERNAL && developer != null
                && app.getDeveloper().getId() != developer.getId()) {
            response.setStatus("error");
            response.setMessage("App not found");
            return ResponseEntity.ok(response);
        }

        List<AppVersionObj> appVersions = appService.getAppVersionByAppId(app.getId());

        response.setMessage("App versions retrieved successfully");
        response.getData().put("appVersions", appVersions);

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<BaseResponse> parseApp(UserDetailsImpl userDetails, String osType, MultipartFile file) {
        GenericResponse response = new GenericResponse();
        try {
            User user = userDetails.getUser();
            Developer developer = user.getDeveloper();
            if (developer != null && !developer.getSetting().isCanAddApp()) {
                response.setStatus("error");
                response.setMessage("You are not allowed to add apps");
                return ResponseEntity.ok(response);
            }

            AppFileInfo info = new AppFileInfo();

            if (osType.equalsIgnoreCase("android")) {
                info = appParser.parseApkFile(file);
            } else {
                info = appParser.parseLinuxFile(file);
            }

            response.setMessage("App file parsed successfully");
            response.getData().put("info", info);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("parseApp", e.getMessage());
            response.setStatus("error");
            response.setMessage(e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    public ResponseEntity<BaseResponse> screenShotUpload(UserDetailsImpl userDetails, MultipartFile file) {
        GenericResponse response = new GenericResponse();
        try {
            String ext = appParser.getFileExtension(file);

            String fileName = "uploadedAppScreenShoot_app_file_" + System.currentTimeMillis();
            File tempFile = File.createTempFile(fileName, "." + ext);
            file.transferTo(tempFile);

            String fileUrl = appParser.getAwsService().uploadFile("screenshots/" + tempFile.getName(), tempFile);

            tempFile.delete();

            response.setMessage("Screenshoot uploaded successfully");
            response.getData().put("uploadUrl", fileUrl);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("screenShotUpload", e.getMessage());
            response.setStatus("error");
            response.setMessage("Exception occurred while uploading screenshoot");
            return ResponseEntity.ok(response);
        }
    }

    @Transactional
    public ResponseEntity<BaseResponse> submitApp(UserDetailsImpl userDetails, SubmitAppRequest request)
            throws Exception {
        GenericResponse response = new GenericResponse();
        try {
            User user = userDetails.getUser();
            Developer developer = user.getDeveloper();

            Manufacturer manufacturer = resourceService.getManufacturerRepository()
                    .findById(request.getManufacturerId())
                    .orElseThrow(() -> new RuntimeException("Manufacturer not found"));

            String packageName = request.getAppInfo().getPackageName() + "||" + manufacturer.getManufacturerName() + "_"
                    + request.getOsType();

            App exist = appService.getAppByPackageName(packageName);

            if (exist != null) {
                if (exist.getDeveloper() != null && developer != null
                        && (exist.getDeveloper().getId() != developer.getId())) {
                    response.setStatus("error");
                    response.setMessage(
                            "App with package name already published by another developer.");
                    return ResponseEntity.ok(response);
                }

                response.setStatus("error");
                response.setMessage(
                        "App with this package name already exists. To add a new verion, please update the existing one.");
                return ResponseEntity.ok(response);
            }

            Category category = resourceService.getCategoryRepository().findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            List<ManufacturerModel> models = resourceService.getManufacturerModelsByModelIds(manufacturer.getId(),
                    request.getModelIds());

            Distributor distributor = null;
            if (request.getDistributorId() != null) {
                distributor = developerService.getDistributorRepository().findById(request.getDistributorId())
                        .orElse(null);
            }

            App app = new App();
            app.setAppType(request.getAppType());
            app.setCategory(category);
            app.setCompileSdk(request.getAppInfo().getCompileSdk());
            app.setDescription(request.getDescription());
            app.setDeveloper(developer);
            app.setDistributor(distributor);
            app.setIcon(request.getAppInfo().getIcon());
            app.setManufacturer(manufacturer);
            app.setMinSdk(request.getAppInfo().getMinSdk());
            app.setName(request.getAppInfo().getAppName());
            app.setPackageName(packageName);
            app.setTargetSdk(request.getAppInfo().getTargetSdk());
            app.setMaxSdk(request.getAppInfo().getMaxSdk());
            app.setModels(new ArrayList<>());
            app.getModels().addAll(models);
            app.setOsType(request.getOsType());
            app.setPermissions(request.getAppInfo().getPermissions());
            app.setUser(user);
            appService.getAppRepository().save(app);

            AppVersion version = new AppVersion();
            version.setApp(app);
            version.setVersionCode(request.getAppInfo().getVersionCode() + "");
            version.setDownloadUrl(request.getAppInfo().getFile());
            version.setSize(request.getAppInfo().getSize());
            version.setUpdateDescription(request.getDescription());
            version.setVersion(request.getAppInfo().getVersionName());
            version.setUser(user);
            appService.getAppVersionRepository().save(version);

            for (String url : request.getScreenShoots()) {
                AppScreenShot screenShoot = new AppScreenShot();
                screenShoot.setApp(app);
                screenShoot.setImageUrl(url);
                appService.getAppScreenShotRepository().save(screenShoot);
            }

            app.setVersion(version);
            appService.getAppRepository().save(app);

            if (developer != null) {
                Map<String, Object> data = new HashMap<>();
                data.put("action", "new_event");
                notificationService.sendWebSocket(data, "notify_developer_" + developer.getUuid());
            }

            response.setMessage("App submitted successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("submitApp", e);

            throw new Exception(e.getMessage());
        }
    }

    public ResponseEntity<BaseResponse> submitAppVersion(UserDetailsImpl userDetails, SubmitAppVersionRequest request) {
        GenericResponse response = new GenericResponse();
        try {
            User user = userDetails.getUser();
            Developer developer = user.getDeveloper();

            App exist = appService.getAppByUuid(request.getAppUuid());

            if (exist == null) {
                response.setStatus("error");
                response.setMessage("No app found");
                return ResponseEntity.ok(response);
            }

            if (developer != null && exist.getDeveloper().getId() != developer.getId()) {
                response.setStatus("error");
                response.setMessage("Unauthoized app version submission");
                return ResponseEntity.ok(response);
            }

            if (request.getAppInfo() == null) {
                response.setStatus("error");
                response.setMessage("Invalid request. App info is required");
                return ResponseEntity.ok(response);
            }

            String packageName = request.getAppInfo().getPackageName() + "||"
                    + exist.getManufacturer().getManufacturerName() + "_" + exist.getOsType();

            if (!exist.getPackageName().equals(packageName)) {
                response.setStatus("error");
                response.setMessage("Invalid app package name");
                return ResponseEntity.ok(response);
            }

            List<AppVersion> existAppVersions = exist.getVersions().stream().filter(
                    version -> version.getVersionCode().equals(request.getAppInfo().getVersionCode().toString()))
                    .collect(Collectors.toList());

            if (!existAppVersions.isEmpty()) {
                response.setStatus("error");
                response.setMessage("App version already exist. Update the Version code and submit");
                return ResponseEntity.ok(response);
            }

            exist.setIcon(request.getAppInfo().getIcon());
            exist.setCompileSdk(request.getAppInfo().getCompileSdk());
            exist.setPermissions(request.getAppInfo().getPermissions());
            exist.setTargetSdk(request.getAppInfo().getTargetSdk());
            exist.setMaxSdk(request.getAppInfo().getMaxSdk());
            exist.setMinSdk(request.getAppInfo().getMinSdk());
            appService.getAppRepository().save(exist);

            AppVersion version = new AppVersion();
            version.setApp(exist);
            version.setVersionCode(request.getAppInfo().getVersionCode() + "");
            version.setDownloadUrl(request.getAppInfo().getFile());
            version.setSize(request.getAppInfo().getSize());
            version.setUpdateDescription(request.getDescription());
            version.setVersion(request.getAppInfo().getVersionName());
            version.setUser(user);
            appService.getAppVersionRepository().save(version);

            exist.setVersion(version);
            appService.getAppRepository().save(exist);

            response.setMessage("App version submitted successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("submitAppVersion", e);
            response.setStatus("error");
            response.setMessage("Exception occured while submitting app version");
            return ResponseEntity.ok(response);
        }
    }

    public ResponseEntity<BaseResponse> editApp(UserDetailsImpl userDetails, EditAppRequest request) {
        GenericResponse response = new GenericResponse();
        try {
            User user = userDetails.getUser();
            Developer developer = user.getDeveloper();

            App exist = appService.getAppByUuid(request.getAppUuid());

            if (exist == null) {
                response.setStatus("error");
                response.setMessage("No app found");
                return ResponseEntity.ok(response);
            }

            if (developer != null && exist.getDeveloper().getId() != developer.getId()) {
                response.setStatus("error");
                response.setMessage("Unauthoized app update");
                return ResponseEntity.ok(response);
            }

            Category category = null;
            if (request.getCategoryId() != null) {
                category = resourceService.getCategoryRepository().findById(request.getCategoryId()).orElse(null);
            }

            List<ManufacturerModel> models = resourceService.getManufacturerModelsByModelIds(
                    exist.getManufacturer().getId(),
                    request.getModelIds());

            Distributor distributor = null;
            if (request.getDistributorId() != null) {
                distributor = developerService.getDistributorRepository().findById(request.getDistributorId())
                        .orElse(null);
            }

            if (category != null) {
                exist.setCategory(category);
            }
            exist.setDescription(request.getDescription());
            if (distributor != null) {
                exist.setDistributor(distributor);
            }
            if (!models.isEmpty()) {
                exist.setModels(new ArrayList<>());
                exist.getModels().addAll(models);
            }
            if (request.getOsType() != null) {
                exist.setOsType(request.getOsType());
            }
            exist.setUser(user);
            appService.getAppRepository().save(exist);

            if (request.getScreenShoots() != null && !request.getScreenShoots().isEmpty()) {
                appService.getAppScreenShotRepository().deleteAll(exist.getScreenShots());
                for (String url : request.getScreenShoots()) {
                    AppScreenShot screenShoot = new AppScreenShot();
                    screenShoot.setApp(exist);
                    screenShoot.setImageUrl(url);
                    appService.getAppScreenShotRepository().save(screenShoot);
                }
            }

            response.setMessage("App updated successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("editApp", e);
            response.setStatus("error");
            response.setMessage("Exception occured while updating app");
            return ResponseEntity.ok(response);
        }
    }

    public ResponseEntity<BaseResponse> deleteApp(UserDetailsImpl userDetails, String appUuid) {
        GenericResponse response = new GenericResponse();
        try {
            User user = userDetails.getUser();
            Developer developer = user.getDeveloper();

            App exist = appService.getAppByUuid(appUuid);

            if (exist == null) {
                response.setStatus("error");
                response.setMessage("No app found");
                return ResponseEntity.ok(response);
            }

            if (developer != null && exist.getDeveloper().getId() != developer.getId()) {
                response.setStatus("error");
                response.setMessage("Unauthoized app delete");
                return ResponseEntity.ok(response);
            }

            exist.setDeleted(true);
            exist.setPackageName(exist.getPackageName() + ".deleted");
            exist.setName(exist.getName() + " (Deleted)");
            exist.setStatus(StatusType.DELETED);
            appService.getAppRepository().save(exist);

            response.setMessage("App deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("deleteApp", e);
            response.setStatus("error");
            response.setMessage("Exception occured while deleting app");
            return ResponseEntity.ok(response);
        }
    }

    public ResponseEntity<BaseResponse> modifyAppStatus(UserDetailsImpl userDetails, String appUuid,
            String status) {
        GenericResponse response = new GenericResponse();
        try {
            User user = userDetails.getUser();
            Developer developer = user.getDeveloper();

            App exist = appService.getAppByUuid(appUuid);

            if (exist == null) {
                response.setStatus("error");
                response.setMessage("No app found");
                return ResponseEntity.ok(response);
            }

            if (developer != null && exist.getDeveloper().getId() != developer.getId()) {
                response.setStatus("error");
                response.setMessage("Unauthoized app status update");
                return ResponseEntity.ok(response);
            }

            exist.setStatus(StatusType.valueOf(status.toUpperCase()));
            appService.getAppRepository().save(exist);

            response.setMessage("App status updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("modifyAppStatus", e);
            response.setStatus("error");
            response.setMessage("Exception occured while updating app status");
            return ResponseEntity.ok(response);
        }

    }
}
