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

import com.kaydev.appstore.models.dto.objects.DeveloperMinObj;
import com.kaydev.appstore.models.dto.objects.DeveloperObj;
import com.kaydev.appstore.models.entities.Developer;
import com.kaydev.appstore.models.enums.StatusType;

@Repository
public interface DeveloperRepository extends JpaRepository<Developer, Long>, JpaSpecificationExecutor<Developer> {

    @NonNull
    Page<Developer> findAll(@NonNull Specification<Developer> spec, @NonNull Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Developer d SET d.status = :status WHERE d.expiryDate <= :yesterday AND d.status = :currentStatus")
    void updateExpiredDevelopers(LocalDateTime yesterday, StatusType status, StatusType currentStatus);

    Optional<Developer> findByUuid(String uuid);

    Optional<Developer> findByOrganizationName(String organizationName);

    @Query("SELECT new com.iisysgroup.itexstore.models.dto.objects.DeveloperMinObj(d) FROM Developer d")
    List<DeveloperMinObj> findAllList();

    @Query("SELECT new com.iisysgroup.itexstore.models.dto.objects.DeveloperObj(d, " +
            "(SELECT COUNT(t) FROM Terminal t WHERE t.developer.id = d.id)) " +
            "FROM Developer d " +
            "LEFT JOIN d.country c " +
            "WHERE (:countryId IS NULL OR c.id = :countryId) " +
            "AND (:status IS NULL OR d.status = :status) " +
            "AND (:search IS NULL OR d.organizationName LIKE CONCAT('%', :search, '%'))")
    Page<DeveloperObj> findAllByDeveloperAndOthers(Pageable pageable, String search, Long countryId, StatusType status);

}
