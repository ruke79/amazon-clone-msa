package com.project.cart-service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubCategoryDTO {
    private String id;
    private CategoryDto parent;
    private String name;
    private String slug;
}
