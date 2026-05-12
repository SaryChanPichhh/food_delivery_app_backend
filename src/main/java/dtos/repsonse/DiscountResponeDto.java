 package dtos.repsonse;

import java.time.LocalDateTime;

public interface DiscountResponeDto extends RestaurantResponseDto {

    String getCouponDesc();
    String getCouponCode();
    Double getDiscountValue();
    Integer getMaxUsage();
    Double getMinAmount();
    LocalDateTime getStartDate();
    LocalDateTime getEndDate();
    String getStatus();
}