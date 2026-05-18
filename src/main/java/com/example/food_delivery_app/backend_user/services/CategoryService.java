package com.example.food_delivery_app.backend_user.services;

import com.example.food_delivery_app.backend_user.interfaces.ICategoryService;
import com.example.food_delivery_app.dto.repsonse.RestaurantResponseDto;
import com.example.food_delivery_app.models.CategoryModel;
import com.example.food_delivery_app.repositories.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Dictionary;
import java.util.List;

@Service
@AllArgsConstructor
public class CategoryService implements ICategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public CategoryModel AddData(CategoryModel model) {
        return categoryRepository.save(model);
    }

    @Override
    public CategoryModel UpdateData(CategoryModel model) {
        return categoryRepository.save(model);
    }

    @Override
    public CategoryModel Delete(CategoryModel model) {
        categoryRepository.delete(model);
        return model;
    }

    @Override
    public List<CategoryModel> GetData() {
        return categoryRepository.findAll();
    }

    @Override
    public List<CategoryModel> FindData(Dictionary<String, Object> model) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'FindData'");
    }

    @Override
    public List<RestaurantResponseDto> GetCategoryDetail(int cateId, int userId) {
        return categoryRepository.GetCategoryDetail(cateId, userId);
    }

    @Override
    public CategoryModel findById(int id) {
        return categoryRepository.findById(id).orElse(null);
    }

}
