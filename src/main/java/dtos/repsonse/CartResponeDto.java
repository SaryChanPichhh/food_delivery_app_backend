package dtos.repsonse;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public interface CartResponeDto {
    Integer getHeaderId();
    Integer getDetailId();
    Integer getResId();
    String getItemCode();
    String getMenuDescription();
    String getMenuImage();
    Integer getQty();
    Double getSalePrice();
    Double getTotal();
    Double getDiscountValue();
    String getResName();
    String getResImage();
    String getResDesc();
    Boolean getIsOpen();
    String getAddress();
    String getAvgEstimateTime();
}