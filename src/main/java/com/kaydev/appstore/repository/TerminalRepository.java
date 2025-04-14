package com.kaydev.appstore.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kaydev.appstore.models.dto.objects.TerminalObj;
import com.kaydev.appstore.models.dto.objects.dashboard.ManufacturerModelSum;
import com.kaydev.appstore.models.dto.objects.dashboard.ManufacturerTerminalSum;
import com.kaydev.appstore.models.dto.objects.export.TerminalObjExp;
import com.kaydev.appstore.models.entities.Developer;
import com.kaydev.appstore.models.entities.Terminal;
import com.kaydev.appstore.models.enums.OsType;
import com.kaydev.appstore.models.enums.StatusType;

@Repository
public interface TerminalRepository extends JpaRepository<Terminal, Long>, JpaSpecificationExecutor<Terminal> {

        @Override
        @Query("SELECT t FROM Terminal t WHERE t.deleted = false")
        @NonNull
        List<Terminal> findAll();

        @Override
        @Query("SELECT t FROM Terminal t LEFT JOIN t.developer LEFT JOIN t.distributor LEFT JOIN t.group LEFT JOIN t.terminalInfo LEFT JOIN t.manufacturer WHERE t.deleted = false AND t.id = :id")
        @NonNull
        Optional<Terminal> findById(@NonNull Long id);

        @Query("SELECT new com.iisysgroup.itexstore.models.dto.objects.TerminalObj(t) FROM Terminal t " +
                        "LEFT JOIN t.developer d " +
                        "LEFT JOIN t.distributor di " +
                        "LEFT JOIN t.group g " +
                        "LEFT JOIN t.manufacturer m " +
                        "LEFT JOIN t.model mm " +
                        "LEFT JOIN t.terminalInfo ti " +
                        "WHERE t.deleted = false " +
                        "AND (:status IS NULL OR t.status = :status) " +
                        "AND (:osType IS NULL OR ti.osType = :osType) " +
                        "AND (:developerId IS NULL OR d.id = :developerId) " +
                        "AND (:developerUuid IS NULL OR d.uuid = :developerUuid) " +
                        "AND (:distributorId IS NULL OR di.id = :distributorId) " +
                        "AND (:distributorUuid IS NULL OR di.uuid = :distributorUuid) " +
                        "AND (:groupId IS NULL OR g.id = :groupId) " +
                        "AND (:groupUuid IS NULL OR g.uuid = :groupUuid) " +
                        "AND (:manufacturerId IS NULL OR m.id = :manufacturerId) " +
                        "AND (:search IS NULL OR (lower(t.serialNumber) LIKE lower(concat('%', :search, '%')))) " +
                        "AND (:modelId IS NULL OR mm.id = :modelId) " +
                        "AND (:startDate IS NULL OR t.createdAt >= :startDate) " +
                        "AND (:endDate IS NULL OR t.createdAt <= :endDate) " +
                        "AND (:lowBattery = false OR (ti.batteryLevel IS NOT NULL AND CAST(ti.batteryLevel AS INTEGER) <= 20)) "
                        +
                        "AND (:noPaper = false OR ti.printer != 'Printer OK') " +
                        "AND (:notCharging = false OR ti.batteryStatus != 'charging') ")
        Page<TerminalObj> findAllByFilter(Pageable pageable, String search, StatusType status, OsType osType,
                        Long developerId, String developerUuid, Long distributorId, String distributorUuid,
                        Long groupId,
                        String groupUuid,
                        Long manufacturerId,
                        Long modelId, LocalDateTime startDate, LocalDateTime endDate, boolean lowBattery,
                        boolean noPaper, boolean notCharging);

