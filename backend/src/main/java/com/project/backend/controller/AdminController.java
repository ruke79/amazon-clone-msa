package com.project.backend.controller;

import org.hibernate.mapping.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.project.backend.constants.StatusMessages;
import com.project.backend.dto.CouponDTO;
import com.project.backend.dto.ProductDTO;
import com.project.backend.dto.UserDTO;
import com.project.backend.model.Role;
import com.project.backend.model.SubCategory;
import com.project.backend.model.ProductCategory;
import com.project.backend.model.ProductColorAttribute;
import com.project.backend.model.ProductDetails;
import com.project.backend.model.ProductQA;
import com.project.backend.model.ProductSizeAttribute;
import com.project.backend.model.ProductSku;
import com.project.backend.model.User;
import com.project.backend.model.Product;
import com.project.backend.repository.CategoryRepository;
import com.project.backend.repository.ProductRepository;
import com.project.backend.repository.ProductSkuRepository;
import com.project.backend.repository.SubCategoryRepository;
import com.project.backend.security.request.CategoryRequest;
import com.project.backend.security.request.CouponRequest;
import com.project.backend.security.request.ImageRequest;
import com.project.backend.security.request.ProductRequest;
import com.project.backend.security.request.SubCategoryRequest;
import com.project.backend.security.response.CategoryResponse;
import com.project.backend.security.response.MessageResponse;
import com.project.backend.security.response.SubCategoryResponse;
import com.project.backend.service.CouponService;
import com.project.backend.service.ProductService;
import com.project.backend.service.UserService;

import jakarta.mail.Multipart;
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
// @PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    private final UserService userService;

    private final ProductService productService;

    private final CouponService couponService;

    private final CategoryRepository categoryRepository;

    private final SubCategoryRepository subCategoryRepository;

    private final ProductRepository productRepository;

    @Autowired
    public AdminController(UserService userService, ProductService productService,
            CategoryRepository categoryRepository, SubCategoryRepository subCategoryRepository,
            ProductRepository productRepository, ProductSkuRepository productskuRepository,
            CouponService couponService) {
        this.userService = userService;
        this.productService = productService;
        this.couponService = couponService;
        this.categoryRepository = categoryRepository;
        this.subCategoryRepository = subCategoryRepository;
        this.productRepository = productRepository;
        this.productskuRepository = productskuRepository;
    }

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
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
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

    @PostMapping("/category")
    ResponseEntity<?> addCategory(@RequestBody CategoryRequest categoryRequest) {

        try {

            if (null == categoryRepository.findByCategoryName(categoryRequest.getName())) {

                ProductCategory category = new ProductCategory();
                category.setCategoryName(categoryRequest.getName());
                category.setSlug(categoryRequest.getSlug());

                categoryRepository.save(category);

                CategoryResponse response = new CategoryResponse(
                        Long.toString(category.getCategoryId()),
                        category.getCategoryName()
                // category.getSlug()
                );

                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(StatusMessages.CATEGORY_IS_EXISTED, HttpStatus.BAD_REQUEST);
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(e.getMessage()));
        }

    }

    @PostMapping("/subcategory")
    ResponseEntity<?> addSubCategory(@RequestBody SubCategoryRequest subcategoryRequest) {

        List<String> existed = new ArrayList<String>();
        existed.add(subcategoryRequest.getSubcategoryName());

        try {

            List<SubCategory> data = subCategoryRepository.findBySubcategoryNameIn(existed);

            if (data.isEmpty()) {

                SubCategory subcategory = new SubCategory();
                subcategory.setSubcategoryName(subcategoryRequest.getSubcategoryName());

                Optional<ProductCategory> category = categoryRepository
                        .findById(Long.parseLong(subcategoryRequest.getParent()));

                subcategory.setCategory(category.get());

                subcategory.setSlug(subcategoryRequest.getSlug());

                subCategoryRepository.save(subcategory);

                SubCategoryResponse response = new SubCategoryResponse(
                        Long.toString(subcategory.getSubcategoryId()),
                        subcategory.getSubcategoryName());

                return new ResponseEntity<>(response, HttpStatus.OK);
            } else
                return new ResponseEntity<>(StatusMessages.SUBCATEGORY_IS_EXISTED, HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/categories")
    ResponseEntity<?> getCategories() {

        try {

            List<ProductCategory> category = categoryRepository.findAll();

            if (!category.isEmpty()) {

                List<CategoryResponse> responses = new ArrayList<>();
                category.forEach(item -> {
                    responses.add(new CategoryResponse(Long.toString(item.getCategoryId()), item.getCategoryName()));
                });

                return new ResponseEntity<>(responses, HttpStatus.OK);
            } else
                return new ResponseEntity<>(StatusMessages.CATEGORY_NOT_FOUND, HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/product/subcategories")
    ResponseEntity<?> getSubcategories(@RequestParam String category) {

        try {

            List<SubCategory> subcategories = categoryRepository
                    .findSubCategoriesByCategoryId(Long.parseLong(category));

            if (!subcategories.isEmpty()) {

                ArrayList<SubCategoryResponse> subCategoryList = new ArrayList<>();

                subcategories.forEach(item -> subCategoryList
                        .add(new SubCategoryResponse(Long.toString(item.getSubcategoryId()),
                                item.getSubcategoryName())));

                return new ResponseEntity<>(subCategoryList, HttpStatus.OK);
            } else
                return new ResponseEntity<>(StatusMessages.SUBCATEGORY_NOT_FOUND, HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping(value = "/product/products")
    ResponseEntity<?> getProducts() {

        try {
            List<Product> products = productRepository.findAll();

            if (!products.isEmpty()) {

                List<ProductDTO> response = new ArrayList<ProductDTO>();
                for (Product product : products) {
                    ProductDTO dto = productService.getProductByName(product.getName());
                    response.add(dto);
                }

                return new ResponseEntity<>(response, HttpStatus.OK);
            } else
                return new ResponseEntity<>(StatusMessages.PRODUCT_IS_EMPTY, HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/product/{productId}")
    ResponseEntity<?> getParentProduct(@PathVariable String productId) {

        try {

            ProductDTO dto = productService.getProductById(Long.parseLong(productId));

            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(e.getMessage()));
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

    @PostMapping("/product")
    ResponseEntity<?> addProduct(
            @RequestPart("product") ProductRequest request,
            @RequestParam(value = "images", required = false) List<String> images,
            @RequestParam(value = "colorImage", required = false) String colorImage) {

        try {

            ProductSku skuProject = productService.addProduct(request, images, colorImage);
            return new ResponseEntity<>(skuProject, HttpStatus.OK);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(e.getMessage()));

        }

    }

    @PostMapping("/coupon")
    ResponseEntity<?> addCoupon(@RequestBody CouponRequest request) {

        try {
            CouponDTO result = couponService.create(request);

            return new ResponseEntity<>(result, HttpStatus.OK);
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
            CouponDTO result = couponService.update(request);

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Faield to update a coupon."));
        }
    }

}
