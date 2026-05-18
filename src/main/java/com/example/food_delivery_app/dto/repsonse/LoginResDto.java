package com.example.food_delivery_app.dto.repsonse;

import com.example.food_delivery_app.dto.request.RegisterReqDto;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class LoginResDto extends RegisterReqDto {
    private int userId;
}
