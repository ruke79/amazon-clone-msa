package com.project.backend.security.request;


import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductsRequest {

    List<ProductRequest> products;
}
