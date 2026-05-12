package com.group_one.food_delivery_app.utils.helper;

import java.time.temporal.TemporalAdjusters;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.group_one.food_delivery_app.dtos.repsonse.CartResponeDto;
import com.group_one.food_delivery_app.models.SaleDetailModel;

public class Convertor {
    private static final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .findAndRegisterModules();

    public static <T> String FromFromListToJson(List<T> list) {
        try {
            return mapper.writeValueAsString(list);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static <T> String FromFromObjectToJson(T object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
