package com.kaydev.appstore.repository.specifications;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.kaydev.appstore.models.entities.Developer;
import com.kaydev.appstore.models.entities.Distributor;
import com.kaydev.appstore.models.entities.Group;
import com.kaydev.appstore.models.entities.Task;
import com.kaydev.appstore.models.entities.User;
import com.kaydev.appstore.models.enums.StatusType;

import jakarta.persistence.criteria.Join;

@Component
public class TaskSpecification {
    public Specification<Task> buildSpecification(
            Long developerId,
            Long distributorId,
            Long groupId,
            Long userId,
            StatusType status,
            String search) {
        return Specification.where(hasDeveloper(developerId))
                .and(hasDistributor(distributorId))
                .and(hasGroup(groupId))
                .and(hasStatus(status))
                .and(containsSearch(search))
                .and(hasUser(userId));
    }

    private Specification<Task> hasDeveloper(Long developerId) {
        return (root, query, criteriaBuilder) -> {
            if (developerId == null) {
                return criteriaBuilder.conjunction();
            }

            Join<Task, Developer> devJoin = root.join("developer");
            return criteriaBuilder.equal(devJoin.get("id"), developerId);
        };
    }

    private Specification<Task> hasDistributor(Long distributorId) {
        return (root, query, criteriaBuilder) -> {
            if (distributorId == null) {
                return criteriaBuilder.conjunction();
            }

            Join<Task, Distributor> disJoin = root.join("distributor");
            return criteriaBuilder.equal(disJoin.get("id"), distributorId);
        };
    }

    private Specification<Task> hasGroup(Long groupId) {
        return (root, query, criteriaBuilder) -> {
            if (groupId == null) {
                return criteriaBuilder.conjunction();
            }

            Join<Task, Group> grpJoin = root.join("group");
            return criteriaBuilder.equal(grpJoin.get("id"), groupId);
        };
    }

    private Specification<Task> hasUser(Long userId) {
        return (root, query, criteriaBuilder) -> {
            if (userId == null) {
                return criteriaBuilder.conjunction();
            }

            Join<Task, User> uJoin = root.join("user");
            return criteriaBuilder.equal(uJoin.get("id"), userId);
        };
    }

    private Specification<Task> hasStatus(StatusType status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), status.name());
        };
    }

    private Specification<Task> containsSearch(String search) {
        return (root, query, criteriaBuilder) -> {
            if (search == null || search.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            String value = "%" + search.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("uuid")), value),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("taskType")), value),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("taskId")), value),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("taskName")), value));
        };
    }
}
