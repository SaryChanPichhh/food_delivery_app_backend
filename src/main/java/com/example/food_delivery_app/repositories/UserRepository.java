package com.example.food_delivery_app.repositories;

import com.example.food_delivery_app.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserModel, Integer> {
    UserModel findByEmail(String email);

    UserModel findByUserName(String userName);
}
