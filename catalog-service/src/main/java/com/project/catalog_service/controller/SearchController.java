package com.project.catalog_service.controller;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.project.catalog_service.dto.SearchResultDto;
import com.project.catalog_service.dto.request.SearchParamsRequest;
import com.project.catalog_service.model.Product;
import com.project.catalog_service.service.ProductService;
import com.project.catalog_service.service.SearchService;
import com.project.common.response.GenericResponse;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@RestController
@RequestMapping("api/")
public class SearchController {

    private final SearchService searchService;

    

    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(SearchParamsRequest params)  {

        
            SearchResultDto dto = searchService.searchProducts(params);       
        
            return new ResponseEntity<>(dto, HttpStatus.OK);                     
        
    }    

    //Test 
    @GetMapping("/search/category")
    public ResponseEntity<?> searchProductsByCategory(@RequestParam Long  category, int pageNo, int pageSize)  {

        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);

        List<Product> products = searchService.findAllByCategory(category, pageRequest);       
        
        return new ResponseEntity<>(products, HttpStatus.OK);              
        
    }   

}
