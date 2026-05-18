package com.example.food_delivery_app.utils.enums;

public enum DiscountValue {
    THIRTY(30),
    FIFTY(50),
    SEVENTY(70),
    ZERO(0);
    private final int _value;
    DiscountValue(int value){
        this._value = value;
    }
    public int getValue() {
        return _value;
    }
}
