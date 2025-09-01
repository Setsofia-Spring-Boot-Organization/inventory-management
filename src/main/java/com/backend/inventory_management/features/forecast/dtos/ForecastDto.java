// ForecastDto.java
package com.backend.inventory_management.features.forecast.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForecastDto {
    private List<String> labels;
    private List<Double> values;
    private String period;
    private LocalDate startDate;
    private LocalDate endDate;
}