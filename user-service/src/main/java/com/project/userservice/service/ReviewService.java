package com.project.userservice.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.project.common.util.FileUtil;
import com.project.userservice.client.CatalogServiceClient;
import com.project.userservice.dto.ReviewDto;
import com.project.userservice.dto.ReviewStyleDto;
import com.project.userservice.dto.ReviewerDto;

import com.project.userservice.model.Review;
import com.project.userservice.model.ReviewStyle;
import com.project.userservice.model.User;
import com.project.userservice.repository.ReviewRepository;
import com.project.userservice.repository.UserRepository;
import com.project.userservice.security.request.ReviewRequest;
import com.project.userservice.security.response.ReviewsResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final UserRepository userRepository;

    private final ReviewRepository reviewRepository;

    private final ImageService imageService;

    private final CatalogServiceClient catalogServiceClient;

    @Transactional
    public List<ReviewDto> deleteReview(String username, Long id) {

        User user = userRepository.findByUsername(username).orElse(null);

        if (null == user) {

            return null;
        }

        // Product p = productRepository.findById(id)
        // .orElseThrow(() -> new RuntimeException("Product not found! " + productId));

        List<Review> reviews = reviewRepository.findByProductId(id);

        if (!reviews.isEmpty()) {

            Review review = reviews.stream().filter(r -> r.getReviewedBy().equals(user)).findFirst()
                    .get();

            if (null != review) {

                reviewRepository.deleteById(review.getReviewId());

                reviews.remove(review);

                if (!reviews.isEmpty()) {
                    float totalPoints = reviews.stream().map(Review::getRating).reduce(0F, Float::sum);
                    float rating = totalPoints / reviews.size();

                    catalogServiceClient.updateRating(id, rating);
                } else
                    catalogServiceClient.updateRating(id, 0.0F);
            }

            List<ReviewDto> result = new ArrayList<>();

            for (Review r : reviews) {

                ReviewDto dto = ReviewDto.builder()
                        .fit(r.getFit())
                        .images(r.getImages())
                        .rating(r.getRating())
                        .review(r.getReview())
                        .style(ReviewStyleDto.builder()
                                .color(r.getStyle().getColor())
                                .image(r.getStyle().getImage()).build())
                        .size(r.getSize())
                        .reviewedBy(ReviewerDto.builder()
                                .name(username)
                                .image(user.getImage())
                                .build())
                        .build();

                result.add(dto);
            }

            return result;
        }

        return null;
    }

    @Transactional(readOnly = true)
    public ReviewsResponse getReviews(Long id) {

        List<Review> reviews = reviewRepository.findByProductId(id);

        List<ReviewDto> dtos = new ArrayList<>();

        for (Review i : reviews) {

            ReviewDto dto = ReviewDto.builder()
                    .fit(i.getFit())
                    .images(i.getImages())
                    .rating(i.getRating())
                    .review(i.getReview())
                    .style(ReviewStyleDto.builder()
                            .color(i.getStyle().getColor())
                            .image(i.getStyle().getImage()).build())
                    .size(i.getSize())
                    .reviewedBy(new ReviewerDto(
                            i.getReviewedBy().getUsername(), i.getReviewedBy().getImage()))
                    .build();

            dtos.add(dto);
        }

        ReviewsResponse result = new ReviewsResponse();
        result.setNum_reviews(dtos.size());
        result.setReviews(dtos);

        return result;
    }

    @Transactional
    public List<ReviewDto> addReview(String username, Long id, ReviewRequest request, List<MultipartFile> images)
            throws IOException {

        User user = userRepository.findByUsername(username).orElse(null);

        if (null == user) {
            return null;
        }

        List<Review> reviews = reviewRepository.findByProductId(id);

        if (!reviews.isEmpty()) {

            Review review = reviews.stream().filter(r -> r.getReviewedBy().equals(user)).findFirst()
                    .get();

            if (null != review) {

                review.setProductId(id);
                review.setReview(request.getReview());
                review.setFit(request.getFit());
                review.setSize(request.getSize());
                review.setRating(request.getRating());

                if (null != images && !images.isEmpty()) {

                    List<String> imageUrls = new ArrayList<String>();

                    for (MultipartFile image : images) {
                        String filename = FileUtil.getRandomFilename();
                        String filepath = imageService.upload(image, filename);
                        imageUrls.add(filepath);
                    }

                    review.setImages(imageUrls);
                }
                review.setStyle(
                        ReviewStyle.builder()
                                .color(request.getStyle().getColor())
                                .image(request.getStyle().getImage()).build());

                review.setReviewedBy(user);

                user.getReviews().add(review);

                reviewRepository.save(review);

                float totalPoints = reviews.stream().map(Review::getRating).reduce(0F, Float::sum);
                float rating = totalPoints / reviews.size();

                // productRepository.updateRating(id, rating);
                catalogServiceClient.updateRating(id, rating);

                List<ReviewDto> result = new ArrayList<>();

                for (Review r : reviews) {

                    ReviewDto dto = ReviewDto.builder()
                            .fit(r.getFit())
                            .images(r.getImages())
                            .rating(r.getRating())
                            .review(r.getReview())
                            .style(ReviewStyleDto.builder()
                                    .color(r.getStyle().getColor())
                                    .image(r.getStyle().getImage()).build())
                            .size(r.getSize())
                            .reviewedBy(ReviewerDto.builder()
                                    .name(username)
                                    .image(user.getImage())
                                    .build())
                            .build();

                    result.add(dto);
                }

                return result;
            }
        } else {

            // Assuming Review also has a builder (e.g., @Builder or @SuperBuilder)
            Review.ReviewBuilder<?,?> rBuilder = Review.builder()
                    .fit(request.getFit())
                    .rating(request.getRating())
                    .review(request.getReview())
                    .style(ReviewStyle.builder()
                            .color(request.getStyle().getColor())
                            .image(request.getStyle().getImage())
                            .build())
                    .size(request.getSize())
                    .reviewedBy(user)
                    .productId(id);

            // Handle image uploads conditionally
            if (null != images && !images.isEmpty()) {
                List<String> imageUrls = new ArrayList<>();
                for (MultipartFile image : images) {
                    String filename = FileUtil.getRandomFilename();
                    String filepath = imageService.upload(image, filename);
                    imageUrls.add(filepath);
                }
                rBuilder.images(imageUrls);
            }

            Review r = rBuilder.build();

            user.getReviews().add(r);

            reviewRepository.save(r);

            float totalPoints = reviews.stream().map(Review::getRating).reduce(0F, Float::sum);

            catalogServiceClient.updateRating(id, totalPoints / reviews.size());

            List<ReviewDto> result = new ArrayList<>();

            for (Review i : reviews) {

                ReviewDto dto = ReviewDto.builder()
                        .fit(i.getFit())
                        .images(i.getImages())
                        .rating(i.getRating())
                        .review(i.getReview())
                        .style(ReviewStyleDto.builder()
                                .color(i.getStyle().getColor())
                                .image(i.getStyle().getImage()).build())
                        .size(i.getSize())
                        .reviewedBy(ReviewerDto.builder()
                                .name(username)
                                .image(user.getImage())
                                .build())
                        .build();

                result.add(dto);
            }

            return result;

        }

        return null;
    }

}
