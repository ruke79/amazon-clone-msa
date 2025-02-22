package com.project.catalog_service.controller;

import java.io.IOException;
import java.io.ObjectInputFilter.Status;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.catalog_service.constants.StatusMessages;
import com.project.catalog_service.dto.ColorAttributeDto;
import com.project.catalog_service.dto.ProductDto;
import com.project.catalog_service.dto.ProductInfoDto;
import com.project.catalog_service.dto.request.ProductInfoLoadRequest;
import com.project.catalog_service.dto.request.ProductInfosLoadRequest;
import com.project.catalog_service.dto.request.ProductRequest;
import com.project.catalog_service.model.Product;
import com.project.catalog_service.model.ProductSku;
import com.project.catalog_service.repository.ProductRepository;
import com.project.catalog_service.dto.response.GenericResponse;
import com.project.catalog_service.dto.response.MessageResponse;
import com.project.catalog_service.service.ProductService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;

    private final ProductRepository productRepository;

    
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

    @GetMapping("/{productId}")
    ResponseEntity<?> getParentProduct(@PathVariable String productId) {

        try {

            ProductDto dto = productService.getProductById(Long.parseLong(productId));

            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(e.getMessage()));
        }
    }
   

    @GetMapping("/product/{slug}")
    public ResponseEntity<?> getProductInfo(@PathVariable(value="slug", required = true) String slug
            ) {
        try {
            List<ProductDto> dto = productService.getProductsBySlug(slug);

            return new ResponseEntity<>(dto.get(0), HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(StatusMessages.PRODUCT_IS_EMPTY));
        }
    }

    @GetMapping("/cart/{product_id}")
    public ResponseEntity<?> getCartProductInfo(@PathVariable(required = true) String product_id,
            @RequestParam("style") int style, @RequestParam("size") int size)

    {
        try {
            ProductInfoDto dto = productService.getCartProductInfo(Long.parseLong(product_id), style, size);

            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(StatusMessages.PRODUCT_IS_EMPTY));
        }

    }

    @GetMapping("/color/{colorId}")
    public ResponseEntity<?> getColorInfo(@PathVariable("colorId") String colorId) {

        log.info("Feign :" + colorId);

        try {
            ColorAttributeDto dto = productService.getColorAttributeInfo(Long.parseLong(colorId));

            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(StatusMessages.COLOR_ATTRIBUTE_NOT_FOUND));
        }

    }

     @PostMapping("/product")
    ResponseEntity<?> addProduct(
            @RequestPart("product") ProductRequest request,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            @RequestParam(value = "colorImage", required = false) MultipartFile colorImage) {

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
    ResponseEntity<?> addProducts(@RequestPart("products") List<ProductInfoLoadRequest> products, @RequestPart("image")
    List<MultipartFile> images, @RequestPart("colorImage") List<MultipartFile> colorImages
    ) {

        
        try {
            List<ProductSku> response = productService.load(products, images, colorImages);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(e.getMessage()));

        }
    }


    @PutMapping("/product/rating")
    public ResponseEntity<ProductDto> updateRating(@RequestParam Long id, @RequestParam float rating) {

        productRepository.updateRating(id, rating);

        ProductDto dto = productService.getProductById(id);
        

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
    // public void updateRating(@RequestParam Long id, @RequestParam float rating) {

    //     productRepository.updateRating(id, rating);
        
    // }
}
