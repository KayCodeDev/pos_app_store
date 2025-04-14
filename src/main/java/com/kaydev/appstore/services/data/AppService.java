package com.kaydev.appstore.services.data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.hibernate.exception.LockAcquisitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kaydev.appstore.models.dto.objects.AppListObj;
import com.kaydev.appstore.models.dto.objects.AppMinObj;
import com.kaydev.appstore.models.dto.objects.AppObj;
import com.kaydev.appstore.models.dto.objects.AppVersionObj;
import com.kaydev.appstore.models.dto.objects.dashboard.AppList;
import com.kaydev.appstore.models.dto.objects.dashboard.AppVersionDownload;
import com.kaydev.appstore.models.dto.objects.dashboard.TopApp;
import com.kaydev.appstore.models.entities.App;
import com.kaydev.appstore.models.entities.AppVersion;
import com.kaydev.appstore.models.entities.Developer;
import com.kaydev.appstore.models.enums.AppType;
import com.kaydev.appstore.models.enums.OsType;
import com.kaydev.appstore.models.enums.StatusType;
import com.kaydev.appstore.repository.AppRepository;
import com.kaydev.appstore.repository.AppScreenShotRepository;
import com.kaydev.appstore.repository.AppVersionRepository;

@Service
@Transactional
public class AppService {
    @Autowired
    private AppRepository appRepository;

    @Autowired
    private AppVersionRepository appVersionRepository;

    @Autowired
    private AppScreenShotRepository appScreenShotRepository;

    public AppRepository getAppRepository() {
        return appRepository;
    }

    public Page<AppListObj> getAllByFilter(Pageable pageable, StatusType status, OsType osType, String search,
            Long developerId,
            Long distributorId,
            Long userId,
            Long manufacturerId,
            Long categoryId,
            AppType appType) {
        // Specification<App> spec = appSpecification.buildSpecification(
        // search,
        // status,
        // osType,
        // developerId,
        // distributorId,
        // userId,
        // manufacturerId,
        // categoryId, appType);
        return appRepository.findAllByFilter(pageable, status, osType, search, developerId, distributorId, userId,
                manufacturerId, categoryId, appType);
    }

    public Page<TopApp> getTopAppByFilter(Pageable pageable, StatusType status, Long developerId) {
        return appRepository.findTopApp(pageable, status, developerId);
    }

    public List<AppMinObj> getAllMinByFilter(StatusType status, OsType osType, String search,
            Long developerId, Long distributorId, Long userId, Long manufacturerId, Long categoryId, AppType appType) {

        return appRepository.findAllMinByFilter(status, osType, search, developerId, distributorId, userId,
                manufacturerId, categoryId, appType);
    }

    public List<TopApp> getNewApps(Long developerId, int limit, LocalDateTime date) {
        return appRepository.findNewApps(developerId, limit, date, StatusType.ACTIVE);
    }

    public List<AppMinObj> getSysemAppsMin(Long manufactturerId) {
        return appRepository.findByAppTypeAndManufacturerId(AppType.SYSTEM, manufactturerId);
    }

    public List<AppList> getMinAppsByDeveloperId(Long developerId) {
        return appRepository.findByDeveloperIdMinList(developerId);
    }

    public App getAppByUuid(String uuid) {
        return appRepository.findByUuid(uuid).orElse(null);
    }

    @Retryable(value = { CannotAcquireLockException.class,
            LockAcquisitionException.class }, maxAttempts = 5, backoff = @Backoff(delay = 10000))
    public App getAppByPackageName(String packageName) {
        return appRepository.findByPackageName(packageName).orElse(null);
    }

    public AppObj getAppByUuidAndDeveloper(String uuid, Developer developer) {
        return appRepository.findByUuidAndDeveloper(uuid, developer).orElse(null);
    }

    public AppVersionRepository getAppVersionRepository() {
        return appVersionRepository;
    }

    @Retryable(value = { CannotAcquireLockException.class,
            LockAcquisitionException.class }, maxAttempts = 5, backoff = @Backoff(delay = 10000))
    public AppVersion getAppVersionByUuid(String uuid) {
        return appVersionRepository.findByUuid(uuid).orElse(null);
    }

    public List<AppVersionObj> getAppVersionByAppId(Long appId) {
        return appVersionRepository.findAllByAppId(appId);
    }

    public AppScreenShotRepository getAppScreenShotRepository() {
        return appScreenShotRepository;
    }

    public List<AppVersionDownload> getAppVersionDownloadByAppId(Long appId) {
        return appVersionRepository.findAppVersionDownloadByAppId(appId);
    }

    @Async("taskExecutor")
    public CompletableFuture<Integer> getTotalAppCount(Long developerId) {
        int appCount = appRepository.countByDeveloperId(developerId);
        return CompletableFuture.completedFuture(appCount);
    }

}
