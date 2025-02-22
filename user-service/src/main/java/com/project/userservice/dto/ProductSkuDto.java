package com.project.userservice.dto;

import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.project.userservice.dto.SizeAttributeDto;
import com.project.userservice.dto.ColorAttributeDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSkuDto {

    private Long id;

    private String sku;

    private List<String> images;
    
    //private List<String> descriptionImages;

    private int discount = 0;

    private int sold = 0;

    private Set<SizeAttributeDto> sizes;
    private ColorAttributeDto color;

}
