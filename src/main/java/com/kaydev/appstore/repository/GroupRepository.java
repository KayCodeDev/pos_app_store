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

import com.kaydev.appstore.models.dto.objects.GroupMinObj;
import com.kaydev.appstore.models.entities.Group;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long>, JpaSpecificationExecutor<Group> {

        // @Query("SELECT new com.iisysgroup.itexstore.models.dto.objects.GroupObj(g)
        // FROM Group g ")
        @NonNull
        Page<Group> findAll(@NonNull Specification<Group> spec, @NonNull Pageable pageable);

        Optional<Group> findByGroupNameAndDeveloperId(@NonNull String name, @NonNull Long developerId);

        Optional<Group> findByUuid(@NonNull String uuid);

        @Query("SELECT new com.iisysgroup.itexstore.models.dto.objects.GroupMinObj(g) FROM Group g WHERE g.developer.id = ?1 AND g.distributor.id = ?2")
        List<GroupMinObj> findAllByDeveloperIdAndDistributorId(Long developerId, Long distributorId);

        @Query("SELECT new com.iisysgroup.itexstore.models.dto.objects.GroupMinObj(g, " +
                        "(SELECT COUNT(t) FROM Terminal t WHERE t.group.id = g.id AND t.deleted = false)) " +
                        "FROM Group g " +
                        "LEFT JOIN g.developer d " +
                        "LEFT JOIN g.distributor di " +
                        "WHERE (:developerId IS NULL OR d.id = :developerId) " +
                        "AND  (:distributorId IS NULL OR di.id = :distributorId) " +
                        "AND (:search IS NULL OR g.groupName LIKE CONCAT('%', :search, '%'))")
        Page<GroupMinObj> findByDistributorAndOthers(Pageable pageable, Long developerId, Long distributorId,
                        String search);
}
