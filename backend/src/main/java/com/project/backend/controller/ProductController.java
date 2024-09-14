package com.project.backend.controller;

import java.io.ObjectInputFilter.Status;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.backend.constants.StatusMessages;
import com.project.backend.dto.ProductDTO;
import com.project.backend.dto.ProductInfoDTO;
import com.project.backend.model.Product;
import com.project.backend.repository.ProductRepository;
import com.project.backend.security.response.MessageResponse;
import com.project.backend.service.ProductService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/product")
public class ProductController {

    final ProductService productService;

    private final ProductRepository productRepository;

    public ProductController(ProductService productService, ProductRepository productRepository) {
        this.productService = productService;
        this.productRepository = productRepository;
    }

    @GetMapping("/{slug}")
    public ResponseEntity<?> getProductInfo(@PathVariable(required = true) String slug
            ) {
        try {
            List<ProductDTO> dto = productService.getProductsBySlug(slug);

            return new ResponseEntity<>(dto.get(0), HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(StatusMessages.PRODUCT_IS_EMPTY));
        }

    }

    @GetMapping("/cart/{product_id}")
    public ResponseEntity<?> getProductInfoWithParams(@PathVariable(required = true) String product_id,
            @RequestParam("style") int style, @RequestParam("size") int size)

    {
        try {
            ProductInfoDTO dto = productService.getCartProductInfo(Long.parseLong(product_id), style, size);

            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(StatusMessages.PRODUCT_IS_EMPTY));
        }

    }

}
