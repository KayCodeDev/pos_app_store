package com.kaydev.appstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kaydev.appstore.models.entities.DeveloperSetting;

@Repository
public interface DeveloperSettingRepository extends JpaRepository<DeveloperSetting, Long> {

}
