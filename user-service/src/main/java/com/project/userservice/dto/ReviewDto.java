package com.project.userservice.dto;

import java.util.List;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {

    private String size;
    private ReviewStyleDto style;
    private String fit;
    private float rating;
    private String review;

    
    List<String> images;
    List<String> likes;

    private ReviewerDto reviewedBy;

}
