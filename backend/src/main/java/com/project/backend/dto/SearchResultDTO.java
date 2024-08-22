package com.project.backend.dto;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultDTO {

    //private Set<ProductDTO> products;
    private Page<ProductDTO> product;

    private List<String> categories;

    private List<String> subCategories;

    private List<String> colors;

    private List<String> brandsDB;

    private List<String> sizes;

    private List<ProductDetailDTO> details;


    private int totalProducts;


    

}
