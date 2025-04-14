package com.kaydev.appstore.models.entities;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.kaydev.appstore.models.enums.PushPeriod;
import com.kaydev.appstore.models.enums.PushTo;
import com.kaydev.appstore.models.enums.StatusType;
import com.kaydev.appstore.models.enums.TaskType;
import com.kaydev.appstore.utils.GenericUtil;

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
import jakarta.persistence.OneToMany;
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
@Table(name = "tasks", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "task_name", "task_id", "uuid" })
}, indexes = {
        @Index(name = "idx_developer_id", columnList = "developer_id"),
        @Index(name = "idx_user_id", columnList = "user_id")
})
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    @Column(nullable = false, name = "uuid")
    private String uuid = UUID.randomUUID().toString();

    @Column(nullable = false, name = "task_name")
    private String taskName;

    @Column(nullable = false, name = "task_id")
    private String taskId;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "app_version_id", nullable = true)
    private AppVersion appVersion;

    @Column(nullable = true, name = "message")
    private String message;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "status")
    private StatusType status = StatusType.NOT_STARTED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "task_type")
    private TaskType taskType;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "push_period")
    private PushPeriod pushPeriod = PushPeriod.IMMEDIATE;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = true, name = "push_to")
    private PushTo pushTo = PushTo.TERMINAL;

    @ToString.Exclude
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TaskTerminal> taskTerminals;

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
    @JoinColumn(name = "group_id", nullable = true)
    private Group group;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @Builder.Default
    @Column(name = "terminal_count", nullable = false)
    private int terminalCount = 0;

    @Column(name = "parameters", nullable = true, columnDefinition = "TEXT")
    private String parameters;

    @Builder.Default
    @Column(name = "completed_count", nullable = false)
    private int completedCount = 0;

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

    public void setParameeterMap(Map<String, Object> parameters) {
        this.parameters = GenericUtil.convertMapToJsonString(parameters);
    }

    public Map<String, Object> getParameeterMap() {
        return GenericUtil.convertJsonStringToMap(this.parameters);
    }
}
