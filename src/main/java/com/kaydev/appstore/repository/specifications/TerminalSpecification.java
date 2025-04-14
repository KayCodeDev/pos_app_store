package com.kaydev.appstore.repository.specifications;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.kaydev.appstore.models.entities.Developer;
import com.kaydev.appstore.models.entities.Distributor;
import com.kaydev.appstore.models.entities.Group;
import com.kaydev.appstore.models.entities.Manufacturer;
import com.kaydev.appstore.models.entities.ManufacturerModel;
import com.kaydev.appstore.models.entities.Terminal;
import com.kaydev.appstore.models.enums.OsType;
import com.kaydev.appstore.models.enums.StatusType;

import jakarta.persistence.criteria.Join;

@Component
public class TerminalSpecification {
    public Specification<Terminal> buildSpecification(
            String search,
            StatusType status,
            OsType osType,
            Long developerId,
            String developerUuid,
            Long distributorId,
            String distributorUuid,
            Long groupId,
            String groupUuid,
            Long manufacturerId,
            Long modelId) {
        return Specification.where(notDeleted())
                .and(hasDeveloper(developerId, developerUuid))
                .and(hasDistributor(distributorId, distributorUuid))
                .and(hasOsType(osType))
                .and(containsSearch(search))
                .and(hasStatus(status))
                .and(hasGroup(groupId, groupUuid))
                .and(hasManufacturer(manufacturerId))
                .and(hasModel(modelId));
    }

    public Specification<Terminal> notDeleted() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("deleted"), false);
    }

    private Specification<Terminal> hasStatus(StatusType status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), status.name());
        };
    }

    private Specification<Terminal> hasOsType(OsType osType) {
        return (root, query, criteriaBuilder) -> {
            if (osType == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("osType"), osType.name());
        };
    }

    private Specification<Terminal> containsSearch(String search) {
        return (root, query, criteriaBuilder) -> {
            if (search == null || search.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            String value = "%" + search.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("serialNumber")), value),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("deviceId")), value));
        };
    }

    private Specification<Terminal> hasDeveloper(Long developerId, String developerUuid) {
        return (root, query, criteriaBuilder) -> {
            if (developerId == null && developerUuid == null) {
                return criteriaBuilder.conjunction();
            }

            Join<Terminal, Developer> devJoin = root.join("developer");
            return criteriaBuilder.or(
                    criteriaBuilder.equal(devJoin.get("id"), developerId),
                    criteriaBuilder.equal(devJoin.get("uuid"), developerUuid));
        };
    }

    private Specification<Terminal> hasDistributor(Long distributorId, String distributorUuid) {
        return (root, query, criteriaBuilder) -> {
            if (distributorId == null && distributorUuid == null) {
                return criteriaBuilder.conjunction();
            }

            Join<Terminal, Distributor> dJoin = root.join("distributor");
            return criteriaBuilder.or(
                    criteriaBuilder.equal(dJoin.get("id"), distributorId),
                    criteriaBuilder.equal(dJoin.get("uuid"), distributorUuid)

            );
        };
    }

    private Specification<Terminal> hasGroup(Long groupId, String groupUuid) {
        return (root, query, criteriaBuilder) -> {
            if (groupId == null && groupUuid == null) {
                return criteriaBuilder.conjunction();
            }

            Join<Terminal, Group> gJoin = root.join("group");
            return criteriaBuilder.or(
                    criteriaBuilder.equal(gJoin.get("id"), groupId),
                    criteriaBuilder.equal(gJoin.get("uuid"), groupUuid)

            );
        };
    }

    private Specification<Terminal> hasManufacturer(Long manufacturerId) {
        return (root, query, criteriaBuilder) -> {
            if (manufacturerId == null) {
                return criteriaBuilder.conjunction();
            }

            Join<Terminal, Manufacturer> mJoin = root.join("manufacturer");
            return criteriaBuilder.equal(mJoin.get("id"), manufacturerId);
        };
    }

    private Specification<Terminal> hasModel(Long modelId) {
        return (root, query, criteriaBuilder) -> {
            if (modelId == null) {
                return criteriaBuilder.conjunction();
            }

            Join<Terminal, ManufacturerModel> mJoin = root.join("model");
            return criteriaBuilder.equal(mJoin.get("id"), modelId);
        };
    }

}
