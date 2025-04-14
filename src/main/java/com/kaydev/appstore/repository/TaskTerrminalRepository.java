package com.kaydev.appstore.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kaydev.appstore.models.dto.objects.TaskTerminalObj;
import com.kaydev.appstore.models.dto.objects.TerminalTaskObj;
import com.kaydev.appstore.models.dto.objects.dashboard.MonthlyDownload;
import com.kaydev.appstore.models.dto.objects.export.TaskTerminalObjExp;
import com.kaydev.appstore.models.entities.Task;
import com.kaydev.appstore.models.entities.TaskTerminal;
import com.kaydev.appstore.models.entities.Terminal;
import com.kaydev.appstore.models.enums.StatusType;
import com.kaydev.appstore.models.enums.TaskType;

@Repository
public interface TaskTerrminalRepository
                extends JpaRepository<TaskTerminal, Long>, JpaSpecificationExecutor<TaskTerminal> {
        // @Query("SELECT new
        // com.iisysgroup.itexstore.models.dto.objects.TaskTerminalObj(t) FROM
        // TaskTerminal t ")
        @NonNull
        Page<TaskTerminal> findAll(@NonNull Specification<TaskTerminal> spec, @NonNull Pageable pageable);

        @Query("SELECT new com.iisysgroup.itexstore.models.dto.objects.TaskTerminalObj(t) FROM TaskTerminal t where t.task.id = :taskId and (:status IS NULL OR t.status = :status) and (:search IS NULL OR lower(t.terminal.serialNumber) like lower(concat('%', :search, '%')))")
        Page<TaskTerminalObj> findAllByTaskIdFilter(Pageable pageable, Long taskId,
                        StatusType status, String search);

        @Query("SELECT new com.iisysgroup.itexstore.models.dto.objects.export.TaskTerminalObjExp(t) FROM TaskTerminal t where t.task.id = :taskId")
        Page<TaskTerminalObjExp> findAllByTaskIdFilterForExport(Pageable pageable, Long taskId);

        Optional<TaskTerminalObj> findByUuid(@NonNull String uuid);

        Optional<TaskTerminal> findByTerminalAndTask(Terminal terminal, Task task);

        @Query("SELECT t FROM TaskTerminal t WHERE t.terminal = :terminal AND t.status = :status")
        List<TaskTerminal> findAllByTerminalAndStatus(Terminal terminal, StatusType status);

        @Query("SELECT new com.iisysgroup.itexstore.models.dto.objects.TerminalTaskObj(t) FROM TaskTerminal t WHERE t.terminal.id = :terminalId AND (:from IS NULL OR t.createdAt >= :from) AND (:to IS NULL OR t.createdAt <= :to)")
        Page<TerminalTaskObj> findAllByTerminal(Long terminalId, LocalDateTime from, LocalDateTime to,
                        Pageable pageable);

        @Modifying
        @Transactional
        @Query("DELETE FROM TaskTerminal t WHERE t.terminal.id = :terminalId")
        void deleteAllByTerminalId(Long terminalId);

        @Modifying
        @Transactional
        @Query("UPDATE TaskTerminal t SET t.status = :status WHERE t.task.id = :taskId AND t.status = :previousStatus")
        int updateAllPendingTaskStatus(Long taskId, StatusType status, StatusType previousStatus);

        @Query("SELECT new com.iisysgroup.itexstore.models.dto.objects.dashboard.MonthlyDownload(DATE(d.updatedAt), COUNT(d.id)) "
                        +
                        "FROM TaskTerminal d " +
                        "WHERE d.updatedAt >= :startOfMonth AND d.updatedAt <= :endOfMonth AND d.status = :status AND d.task.taskType = :taskType "
                        +
                        "AND (:developerId IS NULL OR d.task.developer.id = :developerId) " +
                        "GROUP BY DATE(d.updatedAt)")
        List<MonthlyDownload> findMonthlyDownloads(LocalDateTime startOfMonth,
                        LocalDateTime endOfMonth, StatusType status,
                        TaskType taskType,
                        Long developerId);

}
