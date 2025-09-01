package com.backend.inventory_management.features.inventory;

import com.backend.inventory_management.features.inventory.dtos.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface InventoryService {
    Page<InventoryItemDto> getAllItems(InventoryFilterDto filter);
    InventoryItemDto getItemById(Long id);
    InventoryItemDto createItem(CreateInventoryItemDto createDto);
    InventoryItemDto updateItem(Long id, UpdateInventoryItemDto updateDto);
    void deleteItem(Long id);
    InventoryMetricsDto getMetrics();
    List<InventoryItemDto> getLowStockItems();
    List<InventoryItemDto> getOutOfStockItems();
    List<String> getCategories();
    List<String> getSuppliers();
    List<InventoryItemDto> getReorderRecommendations();
}