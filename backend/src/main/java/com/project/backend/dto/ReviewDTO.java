package com.project.backend.dto;

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
public class ReviewDTO {

    private String size;
    private ReviewStyleDTO style;
    private String fit;
    private float rating;
    private String review;

    
    List<String> images;
    List<String> likes;

    private ReviewerDTO reviewedBy;

}
