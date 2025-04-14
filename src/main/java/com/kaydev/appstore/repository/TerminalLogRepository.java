package com.kaydev.appstore.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kaydev.appstore.models.dto.objects.TerminalLogObj;
import com.kaydev.appstore.models.dto.objects.export.TerminalChargableObjExp;
import com.kaydev.appstore.models.entities.TerminalLog;

@Repository
public interface TerminalLogRepository extends JpaRepository<TerminalLog, Long> {

    @Query("SELECT new com.iisysgroup.itexstore.models.dto.objects.TerminalLogObj(tl) FROM TerminalLog tl WHERE tl.terminal.id = :terminalId")
    Page<TerminalLogObj> findAllByTerminalId(Long terminalId, Pageable pageable);

    @Query("SELECT new com.iisysgroup.itexstore.models.dto.objects.export.TerminalChargableObjExp(tl.terminal.serialNumber, COUNT(tl.terminal.id), MAX(tl.terminal.lastHeartbeat), tl.terminal.developer.organizationName, tl.terminal.distributor.distributorName) FROM TerminalLog tl where tl.terminal.developer.id = :developerId and tl.createdAt between :from and :to group by tl.terminal.serialNumber, tl.terminal.developer.organizationName, tl.terminal.distributor.distributorName")
    Page<TerminalChargableObjExp> findTerminalChargableCount(Pageable pageable, Long developerId, LocalDateTime from,
            LocalDateTime to);
}
