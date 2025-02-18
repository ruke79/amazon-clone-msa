package com.project.user-service.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.project.user-service.model.SubCategory;

public interface  SubCategoryRepository extends JpaRepository<SubCategory, Long>  {
    List<SubCategory> findBySubcategoryNameIn(List<String> subcategoryName);    
    List<SubCategory> findByCategory_CategoryNameAndSubcategoryNameIn(String categoryName, List<String> subcategoryName);    

    List<SubCategory> findBySubcategoryIdIn(List<Long> subcategoryName);    
    List<SubCategory> findByCategory_CategoryIdAndSubcategoryIdIn(Long categoryId, List<Long> subcategoryId);    

    SubCategory findBySubcategoryNameAndCategory_CategoryId(String name, Long parent);
    SubCategory findBySubcategoryNameAndCategory_CategoryName(String name, String parent);

    

    List<SubCategory> findAll();
}
