package com.backend.inventory_management.inventory.stock.requests;

import lombok.Data;

@Data
public class StockAvailabilityResponse {
    private Long productId;
    private Long warehouseId;
    private Integer totalQuantity;
    private Integer availableQuantity;
    private Integer reservedQuantity;
    private boolean isAvailable;
    private String productName;
    private String warehouseName;
}