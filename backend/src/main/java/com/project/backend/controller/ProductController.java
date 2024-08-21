package com.project.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.backend.dto.ProductDTO;
import com.project.backend.model.Product;
import com.project.backend.repository.ProductRepository;
import com.project.backend.service.ProductService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/product")
public class ProductController {


    @Autowired
    ProductService productService;

    @Autowired
    ProductRepository productRepository;

    @GetMapping("/{slug}")
    public ResponseEntity<ProductDTO> getProductInfo(@PathVariable(required = true) String slug 
    ) {
        ProductDTO dto = productService.getProductBySlug(slug);        
        
        return new ResponseEntity<>(dto, HttpStatus.OK);

    }

    @GetMapping("/cart/{product_id}")
    public ResponseEntity<ProductDTO> getProductInfoWithParams(@PathVariable(required = true) String product_id, 
                    int style, int size )     

     {
        
        ProductDTO dto = productService.getProductByName(product_id);

        

        return new ResponseEntity<>(dto, HttpStatus.OK);        

    }
}
