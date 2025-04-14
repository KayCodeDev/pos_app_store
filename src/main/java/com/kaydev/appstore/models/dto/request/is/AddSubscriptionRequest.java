package com.kaydev.appstore.models.dto.request.is;

import java.io.Serializable;

import com.kaydev.appstore.models.enums.SubServiceType;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class AddSubscriptionRequest implements Serializable {
    private SubServiceType serviceType;
    private Long duration;
    private double amount;
    private String currency;
}
