package com.backend.inventory_management.inventory.warehouse;

import java.util.List;
import java.util.Optional;

public interface WarehouseService {
    
    WarehouseEntity createWarehouse(WarehouseEntity warehouse);
    
    WarehouseEntity updateWarehouse(Long id, WarehouseEntity warehouse);
    
    Optional<WarehouseEntity> getWarehouseById(Long id);
    
    Optional<WarehouseEntity> getWarehouseByCode(String warehouseCode);
    
    List<WarehouseEntity> getAllWarehouses();
    
    List<WarehouseEntity> getWarehousesByStore(Long storeId);
    
    List<WarehouseEntity> getWarehousesByType(WarehouseEntity.WarehouseType type);
    
    List<WarehouseEntity> getWarehousesByStatus(WarehouseEntity.WarehouseStatus status);
    
    List<WarehouseEntity> getActiveWarehousesByStore(Long storeId);
    
    void deleteWarehouse(Long id);
    
    boolean existsByWarehouseCode(String warehouseCode);
    
    WarehouseEntity updateWarehouseStatus(Long id, WarehouseEntity.WarehouseStatus status);
}