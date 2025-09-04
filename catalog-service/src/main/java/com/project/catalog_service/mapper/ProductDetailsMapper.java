package com.project.catalog_service.mapper;

import com.project.catalog_service.model.ProductDetails;
import com.project.common.dto.ProductDetailDto;

public class ProductDetailsMapper {
    public static ProductDetailDto toDto(ProductDetails details) {
        if (details == null) {
            return null;
        }
        return ProductDetailDto.builder()
                .name(details.getName())
                .value(details.getValue())
                .build();
    }
}