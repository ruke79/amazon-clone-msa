package com.project.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubCategoryDto {
    private String id;
    private CategoryDto parent;
    private String name;
    private String slug;
}
