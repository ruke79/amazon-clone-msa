package com.project.backend.dto;

import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.project.backend.dto.SizeAttributeDTO;
import com.project.backend.dto.ColorAttributeDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSkuDTO {

    private String id;

    private String sku;

    private List<String> images;
    
    //private List<String> descriptionImages;

    private int discount = 0;

    private int sold = 0;

    private Set<SizeAttributeDTO> sizes;
    private ColorAttributeDTO color;

}
