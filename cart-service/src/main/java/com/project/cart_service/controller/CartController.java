package com.project.cart_service.controller;

import java.util.List;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.project.common.dto.ProductInfoDto;
import com.project.cart_service.dto.CartDto;
import com.project.cart_service.dto.request.CartRequest;
import com.project.cart_service.dto.request.ProductInfoRequest;
import com.project.cart_service.dto.response.CartResponse;
import com.project.cart_service.dto.response.MessageResponse;
import com.project.cart_service.model.Cart;




import com.project.cart_service.service.CartService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/cart/")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    
    


    @PostMapping("/savecart")
    ResponseEntity<?> saveCart(@Valid @RequestBody CartRequest request) {
    
                 Cart cart = cartService.saveCart(request);

                 if (null != cart ) {

                    CartResponse response = new CartResponse(Long.toString(cart.getCartId()), cart.getCartTotal());

                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
                else {
                    return new ResponseEntity<>(null, HttpStatus.OK);
                }

            
    }
    // @DeleteMapping("/deleteItem/{productId}")
    // ResponseEntity<?> deleteCartItem(@PathVariable(required = true) String productId,  @RequestParam("userId") String email  ) {

        
    //         try {
    //             cartService.deleteCartItem(productId, email);

    //             return new ResponseEntity<>("Cart item deleted successfuly.", HttpStatus.OK);
    //         } 
    //         catch(RuntimeException e) {
    //             return ResponseEntity.status(HttpStatus.BAD_REQUEST)
    //             .body(new MessageResponse(e.getMessage()));
    //         }           
       
    // }

    @GetMapping("/loadcart") 
    ResponseEntity<?> loadCart(@RequestParam String userId) {

        
            try {
                List<ProductInfoDto> response = cartService.loadCart(userId);

                return new ResponseEntity<>(response, HttpStatus.OK);
            } 
            catch(RuntimeException e) {
                // return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                // .body(new MessageResponse(e.getMessage()));
                return new ResponseEntity<>(null, HttpStatus.OK);
            }
        
    }

    @PutMapping("/updatecart")
    ResponseEntity<?> updateCart(@RequestBody ProductInfoRequest products) {
        

        try {
            List<ProductInfoDto> result = cartService.updateCart(products);

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(e.getMessage()));
        }

    }

    @GetMapping("/checkout")
    ResponseEntity<?> getCart(@RequestParam("userId") String email) {

            try {
                CartDto response = cartService.getCart(email);

                return new ResponseEntity<>(response, HttpStatus.OK);
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new MessageResponse(e.getMessage()));
            }

    }
        
    @GetMapping("/{cartProductId}") 
    String  getProductId(@PathVariable("cartProductId") String cartProdcutId) {

          Long productId = cartService.getProductId(cartProdcutId);
          return Long.toString(productId);
    }

}
