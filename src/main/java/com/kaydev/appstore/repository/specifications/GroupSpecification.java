package com.kaydev.appstore.repository.specifications;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.kaydev.appstore.models.entities.Developer;
import com.kaydev.appstore.models.entities.Distributor;
import com.kaydev.appstore.models.entities.Group;

import jakarta.persistence.criteria.Join;

@Component
public class GroupSpecification {
    public Specification<Group> buildSpecification(
            String search,
            Long developerId,
            Long distributorId) {
        return Specification.where(hasDeveloper(developerId))
                .and(containsSearch(search))
                .and(hasDistributor(distributorId));
    }

    private Specification<Group> hasDeveloper(Long developerId) {
        return (root, query, criteriaBuilder) -> {
            if (developerId == null) {
                return criteriaBuilder.conjunction();
            }

            Join<Group, Developer> devJoin = root.join("developer");
            return criteriaBuilder.equal(devJoin.get("id"), developerId);
        };
    }

    private Specification<Group> hasDistributor(Long distributorId) {
        return (root, query, criteriaBuilder) -> {
            if (distributorId == null) {
                return criteriaBuilder.conjunction();
            }

            Join<Group, Distributor> dJoin = root.join("distributor");
            return criteriaBuilder.equal(dJoin.get("id"), distributorId);
        };
    }

    private Specification<Group> containsSearch(String search) {
        return (root, query, criteriaBuilder) -> {
            if (search == null || search.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            String value = "%" + search.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("groupName")), value));
        };
    }
}
