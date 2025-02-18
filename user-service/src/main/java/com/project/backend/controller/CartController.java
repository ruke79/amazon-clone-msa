package com.project.backend.controller;

import java.util.List;

import org.hibernate.validator.cfg.defs.pl.REGONDef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.backend.constants.StatusMessages;
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
import com.project.backend.security.response.MessageResponse;
import com.project.backend.security.service.UserDetailsImpl;
import com.project.backend.service.AddressService;
import com.project.backend.service.CartService;

@RestController
@RequestMapping("api/user/cart/")
public class CartController {

    private final CartService cartService;

    private final AddressService addressService;

    @Autowired
    public CartController(CartService cartService, AddressService addressService) {
        this.cartService = cartService;
        this.addressService = addressService;
    }

    @PutMapping("/savecart")
    ResponseEntity<?> saveCart(@RequestBody CartRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (null != userDetails) {
            String username = userDetails.getUsername();

            try {
                Cart cart = cartService.saveCart(request, username);

                CartResponse response = new CartResponse(cart.getCartTotal());

                return new ResponseEntity<>(response, HttpStatus.OK);
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new MessageResponse(e.getMessage()));
            }
        } else {
        }
        return new ResponseEntity<>(StatusMessages.USER_NOT_FOUND, HttpStatus.UNAUTHORIZED);

    }
    @DeleteMapping("/deleteItem/{productId}")
    ResponseEntity<?> deleteCartItem(@PathVariable(required = true) String productId,
    @AuthenticationPrincipal UserDetails userDetails) {

        if (null != userDetails) {

            try {
                cartService.deleteCartItem(productId, userDetails.getUsername());

                return new ResponseEntity<>("Cart item deleted successfuly.", HttpStatus.OK);
            } 
            catch(RuntimeException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new MessageResponse(e.getMessage()));
            }

            
        } else {
            return new ResponseEntity<>(StatusMessages.USER_NOT_FOUND, HttpStatus.UNAUTHORIZED);
        }
        

    }

    @GetMapping("/loadcart") 
    ResponseEntity<?> loadCart(@AuthenticationPrincipal UserDetails userDetails) {

        if (null != userDetails) {

            try {
                List<ProductInfoDTO> response = cartService.loadCart(userDetails.getUsername());

                return new ResponseEntity<>(response, HttpStatus.OK);
            } 
            catch(RuntimeException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new MessageResponse(e.getMessage()));
            }
            
        } else {
            return new ResponseEntity<>(StatusMessages.USER_NOT_FOUND, HttpStatus.UNAUTHORIZED);
        }
        
    }

    @PutMapping("/updatecart")
    ResponseEntity<?> updateCart(@RequestBody ProductInfoRequest products) {

        Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principle.toString() == "anonymousUser") {
            return new ResponseEntity<>(StatusMessages.USER_NOT_FOUND, HttpStatus.UNAUTHORIZED);
        }

        try {
            List<ProductInfoDTO> result = cartService.updateCart(products);

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(e.getMessage()));
        }

    }

    @GetMapping("/checkout")
    ResponseEntity<?> getCart(@AuthenticationPrincipal UserDetails userDetails) {

        if (null != userDetails) {
            String username = userDetails.getUsername();

            try {
                CartDTO response = cartService.getCart(username);

                return new ResponseEntity<>(response, HttpStatus.OK);
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new MessageResponse(e.getMessage()));
            }
        } else {
            return new ResponseEntity<>(StatusMessages.USER_NOT_FOUND, HttpStatus.UNAUTHORIZED);
        }

    }

    @PostMapping("/save_shipping_address")
    ResponseEntity<?> saveShippingAddress(@RequestBody AddressRequest request,
    @AuthenticationPrincipal UserDetails userDetails) {

        if (null != userDetails) {
            String username = userDetails.getUsername();

            try {
                List<AddressDTO> response = addressService.saveShippingAddress(request, username);

                return new ResponseEntity<>(response, HttpStatus.OK);
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new MessageResponse(e.getMessage()));
            }
        } else {
            return new ResponseEntity<>(StatusMessages.USER_NOT_FOUND, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/coupon")
    ResponseEntity<?> applyCoupon(CouponRequest request,
    @AuthenticationPrincipal UserDetails userDetails) {

        if (null != userDetails) {
            String username = userDetails.getUsername();

            try {
                CouponResponse response = cartService.applyCoupon(request, username);

                return new ResponseEntity<>(response, HttpStatus.OK);
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new MessageResponse(e.getMessage()));
            }
        } else {
            return new ResponseEntity<>(StatusMessages.USER_NOT_FOUND, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/selectaddress/{addressId}")
    ResponseEntity<?> selectShippingAddresses(@PathVariable String addressId,
    @AuthenticationPrincipal UserDetails userDetails) {

        if (null != userDetails) {

            String username = userDetails.getUsername();

            try {
                return new ResponseEntity<>(cartService.getShipAddresses(username, addressId), HttpStatus.OK);
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new MessageResponse(e.getMessage()));
            }
        } else {
            return new ResponseEntity<>(StatusMessages.USER_NOT_FOUND, HttpStatus.UNAUTHORIZED);
        }
    }

    @DeleteMapping("/deleteaddress/{addressId}")
    ResponseEntity<?> deleteShippingAddresses(@PathVariable String addressId,
    @AuthenticationPrincipal UserDetails userDetails) {

        if (null != userDetails) {
            String username = userDetails.getUsername();
            try {
                return new ResponseEntity<>(cartService.deleteShippingAddress(username, addressId), HttpStatus.OK);
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new MessageResponse(e.getMessage()));
            }
        } else {
            return new ResponseEntity<>(StatusMessages.USER_NOT_FOUND, HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping("/changepm")
    ResponseEntity<?> changePayment(@RequestParam("paymentMethod") String paymentMethod,
    @AuthenticationPrincipal UserDetails userDetails) {

        if (null != userDetails) {
            String username = userDetails.getUsername();

            try {
                return new ResponseEntity<>(cartService.updatePaymentMethod(username, paymentMethod), HttpStatus.OK);
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new MessageResponse(e.getMessage()));
            }
        } else {
            return new ResponseEntity<>(StatusMessages.USER_NOT_FOUND, HttpStatus.UNAUTHORIZED);
        }
    }

}
