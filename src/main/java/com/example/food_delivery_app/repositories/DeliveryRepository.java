package com.example.food_delivery_app.repositories;

import com.example.food_delivery_app.models.DeliveryModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<DeliveryModel, Long> {
}
