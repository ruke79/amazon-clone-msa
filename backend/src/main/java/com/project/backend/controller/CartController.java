package com.project.backend.controller;

import java.util.List;

import org.hibernate.validator.cfg.defs.pl.REGONDef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.backend.dto.AddressDTO;
import com.project.backend.dto.CartDTO;
import com.project.backend.dto.ProductInfoDTO;
import com.project.backend.model.Cart;
import com.project.backend.model.ShippingAddress;
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

    @PutMapping("/savecart")
    ResponseEntity<CartResponse> saveCart(@RequestBody CartRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        Cart cart = cartService.saveCart(request, username);

        CartResponse response = new CartResponse(cart.getCartTotal());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/updatecart")
    ResponseEntity<List<ProductInfoDTO>> updateCart(@RequestBody ProductInfoRequest products,
            @AuthenticationPrincipal UserDetails userDetails) {

        List<ProductInfoDTO> result = cartService.updateCart(products);

        return new ResponseEntity<>(result, HttpStatus.OK);

    }

    @GetMapping("/checkout")
    ResponseEntity<CartDTO> getCart(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();

        CartDTO response = cartService.getCart(username);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/save_shipping_address")
    ResponseEntity<List<AddressDTO>> saveShippingAddress(@RequestBody AddressRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();

        List<AddressDTO> response = addressService.saveShippingAddress(request, username);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/coupon")
    ResponseEntity<CouponResponse> applyCoupon(CouponRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();

        CouponResponse response = cartService.applyCoupon(request, username);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/selectaddress/{addressId}")
    ResponseEntity<List<AddressDTO>> selectShippingAddresses(@PathVariable String addressId,
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();

        return new ResponseEntity<>(cartService.getShipAddresses(username, addressId), HttpStatus.OK);
    }

    @GetMapping("/deleteaddress/{addressId}")
    ResponseEntity<List<AddressDTO>> deleteShippingAddresses(@PathVariable String addressId,
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();

        return new ResponseEntity<>(cartService.deleteShippingAddress(username, addressId), HttpStatus.OK);
    }

    @PutMapping("/changepm")
    String changePayment(@RequestParam("paymentMethod") String paymentMethod,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();

        return cartService.updatePaymentMethod(username, paymentMethod);
    }

}
