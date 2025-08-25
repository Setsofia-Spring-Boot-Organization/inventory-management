package com.backend.inventory_management.inventory.warehouse;

import com.backend.inventory_management.common.dto.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PostMapping
    public ResponseEntity<BaseResponse<WarehouseEntity>> createWarehouse(@Valid @RequestBody WarehouseEntity warehouse) {
        try {
            WarehouseEntity createdWarehouse = warehouseService.createWarehouse(warehouse);
            return new ResponseEntity<>(BaseResponse.success("Warehouse created successfully", createdWarehouse), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(BaseResponse.error(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<WarehouseEntity>>> getAllWarehouses() {
        List<WarehouseEntity> warehouses = warehouseService.getAllWarehouses();
        return ResponseEntity.ok(BaseResponse.success("Fetched all warehouses", warehouses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<WarehouseEntity>> getWarehouseById(@PathVariable Long id) {
        return warehouseService.getWarehouseById(id)
                .map(warehouse -> ResponseEntity.ok(BaseResponse.success("Warehouse found", warehouse)))
                .orElse(new ResponseEntity<>(BaseResponse.error("Warehouse not found with id: " + id), HttpStatus.NOT_FOUND));
    }

    @GetMapping("/code/{warehouseCode}")
    public ResponseEntity<BaseResponse<WarehouseEntity>> getWarehouseByCode(@PathVariable String warehouseCode) {
        return warehouseService.getWarehouseByCode(warehouseCode)
                .map(warehouse -> ResponseEntity.ok(BaseResponse.success("Warehouse found", warehouse)))
                .orElse(new ResponseEntity<>(BaseResponse.error("Warehouse not found with code: " + warehouseCode), HttpStatus.NOT_FOUND));
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<BaseResponse<List<WarehouseEntity>>> getWarehousesByStore(@PathVariable Long storeId) {
        List<WarehouseEntity> warehouses = warehouseService.getWarehousesByStore(storeId);
        return ResponseEntity.ok(BaseResponse.success("Fetched warehouses for store " + storeId, warehouses));
    }

    @GetMapping("/store/{storeId}/active")
    public ResponseEntity<BaseResponse<List<WarehouseEntity>>> getActiveWarehousesByStore(@PathVariable Long storeId) {
        List<WarehouseEntity> warehouses = warehouseService.getActiveWarehousesByStore(storeId);
        return ResponseEntity.ok(BaseResponse.success("Fetched active warehouses for store " + storeId, warehouses));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<BaseResponse<List<WarehouseEntity>>> getWarehousesByType(@PathVariable WarehouseEntity.WarehouseType type) {
        List<WarehouseEntity> warehouses = warehouseService.getWarehousesByType(type);
        return ResponseEntity.ok(BaseResponse.success("Fetched warehouses by type: " + type, warehouses));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<BaseResponse<List<WarehouseEntity>>> getWarehousesByStatus(@PathVariable WarehouseEntity.WarehouseStatus status) {
        List<WarehouseEntity> warehouses = warehouseService.getWarehousesByStatus(status);
        return ResponseEntity.ok(BaseResponse.success("Fetched warehouses by status: " + status, warehouses));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<WarehouseEntity>> updateWarehouse(@PathVariable Long id, @Valid @RequestBody WarehouseEntity warehouse) {
        try {
            WarehouseEntity updatedWarehouse = warehouseService.updateWarehouse(id, warehouse);
            return ResponseEntity.ok(BaseResponse.success("Warehouse updated successfully", updatedWarehouse));
        } catch (RuntimeException e) {
            return new ResponseEntity<>(BaseResponse.error(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<BaseResponse<WarehouseEntity>> updateWarehouseStatus(@PathVariable Long id, @RequestParam WarehouseEntity.WarehouseStatus status) {
        try {
            WarehouseEntity updatedWarehouse = warehouseService.updateWarehouseStatus(id, status);
            return ResponseEntity.ok(BaseResponse.success("Warehouse status updated successfully", updatedWarehouse));
        } catch (RuntimeException e) {
            return new ResponseEntity<>(BaseResponse.error(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteWarehouse(@PathVariable Long id) {
        try {
            warehouseService.deleteWarehouse(id);
            return ResponseEntity.ok(BaseResponse.success("Warehouse deleted successfully", null));
        } catch (RuntimeException e) {
            return new ResponseEntity<>(BaseResponse.error(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/exists/{warehouseCode}")
    public ResponseEntity<BaseResponse<Boolean>> existsByWarehouseCode(@PathVariable String warehouseCode) {
        boolean exists = warehouseService.existsByWarehouseCode(warehouseCode);
        return ResponseEntity.ok(BaseResponse.success("Warehouse existence checked", exists));
    }
}
