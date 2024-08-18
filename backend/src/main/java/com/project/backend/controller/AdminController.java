package com.project.backend.controller;



import org.hibernate.mapping.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
import com.project.backend.security.request.ProductRequest;
import com.project.backend.security.request.SubCategoryRequest;
import com.project.backend.security.response.SubCategoryResponse;
import com.project.backend.service.ProductService;
import com.project.backend.service.UserService;

import jakarta.mail.Multipart;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



@Slf4j
@RestController
@RequestMapping("/api/admin")
//@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    @Autowired
    UserService userService;

    @Autowired 
    ProductService productService;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    SubCategoryRepository subCategoryRepository;

    @Autowired
    ProductRepository  productRepository;

    @Autowired
    ProductSkuRepository productskuRepository;

    @GetMapping("/getusers")
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(),
                HttpStatus.OK);
    }

    @PutMapping("/update-role")
    public ResponseEntity<String> updateUserRole(@RequestParam Long userId, 
                                                 @RequestParam String roleName) {
        userService.updateUserRole(userId, roleName);
        return ResponseEntity.ok("User role updated");
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
        return ResponseEntity.ok("Account lock status updated");
    }

    @GetMapping("/roles")
    public List<Role> getAllRoles() {
        return userService.getAllRoles();
    }

    @PutMapping("/update-expiry-status")
    public ResponseEntity<String> updateAccountExpiryStatus(@RequestParam Long userId,
                                                            @RequestParam boolean expire) {
        userService.updateAccountExpiryStatus(userId, expire);
        return ResponseEntity.ok("Account expiry status updated");
    }

    @PutMapping("/update-enabled-status")
    public ResponseEntity<String> updateAccountEnabledStatus(@RequestParam Long userId,
                                                             @RequestParam boolean enabled) {
        userService.updateAccountEnabledStatus(userId, enabled);
        return ResponseEntity.ok("Account enabled status updated");
    }

    @PutMapping("/update-credentials-expiry-status")
    public ResponseEntity<String> updateCredentialsExpiryStatus(@RequestParam Long userId, @RequestParam boolean expire) {
        userService.updateCredentialsExpiryStatus(userId, expire);
        return ResponseEntity.ok("Credentials expiry status updated");
    }

    @PutMapping("/update-password")
    public ResponseEntity<String> updatePassword(@RequestParam Long userId,
                                                 @RequestParam String password) {
        try {
            userService.updatePassword(userId, password);
            return ResponseEntity.ok("Password updated");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/category")
    public ResponseEntity<ProductCategory> addCategory(@RequestBody CategoryRequest categoryRequest) {

            log.info(categoryRequest.getName() +' ' + categoryRequest.getSlug());

            ProductCategory category = new ProductCategory();
            category.setCategoryName(categoryRequest.getName());
            category.setSlug(categoryRequest.getSlug());

            categoryRepository.save(category);
            
            return new ResponseEntity<>(category,  HttpStatus.OK);

    }

    @PostMapping("/subcategory")
    public ResponseEntity<SubCategory> addSubCategory(@RequestBody SubCategoryRequest subcategoryRequest) {

            log.info(subcategoryRequest.getSubcategoryName() + ' ' + subcategoryRequest.getParent() + ' ' + subcategoryRequest.getSlug());

            SubCategory subcategory = new SubCategory();
            subcategory.setSubcategoryName(subcategoryRequest.getSubcategoryName());

            ProductCategory category = categoryRepository.findByCategoryName(subcategoryRequest.getParent());
            subcategory.setCategory(category);
            subcategory.setSlug(subcategoryRequest.getSlug());
                      

            subCategoryRepository.save(subcategory);
            
            return new ResponseEntity<>(subcategory,  HttpStatus.OK);

    }

    @GetMapping("/categories")
    public ResponseEntity<List<ProductCategory>> getCategories() {

        List<ProductCategory> category = categoryRepository.findAll();  
        
        return new ResponseEntity<>(category,  HttpStatus.OK);
    }

    @GetMapping("/product/subcategories")
    public ResponseEntity<ArrayList<SubCategoryResponse>> getSubcategories(@RequestParam String category) {

        List<SubCategory> subcategories = categoryRepository.findSubCategoriesByCategoryName(category);

        ArrayList<SubCategoryResponse> subCategoryList = new ArrayList<>();

        subcategories.forEach(item -> subCategoryList.add(new SubCategoryResponse(item.getSubcategoryId(), item.getSubcategoryName())) );
        
        return new ResponseEntity<>(subCategoryList,  HttpStatus.OK);
    }

    @GetMapping(value="/product/products")
    //produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE} )
    public ResponseEntity<List<ProductDTO>> getProducts() {
        List<Product> products = productRepository.findAll();

        List<ProductDTO> response = new ArrayList<ProductDTO>();
        for (Product product : products) {
              ProductDTO dto = productService.getProduct(product.getName());
              response.add(dto);
        }
        

        return new ResponseEntity<>(response,  HttpStatus.OK);
    }
    
    @GetMapping("/product/{productName}")
    public ResponseEntity<ProductDTO> getParentProduct(@PathVariable String productName) {

            ProductDTO dto = productService.getProduct(productName);

            return new ResponseEntity<>(dto, HttpStatus.OK);                           
    }
    
    
    @PostMapping("/product")
    public ResponseEntity<ProductSku> addProduct(
    @RequestPart("product") ProductRequest request, @RequestPart("image") MultipartFile[] images, 
        @RequestPart("colorImage") MultipartFile colorImage) throws IOException {

        Product product = productRepository.findByName(request.getName());

        if (product != null)
            return null;

       
        ProductSku skuProject = productService.addProduct(request, images, colorImage);

        return new ResponseEntity<>(skuProject,  HttpStatus.OK);       
       
    } 
}

