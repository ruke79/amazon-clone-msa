package com.project.catalog_service.mapper;

import com.project.catalog_service.model.ProductQA;
import com.project.common.dto.ProductQADto;

public class ProductQAMapper {
    public static ProductQADto toDto(ProductQA qa) {
        if (qa == null) {
            return null;
        }
        return ProductQADto.builder()
                .question(qa.getQuestion())
                .answer(qa.getAnswer())
                .build();
    }
}