package com.kaydev.appstore.repository.specifications;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.kaydev.appstore.models.entities.App;
import com.kaydev.appstore.models.entities.Category;
import com.kaydev.appstore.models.entities.Developer;
import com.kaydev.appstore.models.entities.Distributor;
import com.kaydev.appstore.models.entities.Manufacturer;
import com.kaydev.appstore.models.entities.User;
import com.kaydev.appstore.models.enums.AppType;
import com.kaydev.appstore.models.enums.OsType;
import com.kaydev.appstore.models.enums.StatusType;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

@Component
public class AppSpecification {
    public Specification<App> buildSpecification(
            String search,
            StatusType status,
            OsType osType,
            Long developerId,
            Long distributorId,
            Long userId,
            Long manufacturerId,
            Long categoryId,
            AppType appType) {
        return Specification.where(notDeleted())
                .and(hasDeveloper(developerId))
                .and(hasDistributor(distributorId))
                .and(hasOsType(osType))
                .and(containsSearch(search))
                .and(hasStatus(status))
                .and(hasUser(userId))
                .and(hasAppType(appType))
                .and(hasManufacturer(manufacturerId))
                .and(hasCategory(categoryId))

        ;
    }

    public Specification<App> notDeleted() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("deleted"), false);
    }

    private static Specification<App> hasStatus(StatusType status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    private Specification<App> hasOsType(OsType osType) {
        return (root, query, criteriaBuilder) -> {
            if (osType == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("osType"), osType);
        };
    }

    private Specification<App> hasAppType(AppType appType) {
        return (root, query, criteriaBuilder) -> {
            if (appType == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("appType"), appType);
        };
    }

    private Specification<App> containsSearch(String search) {
        return (root, query, criteriaBuilder) -> {
            if (search == null || search.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            String value = "%" + search.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), value));
        };
    }

    private static Specification<App> hasDeveloper(Long developerId) {
        return (root, query, criteriaBuilder) -> {
            if (developerId == null) {
                return criteriaBuilder.conjunction();
            }
            Join<App, Developer> devJoin = root.join("developer", JoinType.LEFT);
            return criteriaBuilder.or(
                    criteriaBuilder.equal(devJoin.get("id"), developerId),
                    criteriaBuilder.isNull(devJoin));
        };
    }

    private Specification<App> hasDistributor(Long distributorId) {
        return (root, query, criteriaBuilder) -> {
            if (distributorId == null) {
                return criteriaBuilder.conjunction();
            }
            Join<App, Distributor> dJoin = root.join("distributor", JoinType.LEFT);
            return criteriaBuilder.or(
                    criteriaBuilder.equal(dJoin.get("id"), distributorId),
                    criteriaBuilder.isNull(dJoin));
        };
    }

    private Specification<App> hasUser(Long userId) {
        return (root, query, criteriaBuilder) -> {
            if (userId == null) {
                return criteriaBuilder.conjunction();
            }
            Join<App, User> uJoin = root.join("user");
            return criteriaBuilder.equal(uJoin.get("id"), userId);
        };
    }

    private Specification<App> hasManufacturer(Long manufacturerId) {
        return (root, query, criteriaBuilder) -> {
            if (manufacturerId == null) {
                return criteriaBuilder.conjunction();
            }
            Join<App, Manufacturer> mJoin = root.join("manufacturer");
            return criteriaBuilder.equal(mJoin.get("id"), manufacturerId);
        };
    }

    private Specification<App> hasCategory(Long categoryId) {
        return (root, query, criteriaBuilder) -> {
            if (categoryId == null) {
                return criteriaBuilder.conjunction();
            }
            Join<App, Category> cJoin = root.join("category");
            return criteriaBuilder.equal(cJoin.get("id"), categoryId);
        };
    }
}
