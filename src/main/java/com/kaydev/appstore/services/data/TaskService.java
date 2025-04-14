package com.kaydev.appstore.services.data;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.exception.LockAcquisitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kaydev.appstore.models.dto.objects.TaskMinObj;
import com.kaydev.appstore.models.dto.objects.TaskTerminalObj;
import com.kaydev.appstore.models.dto.objects.TerminalTaskObj;
import com.kaydev.appstore.models.dto.objects.dashboard.MonthlyDownload;
import com.kaydev.appstore.models.dto.objects.export.TaskTerminalObjExp;
import com.kaydev.appstore.models.entities.Task;
import com.kaydev.appstore.models.entities.TaskTerminal;
import com.kaydev.appstore.models.entities.Terminal;
import com.kaydev.appstore.models.enums.StatusType;
import com.kaydev.appstore.models.enums.TaskType;
import com.kaydev.appstore.repository.TaskRepository;
import com.kaydev.appstore.repository.TaskTerrminalRepository;

@Service
@Transactional
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    // @Autowired
    // private TaskSpecification taskSpecification;

    @Autowired
    private TaskTerrminalRepository taskTerminalRepository;

    public TaskRepository getTaskRepository() {
        return taskRepository;
    }

    public Page<TaskMinObj> getAllTaskByFilter(Pageable pageable, Long developerId, Long distributorId, Long groupId,
            Long userId, StatusType status, String search) {
        // Specification<Task> spec = taskSpecification.buildSpecification(
        // developerId,
        // distributorId,
        // groupId,
        // userId, status, search);
        // return taskRepository.findAll(spec, pageable);

        return taskRepository.findMinAllByFilter(pageable, developerId, distributorId, groupId,
                userId, status, search);
    }

    // public Page<TaskObjExp> getAllTaskFoExport(Pageable pageable, Long
    // developerId, Long distributorId, Long groupId,
    // Long userId, StatusType status, String search) {

    // return taskRepository.findMinAllByFilterExport(pageable, developerId,
    // distributorId, groupId,
    // userId, status, search);
    // }

    @Retryable(value = { CannotAcquireLockException.class,
            LockAcquisitionException.class }, maxAttempts = 5, backoff = @Backoff(delay = 10000))
    public Task getTaskByUuid(String uuid) {
        return taskRepository.findByUuid(uuid).orElse(null);
    }

    @Retryable(value = { CannotAcquireLockException.class,
            LockAcquisitionException.class }, maxAttempts = 5, backoff = @Backoff(delay = 10000))
    public Task getTaskByTaskId(String taskId) {
        return taskRepository.findByTaskId(taskId).orElse(null);
    }

    public TaskTerrminalRepository getTaskTerminalRepository() {
        return taskTerminalRepository;
    }

    public Page<TaskTerminalObj> getAllTaskTerminalByFilter(Pageable pageable, Long taskId,
            StatusType status, String search) {
        // Specification<TaskTerminal> spec =
        // taskTerminalSpecification.buildSpecification(
        // taskId,
        // status,
        // search);
        return taskTerminalRepository.findAllByTaskIdFilter(pageable, taskId,
                status, search);
    }

    public Page<TaskTerminalObjExp> getAllTaskTerminalByFilterForExport(Pageable pageable, Long taskId) {
        return taskTerminalRepository.findAllByTaskIdFilterForExport(pageable, taskId);
    }

    public TaskTerminalObj getTaskTerminalByUuid(String uuid) {
        return taskTerminalRepository.findByUuid(uuid).orElse(null);
    }

    public Page<TerminalTaskObj> getTaskTerminalByTerminal(Pageable pageable, Long terminalId, LocalDateTime from,
            LocalDateTime to) {
        return taskTerminalRepository.findAllByTerminal(terminalId, from, to, pageable);
    }

    public void updateAllPendingTaskStatus(Long taskId, StatusType status, StatusType previousStatus) {
        taskTerminalRepository.updateAllPendingTaskStatus(taskId, status, previousStatus);
    }

    public List<TaskTerminal> getTaskTerminalByTerminalAndStatus(Terminal terminal, StatusType status) {
        return taskTerminalRepository.findAllByTerminalAndStatus(terminal, status);
    }

    @Retryable(value = { CannotAcquireLockException.class,
            LockAcquisitionException.class }, maxAttempts = 5, backoff = @Backoff(delay = 10000))
    public TaskTerminal getTaskTerminalByTerminalAndTask(Terminal terminal, Task task) {
        return taskTerminalRepository.findByTerminalAndTask(terminal, task).orElse(null);
    }

    public List<MonthlyDownload> getMonthlyDownloads(LocalDateTime startOfMonth,
            LocalDateTime endOfMonth, StatusType status,
            Long developerId) {
        return taskTerminalRepository.findMonthlyDownloads(startOfMonth, endOfMonth, status, TaskType.PUSH_APP,
                developerId);
    }
}
