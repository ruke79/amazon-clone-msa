package com.project.catalog_service.controller;

import java.io.IOException;
import java.io.ObjectInputFilter.Status;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.catalog_service.constants.StatusMessages;
import com.project.catalog_service.dto.ProductDto;
import com.project.catalog_service.dto.ProductInfoDto;
import com.project.catalog_service.dto.request.ProductInfosLoadRequest;
import com.project.catalog_service.dto.request.ProductRequest;
import com.project.catalog_service.model.Product;
import com.project.catalog_service.model.ProductSku;
import com.project.catalog_service.repository.ProductRepository;
import com.project.catalog_service.dto.response.MessageResponse;
import com.project.catalog_service.service.ProductService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;

    private final ProductRepository productRepository;

    public ProductController(ProductService productService, ProductRepository productRepository) {
        this.productService = productService;
        this.productRepository = productRepository;
    }

    @GetMapping(value = "/product/products")
    ResponseEntity<?> getProducts() {

        try {
            List<Product> products = productRepository.findAll();

            List<ProductDto> response = new ArrayList<ProductDto>();

            for (Product product : products) {
                List<ProductDto> sameNameeProducts = productService.getProductsByName(product.getName());

                for (ProductDto p : sameNameeProducts) {
                    response.add(p);
                }
            }

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(e.getMessage()));
        }

    }

    @GetMapping("/product/{productId}")
    ResponseEntity<?> getParentProduct(@PathVariable String productId) {

        try {

            ProductDto dto = productService.getProductById(Long.parseLong(productId));

            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(e.getMessage()));
        }
    }
   

    @GetMapping("/{slug}")
    public ResponseEntity<?> getProductInfo(@PathVariable(required = true) String slug
            ) {
        try {
            List<ProductDto> dto = productService.getProductsBySlug(slug);

            return new ResponseEntity<>(dto.get(0), HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(StatusMessages.PRODUCT_IS_EMPTY));
        }

    }

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
    

    // Create Products
    @PostMapping("/products")
    ResponseEntity<?> addProducts(@RequestBody ProductInfosLoadRequest products
    ) {

        log.info("/Products");

        try {
            List<ProductSku> response = productService.load(products);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(e.getMessage()));

        }
    }


}
