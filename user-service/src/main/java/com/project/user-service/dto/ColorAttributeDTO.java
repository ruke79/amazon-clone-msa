package com.project.user-service.dto;


import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor

public class ColorAttributeDTO {

    private String id;
    private String color;     
    
    private String colorImage;
}
