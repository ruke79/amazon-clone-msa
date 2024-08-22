package com.project.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.backend.model.ProductCategory;
import com.project.backend.model.SubCategory;


import java.util.List;


@Repository
public interface CategoryRepository extends JpaRepository<ProductCategory, Long> {

    public ProductCategory findByCategoryName(String categoryName);

    @Query("select categoryName from ProductCategory")
    public List<String> findAllCategoryNames();

    @Query("select categoryId from ProductCategory where categoryName = :name")
    public Long findIdByCategoryName(@Param("name") String categoryName);

    @Query(value ="SELECT s FROM ProductCategory c INNER JOIN SubCategory s on c.categoryId = s.category.categoryId WHERE (:categoryName is null or c.categoryName = :categoryName)")    
    public List<SubCategory> findSubCategoriesByCategoryName(@Param("categoryName") String categoryName);
}
