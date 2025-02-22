package com.project.userservice.controller;
 
import org.hibernate.mapping.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.project.userservice.constants.StatusMessages;
import com.project.userservice.dto.CategoryDto;
import com.project.userservice.dto.ColorAttributeDto;
import com.project.userservice.dto.CouponDto;
import com.project.userservice.dto.ProductDto;
import com.project.userservice.dto.SubCategoryDto;
import com.project.userservice.dto.UserProfileDto;
import com.project.userservice.model.Role;
import com.project.userservice.model.SubCategory;
import com.project.userservice.model.ProductCategory;
import com.project.userservice.model.ProductColorAttribute;
import com.project.userservice.model.ProductDetails;
import com.project.userservice.model.ProductQA;
import com.project.userservice.model.ProductSizeAttribute;
import com.project.userservice.model.ProductSku;
import com.project.userservice.model.User;
import com.project.userservice.model.Product;
import com.project.userservice.repository.CategoryRepository;
import com.project.userservice.repository.ProductRepository;
import com.project.userservice.repository.ProductSkuRepository;
import com.project.userservice.repository.SubCategoryRepository;
import com.project.userservice.security.request.CategoryRequest;
import com.project.userservice.security.request.CouponRequest;
import com.project.userservice.security.request.ImageRequest;
import com.project.userservice.security.request.ProductInfosLoadRequest;
import com.project.userservice.security.request.ProductRequest;

import com.project.userservice.security.request.SubCategoryRequest;
import com.project.userservice.security.response.CategoryResponse;
import com.project.userservice.security.response.GenericResponse;
import com.project.userservice.security.response.MessageResponse;
import com.project.userservice.security.response.SubCategoryResponse;
import com.project.userservice.service.CouponService;
import com.project.userservice.service.UserService;

