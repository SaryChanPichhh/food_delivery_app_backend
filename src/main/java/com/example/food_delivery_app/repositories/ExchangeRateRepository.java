package com.example.food_delivery_app.repositories;

import com.example.food_delivery_app.models.ExchangeRateModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRateModel, Long> {
    Optional<ExchangeRateModel> findByCurrencyCode(String currencyCode);

    Optional<ExchangeRateModel> findByDefaultRateTrue();

    java.util.List<ExchangeRateModel> findAllByOrderByUpdatedAtDesc();

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE ExchangeRateModel e SET e.defaultRate = false")
    void resetAllDefaultRates();
}
