package com.kaydev.appstore.services.data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.hibernate.exception.LockAcquisitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kaydev.appstore.models.dto.objects.TerminalInstalledAppObj;
import com.kaydev.appstore.models.dto.objects.TerminalLogObj;
import com.kaydev.appstore.models.dto.objects.TerminalObj;
import com.kaydev.appstore.models.dto.objects.dashboard.ManufacturerModelSum;
import com.kaydev.appstore.models.dto.objects.dashboard.ManufacturerTerminalSum;
import com.kaydev.appstore.models.dto.objects.export.TerminalChargableObjExp;
import com.kaydev.appstore.models.dto.objects.export.TerminalObjExp;
import com.kaydev.appstore.models.entities.Developer;
import com.kaydev.appstore.models.entities.Terminal;
import com.kaydev.appstore.models.entities.TerminalInstalledApp;
import com.kaydev.appstore.models.enums.OsType;
import com.kaydev.appstore.models.enums.StatusType;
import com.kaydev.appstore.repository.TaskTerrminalRepository;
import com.kaydev.appstore.repository.TerminalGeoFenceRepository;
import com.kaydev.appstore.repository.TerminalInfoRepository;
import com.kaydev.appstore.repository.TerminalInstalledAppRepository;
import com.kaydev.appstore.repository.TerminalLogRepository;
import com.kaydev.appstore.repository.TerminalRepository;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

@Service
@Transactional
public class TerminalService {
    @Autowired
    private TerminalRepository terminalRepository;

    @Autowired
    private TerminalInfoRepository terminalInfoRepository;

    @Autowired
    private TerminalGeoFenceRepository terminalGeoFenceRepository;

    @Autowired
    private TerminalInstalledAppRepository terminalInstalledAppRepository;

    @Autowired
    private TaskTerrminalRepository taskTerminalRepository;

    @Autowired
    private TerminalLogRepository terminalLogRepository;

    @Scheduled(fixedRate = 30 * 60 * 1000)
    public void updateTerminalStatus() {
        LocalDateTime thirtyMinsAgo = LocalDateTime.now().minusMinutes(30);
        terminalRepository.updateByLastHeartBeatBefore(thirtyMinsAgo, StatusType.OFFLINE, StatusType.ACTIVE);
    }

    public TerminalRepository getTerminalRepository() {
        return terminalRepository;
    }

    @Transactional
    public void deleteTerminal(Long terminalId) {
        Terminal terminal = terminalRepository.findById(
                terminalId)
                .orElse(null);

        taskTerminalRepository.deleteAllByTerminalId(terminalId);

        if (terminal != null) {
            terminalRepository.delete(terminal);
        }
    }

    public Page<TerminalObj> getAllTerminalByFilter(Pageable pageable, String search, StatusType status, OsType osType,
            Long developerId, String developerUuid, Long distributorId, String distributorUuid, Long groupId,
            String groupUuid,
            Long manufacturerId,
            Long modelId, LocalDateTime startDate, LocalDateTime endDate, boolean lowBattery,
            boolean noPaper, boolean notCharging) {
        // Specification<Terminal> spec = terminalSpecification.buildSpecification(
        // search,
        // status,
        // osType,
        // developerId,
        // developerUuid,
        // distributorId,
        // distributorUuid,
        // groupId,
        // groupUuid,
        // manufacturerId,
        // modelId);
        return terminalRepository.findAllByFilter(pageable, search, status, osType, developerId, developerUuid,
                distributorId, distributorUuid, groupId, groupUuid, manufacturerId, modelId, startDate, endDate,
                lowBattery, noPaper, notCharging);
    }

    public Page<TerminalObjExp> getAllTerminalByFilterForExport(Pageable pageable, String search, StatusType status,
            OsType osType,
            Long developerId, String developerUuid, Long distributorId, String distributorUuid, Long groupId,
            String groupUuid,
            Long manufacturerId,
            Long modelId, LocalDateTime startDate, LocalDateTime endDate, boolean lowBattery,
            boolean noPaper, boolean notCharging) {

        return terminalRepository.findAllByFilterForExport(pageable, search, status, osType, developerId, developerUuid,
                distributorId, distributorUuid, groupId, groupUuid, manufacturerId, modelId, startDate, endDate,
                lowBattery, noPaper, notCharging);
    }

