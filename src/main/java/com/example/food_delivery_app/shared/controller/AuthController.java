package com.example.food_delivery_app.shared.controller;

import com.example.food_delivery_app.config.JwtService;
import com.example.food_delivery_app.dto.repsonse.ApiResponse;
import com.example.food_delivery_app.dto.repsonse.LoginResDto;
import com.example.food_delivery_app.dto.request.LoginReqDto;
import com.example.food_delivery_app.dto.request.RegisterReqDto;
import com.example.food_delivery_app.models.UserModel;
import com.example.food_delivery_app.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.function.EntityResponse;
import java.util.Map;
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, String>>> login(
            @RequestBody LoginReqDto request
    ) {
        var user = userRepository.findByUserName(request.getUserName());
        if (user.getUserName().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.<Map<String, String>>builder()
                            .success(false)
                            .message("Invalid username or password")
                            .build()
            );
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.<Map<String, String>>builder()
                            .success(false)
                            .message("Invalid username or password")
                            .build()
            );
        }
        var loginRes =new LoginResDto();
        loginRes.setUserId(user.getId());
        loginRes.setFirstName(user.getFirstName());
        loginRes.setLastName(user.getLastName());
        loginRes.setUserName(user.getUserName());
        loginRes.setEmail(user.getEmail());
        loginRes.setPhone(user.getPhone());

        String token = jwtService.generateToken(loginRes);
        var data = Map.of("token", token);
        return ResponseEntity.ok(
                ApiResponse.<Map<String, String>>builder()
                        .success(true)
                        .message("Login Success")
                        .data(data)
                        .build()
        );
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterReqDto>> register(
            @RequestBody RegisterReqDto request
    ) {
        var user = new UserModel();
        user.setUserName(request.getUserName());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );
        userRepository.save(user);
        var response = ApiResponse.<RegisterReqDto>builder().success(true).message("Register Success").data(request);
        return ResponseEntity.ok(response.build());
    }
}
