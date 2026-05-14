package com.example.food_delivery_app.services;

import com.example.food_delivery_app.Interfaces.IReviewService;
import com.example.food_delivery_app.models.RestaurantModel;
import com.example.food_delivery_app.models.ReviewModel;
import com.example.food_delivery_app.models.UserModel;
import com.example.food_delivery_app.repositories.RestaurantRepository;
import com.example.food_delivery_app.repositories.ReviewRepository;
import com.example.food_delivery_app.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.List;

@Service
@AllArgsConstructor
public class ReviewService implements IReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    @Override
    @Transactional
    public ReviewModel submitReview(int userId, int resId, int rating, String comment) {
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        RestaurantModel restaurant = restaurantRepository.findById(resId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        ReviewModel review = new ReviewModel();
        review.setUser(user);
        review.setRestaurant(restaurant);
        review.setRating(rating);
        review.setComment(comment);

        ReviewModel savedReview = reviewRepository.save(review);

        // Update restaurant average rating
        updateRestaurantRating(resId);

        return savedReview;
    }

    @Override
    public List<ReviewModel> getReviewsByRestaurant(int resId) {
        return reviewRepository.findByRestaurantResIdOrderByCreatedAtDesc(resId);
    }

    @Override
    public Double getAverageRating(int resId) {
        List<ReviewModel> reviews = reviewRepository.findByRestaurantResIdOrderByCreatedAtDesc(resId);
        if (reviews.isEmpty()) return 0.0;
        return reviews.stream().mapToInt(ReviewModel::getRating).average().orElse(0.0);
    }

    private void updateRestaurantRating(int resId) {
        RestaurantModel restaurant = restaurantRepository.findById(resId).orElse(null);
        if (restaurant != null) {
            Double avg = getAverageRating(resId);
            restaurant.setRating(avg);
            restaurantRepository.save(restaurant);
        }
    }
}
