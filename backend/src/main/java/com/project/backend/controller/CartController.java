package com.project.backend.controller;

import java.util.List;

import org.hibernate.validator.cfg.defs.pl.REGONDef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.backend.dto.AddressDTO;
import com.project.backend.dto.CartDTO;
import com.project.backend.dto.CartProductInfoDTO;
import com.project.backend.dto.ProductInfoDTO;
import com.project.backend.model.Cart;
import com.project.backend.security.request.AddressRequest;
import com.project.backend.security.request.CartRequest;
import com.project.backend.security.request.CouponRequest;
import com.project.backend.security.request.ProductInfoRequest;
import com.project.backend.security.response.CartResponse;
import com.project.backend.security.response.CouponResponse;
import com.project.backend.security.response.GenericResponse;
import com.project.backend.service.AddressService;
import com.project.backend.service.CartService;

@RestController
@RequestMapping("api/user/cart/")
public class CartController {

    @Autowired
    CartService cartService;

    @Autowired
    AddressService addressService;

    

    @PostMapping("/savecart")
    ResponseEntity<CartResponse> saveCart(@RequestBody CartRequest request) {

        Cart cart = cartService.saveCart(request);

        CartResponse response = new CartResponse(cart.getCartTotal());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping("/updatecart")
    ResponseEntity<List<ProductInfoDTO>> updateCart(@RequestBody ProductInfoRequest products) {

        List<ProductInfoDTO> result = cartService.updateCart(products);
        
        return new ResponseEntity<>(result, HttpStatus.OK);      

    }

    @GetMapping("/checkout")
    ResponseEntity<CartDTO> getCart(@RequestParam("userId") String userId) {

        CartDTO response = cartService.getCart(userId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/save_shipping_address")
    ResponseEntity<GenericResponse> saveShippingAddress(@RequestBody AddressRequest request) {

        if ( null !=  addressService.saveShippingAddress(request) ) {

            GenericResponse success = new GenericResponse("Saving Address Succeeded!");

            return new ResponseEntity<>(success, HttpStatus.OK);
        }

        return null;
    }
    
    @PostMapping("/coupon") 
    ResponseEntity<CouponResponse> applyCoupon(CouponRequest request) {

        CouponResponse response = cartService.applyCoupon(request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/select_shipping_address")
    ResponseEntity<List<AddressDTO>> getShippingAddresses(@RequestParam String userId,
    @RequestParam String addressId) {

        return new ResponseEntity<>(cartService.getShipAddresses(userId, addressId), HttpStatus.OK);
    }
    
}
