package com.project.catalog_service.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.project.catalog_service.model.Subcategory;

@Repository
public interface  SubcategoryRepository extends JpaRepository<Subcategory, Long>  {
    List<Subcategory> findBySubcategoryNameIn(List<String> subcategoryName);    
    List<Subcategory> findByCategory_CategoryNameAndSubcategoryNameIn(String categoryName, List<String> subcategoryName);    

    List<Subcategory> findBySubcategoryIdIn(List<Long> subcategoryName);    
    List<Subcategory> findByCategory_CategoryIdAndSubcategoryIdIn(Long categoryId, List<Long> subcategoryId);    

    Subcategory findBySubcategoryNameAndCategory_CategoryId(String name, Long parent);
    Subcategory findBySubcategoryNameAndCategory_CategoryName(String name, String parent);

    

    List<Subcategory> findAll();
}
