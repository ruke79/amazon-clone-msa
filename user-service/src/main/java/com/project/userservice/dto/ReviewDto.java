package com.project.userservice.dto;

import java.util.List;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ReviewDto {

    private String size;
    private ReviewStyleDto style;
    private String fit;
    private float rating;
    private String review;
    
    private List<String> images;
    
    private ReviewerDto reviewedBy;

}
