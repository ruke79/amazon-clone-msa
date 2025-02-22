package com.project.userservice.security.request;

import java.util.List;

import com.project.userservice.dto.ProductInfoDto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductInfoRequest {

    private List<ProductInfoDto> products;
}
