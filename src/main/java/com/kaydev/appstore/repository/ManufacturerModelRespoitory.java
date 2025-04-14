package com.kaydev.appstore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.kaydev.appstore.models.dto.objects.ManufacturerModelMinObj;
import com.kaydev.appstore.models.dto.objects.ManufacturerModelObj;
import com.kaydev.appstore.models.entities.ManufacturerModel;
import com.kaydev.appstore.models.enums.OsType;
import com.kaydev.appstore.models.enums.StatusType;

@Repository
public interface ManufacturerModelRespoitory extends JpaRepository<ManufacturerModel, Long> {

    // @Query("SELECT new
    // com.iisysgroup.itexstore.models.dto.objects.ManufacturerModelObj(m) FROM
    // ManufacturerModel m ")
    Page<ManufacturerModel> findAll(Specification<ManufacturerModel> spec, Pageable pageable);

    @Query("SELECT new com.iisysgroup.itexstore.models.dto.objects.ManufacturerModelMinObj(m) FROM ManufacturerModel m where m.manufacturer.id = :manufacturerId and m.status = :status order by m.modelName ASC")
    List<ManufacturerModelMinObj> findWithManufacturerId(Long manufacturerId, StatusType status);

    @Query("SELECT new com.iisysgroup.itexstore.models.dto.objects.ManufacturerModelObj(m) FROM ManufacturerModel m where m.manufacturer.id = :manufacturerId and (:search IS NULL OR lower(m.modelName) like lower(concat('%', :search, '%'))) order by m.id DESC")
    Page<ManufacturerModelObj> findAllModelsByManufacturer(Pageable pageable, Long manufacturerId, String search);

    @Query("SELECT m FROM ManufacturerModel m where m.manufacturer.id = :manufacturerId and m.id in :modelIds")
    List<ManufacturerModel> findWithManufacturerAndModelIds(Long manufacturerId, List<Long> modelIds);

    @Query("SELECT m FROM ManufacturerModel m where m.manufacturer.id = :manufacturerId and m.id in :modelIds and m.osType = :osType")
    List<ManufacturerModel> findWithManufacturerAndModelIdsAndOsType(Long manufacturerId, List<Long> modelIds,
            OsType osType);

    Optional<ManufacturerModelObj> findByUuid(@NonNull String uuid);

    @Query("SELECT m FROM ManufacturerModel m where m.manufacturer.id = :manufacturerId and m.modelName = :modelName")
    Optional<ManufacturerModel> findWithModelNameAndManufacturerId(String modelName, Long manufacturerId);
}
