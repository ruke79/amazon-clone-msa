package com.project.backend.controller;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.backend.dto.ReviewDTO;
import com.project.backend.security.request.ReviewRequest;
import com.project.backend.service.impl.ReviewService;

@RestController
@RequestMapping("api/product/")
public class ReviewController {

    @Autowired
    ReviewService reviewService;

    
    @PutMapping("{productId}/review")
    ResponseEntity<List<ReviewDTO>> addReview(@PathVariable("productId") String id, @RequestBody ReviewRequest request,
        @AuthenticationPrincipal UserDetails userDetails)  {

            String username = userDetails.getUsername();

            List<ReviewDTO> result = reviewService.addReview(username, id, request);

            return new ResponseEntity<>(result, HttpStatus.OK);
            
        }
    
}
