package com.project.userservice.security.request;


import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductInfosLoadRequest {

    List<ProductInfoLoadRequest> products;
}
