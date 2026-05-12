package com.group_one.food_delivery_app.services;

import com.group_one.food_delivery_app.models.ExchangeRateModel;
import com.group_one.food_delivery_app.repositories.ExchangeRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ExchangeRateService {

    @Autowired
    private ExchangeRateRepository exchangeRateRepository;

    public List<ExchangeRateModel> getAllRates() {
        return exchangeRateRepository.findAllByOrderByUpdatedAtDesc();
    }

    public Optional<ExchangeRateModel> getRateById(Long id) {
        return exchangeRateRepository.findById(id);
    }

    @Transactional
    public ExchangeRateModel saveRate(ExchangeRateModel model) {
        if (model.isDefaultRate()) {
            resetDefaultRate();
        }
        return exchangeRateRepository.save(model);
    }

    @Transactional
    public void deleteRate(Long id) {
        exchangeRateRepository.deleteById(id);
    }

    @Transactional
    public void setAsDefault(Long id) {
        resetDefaultRate();
        Optional<ExchangeRateModel> rateOpt = exchangeRateRepository.findById(id);
        if (rateOpt.isPresent()) {
            ExchangeRateModel rate = rateOpt.get();
            rate.setDefaultRate(true);
            exchangeRateRepository.save(rate);
        }
    }

    private void resetDefaultRate() {
        exchangeRateRepository.resetAllDefaultRates();
    }

    public Optional<ExchangeRateModel> getDefaultRate() {
        return exchangeRateRepository.findByDefaultRateTrue();
    }
}
