package com.group_one.food_delivery_app.Interfaces;

import com.group_one.food_delivery_app.models.UserModel;

public interface IUserService extends IBasedService<UserModel> {
    UserModel findByUserName(String userName);
    UserModel findById(int id);
    UserModel findByEmail(String email);
}
