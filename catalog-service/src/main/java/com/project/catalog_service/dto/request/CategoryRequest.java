package com.project.catalog_service.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CategoryRequest {
    private String name;
    private String slug;
}
