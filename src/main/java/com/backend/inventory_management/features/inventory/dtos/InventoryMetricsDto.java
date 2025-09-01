package com.backend.inventory_management.features.inventory.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryMetricsDto {
    private Long totalItems;
    private Long lowStockCount;
    private Long outOfStockCount;
    private BigDecimal totalValue;
    private Double forecastAccuracy;
}