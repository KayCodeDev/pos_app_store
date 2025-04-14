package com.kaydev.appstore.models.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import com.kaydev.appstore.models.enums.StatusType;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToOne;
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
@Table(name = "developers", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "organization_name", "uuid" })
}, indexes = {
        @Index(name = "idx_country_id", columnList = "country_id"),
})
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Developer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    @Column(nullable = false, name = "uuid")
    private String uuid = UUID.randomUUID().toString();

    @Column(nullable = false, name = "organization_name")
    private String organizationName;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "status")
    private StatusType status = StatusType.ACTIVE;

    @Column(nullable = true, name = "website_url")
    private String websiteUrl;

    @Column(nullable = true, name = "support_email")
    private String supportEmail;

    @Column(nullable = true, name = "expiry_date")
    private LocalDateTime expiryDate;

    @Builder.Default
    @Column(nullable = false, name = "remote_hours")
    private double remoteHours = 10;

    @Builder.Default
    @Column(nullable = false, name = "exhausted_remote_hours")
    private double exhaustedRemoteHours = 0;

    @OneToOne(mappedBy = "developer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DeveloperSetting setting;

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
