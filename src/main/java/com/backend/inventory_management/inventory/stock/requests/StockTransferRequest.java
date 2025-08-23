package com.backend.inventory_management.inventory.stock.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class StockTransferRequest {
    @NotNull
    private Long fromWarehouseId;
    
    @NotNull
    private Long toWarehouseId;
    
    @NotNull
    private Long productId;
    
    @NotNull
    @Positive
    private Integer quantity;
    
    @NotNull
    private String performedBy;
    
    private String notes;
    private String referenceNumber;
}