package com.example.food_delivery_app.Interfaces;

import com.example.food_delivery_app.models.ReviewModel;
import java.util.List;

public interface IReviewService {
    ReviewModel submitReview(int userId, int resId, int rating, String comment);
    List<ReviewModel> getReviewsByRestaurant(int resId);
    Double getAverageRating(int resId);
}
