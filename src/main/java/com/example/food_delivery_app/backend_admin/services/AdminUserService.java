package com.example.food_delivery_app.backend_admin.services;

import com.example.food_delivery_app.backend_admin.interfaces.IUserService;
import com.example.food_delivery_app.models.UserModel;
import com.example.food_delivery_app.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Dictionary;
import java.util.List;

@Service
@AllArgsConstructor
public class AdminUserService implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserModel AddData(UserModel model) {
        // Encrypt password before saving
        model.setPassword(passwordEncoder.encode(model.getPassword()));
        if (model.getCreatedAt() == null) {
            model.setCreatedAt(java.time.LocalDate.now());
        }
        return userRepository.save(model);
    }

    @Override
    public UserModel UpdateData(UserModel model) {
        UserModel existingUser = userRepository.findById(model.getId()).orElse(null);
        if (existingUser != null) {
            // Only update password if a new one is provided
            if (model.getPassword() != null && !model.getPassword().isEmpty() && !model.getPassword().equals(existingUser.getPassword())) {
                model.setPassword(passwordEncoder.encode(model.getPassword()));
            } else {
                model.setPassword(existingUser.getPassword());
            }

            if (model.getCreatedAt() == null) {
                model.setCreatedAt(existingUser.getCreatedAt());
            }
        }
        return userRepository.save(model);
    }

    @Override
    public UserModel Delete(UserModel model) {
        userRepository.delete(model);
        return model;
    }

    @Override
    public List<UserModel> GetData() {
        return userRepository.findAll();
    }

    @Override
    public List<UserModel> FindData(Dictionary<String, Object> model) {
        return List.of();
    }

    @Override
    public UserModel findByUserName(String userName) {
        // This would require a custom query in UserRepository if needed
        return null; 
    }

    @Override
    public UserModel findById(int id) {
        return userRepository.findById(id).orElse(new UserModel());
    }

    @Override
    public UserModel findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
