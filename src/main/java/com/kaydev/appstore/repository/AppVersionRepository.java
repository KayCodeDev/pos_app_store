package com.kaydev.appstore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kaydev.appstore.models.dto.objects.AppVersionObj;
import com.kaydev.appstore.models.dto.objects.dashboard.AppVersionDownload;
import com.kaydev.appstore.models.entities.AppVersion;

@Repository
public interface AppVersionRepository extends JpaRepository<AppVersion, Long> {

    @Query("SELECT new com.iisysgroup.itexstore.models.dto.objects.AppVersionObj(av) FROM AppVersion av WHERE av.app.id = :appId ORDER BY av.versionCode DESC")
    List<AppVersionObj> findAllByAppId(Long appId);

    Optional<AppVersion> findByUuid(String uuid);

    @Query("DELETE FROM AppVersion av WHERE av.app.id = :appId")
    void deleteByAppId(long appId);

    @Query("SELECT new com.iisysgroup.itexstore.models.dto.objects.dashboard.AppVersionDownload(av.version, av.downloadCount) FROM AppVersion av WHERE av.app.id = :appId")
    List<AppVersionDownload> findAppVersionDownloadByAppId(Long appId);
}
