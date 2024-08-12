package com.project.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.backend.model.Note;
import com.project.backend.model.ProductCategory;

@Repository
public interface CategoryRepository extends JpaRepository<ProductCategory, String> {

}
