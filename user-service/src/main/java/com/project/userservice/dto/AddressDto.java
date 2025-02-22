package com.project.userservice.dto;

import com.project.userservice.model.ShippingAddress;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDto {

    private String id;

    private String firstname;

    private String lastname;

    private String address1;

    private String address2;

    private String city;

    private String state;

    private String zipCode;
    
    private String country;

    private String phoneNumber;

    private boolean active;


    public static void deepCopyShippingAddressDto(AddressDto address, ShippingAddress src ) {

        address.setId(Long.toString(src.getShippingAddressId()));
        address.setAddress1(src.getAddress1());
        address.setAddress2(src.getAddress2());
        address.setCity(src.getCity());
        address.setState(src.getState());
        address.setCountry(src.getCountry());
        address.setFirstname(src.getFirstname());
        address.setLastname(src.getLastname());
        address.setPhoneNumber(src.getPhoneNumber());
        address.setZipCode(src.getZipCode());
    }
}
