package com.backend.inventory_management.features.forecast.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductForecastDto {
    private Long productId;
    private String productName;
    private ForecastDto forecast;
    private List<ForecastInsightDto> insights;
}