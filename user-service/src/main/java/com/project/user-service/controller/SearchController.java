package com.project.user-service.controller;

import java.util.List;

import org.hibernate.validator.cfg.defs.pl.REGONDef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.user-service.constants.StatusMessages;
import com.project.user-service.dto.SearchResultDTO;
import com.project.user-service.model.Product;
import com.project.user-service.model.ProductCategory;
import com.project.user-service.model.Review;
import com.project.user-service.repository.CategoryRepository;
import com.project.user-service.repository.ProductRepository;
import com.project.user-service.repository.ProductSkuRepository;
import com.project.user-service.security.request.SearchParamsRequest;
import com.project.user-service.security.response.MessageResponse;
import com.project.user-service.service.ProductService;

@RestController
@RequestMapping("api/")
public class SearchController {

    private final ProductService productService;

    @Autowired
    public SearchController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(SearchParamsRequest params)  {

        SearchResultDTO dto = productService.searchProducts(params);       
        return new ResponseEntity<>(dto, HttpStatus.OK);      

    }    

}
