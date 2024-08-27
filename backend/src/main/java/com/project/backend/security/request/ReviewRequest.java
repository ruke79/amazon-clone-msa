package com.project.backend.security.request;

import java.util.List;

import com.project.backend.dto.ReviewStyleDTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReviewRequest {

    private String size;
    private ReviewStyleDTO style;
    private String fit;
    private int rating;
    private String review;
    List<String> images;

}
