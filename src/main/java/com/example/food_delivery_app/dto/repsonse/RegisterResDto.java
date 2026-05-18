package com.example.food_delivery_app.dto.repsonse;

import com.example.food_delivery_app.dto.request.RegisterReqDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RegisterResDto extends RegisterReqDto {
     private int userId;
}
