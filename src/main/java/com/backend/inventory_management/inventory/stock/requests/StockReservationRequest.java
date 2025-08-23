package com.backend.inventory_management.inventory.stock.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class StockReservationRequest {
    @NotNull
    private Long stockId;
    
    @NotNull
    @Positive
    private Integer quantity;
    
    @NotNull
    private String reason;
    
    @NotNull
    private String performedBy;
    
    private LocalDate reservationExpiry;
}