package com.project.userservice.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class SizeAttributeDto {

    private Long id;

    private String size;

    private int quantity;

    private int price;

}
