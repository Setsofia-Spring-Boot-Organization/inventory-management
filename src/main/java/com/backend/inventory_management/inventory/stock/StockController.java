package com.backend.inventory_management.inventory.stock;

import com.backend.inventory_management.common.constants.Constants;
import com.backend.inventory_management.common.dto.BaseResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(Constants.API_PREFIX + "/stocks")
@CrossOrigin(origins = "*")
public class StockController {

    private final StockService stockService;

    @Autowired
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('INVENTORY_CLERK')")
    public ResponseEntity<BaseResponse<StockEntity>> createStock(@Valid @RequestBody StockEntity stock) {
        try {
            StockEntity createdStock = stockService.createStock(stock);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(BaseResponse.success("Stock created successfully", createdStock));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(BaseResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('INVENTORY_CLERK')")
    public ResponseEntity<BaseResponse<StockEntity>> getStockById(@PathVariable Long id) {
        return stockService.findById(id)
                .map(stock -> ResponseEntity.ok(BaseResponse.success(stock)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('INVENTORY_CLERK')")
    public ResponseEntity<BaseResponse<List<StockEntity>>> getAllStock() {
        List<StockEntity> stock = stockService.findAll();
        return ResponseEntity.ok(BaseResponse.success(stock));
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('INVENTORY_CLERK')")
    public ResponseEntity<BaseResponse<List<StockEntity>>> getLowStockItems() {
        List<StockEntity> lowStockItems = stockService.findLowStockItems();
        return ResponseEntity.ok(BaseResponse.success(lowStockItems));
    }

    @GetMapping("/expiring")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('INVENTORY_CLERK')")
    public ResponseEntity<BaseResponse<List<StockEntity>>> getExpiringItems(@RequestParam(defaultValue = "30") int days) {
        List<StockEntity> expiringItems = stockService.findExpiringItems(days);
        return ResponseEntity.ok(BaseResponse.success(expiringItems));
    }

    @GetMapping("/available")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('INVENTORY_CLERK')")
    public ResponseEntity<BaseResponse<List<StockEntity>>> getAvailableStock() {
        List<StockEntity> availableStock = stockService.findAvailableStock();
        return ResponseEntity.ok(BaseResponse.success(availableStock));
    }

    @PostMapping("/receive")
    @PreAuthorize("hasRole('ADMIN') or hasRole('INVENTORY_CLERK')")
    public ResponseEntity<BaseResponse<StockEntity>> receiveStock(
            @RequestParam Long productId,
            @RequestParam Long warehouseId,
            @RequestParam Integer quantity,
            @RequestParam BigDecimal unitCost,
            @RequestParam(required = false) String batchNumber,
            @RequestParam(required = false) LocalDate expiryDate,
            @RequestParam String performedBy,
            @RequestParam(required = false) String notes) {
        try {
            StockEntity stock = stockService.receiveStock(productId, warehouseId, quantity,
                    unitCost, batchNumber, expiryDate, performedBy, notes);
            return ResponseEntity.ok(BaseResponse.success("Stock received successfully", stock));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(BaseResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{id}/adjust")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('INVENTORY_CLERK')")
    public ResponseEntity<BaseResponse<Void>> adjustStock(
            @PathVariable Long id,
            @RequestParam Integer newQuantity,
            @RequestParam String reason,
            @RequestParam String performedBy) {
        try {
            stockService.adjustStock(id, newQuantity, reason, performedBy);
            return ResponseEntity.ok(BaseResponse.success("Stock adjusted successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(BaseResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('INVENTORY_CLERK')")
    public ResponseEntity<BaseResponse<Void>> transferStock(
            @RequestParam Long fromWarehouseId,
            @RequestParam Long toWarehouseId,
            @RequestParam Long productId,
            @RequestParam Integer quantity,
            @RequestParam String performedBy,
            @RequestParam(required = false) String notes) {
        try {
            stockService.transferStock(fromWarehouseId, toWarehouseId, productId, quantity, performedBy, notes);
            return ResponseEntity.ok(BaseResponse.success("Stock transferred successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(BaseResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/sell")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('CASHIER')")
    public ResponseEntity<BaseResponse<StockEntity>> sellStock(
            @RequestParam Long productId,
            @RequestParam Long warehouseId,
            @RequestParam Integer quantity,
            @RequestParam String transactionRef,
            @RequestParam String performedBy) {
        try {
            StockEntity stock = stockService.sellStock(productId, warehouseId, quantity, transactionRef, performedBy);
            return ResponseEntity.ok(BaseResponse.success("Stock sold successfully", stock));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(BaseResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{id}/reserve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('INVENTORY_CLERK')")
    public ResponseEntity<BaseResponse<Void>> reserveStock(
            @PathVariable Long id,
            @RequestParam Integer quantity,
            @RequestParam String reason,
            @RequestParam String performedBy) {
        try {
            stockService.reserveStock(id, quantity, reason, performedBy);
            return ResponseEntity.ok(BaseResponse.success("Stock reserved successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(BaseResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{id}/release-reservation")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('INVENTORY_CLERK')")
    public ResponseEntity<BaseResponse<Void>> releaseReservedStock(
            @PathVariable Long id,
            @RequestParam Integer quantity,
            @RequestParam String reason,
            @RequestParam String performedBy) {
        try {
            stockService.releaseReservedStock(id, quantity, reason, performedBy);
            return ResponseEntity.ok(BaseResponse.success("Reserved stock released successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(BaseResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/product/{productId}/total")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('INVENTORY_CLERK')")
    public ResponseEntity<BaseResponse<Integer>> getTotalStockByProduct(@PathVariable Long productId) {
        Integer totalStock = stockService.getTotalStockByProduct(productId);
        return ResponseEntity.ok(BaseResponse.success(totalStock));
    }

    @GetMapping("/check-availability")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('INVENTORY_CLERK') or hasRole('CASHIER')")
    public ResponseEntity<BaseResponse<Boolean>> checkStockAvailability(
            @RequestParam Long productId,
            @RequestParam Long warehouseId,
            @RequestParam Integer quantity) {
        boolean available = stockService.hasAvailableStock(productId, warehouseId, quantity);
        return ResponseEntity.ok(BaseResponse.success(available));
    }

    @GetMapping("/batch/{batchNumber}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('INVENTORY_CLERK')")
    public ResponseEntity<BaseResponse<List<StockEntity>>> getStockByBatch(@PathVariable String batchNumber) {
        List<StockEntity> batchStock = stockService.findByBatch(batchNumber);
        return ResponseEntity.ok(BaseResponse.success(batchStock));
    }

    @PostMapping("/batch/{batchNumber}/mark-expired")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<BaseResponse<Void>> markBatchAsExpired(
            @PathVariable String batchNumber,
            @RequestParam String reason,
            @RequestParam String performedBy) {
        try {
            stockService.markBatchAsExpired(batchNumber, reason, performedBy);
            return ResponseEntity.ok(BaseResponse.success("Batch marked as expired successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(BaseResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/batch/{batchNumber}/mark-damaged")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<BaseResponse<Void>> markBatchAsDamaged(
            @PathVariable String batchNumber,
            @RequestParam String reason,
            @RequestParam String performedBy) {
        try {
            stockService.markBatchAsDamaged(batchNumber, reason, performedBy);
            return ResponseEntity.ok(BaseResponse.success("Batch marked as damaged successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(BaseResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<BaseResponse<StockEntity>> updateStock(@PathVariable Long id, @Valid @RequestBody StockEntity stock) {
        try {
            StockEntity updatedStock = stockService.updateStock(id, stock);
            return ResponseEntity.ok(BaseResponse.success("Stock updated successfully", updatedStock));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(BaseResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<Void>> deleteStock(@PathVariable Long id) {
        try {
            stockService.deleteStock(id);
            return ResponseEntity.ok(BaseResponse.success("Stock deleted successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(BaseResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/product/{productId}/warehouse/{warehouseId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('INVENTORY_CLERK')")
    public ResponseEntity<BaseResponse<List<StockEntity>>> getStockByProductAndWarehouse(
            @PathVariable Long productId,
            @PathVariable Long warehouseId) {
        List<StockEntity> stock = stockService.findByProductAndWarehouse(productId, warehouseId);
        return ResponseEntity.ok(BaseResponse.success(stock));
    }
}