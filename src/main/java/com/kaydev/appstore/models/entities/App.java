package com.kaydev.appstore.models.entities;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.kaydev.appstore.models.enums.AppType;
import com.kaydev.appstore.models.enums.OsType;
import com.kaydev.appstore.models.enums.StatusType;
import com.kaydev.appstore.utils.JsonStringConverter;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "apps", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "name", "packageName", "uuid" })
}, indexes = {
        @Index(name = "idx_developer_id", columnList = "developer_id"),
        @Index(name = "idx_distributor_id", columnList = "distributor_id"),
        @Index(name = "idx_manufacturer_id", columnList = "manufacturer_id"),
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_category_id", columnList = "category_id"),
})
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class App {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    @Column(nullable = false, name = "uuid")
    private String uuid = UUID.randomUUID().toString();

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "status")
    private StatusType status = StatusType.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "os_type")
    private OsType osType;

    private String name;

    private String packageName;

    @Column(nullable = true, name = "icon", length = 500)
    private String icon;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "developer_id", nullable = true)
    private Developer developer;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "distributor_id", nullable = true)
    private Distributor distributor;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, name = "description", length = 1000)
    private String description;

    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "app", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AppScreenShot> screenShots = List.of();

    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "app", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AppVersion> versions = List.of();

    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "version_id", nullable = true)
    private AppVersion version;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manufacturer_id", nullable = false)
    private Manufacturer manufacturer;

    @ToString.Exclude
    @Builder.Default
    @ManyToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
    @JoinTable(name = "apps_models", joinColumns = @JoinColumn(name = "app_id"), inverseJoinColumns = @JoinColumn(name = "model_id"))
    private List<ManufacturerModel> models = List.of();

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = true, name = "app_type")
    private AppType appType = AppType.SYSTEM;

    @Builder.Default
    private int downloadCount = 0;

    @Column(columnDefinition = "text")
    @Convert(converter = JsonStringConverter.class)
    private List<String> permissions;

    @Column(nullable = true, name = "target_sdk")
    private String targetSdk;
    @Column(nullable = true, name = "compile_sdk")
    private String compileSdk;
    @Column(nullable = true, name = "min_sdk")
    private String minSdk;
    @Column(nullable = true, name = "max_sdk")
    private String maxSdk;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;

    @Builder.Default
    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}