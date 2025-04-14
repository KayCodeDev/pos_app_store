package com.kaydev.appstore.models.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import com.kaydev.appstore.models.enums.StatusType;
import com.kaydev.appstore.models.enums.SubServiceType;
import com.kaydev.appstore.utils.GenericUtil;

import jakarta.persistence.CascadeType;
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
@Table(name = "developer_subscriptions", indexes = {
        @Index(name = "idx_developer_id", columnList = "developer_id"),
        @Index(name = "idx_reference", columnList = "reference"),
        @Index(name = "idx_user_id", columnList = "user_id")
}, uniqueConstraints = {
        @UniqueConstraint(columnNames = { "reference" })
})

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeveloperSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    @Column(nullable = false, name = "uuid")
    private String uuid = UUID.randomUUID().toString();

    @Builder.Default
    @Column(nullable = false, name = "reference")
    private String reference = GenericUtil.generateUniqueNumber();

    @ToString.Exclude
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "developer_id", nullable = false)
    private Developer developer;

    @Column(nullable = false, name = "service_type")
    private SubServiceType serviceType;

    @Column(nullable = false, name = "duration")
    private String duration;

    @Column(nullable = false, name = "previous_value")
    private String previousValue;

    @Column(nullable = false, name = "after_value")
    private String afterValue;

    @Column(nullable = false, name = "amount")
    private double amount;

    @Builder.Default
    @Column(nullable = false, name = "currency")
    private String currency = "USD";

    @Builder.Default
    @Column(nullable = false, name = "status")
    private StatusType status = StatusType.SUCCESS;

    @Column(nullable = true, name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
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
