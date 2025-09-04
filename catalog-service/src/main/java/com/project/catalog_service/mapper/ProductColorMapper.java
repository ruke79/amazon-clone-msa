package com.project.catalog_service.mapper;

import com.project.catalog_service.model.ProductColor;
import com.project.common.dto.ProductColorDto;

public class ProductColorMapper {
    public static ProductColorDto toDto(ProductColor color) {
        if (color == null) {
            return null;
        }
        return ProductColorDto.builder()
                .id(Long.toString(color.getColorId()))
                .color(color.getColor())
                .colorImage(color.getColorImage())
                .build();
    }
}