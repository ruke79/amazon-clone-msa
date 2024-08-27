package com.project.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.backend.dto.ReviewDTO;
import com.project.backend.dto.ReviewStyleDTO;
import com.project.backend.dto.ReviewerDTO;
import com.project.backend.model.Product;
import com.project.backend.model.Review;
import com.project.backend.model.ReviewStyle;
import com.project.backend.model.User;
import com.project.backend.repository.ProductRepository;
import com.project.backend.repository.ReviewRepository;
import com.project.backend.repository.UserRepository;
import com.project.backend.security.request.ReviewRequest;

@Service
public class ReviewService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ReviewRepository reviewRepository;

    public List<ReviewDTO> addReview(String username, String productId, ReviewRequest request) {

        Optional<User> user = userRepository.findByUserName(username);

        if (!user.isPresent()) {
            return null;
        }

        Long id = Long.parseLong(productId);

        Optional<Product> existed = productRepository.findById(id);

        if (existed.isPresent()) {

            Product p = existed.get();

            if (!p.getReviews().isEmpty()) {

                Review review = p.getReviews().stream().filter(r -> r.getReviewedBy().equals(user.get())).findFirst()
                        .get();

                if (null != review) {

                    review.setReview(request.getReview());
                    review.setFit(request.getFit());
                    review.setSize(request.getSize());
                    review.setRating(request.getRating());
                    review.setImages(request.getImages());
                    review.setStyle(
                            ReviewStyle.builder()
                                    .color(request.getStyle().getColor())
                                    .image(request.getStyle().getImage()).build());
                    review.setProduct(p);

                    reviewRepository.save(review);

                    float totalPoints = p.getReviews().stream().map(Review::getRating).reduce(0F, Float::sum);
                    float rating = totalPoints / p.getReviews().size();

                    productRepository.updateRating(id, rating);

                    List<ReviewDTO> result = new ArrayList<>();

                    for (Review r : p.getReviews()) {

                        ReviewDTO dto = new ReviewDTO();
                        dto.setFit(r.getFit());
                        dto.setImages(r.getImages());
                        dto.setRating(r.getRating());
                        dto.setReview(r.getReview());
                        dto.setStyle(ReviewStyleDTO.builder()
                                .color(r.getStyle().getColor())
                                .image(r.getStyle().getImage()).build());
                        dto.setSize(r.getSize());

                        result.add(dto);
                    }

                    return result;
                }
            } else {

                Review r = new Review();
                r.setFit(request.getFit());
                r.setImages(request.getImages());
                r.setRating(request.getRating());
                r.setReview(request.getReview());
                r.setStyle(ReviewStyle.builder()
                        .color(request.getStyle().getColor())
                        .image(request.getStyle().getImage()).build());
                r.setSize(request.getSize());

                r.setProduct(p);
                r.setReviewedBy(user.get());
                
                p.getReviews().add(r);
                

                p.setNum_reviews(p.getReviews().size());

                float totalPoints = p.getReviews().stream().map(Review::getRating).reduce(0F, Float::sum);
                p.setRating(totalPoints / p.getReviews().size());

                
                reviewRepository.save(r);
                
                productRepository.save(p);
                

                List<ReviewDTO> result = new ArrayList<>();

                for (Review i : p.getReviews()) {

                    ReviewDTO dto = new ReviewDTO();
                    dto.setFit(i.getFit());
                    dto.setImages(i.getImages());
                    dto.setRating(i.getRating());
                    dto.setReview(i.getReview());
                    dto.setStyle(ReviewStyleDTO.builder()
                            .color(i.getStyle().getColor())
                            .image(i.getStyle().getImage()).build());
                    dto.setSize(i.getSize());
                    dto.setReviewedBy(new ReviewerDTO(username, user.get().getImage()));

                    result.add(dto);
                }

                return result;

            }
        }
        return null;
    }

}
