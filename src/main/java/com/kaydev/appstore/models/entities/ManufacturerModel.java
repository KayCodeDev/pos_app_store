package com.kaydev.appstore.models.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import com.kaydev.appstore.models.enums.OsType;
import com.kaydev.appstore.models.enums.StatusType;

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
@Table(name = "manufacturer_models", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "model_name", "uuid" }) }, indexes = {
                @Index(name = "idx_manufacturer_id", columnList = "manufacturer_id")
        })
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManufacturerModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    @Column(nullable = false, name = "uuid")
    private String uuid = UUID.randomUUID().toString();

    @Column(nullable = false, name = "model_name")
    private String modelName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "os_type")
    private OsType osType;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "manufacturer_id")
    private Manufacturer manufacturer;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "status")
    private StatusType status = StatusType.ACTIVE;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

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
