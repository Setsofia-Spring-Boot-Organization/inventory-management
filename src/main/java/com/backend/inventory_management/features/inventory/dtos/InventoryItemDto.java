// InventoryItemDto.java
package com.backend.inventory_management.features.inventory.dtos;

import com.backend.inventory_management.features.inventory.StockStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryItemDto {
    private Long id;
    private String name;
    private String category;
    private Integer quantity;
    private BigDecimal price;
    private String supplier;
    private Integer minStockLevel;
    private Integer maxStockLevel;
    private StockStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
