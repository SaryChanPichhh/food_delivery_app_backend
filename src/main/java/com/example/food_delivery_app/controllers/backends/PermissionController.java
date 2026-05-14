package com.example.food_delivery_app.controllers.backends;

import com.example.food_delivery_app.Interfaces.IUserService;
import com.example.food_delivery_app.models.UserModel;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/backend/settings/permissions")
@AllArgsConstructor
public class PermissionController {

    private final IUserService userService;

    @GetMapping("")
    public Map<String, Object> index() {
        List<UserModel> users = userService.GetData();
        Map<String, Object> response = new HashMap<>();
        response.put("users", users);
        response.put("pageTitle", "Role & Permission Management");
        return response;
    }

    @PostMapping("/update-role/{userId}")
    public ResponseEntity<?> updateRole(@PathVariable int userId, @RequestParam String role) {
        UserModel user = userService.GetData().stream()
                .filter(u -> u.getId() == userId)
                .findFirst()
                .orElse(null);

        Map<String, String> response = new HashMap<>();
        if (user != null) {
            user.setRole(role);
            userService.UpdateData(user);
            response.put("success", "Role updated for user: " + user.getUserName());
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "User not found.");
            return ResponseEntity.status(404).body(response);
        }
    }
}
