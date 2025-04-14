package com.kaydev.appstore.repository;

import java.util.Optional;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.kaydev.appstore.models.dto.objects.AppListObj;
import com.kaydev.appstore.models.dto.objects.AppMinObj;
import com.kaydev.appstore.models.dto.objects.AppObj;
import com.kaydev.appstore.models.dto.objects.dashboard.AppList;
import com.kaydev.appstore.models.dto.objects.dashboard.TopApp;
import com.kaydev.appstore.models.entities.App;
import com.kaydev.appstore.models.entities.Developer;
import com.kaydev.appstore.models.enums.AppType;
import com.kaydev.appstore.models.enums.OsType;
import com.kaydev.appstore.models.enums.StatusType;

@Repository
public interface AppRepository extends JpaRepository<App, Long>, JpaSpecificationExecutor<App> {

        @Override
        @Query("SELECT a FROM App a WHERE a.deleted = false")
        @NonNull
        List<App> findAll();

        @Override
        @Query("SELECT a FROM App a WHERE a.deleted = false AND a.id = :id")
        @NonNull
        Optional<App> findById(@NonNull Long id);

        @NonNull
        Page<App> findAll(@NonNull Specification<App> spec, @NonNull Pageable pageable);

        @Query("SELECT new com.iisysgroup.itexstore.models.dto.objects.AppListObj(a) " +
                        "FROM App a LEFT JOIN a.developer d LEFT JOIN a.distributor di LEFT JOIN a.user u LEFT JOIN a.manufacturer m LEFT JOIN a.category c WHERE a.deleted = false AND (:status IS NULL OR a.status = :status) AND (:osType IS NULL OR a.osType = :osType) AND (:search IS NULL OR lower(a.name) like lower(concat('%', :search, '%'))) and (:developerId IS NULL OR d.id = :developerId)  and (:distributorId IS NULL OR di.id = :distributorId) and (:userId IS NULL OR u.id = :userId) and (:manufacturerId IS NULL OR m.id = :manufacturerId) and (:categoryId IS NULL OR c.id = :categoryId) and (:appType IS NULL OR a.appType = :appType)")
        Page<AppListObj> findAllByFilter(Pageable pageable, StatusType status, OsType osType, String search,
                        Long developerId,
                        Long distributorId,
                        Long userId,
                        Long manufacturerId,
                        Long categoryId,
                        AppType appType);

        @Query("SELECT new com.iisysgroup.itexstore.models.dto.objects.dashboard.TopApp(a) FROM App a WHERE a.deleted = false AND (:status IS NULL OR a.status = :status) AND a.developer.id = :developerId")
        Page<TopApp> findTopApp(Pageable pageable, StatusType status, Long developerId);

        @NonNull
        List<App> findAll(@NonNull Specification<App> spec);

        @Query("SELECT new com.iisysgroup.itexstore.models.dto.objects.AppMinObj(a) FROM App a LEFT JOIN a.developer d LEFT JOIN a.distributor di LEFT JOIN a.user u LEFT JOIN a.manufacturer m LEFT JOIN a.category c WHERE a.deleted = false AND (:status IS NULL OR a.status = :status) AND (:osType IS NULL OR a.osType = :osType) AND (:search IS NULL OR lower(a.name) like lower(concat('%', :search, '%'))) and (:developerId IS NULL OR d.id = :developerId)  and (:distributorId IS NULL OR di.id = :distributorId) and (:userId IS NULL OR u.id = :userId) and (:manufacturerId IS NULL OR m.id = :manufacturerId)  and (:categoryId IS NULL OR c.id = :categoryId) and (:appType IS NULL OR a.appType = :appType)")
        List<AppMinObj> findAllMinByFilter(StatusType status, OsType osType, String search,
                        Long developerId,
                        Long distributorId,
                        Long userId,
                        Long manufacturerId,
                        Long categoryId,
                        AppType appType);

        @Query("SELECT new com.iisysgroup.itexstore.models.dto.objects.dashboard.TopApp(a) FROM App a WHERE a.deleted = false AND (:developerId IS NULL OR a.developer.id = :developerId) AND a.version.createdAt > :date AND a.status = :status ORDER BY a.version.createdAt DESC LIMIT :limit")
        List<TopApp> findNewApps(Long developerId, int limit, LocalDateTime date, StatusType status);

        @Query("SELECT new com.iisysgroup.itexstore.models.dto.objects.AppMinObj(a) FROM App a WHERE a.deleted = false AND a.appType = :appType AND a.manufacturer.id = :manufacturerId AND a.developer IS NULL")
        List<AppMinObj> findByAppTypeAndManufacturerId(AppType appType, Long manufacturerId);

        @Query("SELECT a FROM App a WHERE a.deleted = false AND a.uuid = :uuid")
        Optional<App> findByUuid(@NonNull String uuid);

        @Query("SELECT a FROM App a WHERE a.deleted = false AND a.packageName = :packageName")
        Optional<App> findByPackageName(@NonNull String packageName);

        @Query("SELECT new com.iisysgroup.itexstore.models.dto.objects.AppObj(a) FROM App a WHERE a.deleted = false AND a.uuid = :uuid AND a.developer = :developer")
        Optional<AppObj> findByUuidAndDeveloper(@NonNull String uuid, Developer developer);

        @Query("SELECT new com.iisysgroup.itexstore.models.dto.objects.dashboard.AppList(a.id, a.uuid, a.osType, a.name) FROM App a WHERE a.deleted = false AND (:developerId IS NULL OR a.developer.id = :developerId)")
        List<AppList> findByDeveloperIdMinList(Long developerId);

        @Query("SELECT count(a) FROM App a WHERE a.deleted = false AND (:developerId IS NULL OR a.developer.id = :developerId)")
        int countByDeveloperId(Long developerId);
}
