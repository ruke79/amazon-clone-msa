package com.project.catalog_service.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.common.dto.CategoryDto;
import com.project.common.dto.SubCategoryDto;
import com.project.catalog_service.model.Category;
import com.project.catalog_service.model.SubCategory;
import com.project.catalog_service.repository.CategoryRepository;
import com.project.catalog_service.repository.SubCategoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;



@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private final SubCategoryRepository subCategoryRepository;

    
    public boolean findSubCategory(String subCategoryName, String categoryId) {

        return null != subCategoryRepository.findBySubcategoryNameAndCategory_CategoryId(subCategoryName,
                Long.parseLong(categoryId));
    }

    public boolean findCategory(String categoryName) {

        return null != categoryRepository.findByCategoryName(categoryName);
    }

    
    public CategoryDto createCategory(String name, String slug) {

        Category category = categoryRepository.findByCategoryName(name);

        if (null == category) {

            category = new Category(name, slug);            
            category = categoryRepository.save(category);

        }

        return CategoryDto.builder()
                .id(Long.toString(category.getCategoryId()))
                .name(category.getCategoryName())
                .slug(category.getSlug()).build();
    }

    public SubCategoryDto createSubCategory(String subCategoryName, String categoryId, String slug) {

        Category category = categoryRepository
                .findById(Long.parseLong(categoryId))
                .orElseThrow(() -> new RuntimeException("Category not found"));

        SubCategory subcategory = new SubCategory(subCategoryName, slug, category);
        
        subCategoryRepository.save(subcategory);

        return SubCategoryDto.builder()
                .name(subCategoryName)
                .parent(CategoryDto.builder()
                        .id(categoryId)
                        .name(category.getCategoryName())
                        .slug(category.getSlug())
                        .build())
                .build();
    }

}
