package com.example.food_delivery_app.backend_user.interfaces;

import com.example.food_delivery_app.models.ReviewModel;
import org.springframework.stereotype.Service;

import java.util.List;
public interface IReviewService {
    ReviewModel submitReview(int userId, int resId, int rating, String comment);
    List<ReviewModel> getReviewsByRestaurant(int resId);
    Double getAverageRating(int resId);
}