import jakarta.mail.Multipart;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputFilter.Status;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
// @PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    private final UserService userService;

    //private final ProductService productService;

    private final CouponService couponService;
    

    private final ProductRepository productRepository;

    

    public final ProductSkuRepository productskuRepository;

    @GetMapping("/getusers")
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(),
                HttpStatus.OK);
    }

    @PutMapping("/update-role")
    public ResponseEntity<String> updateUserRole(@RequestParam Long userId,
            @RequestParam String roleName) {
        userService.updateUserRole(userId, roleName);
        return ResponseEntity.ok(StatusMessages.USER_ROLE_UPDATED);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<UserProfileDto> getUser(@PathVariable Long id) {
        return new ResponseEntity<>(userService.getUserById(id),
                HttpStatus.OK);
    }

    @PutMapping("/update-lock-status")
    public ResponseEntity<String> updateAccountLockStatus(@RequestParam Long userId,
            @RequestParam boolean lock) {
        userService.updateAccountLockStatus(userId, lock);
        return ResponseEntity.ok(StatusMessages.ACCOUNT_LOCK_STATUS_UPDATED);
    }

    @GetMapping("/roles")
    List<Role> getAllRoles() {
        return userService.getAllRoles();
    }

    @PutMapping("/update-expiry-status")
    ResponseEntity<String> updateAccountExpiryStatus(@RequestParam Long userId,
            @RequestParam boolean expire) {
        userService.updateAccountExpiryStatus(userId, expire);
        return ResponseEntity.ok(StatusMessages.ACCOUNT_EXPIRY_STATUS_UPDATED);
    }

    @PutMapping("/update-enabled-status")
    ResponseEntity<String> updateAccountEnabledStatus(@RequestParam Long userId,
            @RequestParam boolean enabled) {
        userService.updateAccountEnabledStatus(userId, enabled);
        return ResponseEntity.ok(StatusMessages.ACCOUNT_ENABLE_STATUS_UPDATED);
    }

    @PutMapping("/update-credentials-expiry-status")
    ResponseEntity<String> updateCredentialsExpiryStatus(@RequestParam Long userId,
            @RequestParam boolean expire) {
        userService.updateCredentialsExpiryStatus(userId, expire);
        return ResponseEntity.ok(StatusMessages.CREDENTIALS_EXPIRY_STATUS_UPDATED);
    }

    @PutMapping("/update-password")
    ResponseEntity<String> updatePassword(@RequestParam Long userId,
            @RequestParam String password) {
        try {
            userService.updatePassword(userId, password);
            return ResponseEntity.ok(StatusMessages.PASSWORD_UPDATED);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // @PostMapping("/category")
    // ResponseEntity<?> addCategory(@RequestBody CategoryRequest request) {

    //     try {

    //         boolean exist = categoryService.findCategory(request.getName());

    //         if (!exist) {

    //             // ProductCategory category = new ProductCategory();
    //             // category.setCategoryName(categoryRequest.getName());
    //             // category.setSlug(categoryRequest.getSlug());

    //             // categoryRepository.save(category);

    //             CategoryDto dto = categoryService.createCategory(request.getName(), request.getSlug());

    //             CategoryResponse response = new CategoryResponse(
    //                     dto.getId(),
    //                     dto.getName()
    //             // category.getSlug()
    //             );

    //             return new ResponseEntity<>(response, HttpStatus.OK);
    //         } else {
    //             return new ResponseEntity<>(StatusMessages.CATEGORY_IS_EXISTED, HttpStatus.BAD_REQUEST);
    //         }
    //     } catch (RuntimeException e) {
    //         return ResponseEntity.status(HttpStatus.BAD_REQUEST)
    //                 .body(new MessageResponse(e.getMessage()));
    //     }

    // }

    // @PostMapping("/subcategory")
    // ResponseEntity<?> addSubCategory(@RequestBody SubCategoryRequest request) {

    //     try {
    //         boolean exist = categoryService.findSubCategory(request.getSubcategoryName(), request.getParent());

    //         if (!exist) {

    //             SubCategoryDto subcategory = categoryService.createSubCategory(request.getSubcategoryName(),
    //                     request.getParent(), request.getSlug());

    //             SubCategoryResponse response = new SubCategoryResponse(
    //                     subcategory.getId(),
    //                     subcategory.getName(), subcategory.getParent().getName());

    //             return new ResponseEntity<>(response, HttpStatus.OK);

    //         } else
    //             return new ResponseEntity<>(StatusMessages.SUBCATEGORY_IS_EXISTED, HttpStatus.BAD_REQUEST);
    //     } catch (RuntimeException e) {
    //         return ResponseEntity.status(HttpStatus.BAD_REQUEST)
    //                 .body(new MessageResponse(e.getMessage()));
    //     }
    // }

    // @GetMapping("/categories")
    // ResponseEntity<?> getCategories() {

    //     List<ProductCategory> category = categoryRepository.findAll();

    //     List<CategoryResponse> responses = new ArrayList<>();
    //     category.forEach(item -> {
    //         responses.add(new CategoryResponse(Long.toString(item.getCategoryId()), item.getCategoryName()));
    //     });

    //     return new ResponseEntity<>(responses, HttpStatus.OK);

    // }

    // @GetMapping("/product/allsubcategories")
    // ResponseEntity<?> loadAllSubcategories() {

    //     try {

    //         List<SubCategory> subcategories = subCategoryRepository.findAll();

    //         if (!subcategories.isEmpty()) {

    //             ArrayList<SubCategoryResponse> subCategoryList = new ArrayList<>();

    //             subcategories.forEach(item -> {
    //                 ProductCategory category = categoryRepository.findById(item.getCategory().getCategoryId())
    //                         .orElseThrow(() -> new RuntimeException("Category not found"));

    //                 subCategoryList
    //                         .add(new SubCategoryResponse(Long.toString(item.getSubcategoryId()),
    //                                 item.getSubcategoryName(), category.getCategoryName()));
    //             });

    //             return new ResponseEntity<>(subCategoryList, HttpStatus.OK);
    //         } else
    //             return new ResponseEntity<>(StatusMessages.SUBCATEGORY_NOT_FOUND, HttpStatus.BAD_REQUEST);
    //     } catch (RuntimeException e) {
    //         return ResponseEntity.status(HttpStatus.BAD_REQUEST)
    //                 .body(new MessageResponse(e.getMessage()));
    //     }
    // }

    // @GetMapping("/product/subcategories")
    // ResponseEntity<?> getSubcategories(@RequestParam String category) {

    //     try {

    //         List<SubCategory> subcategories = categoryRepository
    //                 .findSubCategoriesByCategoryId(Long.parseLong(category));

    //         if (!subcategories.isEmpty()) {

    //             ArrayList<SubCategoryResponse> subCategoryList = new ArrayList<>();

    //             subcategories.forEach(item -> subCategoryList
    //                     .add(new SubCategoryResponse(Long.toString(item.getSubcategoryId()),
    //                             item.getSubcategoryName(), item.getCategory().getCategoryName())));

    //             return new ResponseEntity<>(subCategoryList, HttpStatus.OK);
    //         } else
    //             return new ResponseEntity<>(StatusMessages.SUBCATEGORY_NOT_FOUND, HttpStatus.BAD_REQUEST);
    //     } catch (RuntimeException e) {
    //         return ResponseEntity.status(HttpStatus.BAD_REQUEST)
    //                 .body(new MessageResponse(e.getMessage()));
    //     }
    // }

    

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

   
    @PostMapping("/coupon")
    ResponseEntity<?> addCoupon(@RequestBody CouponRequest request) {

        try {
            if(couponService.create(request)) {

                return new ResponseEntity<>(couponService.getCoupons(), HttpStatus.OK);
            }
            else 
                return new ResponseEntity<>("Coupon not found", HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Faield to create a coupon."));
        }
    }

    @DeleteMapping("/coupon/delete")
    ResponseEntity<?> deleteCoupon(@RequestParam String id) {

        try {
            couponService.delete(id);

            return new ResponseEntity<>(new MessageResponse("succeeded to delete a coupon"), HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Faield to create a coupon."));
        }
    }

    @PostMapping("/coupon/update")
    ResponseEntity<?> updateCoupon(@RequestBody CouponRequest request) {

        try {
            CouponDto result = couponService.update(request);

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Faield to update a coupon."));
        }
    }

}
