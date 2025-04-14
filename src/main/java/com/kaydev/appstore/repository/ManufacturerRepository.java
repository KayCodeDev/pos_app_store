package com.kaydev.appstore.repository;

import java.util.Optional;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.kaydev.appstore.models.dto.objects.ManufacturerMinObj;
import com.kaydev.appstore.models.dto.objects.ManufacturerObj;
import com.kaydev.appstore.models.dto.objects.dashboard.ManufacturerList;
import com.kaydev.appstore.models.entities.Manufacturer;
import com.kaydev.appstore.models.enums.StatusType;

@Repository
public interface ManufacturerRepository extends JpaRepository<Manufacturer, Long> {

    @Query("SELECT new com.iisysgroup.itexstore.models.dto.objects.ManufacturerObj(m) FROM Manufacturer m order by m.id DESC")
    Page<ManufacturerObj> findAllManufacturer(Pageable pageable);

    @Query("SELECT new com.iisysgroup.itexstore.models.dto.objects.ManufacturerObj(m) FROM Manufacturer m WHERE LOWER(m.manufacturerName) LIKE LOWER(CONCAT('%', :search, '%')) order by m.id DESC")
    Page<ManufacturerObj> findAllBySearch(String search, Pageable pageable);

    @Query("SELECT new com.iisysgroup.itexstore.models.dto.objects.ManufacturerMinObj(m) FROM Manufacturer m where m.status = :status order by m.manufacturerName ASC")
    List<ManufacturerMinObj> findAllByStatus(StatusType status);

    Optional<ManufacturerObj> findByUuid(@NonNull String uuid);

    Optional<Manufacturer> findByManufacturerName(@NonNull String manufacturerName);

    @Query("SELECT new com.iisysgroup.itexstore.models.dto.objects.dashboard.ManufacturerList(m.uuid, m.manufacturerName) FROM Manufacturer m order by m.id DESC")
    List<ManufacturerList> findMinManufacturerList();

}
