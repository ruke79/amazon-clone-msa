package com.project.catalog_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class SubCategoryResponse {

    private String id;

    private String name;

    private String parentName;
}
