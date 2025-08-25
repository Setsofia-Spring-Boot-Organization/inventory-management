package com.backend.inventory_management.inventory.warehouse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WarehouseController {
    
    private final WarehouseService warehouseService;
    
    @PostMapping
    public ResponseEntity<WarehouseEntity> createWarehouse(@Valid @RequestBody WarehouseEntity warehouse) {
        try {
            WarehouseEntity createdWarehouse = warehouseService.createWarehouse(warehouse);
            return new ResponseEntity<>(createdWarehouse, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping
    public ResponseEntity<List<WarehouseEntity>> getAllWarehouses() {
        List<WarehouseEntity> warehouses = warehouseService.getAllWarehouses();
        return new ResponseEntity<>(warehouses, HttpStatus.OK);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<WarehouseEntity> getWarehouseById(@PathVariable Long id) {
        return warehouseService.getWarehouseById(id)
            .map(warehouse -> new ResponseEntity<>(warehouse, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @GetMapping("/code/{warehouseCode}")
    public ResponseEntity<WarehouseEntity> getWarehouseByCode(@PathVariable String warehouseCode) {
        return warehouseService.getWarehouseByCode(warehouseCode)
            .map(warehouse -> new ResponseEntity<>(warehouse, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<WarehouseEntity>> getWarehousesByStore(@PathVariable Long storeId) {
        List<WarehouseEntity> warehouses = warehouseService.getWarehousesByStore(storeId);
        return new ResponseEntity<>(warehouses, HttpStatus.OK);
    }
    
    @GetMapping("/store/{storeId}/active")
    public ResponseEntity<List<WarehouseEntity>> getActiveWarehousesByStore(@PathVariable Long storeId) {
        List<WarehouseEntity> warehouses = warehouseService.getActiveWarehousesByStore(storeId);
        return new ResponseEntity<>(warehouses, HttpStatus.OK);
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<List<WarehouseEntity>> getWarehousesByType(@PathVariable WarehouseEntity.WarehouseType type) {
        List<WarehouseEntity> warehouses = warehouseService.getWarehousesByType(type);
        return new ResponseEntity<>(warehouses, HttpStatus.OK);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<WarehouseEntity>> getWarehousesByStatus(@PathVariable WarehouseEntity.WarehouseStatus status) {
        List<WarehouseEntity> warehouses = warehouseService.getWarehousesByStatus(status);
        return new ResponseEntity<>(warehouses, HttpStatus.OK);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<WarehouseEntity> updateWarehouse(@PathVariable Long id, @Valid @RequestBody WarehouseEntity warehouse) {
        try {
            WarehouseEntity updatedWarehouse = warehouseService.updateWarehouse(id, warehouse);
            return new ResponseEntity<>(updatedWarehouse, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<WarehouseEntity> updateWarehouseStatus(@PathVariable Long id, @RequestParam WarehouseEntity.WarehouseStatus status) {
        try {
            WarehouseEntity updatedWarehouse = warehouseService.updateWarehouseStatus(id, status);
            return new ResponseEntity<>(updatedWarehouse, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWarehouse(@PathVariable Long id) {
        try {
            warehouseService.deleteWarehouse(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @GetMapping("/exists/{warehouseCode}")
    public ResponseEntity<Boolean> existsByWarehouseCode(@PathVariable String warehouseCode) {
        boolean exists = warehouseService.existsByWarehouseCode(warehouseCode);
        return new ResponseEntity<>(exists, HttpStatus.OK);
    }
}