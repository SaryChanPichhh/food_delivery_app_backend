package com.example.food_delivery_app.backend_user.interfaces;

import com.example.food_delivery_app.dto.repsonse.RestaurantResponseDto;
import com.example.food_delivery_app.models.CategoryModel;
import org.springframework.stereotype.Service;

import java.util.List;
public interface ICategoryService extends IBasedService<CategoryModel> {
    List<RestaurantResponseDto> GetCategoryDetail(int cateId , int userId);
    CategoryModel findById(int id);

    
}
