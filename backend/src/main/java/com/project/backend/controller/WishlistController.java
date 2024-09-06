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

import com.project.backend.constants.StatusMessages;
import com.project.backend.model.WishList;
import com.project.backend.security.request.WishListRequest;
import com.project.backend.security.response.GenericResponse;
import com.project.backend.security.response.MessageResponse;
import com.project.backend.service.CartService;

@RestController
@RequestMapping("api/user")
public class WishlistController {

    
    private final CartService cartService;

    
    @Autowired
    public WishlistController(CartService cartService) {
        this.cartService = cartService;   }



    @PutMapping("/wishlist")
    ResponseEntity<?> addWishList(@RequestBody WishListRequest request, 
    @AuthenticationPrincipal UserDetails userDetails) {

        if (null != userDetails ) {            
            String username = userDetails.getUsername();

            try {
                cartService.addWishList(username, request);

                GenericResponse response = new GenericResponse("Product successfully added to your wishlist");

                return new ResponseEntity<>(response, HttpStatus.OK);
            } catch(RuntimeException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(StatusMessages.ADD_WISHLIST_FAILED));       
            }
        }  else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(StatusMessages.USER_NOT_FOUND);
        }
    }


}
