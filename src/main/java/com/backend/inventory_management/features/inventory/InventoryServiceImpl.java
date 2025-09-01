package com.backend.inventory_management.features.inventory;

import com.backend.inventory_management.features.inventory.dtos.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    
    private final InventoryItemRepository inventoryItemRepository;
    
    @Override
    public Page<InventoryItemDto> getAllItems(InventoryFilterDto filter) {
        Sort sort = Sort.by(
            "desc".equalsIgnoreCase(filter.getSortDirection()) 
                ? Sort.Direction.DESC 
                : Sort.Direction.ASC,
            filter.getSortBy()
        );
        
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), sort);
        
        Page<InventoryItem> items = inventoryItemRepository.search(
            filter.getSearchTerm(),
            pageable
        );
        
        return items.map(this::convertToDto);
    }
    
    @Override
    public InventoryItemDto getItemById(Long id) {
        InventoryItem item = inventoryItemRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));
        return convertToDto(item);
    }
    
    @Override
    @Transactional
    public InventoryItemDto createItem(CreateInventoryItemDto createDto) {
        if (inventoryItemRepository.existsByNameIgnoreCase(createDto.getName())) {
            throw new RuntimeException("Item with this name already exists");
        }
        
        String currentUser = getCurrentUsername();
        
        InventoryItem item = InventoryItem.builder()
            .name(createDto.getName())
            .category(createDto.getCategory())
            .quantity(createDto.getQuantity())
            .price(createDto.getPrice())
            .supplier(createDto.getSupplier())
            .minStockLevel(createDto.getMinStockLevel())
            .maxStockLevel(createDto.getMaxStockLevel())
            .createdBy(currentUser)
            .updatedBy(currentUser)
            .build();
        
        InventoryItem savedItem = inventoryItemRepository.save(item);
        log.info("Created new inventory item: {} by user: {}", savedItem.getName(), currentUser);
        
        return convertToDto(savedItem);
    }
    
    @Override
    @Transactional
    public InventoryItemDto updateItem(Long id, UpdateInventoryItemDto updateDto) {
        InventoryItem existingItem = inventoryItemRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));
        
        String currentUser = getCurrentUsername();
        
        // Update only non-null fields
        if (updateDto.getName() != null) {
            existingItem.setName(updateDto.getName());
        }
        if (updateDto.getCategory() != null) {
            existingItem.setCategory(updateDto.getCategory());
        }
        if (updateDto.getQuantity() != null) {
            existingItem.setQuantity(updateDto.getQuantity());
        }
        if (updateDto.getPrice() != null) {
            existingItem.setPrice(updateDto.getPrice());
        }
        if (updateDto.getSupplier() != null) {
            existingItem.setSupplier(updateDto.getSupplier());
        }
        if (updateDto.getMinStockLevel() != null) {
            existingItem.setMinStockLevel(updateDto.getMinStockLevel());
        }
        if (updateDto.getMaxStockLevel() != null) {
            existingItem.setMaxStockLevel(updateDto.getMaxStockLevel());
        }
        
        existingItem.setUpdatedBy(currentUser);
        
        InventoryItem savedItem = inventoryItemRepository.save(existingItem);
        log.info("Updated inventory item: {} by user: {}", savedItem.getName(), currentUser);
        
        return convertToDto(savedItem);
    }
    
    @Override
    @Transactional
    public void deleteItem(Long id) {
        if (!inventoryItemRepository.existsById(id)) {
            throw new RuntimeException("Item not found with id: " + id);
        }
        
        inventoryItemRepository.deleteById(id);
        log.info("Deleted inventory item with id: {} by user: {}", id, getCurrentUsername());
    }
    
    @Override
    public InventoryMetricsDto getMetrics() {
        long totalItems = inventoryItemRepository.count();
        long lowStockCount = inventoryItemRepository.countByStatus(StockStatus.LOW_STOCK);
        long outOfStockCount = inventoryItemRepository.countByStatus(StockStatus.OUT_OF_STOCK);
        BigDecimal totalValue = inventoryItemRepository.getTotalInventoryValue();
        
        // Mock forecast accuracy - in real implementation, this would be calculated
        double forecastAccuracy = 87.5;
        
        return InventoryMetricsDto.builder()
            .totalItems(totalItems)
            .lowStockCount(lowStockCount)
            .outOfStockCount(outOfStockCount)
            .totalValue(totalValue != null ? totalValue : BigDecimal.ZERO)
            .forecastAccuracy(forecastAccuracy)
            .build();
    }
    
    @Override
    public List<InventoryItemDto> getLowStockItems() {
        List<InventoryItem> lowStockItems = inventoryItemRepository.findLowStockItems();
        return lowStockItems.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<InventoryItemDto> getOutOfStockItems() {
        List<InventoryItem> outOfStockItems = inventoryItemRepository.findOutOfStockItems();
        return outOfStockItems.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<String> getCategories() {
        return inventoryItemRepository.findDistinctCategories();
    }
    
    @Override
    public List<String> getSuppliers() {
        return inventoryItemRepository.findDistinctSuppliers();
    }
    
    @Override
    public List<InventoryItemDto> getReorderRecommendations() {
        List<InventoryItem> itemsNeedingReorder = inventoryItemRepository.findItemsNeedingReorder();
        return itemsNeedingReorder.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    private InventoryItemDto convertToDto(InventoryItem item) {
        return InventoryItemDto.builder()
            .id(item.getId())
            .name(item.getName())
            .category(item.getCategory())
            .quantity(item.getQuantity())
            .price(item.getPrice())
            .supplier(item.getSupplier())
            .minStockLevel(item.getMinStockLevel())
            .maxStockLevel(item.getMaxStockLevel())
            .status(item.getStatus())
            .createdAt(item.getCreatedAt())
            .updatedAt(item.getUpdatedAt())
            .createdBy(item.getCreatedBy())
            .updatedBy(item.getUpdatedBy())
            .build();
    }
    
    private String getCurrentUsername() {
        try {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        } catch (Exception e) {
            return "system";
        }
    }
}