package com.project.userservice.controller;
 

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.project.common.constants.StatusMessages;
import com.project.common.response.MessageResponse;

import com.project.userservice.dto.CouponDto;

import com.project.userservice.dto.UserProfileDto;
import com.project.userservice.model.Role;

import com.project.userservice.model.User;


import com.project.userservice.service.UserService;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;


import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    private final UserService userService;

    
    @GetMapping("/getusers")
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(),
                HttpStatus.OK);
    }

    @PutMapping("/update-role")
    public ResponseEntity<String> updateUserRole(@RequestParam String userId,
            @RequestParam String roleName) {
        userService.updateUserRole(Long.parseLong(userId), roleName);
        return ResponseEntity.ok(StatusMessages.USER_ROLE_UPDATED);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<UserProfileDto> getUser(@PathVariable String id) {
        return new ResponseEntity<>(userService.getUserById(Long.parseLong(id)),
                HttpStatus.OK);
    }

    @PutMapping("/update-lock-status")
    public ResponseEntity<String> updateAccountLockStatus(@RequestParam String userId,
            @RequestParam boolean lock) {
        userService.updateAccountLockStatus(Long.parseLong(userId), lock);
        return ResponseEntity.ok(StatusMessages.ACCOUNT_LOCK_STATUS_UPDATED);
    }

    @GetMapping("/roles")
    List<Role> getAllRoles() {
        return userService.getAllRoles();
    }

    @PutMapping("/update-expiry-status")
    ResponseEntity<String> updateAccountExpiryStatus(@RequestParam String userId,
            @RequestParam boolean expire) {
        userService.updateAccountExpiryStatus(Long.parseLong(userId), expire);
        return ResponseEntity.ok(StatusMessages.ACCOUNT_EXPIRY_STATUS_UPDATED);
    }

    @PutMapping("/update-enabled-status")
    ResponseEntity<String> updateAccountEnabledStatus(@RequestParam String userId,
            @RequestParam boolean enabled) {
        userService.updateAccountEnabledStatus(Long.parseLong(userId), enabled);
        return ResponseEntity.ok(StatusMessages.ACCOUNT_ENABLE_STATUS_UPDATED);
    }

    @PutMapping("/update-credentials-expiry-status")
    ResponseEntity<String> updateCredentialsExpiryStatus(@RequestParam String userId,
            @RequestParam boolean expire) {
        userService.updateCredentialsExpiryStatus(Long.parseLong(userId), expire);
        return ResponseEntity.ok(StatusMessages.CREDENTIALS_EXPIRY_STATUS_UPDATED);
    }

    @PutMapping("/update-password")
    ResponseEntity<String> updatePassword(@RequestParam String userId,
            @RequestParam String password) {
        try {
            userService.updatePassword(Long.parseLong(userId), password);
            return ResponseEntity.ok(StatusMessages.PASSWORD_UPDATED);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    

    // DB에 이미지 저장할 때
    // @PostMapping("/product")
    // public ResponseEntity<?> addProduct(
    // @RequestPart("product") ProductRequest request,
    // @RequestPart("images") MultipartFile[] images,
    // @RequestPart("colorImage") MultipartFile colorImage) throws IOException {

    // Product product = productRepository.findByName(request.getName());

    // if (product != null)
    // return null;

    // ProductSku skuProject = productService.addProduct(request, images,
    // colorImage);

    // return new ResponseEntity<>(skuProject, HttpStatus.OK);

    // }

      
}
