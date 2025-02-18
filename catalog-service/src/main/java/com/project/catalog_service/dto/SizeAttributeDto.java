package com.project.catalog_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class SizeAttributeDto {

    private String id;

    private String size;

    private int quantity;

    private int price;

}
