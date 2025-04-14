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

import com.kaydev.appstore.models.dto.objects.RemoteConnectionObj;
import com.kaydev.appstore.models.entities.RemoteConnection;
import com.kaydev.appstore.models.enums.StatusType;

@Repository
public interface RemoteConnectionRepository
                extends JpaRepository<RemoteConnection, Long>, JpaSpecificationExecutor<RemoteConnection> {
        // @Query("SELECT new
        // com.iisysgroup.itexstore.models.dto.objects.RemoteConnectionObj(r) FROM
        // RemoteConnection r ")
        @NonNull
        Page<RemoteConnection> findAll(@NonNull Specification<RemoteConnection> spec, @NonNull Pageable pageable);

        @Query("SELECT new com.iisysgroup.itexstore.models.dto.objects.RemoteConnectionObj(r) FROM RemoteConnection r "
                        +
                        "LEFT JOIN r.developer d " +
                        "LEFT JOIN r.terminal t " +
                        "LEFT JOIN r.user u " +
                        "where (:developerId IS NULL OR d.id = :developerId) and (:connectionId IS NULL OR r.connectionId = :connectionId) and (:terminalId IS NULL OR t.id = :terminalId) and (:userId IS NULL OR u.id = :userId) and (:status IS NULL OR r.status = :status)")
        Page<RemoteConnectionObj> findAllByFilter(Pageable pageable, Long developerId,
                        String connectionId,
                        Long terminalId, Long userId, StatusType status);

        Optional<RemoteConnection> findByConnectionId(@NonNull String connectionId);
}
