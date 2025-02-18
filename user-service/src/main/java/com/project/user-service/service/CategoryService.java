package com.project.user-service.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.user-service.dto.CategoryDTO;
import com.project.user-service.dto.SubCategoryDTO;
import com.project.user-service.model.ProductCategory;
import com.project.user-service.model.SubCategory;
import com.project.user-service.repository.CategoryRepository;
import com.project.user-service.repository.SubCategoryRepository;
import com.project.user-service.security.request.SubCategoryRequest;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private final SubCategoryRepository subCategoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, SubCategoryRepository subCategoryRepository) {
        this.categoryRepository = categoryRepository;
        this.subCategoryRepository = subCategoryRepository;
    }

    public boolean findSubCategory(String subCategoryName, String categoryId) {

        return null != subCategoryRepository.findBySubcategoryNameAndCategory_CategoryId(subCategoryName,
                Long.parseLong(categoryId));
    }

    public boolean findCategory(String categoryName) {

        return null != categoryRepository.findByCategoryName(categoryName);
    }

    
    public CategoryDTO createCategory(String name, String slug) {

        ProductCategory category = categoryRepository.findByCategoryName(name);

        if (null == category) {

            category = new ProductCategory();
            category.setCategoryName(name);
            category.setSlug(slug);

            category = categoryRepository.save(category);

        }

        return CategoryDTO.builder()
                .id(Long.toString(category.getCategoryId()))
                .name(category.getCategoryName())
                .slug(category.getSlug()).build();
    }

    public SubCategoryDTO createSubCategory(String subCategoryName, String categoryId, String slug) {

        ProductCategory category = categoryRepository
                .findById(Long.parseLong(categoryId))
                .orElseThrow(() -> new RuntimeException("Category not found"));

        SubCategory subcategory = new SubCategory();
        subcategory.setSubcategoryName(subCategoryName);

        subcategory.setCategory(category);

        subcategory.setSlug(slug);

        subCategoryRepository.save(subcategory);

        return SubCategoryDTO.builder()
                .name(subCategoryName)
                .parent(CategoryDTO.builder()
                        .id(categoryId)
                        .name(category.getCategoryName())
                        .slug(category.getSlug())
                        .build())
                .build();
    }

}
