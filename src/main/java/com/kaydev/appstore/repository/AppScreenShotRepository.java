package com.kaydev.appstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kaydev.appstore.models.entities.AppScreenShot;

@Repository
public interface AppScreenShotRepository extends JpaRepository<AppScreenShot, Long> {

}
