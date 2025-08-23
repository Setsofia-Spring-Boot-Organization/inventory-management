package com.backend.inventory_management.inventory.stock.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class StockSaleRequest {
    @NotNull
    private Long productId;
    
    @NotNull
    private Long warehouseId;
    
    @NotNull
    @Positive
    private Integer quantity;
    
    @NotNull
    private String transactionRef;
    
    @NotNull
    private String performedBy;
    
    private String notes;
}