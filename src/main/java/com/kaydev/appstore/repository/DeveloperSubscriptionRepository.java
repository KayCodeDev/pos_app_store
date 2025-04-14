package com.kaydev.appstore.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.kaydev.appstore.models.dto.objects.DeveloperSubscriptionObj;
import com.kaydev.appstore.models.entities.DeveloperSubscription;
import com.kaydev.appstore.models.enums.SubServiceType;

@Repository
public interface DeveloperSubscriptionRepository
                extends JpaRepository<DeveloperSubscription, Long>, JpaSpecificationExecutor<DeveloperSubscription> {

        @NonNull
        Page<DeveloperSubscription> findAll(@NonNull Specification<DeveloperSubscription> spec,
                        @NonNull Pageable pageable);

        @Query("SELECT new com.iisysgroup.itexstore.models.dto.objects.DeveloperSubscriptionObj(ds) FROM DeveloperSubscription ds "
                        +
                        "LEFT JOIN ds.developer d " +
                        " where (:developerId IS NULL OR d.id = :developerId) and (:serviceType IS NULL OR ds.serviceType = :serviceType) and (:fromDate IS NULL OR ds.createdAt >= :fromDate) and (:toDate IS NULL OR ds.createdAt <= :toDate)")
        Page<DeveloperSubscriptionObj> findAllSubscriptions(Pageable pageable, Long developerId,
                        SubServiceType serviceType,
                        LocalDateTime fromDate, LocalDateTime toDate);
}
