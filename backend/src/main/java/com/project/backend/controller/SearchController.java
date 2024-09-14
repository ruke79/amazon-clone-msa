package com.project.backend.controller;

import java.util.List;

import org.hibernate.validator.cfg.defs.pl.REGONDef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.backend.constants.StatusMessages;
import com.project.backend.dto.SearchResultDTO;
import com.project.backend.model.Product;
import com.project.backend.model.ProductCategory;
import com.project.backend.model.Review;
import com.project.backend.repository.CategoryRepository;
import com.project.backend.repository.ProductRepository;
import com.project.backend.repository.ProductSkuRepository;
import com.project.backend.security.request.SearchParamsRequest;
import com.project.backend.security.response.MessageResponse;
import com.project.backend.service.ProductService;

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
