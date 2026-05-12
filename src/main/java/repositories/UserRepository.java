package com.group_one.food_delivery_app.repositories;

import com.group_one.food_delivery_app.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserModel, Integer> {
    UserModel findByEmail(String email);
}
