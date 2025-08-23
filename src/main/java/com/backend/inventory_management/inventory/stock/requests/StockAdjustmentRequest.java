package com.backend.inventory_management.inventory.stock.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StockAdjustmentRequest {
    @NotNull
    private Long stockId;
    
    @NotNull
    private Integer newQuantity;
    
    @NotNull
    private String reason;
    
    @NotNull
    private String performedBy;
}