    @Retryable(value = { CannotAcquireLockException.class,
            LockAcquisitionException.class }, maxAttempts = 5, backoff = @Backoff(delay = 10000))
    public Terminal getTerminalByUuid(String uuid) {
        try {
            return terminalRepository.findByUuid(uuid).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    // @Retryable(value = { CannotAcquireLockException.class }, maxAttempts = 5,
    // backoff = @Backoff(delay = 2000))
    public Terminal getTerminalBySerialNumber(String serialNumber) {
        try {
            return terminalRepository.findBySerialNumber(serialNumber).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    public TerminalObj getTerminalByUuidAndDeveloper(String uuid, Developer developer) {
        return terminalRepository.findByUuidAndDeveloper(uuid, developer).orElse(null);
    }

    public Terminal getTerminalByIdAndDeveloper(Long id, Developer developer) {
        return terminalRepository.findByIdAndDeveloper(id, developer).orElse(null);
    }

    public List<Terminal> getTerminalsByIds(List<Long> ids, Long developerId) {
        return terminalRepository.findAllByIdInAndDeveloper(ids, developerId);
    }

    public void reeassignTerminals(List<Long> ids, Long developerId, Long groupId, Long distributorId) {
        terminalRepository.reassignTerminal(ids, groupId, distributorId, developerId);
    }

    public List<Terminal> getTerminalsByDistributorIdAndDeveloperId(Long distributorId, Long developerId) {
        return terminalRepository.findAllByDistributorIdAndDeveloperId(distributorId, developerId);
    }

    public List<Terminal> getTerminalsByDeveloperId(Long developerId) {
        return terminalRepository.findAllByDeveloperId(developerId);
    }

    public List<Terminal> getTerminalsByDistributorIdAAndGroupId(Long distributorId, Long groupId, Long developerId) {
        return terminalRepository.findAllByDistributorIdAndGroupIdAndDeveloperId(distributorId, groupId, developerId);
    }

    public int countTerminalByDistributorId(Long distributorId) {
        return terminalRepository.countByDistributorId(distributorId);
    }

    public int countTerminalByGroupId(Long groupId) {
        return terminalRepository.countByGroupId(groupId);
    }

    public TerminalInfoRepository getTerminalInfoRepository() {
        return terminalInfoRepository;
    }

    public TerminalInstalledAppRepository getTerminalInstalledAppRepository() {
        return terminalInstalledAppRepository;
    }

    public List<TerminalInstalledAppObj> getTerminalApps(Long terminalId) {
        return terminalInstalledAppRepository.findAllByTerminal(terminalId);
    }

    public Long countTerminalApp(Terminal terminal) {
        return terminalInstalledAppRepository.countByTerminal(terminal);
    }

    public void deleteAllByTerminalId(Long terminalId) {
        terminalInstalledAppRepository.deleteAllByTerminalId(terminalId);
    }

    public TerminalInstalledAppObj getTerminalAppByUuid(String uuid) {
        return terminalInstalledAppRepository.findByUuid(uuid).orElse(null);
    }

    public TerminalInstalledApp getTerminalAppByPackageName(Terminal terminal, String packageName) {
        return terminalInstalledAppRepository
                .findByTerminalIdAndPackageName(terminal.getId(), packageName)
                .orElse(null);
    }

    public TerminalLogRepository getTerminalLogRepository() {
        return terminalLogRepository;
    }

    public Page<TerminalLogObj> getTerminalLogs(Long terminalId, Pageable pageable) {
        return terminalLogRepository.findAllByTerminalId(terminalId, pageable);
    }

    public Page<TerminalChargableObjExp> getTerminalChargableCount(Pageable pageable, Long developerId,
            LocalDateTime from, LocalDateTime to) {
        return terminalLogRepository.findTerminalChargableCount(pageable, developerId, from, to);
    }

    public TerminalGeoFenceRepository getTerminalGeoFenceRepository() {
        return terminalGeoFenceRepository;
    }

    public List<ManufacturerTerminalSum> getManufacturerTerminalSum(Long developerId) {
        return terminalRepository.findManufacturerTerminalSum(developerId);
    }

    public List<ManufacturerModelSum> getManufacturerModelSum(String manufacturerUuid, Long developerId) {
        return terminalRepository.findManufacturerModelSum(manufacturerUuid, developerId);
    }

    @Async("taskExecutor")
    public CompletableFuture<Integer> getTotalTerminalCount(Long developerId) {
        int terminalCount = terminalRepository.countByDeveloperId(developerId);
        return CompletableFuture.completedFuture(terminalCount);
    }

    @Async("taskExecutor")
    public CompletableFuture<Integer> getTerminalCountByStatus(Long developerId, StatusType status) {
        int terminalCount = terminalRepository.countByDeveloperIdAndStatus(developerId, status);
        return CompletableFuture.completedFuture(terminalCount);
    }

    @Async("taskExecutor")
    public CompletableFuture<Integer> getTerminalCountByGeoStatus(Long developerId, StatusType status) {
        int terminalCount = terminalRepository.countByDeveloperIdAndGeoStatus(developerId, status);
        return CompletableFuture.completedFuture(terminalCount);
    }

}
