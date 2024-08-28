package com.project.backend.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.backend.model.WishList;
import com.project.backend.security.request.WishListRequest;
import com.project.backend.security.response.GenericResponse;
import com.project.backend.service.CartService;

@RestController
@RequestMapping("api/user")
public class WishlistController {

    @Autowired
    CartService cartService;

    @PutMapping("/wishlist")
    ResponseEntity<GenericResponse> addWishList(@RequestBody WishListRequest request, 
    @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();

        cartService.addWishList(username, request);

        GenericResponse response = new GenericResponse("Product successfully added to your wishlist");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }



}
