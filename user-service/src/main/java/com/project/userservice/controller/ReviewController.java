package com.project.userservice.controller;

import java.io.IOException;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.common.constants.StatusMessages;
import com.project.common.response.MessageResponse;
import com.project.userservice.dto.ReviewDto;

import com.project.userservice.security.request.ReviewRequest;
import com.project.userservice.security.response.ReviewsResponse;
import com.project.userservice.service.ReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/review")
@RequiredArgsConstructor
public class ReviewController {

    
    private final ReviewService reviewService;

    
    @GetMapping("/reviews/{productId}")
    ResponseEntity<?> getReviews(@PathVariable("productId") Long productId) {

        
            try {

                ReviewsResponse result = reviewService.getReviews(productId);

                return new ResponseEntity<>(result, HttpStatus.OK);
            } catch(RuntimeException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new MessageResponse(StatusMessages.REVIEW_REGISTRATION_FAILED));
            }

    }



    @PostMapping("/{productId}/add")
    ResponseEntity<?> addReview(@PathVariable("productId") Long id, @RequestPart("review") ReviewRequest request, 
    @RequestPart(value="image", required=false) List<MultipartFile> images,    @AuthenticationPrincipal UserDetails userDetails) throws IOException  {
           
            if (null != userDetails) {
                String username = userDetails.getUsername();

                try {

                    List<ReviewDto> result = reviewService.addReview(username, id, request, images);

                    return new ResponseEntity<>(result, HttpStatus.OK);
                } catch(RuntimeException e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(e.getMessage()));
                }

            }
            else {        
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(StatusMessages.USER_NOT_FOUND);
            }
            
        }

        
        @DeleteMapping("/delete/{productId}") 
        ResponseEntity<?> deleteReview( @PathVariable("productId") Long productId,
        @AuthenticationPrincipal UserDetails userDetails
        )  {
            
           
            if (null != userDetails) {
                String username = userDetails.getUsername();

                try {

                    if (reviewService.deleteReview(username, productId))
                        return new ResponseEntity<>(new MessageResponse(StatusMessages.REVIEW_DELETE_SUCCESS), HttpStatus.OK);
                    else {
                        return new ResponseEntity<>(new MessageResponse(StatusMessages.REVIEW_DELETE_FAILED), HttpStatus.BAD_REQUEST);
                    }
                } catch(RuntimeException e) {
                    e.printStackTrace();
                    
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(StatusMessages.REVIEW_DELETE_FAILED));

                    
                }

            }
            else {        
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(StatusMessages.USER_NOT_FOUND);
            }
            
        }

    
}
