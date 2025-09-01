package com.backend.inventory_management.features.inventory.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateInventoryItemDto {
    
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;
    
    @Size(max = 50, message = "Category cannot exceed 50 characters")
    private String category;
    
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Price format is invalid")
    private BigDecimal price;
    
    @Size(max = 100, message = "Supplier name cannot exceed 100 characters")
    private String supplier;
    
    @Min(value = 0, message = "Minimum stock level cannot be negative")
    private Integer minStockLevel;
    
    @Min(value = 1, message = "Maximum stock level must be at least 1")
    private Integer maxStockLevel;
}