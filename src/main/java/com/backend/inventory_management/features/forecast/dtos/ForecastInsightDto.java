package com.backend.inventory_management.features.forecast.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForecastInsightDto {
    private String label;
    private String value;
    private String icon;
    private String description;
}