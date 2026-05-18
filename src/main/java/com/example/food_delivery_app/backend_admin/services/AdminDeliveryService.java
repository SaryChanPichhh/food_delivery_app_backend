package com.example.food_delivery_app.backend_admin.services;

import com.example.food_delivery_app.backend_admin.interfaces.IDeliveryService;
import com.example.food_delivery_app.models.DeliveryModel;
import com.example.food_delivery_app.repositories.DeliveryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Dictionary;
import java.util.List;

@Service
@AllArgsConstructor
public class AdminDeliveryService implements IDeliveryService {
    private final DeliveryRepository deliveryRepository;

    @Override
    public DeliveryModel AddData(DeliveryModel model) {
        return deliveryRepository.save(model);
    }

    @Override
    public DeliveryModel UpdateData(DeliveryModel model) {
        return deliveryRepository.save(model);
    }

    @Override
    public DeliveryModel Delete(DeliveryModel model) {
        deliveryRepository.delete(model);
        return model;
    }

    @Override
    public List<DeliveryModel> GetData() {
        return deliveryRepository.findAll();
    }

    @Override
    public List<DeliveryModel> FindData(Dictionary<String, Object> model) {
        return List.of();
    }
}
