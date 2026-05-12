package com.group_one.food_delivery_app.Interfaces;

import java.util.List;
import org.springframework.stereotype.Service;

import com.group_one.food_delivery_app.dtos.repsonse.RestaurantResponseDto;
import com.group_one.food_delivery_app.models.CategoryModel;
@Service
public interface ICategoryService extends IBasedService<CategoryModel> {
        List<RestaurantResponseDto> GetCategoryDetail(int cateId ,int userId);
    CategoryModel findById(int id);

    
}
