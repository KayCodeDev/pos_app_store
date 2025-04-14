package com.kaydev.appstore.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.kaydev.appstore.models.dto.objects.TaskMinObj;
import com.kaydev.appstore.models.entities.Task;
import com.kaydev.appstore.models.enums.StatusType;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
        // @Query("SELECT new com.iisysgroup.itexstore.models.dto.objects.TaskObj(t)
        // FROM Task t ")
        @NonNull
        Page<Task> findAll(@NonNull Specification<Task> spec, @NonNull Pageable pageable);

        @Query("SELECT new com.iisysgroup.itexstore.models.dto.objects.TaskMinObj(t) FROM Task t " +
                        "LEFT JOIN t.developer d " +
                        "LEFT JOIN t.distributor di " +
                        "LEFT JOIN t.group g " +
                        "LEFT JOIN t.user u " +
                        "where (:developerId IS NULL OR d.id = :developerId) and (:distributorId IS NULL OR di.id = :distributorId) and (:groupId IS NULL OR g.id = :groupId) and (:userId IS NULL OR u.id = :userId) and (:status IS NULL OR t.status = :status) and (:search IS NULL OR lower(t.taskName) like lower(concat('%', :search, '%')))")
        Page<TaskMinObj> findMinAllByFilter(Pageable pageable, Long developerId, Long distributorId, Long groupId,
                        Long userId, StatusType status, String search);

        // @Query("SELECT new
        // com.iisysgroup.itexstore.models.dto.objects.export.TaskObjExp(t) FROM Task t
        // where (:developerId IS NULL OR t.developer.id = :developerId) and
        // (:distributorId IS NULL OR t.distributor.id = :distributorId) and (:groupId
        // IS NULL OR t.group.id = :groupId) and (:userId IS NULL OR t.user.id =
        // :userId) and (:status IS NULL OR t.status = :status) and (:search IS NULL OR
        // lower(t.taskName) like lower(concat('%', :search, '%')))")
        // Page<TaskObjExp> findMinAllByFilterExport(Pageable pageable, Long
        // developerId, Long distributorId, Long groupId,
        // Long userId, StatusType status, String search);

        // @Query("SELECT new com.iisysgroup.itexstore.models.dto.objects.TaskMinObj(t)
        // FROM Task t ")
        // List<TaskMin> findAll(Specification<Task> spec, Pageable pageable);

        @Query("SELECT t FROM Task t LEFT JOIN FETCH t.developer LEFT JOIN FETCH t.appVersion where t.uuid = :uuid")
        Optional<Task> findByUuid(@NonNull String uuid);

        @Query("SELECT t FROM Task t LEFT JOIN FETCH t.developer LEFT JOIN FETCH t.appVersion WHERE t.taskId = :taskId")
        Optional<Task> findByTaskId(@NonNull String taskId);
}
