package com.kaydev.appstore.models.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import com.kaydev.appstore.models.enums.AppType;
import com.kaydev.appstore.models.enums.OsType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "terminal_installed_apps", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "uuid" })
}, indexes = {
        @Index(name = "idx_terminal_id", columnList = "terminal_id"),
})
@Data
@ToString
@Builder

@AllArgsConstructor
@NoArgsConstructor
public class TerminalInstalledApp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    @Column(nullable = false, name = "uuid")
    private String uuid = UUID.randomUUID().toString();

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "terminal_id", nullable = false)
    private Terminal terminal;

    @Column(nullable = false, name = "app_name")
    private String appName;

    @Column(nullable = true, name = "package_name")
    private String packageName;

    @Column(nullable = true, name = "version")
    private String version;

    @Column(nullable = true, name = "version_code")
    private String versionCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true, name = "os_type")
    private OsType osType;

    @Column(nullable = true, name = "icon")
    private String icon;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = true, name = "appType")
    private AppType appType = AppType.EXTERNAL;

    @Column(nullable = true, name = "built_with")
    private String builtWith;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_id", nullable = true)
    private App app;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}