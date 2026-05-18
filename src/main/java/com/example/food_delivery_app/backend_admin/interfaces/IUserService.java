package com.example.food_delivery_app.backend_admin.interfaces;

import com.example.food_delivery_app.models.UserModel;
import org.springframework.stereotype.Service;


public interface IUserService extends IBasedService<UserModel> {
    UserModel findByUserName(String userName);
    UserModel findById(int id);
    UserModel findByEmail(String email);
}