        @Query("SELECT new com.iisysgroup.itexstore.models.dto.objects.export.TerminalObjExp(t) from Terminal t " +
                        "LEFT JOIN t.developer d " +
                        "LEFT JOIN t.distributor di " +
                        "LEFT JOIN t.group g " +
                        "LEFT JOIN t.terminalInfo ti " +
                        "where t.deleted = false AND (:search IS NULL OR lower(t.serialNumber) like lower(concat('%', :search, '%'))) AND (:status IS NULL OR t.status = :status) AND (:osType IS NULL OR ti.osType = :osType) AND (:developerId IS NULL OR d.id = :developerId) AND (:developerUuid IS NULL OR d.uuid = :developerUuid) AND (:distributorId IS NULL OR di.id = :distributorId) AND (:distributorUuid IS NULL OR di.uuid = :distributorUuid) AND (:groupId IS NULL OR g.id = :groupId) AND (:groupUuid IS NULL OR g.uuid = :groupUuid) AND (:manufacturerId IS NULL OR t.manufacturer.id = :manufacturerId) AND (:modelId IS NULL OR t.model.id = :modelId) "
                        +
                        "AND (:startDate IS NULL OR t.createdAt >= :startDate) " +
                        "AND (:endDate IS NULL OR t.createdAt <= :endDate) " +
                        "AND (:lowBattery = false OR (ti.batteryLevel IS NOT NULL AND CAST(ti.batteryLevel AS INTEGER) <= 20)) "
                        +
                        "AND (:noPaper = false OR ti.printer != 'Printer OK') " +
                        "AND (:notCharging = false OR ti.batteryStatus != 'charging')")
        Page<TerminalObjExp> findAllByFilterForExport(Pageable pageable, String search, StatusType status,
                        OsType osType,
                        Long developerId, String developerUuid, Long distributorId, String distributorUuid,
                        Long groupId,
                        String groupUuid,
                        Long manufacturerId,
                        Long modelId, LocalDateTime startDate, LocalDateTime endDate, boolean lowBattery,
                        boolean noPaper, boolean notCharging);

        @Query("SELECT t FROM Terminal t LEFT JOIN t.developer LEFT JOIN t.distributor LEFT JOIN t.group LEFT JOIN t.terminalInfo LEFT JOIN t.manufacturer WHERE t.deleted = false AND (:developerId IS NULL OR t.developer.id = :developerId) AND t.id IN :ids")
        List<Terminal> findAllByIdInAndDeveloper(List<Long> ids, Long developerId);

        @Query("SELECT t FROM Terminal t LEFT JOIN t.developer LEFT JOIN t.distributor LEFT JOIN t.group LEFT JOIN t.terminalInfo LEFT JOIN t.manufacturer WHERE t.deleted = false AND t.developer.id = :developerId AND t.distributor.id = :distributorId")
        List<Terminal> findAllByDistributorIdAndDeveloperId(Long distributorId, Long developerId);

        @Query("SELECT t FROM Terminal t LEFT JOIN t.developer LEFT JOIN t.distributor LEFT JOIN t.group LEFT JOIN t.terminalInfo LEFT JOIN t.manufacturer WHERE t.deleted = false AND t.developer.id = :developerId")
        List<Terminal> findAllByDeveloperId(Long developerId);

        @Modifying
        @Transactional
        @Query("UPDATE Terminal t SET t.group.id = :groupId, t.distributor.id = :distributorId WHERE t.id in :ids AND t.developer.id = :developerId")
        void reassignTerminal(List<Long> ids, Long groupId, Long distributorId, Long developerId);

        @Query("SELECT t FROM Terminal t LEFT JOIN t.developer LEFT JOIN t.distributor LEFT JOIN t.group LEFT JOIN t.terminalInfo LEFT JOIN t.manufacturer WHERE t.deleted = false AND t.developer.id = :developerId AND t.distributor.id = :distributorId AND t.group.id = :groupId")
        List<Terminal> findAllByDistributorIdAndGroupIdAndDeveloperId(Long distributorId, Long groupId,
                        Long developerId);

