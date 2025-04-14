package com.kaydev.appstore.repository.specifications;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.kaydev.appstore.models.entities.Country;
import com.kaydev.appstore.models.entities.Developer;
import com.kaydev.appstore.models.enums.StatusType;

import jakarta.persistence.criteria.Join;

@Component
public class DeveloperSpecification {
    public Specification<Developer> buildSpecification(
            String search,
            Long countryId,
            StatusType status) {
        return Specification.where(hasCountry(countryId))
                .and(containsSearch(search))
                .and(hasStatus(status));
    }

    private Specification<Developer> hasStatus(StatusType status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), status.name());
        };
    }

    private Specification<Developer> hasCountry(Long countryId) {
        return (root, query, criteriaBuilder) -> {
            if (countryId == null) {
                return criteriaBuilder.conjunction();
            }
            Join<Developer, Country> cJoin = root.join("country");
            return criteriaBuilder.equal(cJoin.get("id"), countryId);
        };
    }

    private Specification<Developer> containsSearch(String search) {
        return (root, query, criteriaBuilder) -> {
            if (search == null || search.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            String value = "%" + search.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("organizationName")), value),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("websiteUrl")), value),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("supportEmail")), value));
        };
    }

}
