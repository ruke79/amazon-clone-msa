package com.project.catalog_service.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.catalog_service.dto.CategoryDto;
import com.project.catalog_service.dto.SubCategoryDto;
import com.project.catalog_service.model.ProductCategory;
import com.project.catalog_service.model.SubCategory;
import com.project.catalog_service.repository.CategoryRepository;
import com.project.catalog_service.repository.SubCategoryRepository;
import com.project.catalog_service.dto.request.SubCategoryRequest;

@Slf4j
@Service
@Requiredargsconstructor
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

    
    public CategoryDto createCategory(String name, String slug) {

        ProductCategory category = categoryRepository.findByCategoryName(name);

        if (null == category) {

            category = new ProductCategory();
            category.setCategoryName(name);
            category.setSlug(slug);

            category = categoryRepository.save(category);

        }

        return CategoryDto.builder()
                .id(Long.toString(category.getCategoryId()))
                .name(category.getCategoryName())
                .slug(category.getSlug()).build();
    }

    public SubCategoryDto createSubCategory(String subCategoryName, String categoryId, String slug) {

        ProductCategory category = categoryRepository
                .findById(Long.parseLong(categoryId))
                .orElseThrow(() -> new RuntimeException("Category not found"));

        SubCategory subcategory = new SubCategory();
        subcategory.setSubcategoryName(subCategoryName);

        subcategory.setCategory(category);

        subcategory.setSlug(slug);

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
