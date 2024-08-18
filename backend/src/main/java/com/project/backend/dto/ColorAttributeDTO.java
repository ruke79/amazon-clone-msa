package com.project.backend.dto;


import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor

public class ColorAttributeDTO {

    private Long colorId;
    private String color;     
    
    private String colorImage;
}
