package com.project.user-service.security.request;

import java.util.List;

import com.project.user-service.dto.ReviewStyleDTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReviewRequest {

    
    private String productId;
    
    private String size;
    private ReviewStyleDTO style;
    private String fit;
    private float rating;
    private String review;
    private List<String> images;

    

}
