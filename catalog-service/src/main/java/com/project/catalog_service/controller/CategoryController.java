package com.project.catalog_service.controller;
 
import org.hibernate.mapping.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.project.common.constants.StatusMessages;
import com.project.common.dto.CategoryDto;
import com.project.common.dto.ProductColorDto;
import com.project.common.dto.ProductDto;

import com.project.common.dto.SubCategoryDto;
import com.project.common.response.MessageResponse;
import com.project.catalog_service.model.Subcategory;
import com.project.catalog_service.model.Category;
import com.project.catalog_service.repository.CategoryRepository;

import com.project.catalog_service.repository.SubcategoryRepository;
import com.project.catalog_service.dto.request.CategoryRequest;


import com.project.catalog_service.dto.request.SubCategoryRequest;
import com.project.catalog_service.dto.response.CategoryResponse;

import com.project.catalog_service.dto.response.SubCategoryResponse;
import com.project.catalog_service.service.CategoryService;



//import jakarta.mail.Multipart;
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
@RequestMapping("/api/category")
// @PreAuthorize("hasRole('ROLE_ADMIN')")
@RequiredArgsConstructor
public class CategoryController {

    
    private final CategoryRepository categoryRepository;

    private final SubcategoryRepository subCategoryRepository;

    private final CategoryService categoryService;

     @PostMapping("/category")
    ResponseEntity<?> addCategory(@RequestBody CategoryRequest request) {

        try {

            boolean exist = categoryService.findCategory(request.getName());

            if (!exist) {

                // Category category = new Category();
                // category.setCategoryName(categoryRequest.getName());
                // category.setSlug(categoryRequest.getSlug());

                // categoryRepository.save(category);

                CategoryDto dto = categoryService.createCategory(request.getName(), request.getSlug());

                CategoryResponse response = new CategoryResponse(
                        dto.getId(),
                        dto.getName()
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
    ResponseEntity<?> addSubCategory(@RequestBody SubCategoryRequest request) {

        try {
            boolean exist = categoryService.findSubCategory(request.getSubcategoryName(), request.getParent());

            if (!exist) {

                SubCategoryDto subcategory = categoryService.createSubCategory(request.getSubcategoryName(),
                        request.getParent(), request.getSlug());

                SubCategoryResponse response = new SubCategoryResponse(
                        subcategory.getId(),
                        subcategory.getName(), subcategory.getParent().getName());

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

        List<Category> category = categoryRepository.findAll();

        List<CategoryResponse> responses = new ArrayList<>();
        category.forEach(item -> {
            responses.add(new CategoryResponse(Long.toString(item.getCategoryId()), item.getCategoryName()));
        });

        return new ResponseEntity<>(responses, HttpStatus.OK);

    }

    @GetMapping("/product/allsubcategories")
    ResponseEntity<?> loadAllSubcategories() {

        try {

            List<Subcategory> subcategories = subCategoryRepository.findAll();

            if (!subcategories.isEmpty()) {

                ArrayList<SubCategoryResponse> subCategoryList = new ArrayList<>();

                subcategories.forEach(item -> {
                    Category category = categoryRepository.findById(item.getCategory().getCategoryId())
                            .orElseThrow(() -> new RuntimeException("Category not found"));

                    subCategoryList
                            .add(new SubCategoryResponse(Long.toString(item.getSubcategoryId()),
                                    item.getSubcategoryName(), category.getCategoryName()));
                });

                return new ResponseEntity<>(subCategoryList, HttpStatus.OK);
            } else
                return new ResponseEntity<>(StatusMessages.SUBCATEGORY_NOT_FOUND, HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/product/subcategories")
    ResponseEntity<?> getSubcategories(@RequestParam("category") String category) {

        try {

            List<Subcategory> subcategories = categoryRepository
                    .findSubCategoriesByCategoryId(Long.parseLong(category));

            if (!subcategories.isEmpty()) {

                ArrayList<SubCategoryResponse> subCategoryList = new ArrayList<>();

                subcategories.forEach(item -> subCategoryList
                        .add(new SubCategoryResponse(Long.toString(item.getSubcategoryId()),
                                item.getSubcategoryName(), item.getCategory().getCategoryName())));

                return new ResponseEntity<>(subCategoryList, HttpStatus.OK);
            } else
                return new ResponseEntity<>(StatusMessages.SUBCATEGORY_NOT_FOUND, HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(e.getMessage()));
        }
    }

    
}
