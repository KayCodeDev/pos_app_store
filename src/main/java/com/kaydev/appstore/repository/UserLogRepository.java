package com.kaydev.appstore.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kaydev.appstore.models.entities.UserLog;

@Repository
public interface UserLogRepository extends JpaRepository<UserLog, Long> {

    @Query("SELECT ul from UserLog ul where ul.user.id = :userId AND (:start IS NULL OR ul.createdAt between :start AND :end) ")
    Page<UserLog> findAllByUserIdAndDate(Pageable pageable, Long userId, LocalDateTime start, LocalDateTime end);
}
