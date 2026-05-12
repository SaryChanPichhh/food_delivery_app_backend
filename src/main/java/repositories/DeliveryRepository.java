package com.group_one.food_delivery_app.repositories;

import com.group_one.food_delivery_app.models.DeliveryModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<DeliveryModel, Long> {
}
