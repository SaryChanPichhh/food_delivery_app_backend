package com.example.food_delivery_app.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
public class RegisterReqDto {
    @NotNull
    @NotEmpty
    @Range(min = 1, max = 100)
    private String firstName;
    @NotNull
    @NotEmpty
    @Range(min = 1, max = 100)
    private String lastName;
    @NotNull
    @NotEmpty
    @Range(min = 1, max = 100)
    private String userName;
    @NotNull
    @NotEmpty
    @Range(min = 1, max = 100)
    private String password;
    @NotNull
    @NotEmpty
    @Range(min = 1, max = 100)
    @Email
    private String email;
    @NotNull
    @NotEmpty
    @Range(min = 1, max = 10) // 0978907057
    private String phone;
}
