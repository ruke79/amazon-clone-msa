package com.project.cart_service.dto.request;


import java.util.List;

import com.project.cart_service.dto.ProductInfoDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CartRequest {

    @NotNull
    private List<ProductInfoDto> products;
    @Email
    @NotNull
    private String email;
 }
