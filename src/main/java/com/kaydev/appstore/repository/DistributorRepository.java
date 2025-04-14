package com.kaydev.appstore.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.kaydev.appstore.models.dto.objects.DistributorMinObj;
import com.kaydev.appstore.models.dto.objects.DistributorObj;
import com.kaydev.appstore.models.entities.Distributor;
import com.kaydev.appstore.models.enums.StatusType;

@Repository
public interface DistributorRepository extends JpaRepository<Distributor, Long>, JpaSpecificationExecutor<Distributor> {
        // @Query("SELECT new
        // com.iisysgroup.itexstore.models.dto.objects.DistributorObj(d) FROM
        // Distributor d ")
        @NonNull
        Page<Distributor> findAll(@NonNull Specification<Distributor> spec, @NonNull Pageable pageable);

        Optional<Distributor> findByUuid(@NonNull String uuid);

        @Query("SELECT d FROM Distributor d where d.developer.id = :developerId AND LOWER(d.distributorName) = LOWER(:distributorName)")
        Optional<Distributor> findByDistributorNameAndDeveloperId(String distributorName, Long developerId);

        Optional<Distributor> findByIdAndDeveloperId(Long id, Long developerId);

        @Query("SELECT new com.iisysgroup.itexstore.models.dto.objects.DistributorMinObj(d) FROM Distributor d where d.developer.id = :developerId")
        List<DistributorMinObj> findAllByDeveloperId(Long developerId);

        @Query("SELECT new com.iisysgroup.itexstore.models.dto.objects.DistributorObj(d, " +
                        "(SELECT COUNT(t) FROM Terminal t WHERE t.distributor.id = d.id AND t.deleted = false )) " +
                        "FROM Distributor d " +
                        "WHERE (:developerId IS NULL OR d.developer.id = :developerId) " +
                        "AND (:status IS NULL OR d.status = :status) " +
                        "AND (:search IS NULL OR d.distributorName LIKE CONCAT('%', :search, '%'))")
        Page<DistributorObj> findByDeveloperAndOthers(Pageable pageable, Long developerId, StatusType status,
                        String search);

        int countByDeveloperId(Long developerId);
}
