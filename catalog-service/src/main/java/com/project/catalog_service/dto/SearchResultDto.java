package com.project.catalog_service.dto;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultDto {

    //private Set<ProductDTO> products;
    private Page<ProductDto> product;

    private List<CategoryDto> categories;

    private List<SubCategoryDTO> subCategories;

    private List<String> colors;

    private List<String> brandsDB;

    private List<String> sizes;

    private List<ProductDetailDto> details;


    private int totalProducts;


    

}
