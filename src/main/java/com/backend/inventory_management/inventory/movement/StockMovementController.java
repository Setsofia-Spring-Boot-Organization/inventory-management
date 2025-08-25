package com.backend.inventory_management.inventory.movement;

import com.backend.inventory_management.common.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/stock-movements")
@RequiredArgsConstructor
public class StockMovementController {

    private final StockMovementService stockMovementService;

    @PostMapping
    public ResponseEntity<BaseResponse<StockMovementEntity>> createStockMovement(@RequestBody StockMovementEntity stockMovement) {
        return ResponseEntity.status(201).body(stockMovementService.createStockMovement(stockMovement));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<StockMovementEntity>> getStockMovementById(@PathVariable Long id) {
        return ResponseEntity.ok(stockMovementService.getStockMovementById(id));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<StockMovementEntity>>> getAllStockMovements() {
        return ResponseEntity.ok(stockMovementService.getAllStockMovements());
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<BaseResponse<List<StockMovementEntity>>> getByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(stockMovementService.getByProductId(productId));
    }

    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<BaseResponse<List<StockMovementEntity>>> getByWarehouseId(@PathVariable Long warehouseId) {
        return ResponseEntity.ok(stockMovementService.getByWarehouseId(warehouseId));
    }

    @GetMapping("/type/{movementType}")
    public ResponseEntity<BaseResponse<List<StockMovementEntity>>> getByMovementType(@PathVariable StockMovementEntity.MovementType movementType) {
        return ResponseEntity.ok(stockMovementService.getByMovementType(movementType));
    }

    @GetMapping("/reference/{referenceNumber}")
    public ResponseEntity<BaseResponse<List<StockMovementEntity>>> getByReferenceNumber(@PathVariable String referenceNumber) {
        return ResponseEntity.ok(stockMovementService.getByReferenceNumber(referenceNumber));
    }

    @GetMapping("/date-range")
    public ResponseEntity<BaseResponse<List<StockMovementEntity>>> getByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(stockMovementService.getByDateRange(startDate, endDate));
    }

    @GetMapping("/product-history/{productId}")
    public ResponseEntity<BaseResponse<Page<StockMovementEntity>>> getProductMovementHistory(
            @PathVariable Long productId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable) {
        return ResponseEntity.ok(stockMovementService.getProductMovementHistory(productId, startDate, endDate, pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteStockMovement(@PathVariable Long id) {
        return ResponseEntity.ok(stockMovementService.deleteStockMovement(id));
    }
}
