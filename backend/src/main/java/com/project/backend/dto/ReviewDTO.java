package com.project.backend.dto;

import java.util.List;

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
    private int rating;
    private String review;
    List<String> images;

}
