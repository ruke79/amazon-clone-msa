package com.project.user-service.security.request;


import java.util.List;

import com.project.user-service.dto.CartProductDTO;
import com.project.user-service.dto.ProductInfoDTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CartRequest {

    List<ProductInfoDTO> products;
 }
