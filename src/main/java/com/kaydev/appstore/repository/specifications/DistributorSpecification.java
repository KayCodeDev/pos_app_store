package com.kaydev.appstore.repository.specifications;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.kaydev.appstore.models.entities.Country;
import com.kaydev.appstore.models.entities.Developer;
import com.kaydev.appstore.models.entities.Distributor;
import com.kaydev.appstore.models.enums.StatusType;

import jakarta.persistence.criteria.Join;

@Component
public class DistributorSpecification {
    public Specification<Distributor> buildSpecification(
            Long developerId,
            String search,
            Long countryId,
            StatusType status) {
        return Specification.where(hasCountry(countryId))
                .and(containsSearch(search))
                .and(hasDeveloper(developerId))
                .and(hasStatus(status));
    }

    private Specification<Distributor> hasStatus(StatusType status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.get("status"), status.name());
        };
    }

    private Specification<Distributor> hasDeveloper(Long developerId) {
        return (root, query, criteriaBuilder) -> {
            if (developerId == null) {
                return criteriaBuilder.conjunction();
            }

            Join<Distributor, Developer> devJoin = root.join("developer");
            return criteriaBuilder.equal(devJoin.get("id"), developerId);
        };
    }

    private Specification<Distributor> hasCountry(Long countryId) {
        return (root, query, criteriaBuilder) -> {
            if (countryId == null) {
                return criteriaBuilder.conjunction();
            }
            Join<Distributor, Country> cJoin = root.join("country");
            return criteriaBuilder.equal(cJoin.get("id"), countryId);
        };
    }

    private Specification<Distributor> containsSearch(String search) {
        return (root, query, criteriaBuilder) -> {
            if (search == null || search.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            String value = "%" + search.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("distributorName")), value),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("contactName")), value),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("contactEmail")), value));
        };
    }

}
