package com.project.catalog_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.catalog_service.model.Category;
import com.project.catalog_service.model.SubCategory;


import java.util.List;


@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    public Category findByCategoryName(String categoryName);

    @Query("select categoryName from Category")
    public List<String> findAllCategoryNames();

    @Query("select categoryId from Category where categoryName = :name")
    public Long findIdByCategoryName(@Param("name") String categoryName);

    @Query(value ="SELECT s FROM Category c INNER JOIN SubCategory s on c.categoryId = s.category.categoryId WHERE (:categoryId is null or c.categoryId = :categoryId)")    
    public List<SubCategory> findSubCategoriesByCategoryId(@Param("categoryId") Long categoryId);
}
