package com.project.catalog_service.mapper;

import com.project.catalog_service.model.ProductSku;
import com.project.common.dto.ProductColorDto;
import com.project.common.dto.ProductSkuDto;
import com.project.common.dto.ProductSizeDto;

import java.util.Set;
import java.util.stream.Collectors;

public class ProductSkuMapper {
    public static ProductSkuDto toDto(ProductSku sku) {
        if (sku == null) {
            return null;
        }

        Set<ProductSizeDto> sizes = sku.getSizes().stream()
                .map(ProductSizeMapper::toDto)
                .collect(Collectors.toSet());

        ProductColorDto color = ProductColorMapper.toDto(sku.getColor());

        return ProductSkuDto.builder()
                .id(Long.toString(sku.getSkuproductId()))
                .sku(sku.getSku())
                .images(sku.getImages())
                .discount(sku.getDiscount())
                .sold(sku.getSold())
                .sizes(sizes)
                .color(color)
                .build();
    }
}