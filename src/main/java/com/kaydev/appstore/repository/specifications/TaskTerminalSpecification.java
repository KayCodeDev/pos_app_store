package com.kaydev.appstore.repository.specifications;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.kaydev.appstore.models.entities.Task;
import com.kaydev.appstore.models.entities.TaskTerminal;
import com.kaydev.appstore.models.entities.Terminal;
import com.kaydev.appstore.models.enums.StatusType;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

@Component
public class TaskTerminalSpecification {
    public Specification<TaskTerminal> buildSpecification(
            Long taskId,
            StatusType status,
            String search) {
        return Specification.where(hasTask(taskId))
                .and(hasStatus(status))
                .and(containsSearch(search));
    }

    private Specification<TaskTerminal> hasTask(Long taskId) {
        return (root, query, criteriaBuilder) -> {
            if (taskId == null) {
                return criteriaBuilder.conjunction();
            }

            Join<TaskTerminal, Task> tJoin = root.join("task");
            return criteriaBuilder.equal(tJoin.get("id"), taskId);
        };
    }

    private Specification<TaskTerminal> hasStatus(StatusType status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), status.name());
        };
    }

    private Specification<TaskTerminal> containsSearch(String search) {
        return (root, query, criteriaBuilder) -> {
            if (search == null || search.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            String value = "%" + search.toLowerCase() + "%";
            Join<TaskTerminal, Terminal> terminalJoin = root.join("terminal", JoinType.INNER);

            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("uuid")), value),
                    criteriaBuilder.like(criteriaBuilder.lower(terminalJoin.get("serialNumber")), value));
        };
    }
}
