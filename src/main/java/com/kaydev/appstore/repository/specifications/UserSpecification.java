package com.kaydev.appstore.repository.specifications;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.kaydev.appstore.models.entities.Developer;
import com.kaydev.appstore.models.entities.Role;
import com.kaydev.appstore.models.entities.User;
import com.kaydev.appstore.models.enums.StatusType;
import com.kaydev.appstore.models.enums.UserType;

import jakarta.persistence.criteria.Join;

@Component
public class UserSpecification {
    public Specification<User> buildSpecification(
            String search,
            UserType userType,
            Long developerId,
            StatusType status, String role) {
        return Specification.where(hasDeveloper(developerId))
                .and(containsSearch(search))
                .and(hasUserType(userType))
                .and(hasRole(role))
                .and(hasStatus(status));
    }

    private Specification<User> hasStatus(StatusType status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("active"), status.name().equals("ACTIVE"));
        };
    }

    private Specification<User> hasUserType(UserType userType) {
        return (root, query, criteriaBuilder) -> {
            if (userType == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("userType"), userType.name());
        };
    }

    private Specification<User> hasDeveloper(Long developerId) {
        return (root, query, criteriaBuilder) -> {
            if (developerId == null) {
                return criteriaBuilder.conjunction();
            }

            Join<User, Developer> devJoin = root.join("developer");
            return criteriaBuilder.equal(devJoin.get("id"), developerId);
        };
    }

    private Specification<User> hasRole(String role) {
        return (root, query, criteriaBuilder) -> {
            if (role == null) {
                return criteriaBuilder.conjunction();
            }

            Join<User, Role> roleJoin = root.join("role");
            return criteriaBuilder.equal(roleJoin.get("name"), role);
        };
    }

    private Specification<User> containsSearch(String search) {
        return (root, query, criteriaBuilder) -> {
            if (search == null || search.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            String value = "%" + search.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("fullName")), value));
        };
    }
}
