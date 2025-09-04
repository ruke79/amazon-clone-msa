package com.project.catalog_service.mapper;

import com.project.catalog_service.model.ProductSubcategory;
import com.project.common.dto.SubCategoryDto;
import com.project.common.dto.CategoryDto;

public class ProductSubcategoryMapper {
    public static SubCategoryDto toDto(ProductSubcategory productSubcategory) {
        if (productSubcategory == null || productSubcategory.getSubcategory() == null) {
            return null;
        }
        return SubCategoryDto.builder()
                .id(Long.toString(productSubcategory.getSubcategory().getSubcategoryId()))
                .parent(CategoryMapper.toDto(productSubcategory.getSubcategory().getCategory()))
                .name(productSubcategory.getSubcategory().getSubcategoryName())
                .slug(productSubcategory.getSubcategory().getSlug())
                .build();
    }
}