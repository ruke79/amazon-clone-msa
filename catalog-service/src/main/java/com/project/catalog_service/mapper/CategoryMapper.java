package com.project.catalog_service.mapper;

import com.project.catalog_service.model.Category;
import com.project.common.dto.CategoryDto;

public class CategoryMapper {
    public static CategoryDto toDto(Category category) {
        if (category == null) {
            return null;
        }
        return CategoryDto.builder()
                .id(Long.toString(category.getCategoryId()))
                .name(category.getCategoryName())
                .slug(category.getSlug())
                .build();
    }
}