package com.project.common.dto;

import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSkuDto {

    private String id;

    private String sku;

    private List<String> images;
    
    //private List<String> descriptionImages;

    private int discount;

    private int sold;

    private Set<ProductSizeDto> sizes;
    private ProductColorDto color;

}
