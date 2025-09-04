package com.project.catalog_service.mapper;

import com.project.catalog_service.model.*;
import com.project.common.dto.*;

import java.util.stream.Collectors;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

public class ProductMapper {

    /**
     * Product 엔티티를 ProductDto로 변환합니다.
     * @param product 변환할 Product 엔티티
     * @return 변환된 ProductDto
     */
    public static ProductDto toDto(Product product) {

        CategoryDto parent = CategoryDto.builder()
                .id(Long.toString(product.getCategory().getCategoryId()))
                .name(product.getCategory().getCategoryName())
                .slug(product.getCategory().getSlug()).build();

        List<SubCategoryDto> subCategories = product.getSubcategories().stream()
                .map(productSubcategory -> new SubCategoryDto(
                        Long.toString(productSubcategory.getSubcategory().getSubcategoryId()),
                        parent,
                        productSubcategory.getSubcategory().getSubcategoryName(),
                        productSubcategory.getSubcategory().getSlug()))
                .collect(Collectors.toList());

        List<ProductDetailDto> details = product.getDetails().stream().map(detail -> {
            return ProductDetailDto.builder()
                    .name(detail.getName())
                    .value(detail.getValue())
                    .build();
        }).collect(Collectors.toList());


        List<ProductQADto> questions = product.getQuestions().stream().map(q -> {
            return ProductQADto.builder()
                    .question(q.getQuestion())
                    .answer(q.getAnswer())
                    .build();
        }).collect(Collectors.toList());

        List<ProductSkuDto> skus = product.getSkus().stream().map(sku -> {

            List<String> images = new ArrayList<String>();
            for (String image : sku.getImages()) {
                images.add(image);
            }

            Set<ProductSizeDto> sizes = sku.getSizes().stream().map(item -> {
                ProductSizeDto size = new ProductSizeDto(
                        Long.toString(item.getSizeId()),
                        item.getSize(),
                        item.getQuantity(),
                        item.getPrice());
                return size;
            }).collect(Collectors.toSet());

            ProductColorDto color = new ProductColorDto(
                    Long.toString(sku.getColor().getColorId()),
                    sku.getColor().getColor(),
                    sku.getColor().getColorImage());

            ProductSkuDto dto = ProductSkuDto.builder()
                    .id(Long.toString(sku.getSkuproductId()))
                    .sku(sku.getSku())
                    .images(images)
                    .discount(sku.getDiscount())
                    .sold(sku.getSold())
                    .sizes(sizes)
                    .color(color)
                    .build();

            return dto;
        }).collect(Collectors.toList());

        return ProductDto.builder()
                .id(Long.toString(product.getProductId()))
                .name(product.getName())
                .description(product.getDescription())
                .brand(product.getBrand())
                .slug(product.getSlug())
                .category(parent)
                .subCategories(subCategories)
                .details(details)
                .questions(questions)
                .skus(skus)
                .refundPolicy(product.getRefundPolicy())
                .rating(product.getRating())
                .shipping(product.getShipping().toPlainString())
                .createdAt(product.getCreatedAt().toString())
                .build();
    }
}