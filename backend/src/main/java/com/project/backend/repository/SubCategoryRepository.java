package com.project.backend.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.project.backend.model.SubCategory;

public interface  SubCategoryRepository extends JpaRepository<SubCategory, Long>  {
    List<SubCategory> findBySubcategoryNameIn(List<String> subcategoryName);    

    List<SubCategory> findAll();
}
