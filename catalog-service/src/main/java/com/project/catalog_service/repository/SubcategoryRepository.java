package com.project.catalog_service.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.project.catalog_service.model.Subcategory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

@Repository
public interface  SubcategoryRepository extends JpaRepository<Subcategory, Long>  {

    // --- 비관적 잠금 적용 ---
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Subcategory> findBySubcategoryNameIn(List<String> subcategoryName);
    
    // --- 낙관적 잠금 적용 ---
    @Lock(LockModeType.OPTIMISTIC)
    List<Subcategory> findByCategory_CategoryNameAndSubcategoryNameIn(String categoryName, List<String> subcategoryName);

    // 기존 메서드들은 유지
    List<Subcategory> findBySubcategoryIdIn(List<Long> subcategoryName);    
    List<Subcategory> findByCategory_CategoryIdAndSubcategoryIdIn(Long categoryId, List<Long> subcategoryId);    
    Subcategory findBySubcategoryNameAndCategory_CategoryId(String name, Long parent);
    Subcategory findBySubcategoryNameAndCategory_CategoryName(String name, String parent);
    List<Subcategory> findAll();
}