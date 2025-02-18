package com.project.catalog_service.dto.request;

import java.util.List;

import com.project.catalog_service.dto.ProductInfoDto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductInfoRequest {

    private List<ProductInfoDto> products;
}
