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
public class CreateInventoryItemDto {
    
    @NotBlank(message = "Item name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;
    
    @NotBlank(message = "Category is required")
    @Size(max = 50, message = "Category cannot exceed 50 characters")
    private String category;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Price format is invalid")
    private BigDecimal price;
    
    @NotBlank(message = "Supplier is required")
    @Size(max = 100, message = "Supplier name cannot exceed 100 characters")
    private String supplier;
    
    @Min(value = 0, message = "Minimum stock level cannot be negative")
    private Integer minStockLevel = 10;
    
    @Min(value = 1, message = "Maximum stock level must be at least 1")
    private Integer maxStockLevel = 100;
}