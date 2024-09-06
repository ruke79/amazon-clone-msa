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

import com.project.backend.constants.StatusMessages;
import com.project.backend.dto.ReviewDTO;
import com.project.backend.security.request.ReviewRequest;
import com.project.backend.security.response.MessageResponse;
import com.project.backend.service.ReviewService;

@RestController
@RequestMapping("api/product/")
public class ReviewController {

    
    private final ReviewService reviewService;

    
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }


    @PutMapping("{productId}/review")
    ResponseEntity<?> addReview(@PathVariable("productId") String id, @RequestBody ReviewRequest request,
        @AuthenticationPrincipal UserDetails userDetails)  {
           
            if (null != userDetails) {
                String username = userDetails.getUsername();

                try {

                    List<ReviewDTO> result = reviewService.addReview(username, id, request);

                    return new ResponseEntity<>(result, HttpStatus.OK);
                } catch(RuntimeException e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(StatusMessages.REVIEW_REGISTRATION_FAILED));
                }

            }
            else {        
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(StatusMessages.USER_NOT_FOUND);
            }
            
        }
    
}
