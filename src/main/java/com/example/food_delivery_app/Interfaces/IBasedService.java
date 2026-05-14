package com.example.food_delivery_app.Interfaces;

import java.util.Dictionary;
import java.util.List;

public interface IBasedService<T> {
    T AddData(T model);
    T UpdateData(T model);
    T Delete(T model);
    List<T> GetData();
    List<T> FindData(Dictionary<String,Object> model);
}
