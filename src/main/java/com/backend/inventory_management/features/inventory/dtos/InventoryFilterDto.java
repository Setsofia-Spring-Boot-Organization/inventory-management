package com.backend.inventory_management.features.inventory.dtos;

import com.backend.inventory_management.features.inventory.StockStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryFilterDto {
    private String searchTerm;
    private String category;
    private String supplier;
    private StockStatus status;
    private Integer page = 0;
    private Integer size = 20;
    private String sortBy = "name";
    private String sortDirection = "asc";
}