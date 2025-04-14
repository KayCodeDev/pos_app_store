package com.kaydev.appstore.models.entities;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;

import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "developer_settings", indexes = {
        @Index(name = "idx_developer_id", columnList = "developer_id")
})

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeveloperSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "developer_id", unique = true, nullable = false)
    private Developer developer;

    @Builder.Default
    @Column(nullable = false, name = "max_distributors")
    private int maxDistributors = 500;

    @Builder.Default
    @Column(nullable = false, name = "max_apps")
    private int maxApps = 100;

    @Builder.Default
    @Column(nullable = false, name = "can_push")
    private boolean canPush = true;

    @Builder.Default
    @Column(nullable = false, name = "can_add_app")
    private boolean canAddApp = true;

    @Builder.Default
    @Column(nullable = false, name = "can_remote")
    private boolean canRemote = false;

    @Builder.Default
    @Column(nullable = false, name = "can_add_distributor")
    private boolean canAddDistributor = true;

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
