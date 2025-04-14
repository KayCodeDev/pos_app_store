package com.kaydev.appstore.models.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "terminal_logs", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "uuid" })
}, indexes = {
        @Index(name = "idx_terminal_id", columnList = "terminal_id")
})
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TerminalLog {
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

    @Column(nullable = true, name = "longitude")
    private String longitude;

    @Column(nullable = true, name = "latitude")
    private String latitude;

    @Column(nullable = true, name = "battery_level")
    private String batteryLevel;

    @Column(nullable = true, name = "battery_status")
    private String batteryStatus;

    @Column(nullable = true, name = "ram")
    private String ram;

    @Column(nullable = true, name = "rom")
    private String rom;

    @Column(nullable = true, name = "firmware")
    private String firmware;

    @Column(nullable = true, name = "battery_temp")
    private String batteryTemp;

    @Column(nullable = true, name = "network_type")
    private String networkType;

    @Column(nullable = true, name = "ip_address")
    private String ipAddress;

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
