package com.project.user-service.security.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CategoryRequest {
    private String name;
    private String slug;
}
