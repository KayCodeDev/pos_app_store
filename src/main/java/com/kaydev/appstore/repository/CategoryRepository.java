package com.kaydev.appstore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kaydev.appstore.models.dto.objects.CategoryMinObj;
import com.kaydev.appstore.models.dto.objects.CategoryObj;
import com.kaydev.appstore.models.entities.Category;
import com.kaydev.appstore.models.enums.StatusType;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT new com.iisysgroup.itexstore.models.dto.objects.CategoryObj(c) FROM Category c order by c.id")
    List<CategoryObj> findAllCategory();

    @Query("SELECT new com.iisysgroup.itexstore.models.dto.objects.CategoryObj(c) FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<CategoryObj> findBySearch(String search);

    Optional<Category> findByName(String name);

    @Query("SELECT new com.iisysgroup.itexstore.models.dto.objects.CategoryMinObj(c) FROM Category c where c.status = :status ORDER BY c.name ASC")
    List<CategoryMinObj> findAllByStatus(StatusType status);
}
