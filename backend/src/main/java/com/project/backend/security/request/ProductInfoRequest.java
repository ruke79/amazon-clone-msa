package com.project.backend.security.request;

import java.util.List;

import com.project.backend.dto.ProductInfoDTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductInfoRequest {

    private List<ProductInfoDTO> products;
}
