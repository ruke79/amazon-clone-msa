package com.project.common.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor

public class ProductColorDto {

    private String id;
    private String color;     
    
    private String colorImage;
}
