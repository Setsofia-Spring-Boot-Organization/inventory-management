package com.backend.inventory_management.inventory.stock.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class StockReceiptRequest {
    @NotNull
    private Long productId;
    
    @NotNull
    private Long warehouseId;
    
    @NotNull
    @Positive
    private Integer quantity;
    
    @NotNull
    @Positive
    private BigDecimal unitCost;
    
    private String batchNumber;
    private LocalDate expiryDate;
    private LocalDate manufactureDate;
    private String supplierBatch;
    private String notes;
    
    @NotNull
    private String performedBy;
    private String referenceNumber;
}