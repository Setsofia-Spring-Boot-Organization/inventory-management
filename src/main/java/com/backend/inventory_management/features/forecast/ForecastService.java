package com.backend.inventory_management.features.forecast;

import com.backend.inventory_management.features.forecast.dtos.ForecastDto;
import com.backend.inventory_management.features.forecast.dtos.ProductForecastDto;

public interface ForecastService {
    ForecastDto getOverallForecast(int days);
    ProductForecastDto getProductForecast(Long productId, int days);
}