        @Query("SELECT t FROM Terminal t LEFT JOIN t.developer LEFT JOIN t.distributor LEFT JOIN t.group LEFT JOIN t.terminalInfo LEFT JOIN t.manufacturer WHERE t.deleted = false AND t.id = :id AND t.developer = :developer")
        Optional<Terminal> findByIdAndDeveloper(@NonNull Long id, Developer developer);

        @Query("SELECT new com.iisysgroup.itexstore.models.dto.objects.TerminalObj(t) FROM Terminal t WHERE t.deleted = false AND t.uuid = :uuid AND t.developer = :developer")
        Optional<TerminalObj> findByUuidAndDeveloper(@NonNull String uuid, Developer developer);

        @Query("SELECT t FROM Terminal t LEFT JOIN t.developer LEFT JOIN t.distributor LEFT JOIN t.group LEFT JOIN t.terminalInfo LEFT JOIN t.manufacturer LEFT JOIN t.model LEFT JOIN t.terminalGeoFence WHERE t.deleted = false AND t.uuid = :uuid")
        Optional<Terminal> findByUuid(@NonNull String uuid);

        @Query("SELECT t FROM Terminal t LEFT JOIN t.developer LEFT JOIN t.distributor LEFT JOIN t.group LEFT JOIN t.terminalInfo LEFT JOIN t.manufacturer LEFT JOIN t.model LEFT JOIN t.terminalGeoFence WHERE t.deleted = false AND t.serialNumber = :serialNumber")
        Optional<Terminal> findBySerialNumber(@NonNull String serialNumber);

        @Query("SELECT count(t) FROM Terminal t WHERE t.deleted = false AND t.distributor.id = :distributorId")
        int countByDistributorId(Long distributorId);

        @Query("SELECT count(t) FROM Terminal t WHERE t.deleted = false AND t.group.id = :groupId")
        int countByGroupId(Long groupId);

        @Query("SELECT count(t) FROM Terminal t WHERE t.deleted = false AND (:developerId IS NULL OR t.developer.id = :developerId)")
        int countByDeveloperId(Long developerId);

        @Query("SELECT count(t) FROM Terminal t WHERE t.deleted = false AND (:developerId IS NULL OR t.developer.id = :developerId) AND t.status = :status")
        int countByDeveloperIdAndStatus(Long developerId, StatusType status);

        @Query("SELECT count(t) FROM Terminal t WHERE t.deleted = false AND (:developerId IS NULL OR t.developer.id = :developerId) AND t.terminalGeoFence.status = :status")
        int countByDeveloperIdAndGeoStatus(Long developerId, StatusType status);

        @Query("SELECT new com.iisysgroup.itexstore.models.dto.objects.dashboard.ManufacturerTerminalSum(t.manufacturer.manufacturerName, COUNT(t.id)) "
                        +
                        "FROM Terminal t " +
                        "WHERE t.deleted = false AND (:developerId IS NULL OR t.developer.id = :developerId) " +
                        "GROUP BY t.manufacturer.manufacturerName ORDER BY COUNT(t.manufacturer.manufacturerName) DESC")
        List<ManufacturerTerminalSum> findManufacturerTerminalSum(Long developerId);

        @Query("SELECT new com.iisysgroup.itexstore.models.dto.objects.dashboard.ManufacturerModelSum(t.model.modelName, COUNT(t.id)) "
                        +
                        "FROM Terminal t " +
                        "WHERE t.deleted = false AND (:developerId IS NULL OR t.developer.id = :developerId) AND t.manufacturer.uuid = :manufacturerUuid "
                        +
                        "GROUP BY t.model.modelName ORDER BY COUNT(t.model.modelName) DESC")
        List<ManufacturerModelSum> findManufacturerModelSum(String manufacturerUuid, Long developerId);

        @Modifying
        @Transactional
        @Query("UPDATE Terminal t SET t.status = :status WHERE t.deleted = false AND t.lastHeartbeat < :lastHeartBeatBefore AND t.status = :previousStatus")
        void updateByLastHeartBeatBefore(LocalDateTime lastHeartBeatBefore, StatusType status,
                        StatusType previousStatus);
}
