package com.kaydev.appstore.repository.specifications;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.kaydev.appstore.models.entities.Developer;
import com.kaydev.appstore.models.entities.DeveloperSubscription;
import com.kaydev.appstore.models.enums.SubServiceType;

import jakarta.persistence.criteria.Join;

@Component
public class DeveloperSubcriptionSpecification {
    public Specification<DeveloperSubscription> buildSpecification(
            SubServiceType serviceType,
            Long developerId,
            LocalDateTime fromDate, LocalDateTime toDate) {
        return Specification.where(hasDeveloper(developerId))
                .and(hasService(serviceType))
                .and(hasFromDate(fromDate))
                .and(hasToDate(toDate));
    }

    private static Specification<DeveloperSubscription> hasDeveloper(Long developerId) {
        return (root, query, criteriaBuilder) -> {
            if (developerId == null) {
                return criteriaBuilder.conjunction();
            }
            Join<DeveloperSubscription, Developer> devJoin = root.join("developer");
            return criteriaBuilder.or(
                    criteriaBuilder.equal(devJoin.get("id"), developerId));
        };
    }

    private static Specification<DeveloperSubscription> hasService(SubServiceType serviceType) {
        return (root, query, criteriaBuilder) -> {
            if (serviceType == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("serviceType"), serviceType);
        };
    }

    private static Specification<DeveloperSubscription> hasFromDate(LocalDateTime fromDate) {
        return (root, query, criteriaBuilder) -> {
            if (fromDate == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), fromDate);
        };
    }

    private static Specification<DeveloperSubscription> hasToDate(LocalDateTime toDate) {
        return (root, query, criteriaBuilder) -> {
            if (toDate == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), toDate);
        };
    }
}
