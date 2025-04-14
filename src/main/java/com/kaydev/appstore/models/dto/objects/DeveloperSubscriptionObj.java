package com.kaydev.appstore.models.dto.objects;

import java.time.LocalDateTime;

import com.kaydev.appstore.models.entities.DeveloperSubscription;
import com.kaydev.appstore.models.enums.StatusType;
import com.kaydev.appstore.models.enums.SubServiceType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeveloperSubscriptionObj {
    private Long id;
    private String uuid;
    private String reference;
    private DeveloperMinObj developer;
    private SubServiceType serviceType;
    private String duration;
    private String previousValue;
    private String afterValue;
    private double amount;
    private String currency;
    private StatusType status;
    private String description;
    private UserMinObj user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public DeveloperSubscriptionObj(DeveloperSubscription developerSubscription) {
        this.id = developerSubscription.getId();
        this.uuid = developerSubscription.getUuid();
        this.reference = developerSubscription.getReference();
        this.developer = new DeveloperMinObj(developerSubscription.getDeveloper());
        this.serviceType = developerSubscription.getServiceType();
        this.duration = developerSubscription.getDuration();
        this.previousValue = developerSubscription.getPreviousValue();
        this.afterValue = developerSubscription.getAfterValue();
        this.amount = developerSubscription.getAmount();
        this.currency = developerSubscription.getCurrency();
        this.status = developerSubscription.getStatus();
        this.description = developerSubscription.getDescription();
        this.user = new UserMinObj(developerSubscription.getUser());
        this.createdAt = developerSubscription.getCreatedAt();
        this.updatedAt = developerSubscription.getUpdatedAt();
    }
}
