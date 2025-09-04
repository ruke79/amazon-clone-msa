package com.project.catalog_service.mapper;

import com.project.catalog_service.model.ProductSize;
import com.project.common.dto.ProductSizeDto;

public class ProductSizeMapper {
    public static ProductSizeDto toDto(ProductSize size) {
        if (size == null) {
            return null;
        }
        return ProductSizeDto.builder()
                .id(Long.toString(size.getSizeId()))
                .size(size.getSize())
                .quantity(size.getQuantity())
                .price(size.getPrice())
                .build();
    }
}