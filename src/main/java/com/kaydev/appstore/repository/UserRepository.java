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

import com.kaydev.appstore.models.dto.objects.UserMinObj;
import com.kaydev.appstore.models.dto.objects.UserObj;
import com.kaydev.appstore.models.entities.Developer;
import com.kaydev.appstore.models.entities.User;
import com.kaydev.appstore.models.enums.UserType;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    // @Query("SELECT new com.iisysgroup.itexstore.models.dto.objects.UserObj(u)
    // FROM User u ")
    @NonNull
    Page<User> findAll(@NonNull Specification<User> spec, @NonNull Pageable pageable);

    @Query("SELECT new com.iisysgroup.itexstore.models.dto.objects.UserMinObj(u) FROM User u where (:developerId IS NULL OR u.developer.id = :developerId) and (:search IS NULL OR lower(u.fullName) like lower(concat('%', :search, '%'))) AND (:userType IS NULL OR u.userType = :userType) AND (:role IS NULL OR u.role.name = :role)")
    Page<UserMinObj> findAllUsersByFilter(Pageable pageable, String search,
            UserType userType,
            Long developerId, String role);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.role WHERE u.uuid = :uuid")
    Optional<User> findByUuid(@NonNull String uuid);

    Optional<UserObj> findByUuidAndDeveloper(@NonNull String uuid, Developer developer);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.developer WHERE u.email = :email")
    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    @Query("SELECT u.email FROM User u")
    List<String> findAllEmails(Long developerId);

    @Query("SELECT u.email FROM User u WHERE u.developer.id = :developerId")
    List<String> findAllEmailsByDeveloperId(Long developerId);

}