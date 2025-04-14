package com.kaydev.appstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kaydev.appstore.models.entities.UserPermission;

@Repository
public interface UserPermissionRepository extends JpaRepository<UserPermission, Long> {

    @Modifying
    @Query("DELETE FROM UserPermission")
    void truncateTable();
}