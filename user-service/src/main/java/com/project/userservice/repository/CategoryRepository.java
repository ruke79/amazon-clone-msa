package com.project.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.userservice.model.ProductCategory;
import com.project.userservice.model.SubCategory;


import java.util.List;


@Repository
public interface CategoryRepository extends JpaRepository<ProductCategory, Long> {

    public ProductCategory findByCategoryName(String categoryName);

    @Query("select categoryName from ProductCategory")
    public List<String> findAllCategoryNames();

    @Query("select categoryId from ProductCategory where categoryName = :name")
    public Long findIdByCategoryName(@Param("name") String categoryName);

    @Query(value ="SELECT s FROM ProductCategory c INNER JOIN SubCategory s on c.categoryId = s.category.categoryId WHERE (:categoryId is null or c.categoryId = :categoryId)")    
    public List<SubCategory> findSubCategoriesByCategoryId(@Param("categoryId") Long categoryId);
}
