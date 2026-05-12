package dtos.repsonse;

import lombok.Data;

@Data
public class ViewHistOrderDto {
    private String invoiceType;
    private String invoiceNo;
    private String invoiceDate;
    private String invoiceStatus;
    private double invoiceTotalPrice;
    private String paymentMethod;
    private int invoiceTotalQuantity;
    private double exchangeRate;
}
