package com.kaydev.appstore.repository.specifications;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.kaydev.appstore.models.entities.Manufacturer;
import com.kaydev.appstore.models.entities.ManufacturerModel;

import jakarta.persistence.criteria.Join;

@Component
public class ManufactuerModelSpecification {
    public Specification<ManufacturerModel> buildSpecification(
            Long manufacturerId,
            String search) {
        return Specification.where(hasManufacturer(manufacturerId))
                .and(containsSearch(search));
    }

    private Specification<ManufacturerModel> hasManufacturer(Long manufacturerId) {
        return (root, query, criteriaBuilder) -> {
            if (manufacturerId == null) {
                return criteriaBuilder.conjunction();
            }

            Join<ManufacturerModel, Manufacturer> mJoin = root.join("manufacturer");
            return criteriaBuilder.equal(mJoin.get("id"), manufacturerId);
        };
    }

    private Specification<ManufacturerModel> containsSearch(String search) {
        return (root, query, criteriaBuilder) -> {
            if (search == null || search.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            String value = "%" + search.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("modelName")), value));
        };
    }
}
