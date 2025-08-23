package com.backend.inventory_management.inventory.stock.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BatchStatusUpdateRequest {
    @NotNull
    private String batchNumber;
    
    @NotNull
    private String reason;
    
    @NotNull
    private String performedBy;
    
    private String notes;
}