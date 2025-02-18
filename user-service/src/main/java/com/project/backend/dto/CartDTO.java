package com.project.backend.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {

    private String userId;
    private String userImage;
    private List<AddressDTO> address;


    private List<CartProductDTO> products;

    private int cartTotal;
    private int totalAfterDiscount;

}
