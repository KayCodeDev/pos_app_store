package com.kaydev.appstore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kaydev.appstore.models.dto.objects.CountryObj;
import com.kaydev.appstore.models.entities.Country;
import com.kaydev.appstore.models.enums.StatusType;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

    List<Country> findByCountryNameContainingIgnoreCase(String search);

    @Query("SELECT new com.iisysgroup.itexstore.models.dto.objects.CountryObj(c) FROM Country c where c.status = :status ORDER BY c.countryName")
    List<CountryObj> findAllByStatus(StatusType status);

    Optional<Country> findByCountryName(String countryName);
}
