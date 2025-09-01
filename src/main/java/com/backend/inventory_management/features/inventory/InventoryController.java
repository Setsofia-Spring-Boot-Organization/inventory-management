// InventoryController.java
package com.backend.inventory_management.features.inventory;

import com.backend.inventory_management.core.Response;
import com.backend.inventory_management.features.inventory.dtos.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InventoryController {
    
    private final InventoryService inventoryService;
    
    @GetMapping
    public ResponseEntity<Response<Page<InventoryItemDto>>> getAllItems(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String supplier,
            @RequestParam(required = false) StockStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        
        try {
            InventoryFilterDto filter = InventoryFilterDto.builder()
                .searchTerm(searchTerm)
                .category(category)
                .supplier(supplier)
                .status(status)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();
            
            Page<InventoryItemDto> items = inventoryService.getAllItems(filter);
            return ResponseEntity.ok(Response.success(items));
        } catch (Exception e) {
            log.error("Failed to retrieve inventory items", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error("Failed to retrieve inventory items"));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Response<InventoryItemDto>> getItemById(@PathVariable Long id) {
        try {
            InventoryItemDto item = inventoryService.getItemById(id);
            return ResponseEntity.ok(Response.success(item));
        } catch (RuntimeException e) {
            log.error("Item not found with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Response.error("Item not found"));
        } catch (Exception e) {
            log.error("Failed to retrieve item with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error("Failed to retrieve item"));
        }
    }
    
    @PostMapping
    public ResponseEntity<Response<InventoryItemDto>> createItem(@Valid @RequestBody CreateInventoryItemDto createDto) {
        try {
            InventoryItemDto createdItem = inventoryService.createItem(createDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success(createdItem, "Item created successfully"));
        } catch (RuntimeException e) {
            log.error("Failed to create item", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Response.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to create item", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error("Failed to create item"));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Response<InventoryItemDto>> updateItem(
            @PathVariable Long id,
            @Valid @RequestBody UpdateInventoryItemDto updateDto) {
        try {
            InventoryItemDto updatedItem = inventoryService.updateItem(id, updateDto);
            return ResponseEntity.ok(Response.success(updatedItem, "Item updated successfully"));
        } catch (RuntimeException e) {
            log.error("Failed to update item with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Response.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to update item with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error("Failed to update item"));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Void>> deleteItem(@PathVariable Long id) {
        try {
            inventoryService.deleteItem(id);
            return ResponseEntity.ok(Response.success(null, "Item deleted successfully"));
        } catch (RuntimeException e) {
            log.error("Failed to delete item with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Response.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to delete item with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error("Failed to delete item"));
        }
    }
    
    @GetMapping("/metrics")
    public ResponseEntity<Response<InventoryMetricsDto>> getMetrics() {
        try {
            InventoryMetricsDto metrics = inventoryService.getMetrics();
            return ResponseEntity.ok(Response.success(metrics));
        } catch (Exception e) {
            log.error("Failed to retrieve inventory metrics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error("Failed to retrieve metrics"));
        }
    }
    
    @GetMapping("/low-stock")
    public ResponseEntity<Response<List<InventoryItemDto>>> getLowStockItems() {
        try {
            List<InventoryItemDto> lowStockItems = inventoryService.getLowStockItems();
            return ResponseEntity.ok(Response.success(lowStockItems));
        } catch (Exception e) {
            log.error("Failed to retrieve low stock items", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error("Failed to retrieve low stock items"));
        }
    }
    
    @GetMapping("/out-of-stock")
    public ResponseEntity<Response<List<InventoryItemDto>>> getOutOfStockItems() {
        try {
            List<InventoryItemDto> outOfStockItems = inventoryService.getOutOfStockItems();
            return ResponseEntity.ok(Response.success(outOfStockItems));
        } catch (Exception e) {
            log.error("Failed to retrieve out of stock items", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error("Failed to retrieve out of stock items"));
        }
    }
    
    @GetMapping("/categories")
    public ResponseEntity<Response<List<String>>> getCategories() {
        try {
            List<String> categories = inventoryService.getCategories();
            return ResponseEntity.ok(Response.success(categories));
        } catch (Exception e) {
            log.error("Failed to retrieve categories", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error("Failed to retrieve categories"));
        }
    }
    
    @GetMapping("/suppliers")
    public ResponseEntity<Response<List<String>>> getSuppliers() {
        try {
            List<String> suppliers = inventoryService.getSuppliers();
            return ResponseEntity.ok(Response.success(suppliers));
        } catch (Exception e) {
            log.error("Failed to retrieve suppliers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error("Failed to retrieve suppliers"));
        }
    }
    
    @GetMapping("/reorder-recommendations")
    public ResponseEntity<Response<List<InventoryItemDto>>> getReorderRecommendations() {
        try {
            List<InventoryItemDto> recommendations = inventoryService.getReorderRecommendations();
            return ResponseEntity.ok(Response.success(recommendations));
        } catch (Exception e) {
            log.error("Failed to retrieve reorder recommendations", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error("Failed to retrieve reorder recommendations"));
        }
    }
}