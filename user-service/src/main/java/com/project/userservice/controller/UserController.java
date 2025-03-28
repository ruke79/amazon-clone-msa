package com.project.userservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

import com.project.common.constants.StatusMessages;
import com.project.common.response.MessageResponse;
import com.project.userservice.dto.AddressDto;
import com.project.userservice.dto.UserProfileDto;
import com.project.userservice.model.User;
import com.project.userservice.security.request.AddressRequest;
import com.project.userservice.security.request.PasswordRequest;
import com.project.userservice.security.service.UserDetailsImpl;
import com.project.userservice.service.AddressService;
import com.project.userservice.service.impl.UserServiceImpl;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user/profile")
public class UserController {

    private final UserServiceImpl userService;

    private final AddressService addressService;

    
    @GetMapping("/address")
    ResponseEntity<?> getUserAddresses(@AuthenticationPrincipal UserDetails userDetails) {

        if (null != userDetails) {

            try {

                User user = userService.findByUsername(userDetails.getUsername());

                //UserProfileDto repsonse = userService.findUserWithAddresses(user);
                List<AddressDto> repsonse = userService.findUserAddresses(user);

                return new ResponseEntity<>(repsonse, HttpStatus.OK);
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new MessageResponse(StatusMessages.GET_USERINFO_FAILED));
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(StatusMessages.USER_NOT_FOUND);
        }

    }

    @PostMapping("/save_address")
    ResponseEntity<?> saveShippingAddress(@RequestBody AddressRequest request,
    @AuthenticationPrincipal UserDetails userDetails) {

        if (null != userDetails) {
            String username = userDetails.getUsername();

            try {
                List<AddressDto> response = addressService.saveShippingAddress(request, username);

                return new ResponseEntity<>(response, HttpStatus.OK);
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new MessageResponse(e.getMessage()));
            }
        } else {
            return new ResponseEntity<>(StatusMessages.USER_NOT_FOUND, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/select/{addressId}")
    ResponseEntity<?> selectShippingAddresses(@PathVariable("addressId") String addressId,
    @AuthenticationPrincipal UserDetails userDetails) {

        if (null != userDetails) {

            String username = userDetails.getUsername();

            try {
                return new ResponseEntity<>(addressService.getShipAddresses(username, addressId), HttpStatus.OK);
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new MessageResponse(e.getMessage()));
            }
        } else {
            return new ResponseEntity<>(StatusMessages.USER_NOT_FOUND, HttpStatus.UNAUTHORIZED);
        }
    }

    @DeleteMapping("/delete/{addressId}")
    ResponseEntity<?> deleteShippingAddresses(@PathVariable("addressId") String addressId,
    @AuthenticationPrincipal UserDetails userDetails) {

        if (null != userDetails) {
            String username = userDetails.getUsername();
            try {
                return new ResponseEntity<>(addressService.deleteShippingAddress(username, addressId), HttpStatus.OK);
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new MessageResponse(e.getMessage()));
            }
        } else {
            return new ResponseEntity<>(StatusMessages.USER_NOT_FOUND, HttpStatus.UNAUTHORIZED);
        }
    }


    @GetMapping("/payment")
    ResponseEntity<?> getUserInfoWithPayment(@AuthenticationPrincipal UserDetails userDetails) {

        if (null != userDetails) {

            try {

                User user = userService.findByUsername(userDetails.getUsername());

                UserProfileDto repsonse = userService.findUserWithdefaultPaymentMethod(user);

                return new ResponseEntity<>(repsonse, HttpStatus.OK);
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new MessageResponse(StatusMessages.GET_USERINFO_FAILED));
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(StatusMessages.USER_NOT_FOUND);
        }

    }

    @PutMapping("/changepm")
    ResponseEntity<?> changePayment(@RequestParam("paymentMethod") String paymentMethod,
    @AuthenticationPrincipal UserDetails userDetails) {

        if (null != userDetails) {
            String username = userDetails.getUsername();

            try {
                return new ResponseEntity<>(userService.updatePaymentMethod(username, paymentMethod), HttpStatus.OK);
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new MessageResponse(e.getMessage()));
            }
        } else {
            return new ResponseEntity<>(StatusMessages.USER_NOT_FOUND, HttpStatus.UNAUTHORIZED);
        }
    }

    
    

    @PutMapping("/update-password")
    public ResponseEntity<String> updatePassword(@RequestBody PasswordRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

    if (null != userDetails) {
        try {
            UserDetailsImpl user =(UserDetailsImpl)userDetails;
            userService.updatePassword(user.getId(), request.getCurrent_password(), request.getNew_password());
            return ResponseEntity.ok("Password updated");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(StatusMessages.USER_NOT_FOUND);
    }
}


}
