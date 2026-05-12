package com.group_one.food_delivery_app.repositories;

import com.group_one.food_delivery_app.models.FeedbackModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<FeedbackModel, Long> {
}
