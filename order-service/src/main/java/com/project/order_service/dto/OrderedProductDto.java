package com.project.order_service.dto;


import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderedProductDto {

    private String id;

     private String name;

     private String image;

     private String size;

     private int qty;
        
     private ColorAttributeDto color;

    private int price;      

}
