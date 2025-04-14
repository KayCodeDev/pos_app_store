package com.kaydev.appstore.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kaydev.appstore.models.dto.objects.TerminalInstalledAppObj;
import com.kaydev.appstore.models.entities.Terminal;
import com.kaydev.appstore.models.entities.TerminalInstalledApp;

@Repository
public interface TerminalInstalledAppRepository extends JpaRepository<TerminalInstalledApp, Long> {

    @Query("SELECT new com.iisysgroup.itexstore.models.dto.objects.TerminalInstalledAppObj(tia) FROM TerminalInstalledApp tia WHERE tia.terminal.id = :terminalId")
    List<TerminalInstalledAppObj> findAllByTerminal(Long terminalId);

    Long countByTerminal(Terminal terminal);

    Optional<TerminalInstalledAppObj> findByUuid(@NonNull String uuid);

    @Query("SELECT tia FROM TerminalInstalledApp tia WHERE tia.terminal.id = :terminalId AND tia.packageName = :packageName")
    Optional<TerminalInstalledApp> findByTerminalIdAndPackageName(Long terminalId, String packageName);

    @Modifying
    @Transactional
    @Query("DELETE FROM TerminalInstalledApp tia WHERE tia.terminal.id = :terminalId")
    void deleteAllByTerminalId(Long terminalId);
}