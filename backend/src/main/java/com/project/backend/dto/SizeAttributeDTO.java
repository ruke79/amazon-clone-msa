package com.project.backend.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class SizeAttributeDTO {

    private String id;

    private String size;

    private int quantity;

    private int price;

}
