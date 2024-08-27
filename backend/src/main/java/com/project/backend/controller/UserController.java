package com.project.backend.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.backend.dto.UserDTO;
import com.project.backend.model.User;
import com.project.backend.service.impl.UserServiceImpl;

@RestController
@RequestMapping("/api/user/profile")
public class UserController {

    @Autowired
    UserServiceImpl userService;

    @GetMapping("/address")
    ResponseEntity<UserDTO> getUserInfoWithAddresses(@AuthenticationPrincipal UserDetails userDetails) {

        User user = userService.findByUsername(userDetails.getUsername());

        UserDTO repsonse = userService.findUserWithAddresses(user);

        return new ResponseEntity<>(repsonse, HttpStatus.OK);

    }

    @GetMapping("/payment")
    ResponseEntity<UserDTO> getUserInfoWithPayment(@AuthenticationPrincipal UserDetails userDetails) {

        User user = userService.findByUsername(userDetails.getUsername());

        UserDTO repsonse = userService.findUserWithdefaultPaymentMethod(user);

        return new ResponseEntity<>(repsonse, HttpStatus.OK);

    }

    
}
