package com.kaydev.appstore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kaydev.appstore.models.dto.objects.RoleObj;
import com.kaydev.appstore.models.entities.Role;
import com.kaydev.appstore.models.enums.UserType;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);

    @Query("SELECT new com.iisysgroup.itexstore.models.dto.objects.RoleObj(r) FROM Role r WHERE r.roleType = :userType")
    List<RoleObj> findAllByUserType(UserType userType);
}
