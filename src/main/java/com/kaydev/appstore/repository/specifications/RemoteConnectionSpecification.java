package com.kaydev.appstore.repository.specifications;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.kaydev.appstore.models.entities.Developer;
import com.kaydev.appstore.models.entities.RemoteConnection;
import com.kaydev.appstore.models.entities.Terminal;
import com.kaydev.appstore.models.entities.User;
import com.kaydev.appstore.models.enums.StatusType;

import jakarta.persistence.criteria.Join;

@Component
public class RemoteConnectionSpecification {
    public Specification<RemoteConnection> buildSpecification(
            Long developerId,
            Long terminalId,
            Long userId,
            String connectionId, StatusType status) {
        return Specification.where(hasConnectionId(connectionId))
                .and(hasDeveloper(developerId))
                .and(hasTerminal(terminalId))
                .and(hasUser(userId))
                .and(hasStatus(status));
    }

    private Specification<RemoteConnection> hasDeveloper(Long developerId) {
        return (root, query, criteriaBuilder) -> {
            if (developerId == null) {
                return criteriaBuilder.conjunction();
            }

            Join<RemoteConnection, Developer> devJoin = root.join("developer");
            return criteriaBuilder.equal(devJoin.get("id"), developerId);
        };
    }

    private Specification<RemoteConnection> hasStatus(StatusType status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.get("status"), status.name());
        };
    }

    private Specification<RemoteConnection> hasTerminal(Long terminalId) {
        return (root, query, criteriaBuilder) -> {
            if (terminalId == null) {
                return criteriaBuilder.conjunction();
            }

            Join<RemoteConnection, Terminal> tJoin = root.join("terminal");
            return criteriaBuilder.equal(tJoin.get("id"), terminalId);
        };
    }

    private Specification<RemoteConnection> hasUser(Long userId) {
        return (root, query, criteriaBuilder) -> {
            if (userId == null) {
                return criteriaBuilder.conjunction();
            }

            Join<RemoteConnection, User> uJoin = root.join("user");
            return criteriaBuilder.equal(uJoin.get("id"), userId);
        };
    }

    private Specification<RemoteConnection> hasConnectionId(String connectionId) {
        return (root, query, criteriaBuilder) -> {
            if (connectionId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("connectionId"), connectionId);
        };
    }

}
