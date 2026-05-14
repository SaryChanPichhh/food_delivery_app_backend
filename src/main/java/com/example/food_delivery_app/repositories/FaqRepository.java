package com.example.food_delivery_app.repositories;

import com.example.food_delivery_app.models.FaqModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FaqRepository extends JpaRepository<FaqModel, Long> {
    List<FaqModel> findAllByActiveTrueOrderBySortOrderAsc();
}
