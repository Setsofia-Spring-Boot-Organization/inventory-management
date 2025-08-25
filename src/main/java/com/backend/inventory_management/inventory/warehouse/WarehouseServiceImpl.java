package com.backend.inventory_management.inventory.warehouse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class WarehouseServiceImpl implements WarehouseService {
    
    private final WarehouseRepository warehouseRepository;
    
    @Override
    public WarehouseEntity createWarehouse(WarehouseEntity warehouse) {
        if (warehouseRepository.existsByWarehouseCode(warehouse.getWarehouseCode())) {
            throw new RuntimeException("Warehouse with code " + warehouse.getWarehouseCode() + " already exists");
        }
        return warehouseRepository.save(warehouse);
    }
    
    @Override
    public WarehouseEntity updateWarehouse(Long id, WarehouseEntity warehouse) {
        WarehouseEntity existingWarehouse = warehouseRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Warehouse not found with id: " + id));
        
        // Check if warehouse code is being changed and if new code already exists
        if (!existingWarehouse.getWarehouseCode().equals(warehouse.getWarehouseCode()) 
            && warehouseRepository.existsByWarehouseCode(warehouse.getWarehouseCode())) {
            throw new RuntimeException("Warehouse with code " + warehouse.getWarehouseCode() + " already exists");
        }
        
        existingWarehouse.setWarehouseCode(warehouse.getWarehouseCode());
        existingWarehouse.setWarehouseName(warehouse.getWarehouseName());
        existingWarehouse.setDescription(warehouse.getDescription());
        existingWarehouse.setStore(warehouse.getStore());
        existingWarehouse.setType(warehouse.getType());
        existingWarehouse.setLocation(warehouse.getLocation());
        existingWarehouse.setCapacity(warehouse.getCapacity());
        existingWarehouse.setTemperatureRange(warehouse.getTemperatureRange());
        existingWarehouse.setStatus(warehouse.getStatus());
        
        return warehouseRepository.save(existingWarehouse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<WarehouseEntity> getWarehouseById(Long id) {
        return warehouseRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<WarehouseEntity> getWarehouseByCode(String warehouseCode) {
        return warehouseRepository.findByWarehouseCode(warehouseCode);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<WarehouseEntity> getAllWarehouses() {
        return warehouseRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<WarehouseEntity> getWarehousesByStore(Long storeId) {
        return warehouseRepository.findByStoreId(storeId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<WarehouseEntity> getWarehousesByType(WarehouseEntity.WarehouseType type) {
        return warehouseRepository.findByType(type);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<WarehouseEntity> getWarehousesByStatus(WarehouseEntity.WarehouseStatus status) {
        return warehouseRepository.findByStatus(status);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<WarehouseEntity> getActiveWarehousesByStore(Long storeId) {
        return warehouseRepository.findActiveWarehousesByStore(storeId);
    }
    
    @Override
    public void deleteWarehouse(Long id) {
        if (!warehouseRepository.existsById(id)) {
            throw new RuntimeException("Warehouse not found with id: " + id);
        }
        warehouseRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByWarehouseCode(String warehouseCode) {
        return warehouseRepository.existsByWarehouseCode(warehouseCode);
    }
    
    @Override
    public WarehouseEntity updateWarehouseStatus(Long id, WarehouseEntity.WarehouseStatus status) {
        WarehouseEntity warehouse = warehouseRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Warehouse not found with id: " + id));
        
        warehouse.setStatus(status);
        return warehouseRepository.save(warehouse);
    }
}