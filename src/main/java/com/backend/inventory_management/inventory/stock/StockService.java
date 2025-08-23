package com.backend.inventory_management.inventory.stock;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StockService {

    // Basic CRUD operations
    StockEntity createStock(StockEntity stock);
    StockEntity updateStock(Long id, StockEntity stock);
    Optional<StockEntity> findById(Long id);
    List<StockEntity> findAll();
    void deleteStock(Long id);

    // Query operations
    List<StockEntity> findByProduct(Long productId);
    List<StockEntity> findByWarehouse(Long warehouseId);
    List<StockEntity> findByProductAndWarehouse(Long productId, Long warehouseId);
    List<StockEntity> findLowStockItems();
    List<StockEntity> findExpiringItems(int days);
    List<StockEntity> findAvailableStock();
    Integer getTotalStockByProduct(Long productId);

    // Stock operations
    StockEntity receiveStock(Long productId, Long warehouseId, Integer quantity,
                             BigDecimal unitCost, String batchNumber, LocalDate expiryDate,
                             String performedBy, String notes);
    void adjustStock(Long stockId, Integer newQuantity, String reason, String performedBy);
    void transferStock(Long fromWarehouseId, Long toWarehouseId, Long productId,
                       Integer quantity, String performedBy, String notes);
    StockEntity sellStock(Long productId, Long warehouseId, Integer quantity,
                          String transactionRef, String performedBy);
    void reserveStock(Long stockId, Integer quantity, String reason, String performedBy);
    void releaseReservedStock(Long stockId, Integer quantity, String reason, String performedBy);

    // Validation methods
    boolean hasAvailableStock(Long productId, Long warehouseId, Integer requiredQuantity);
    boolean canFulfillOrder(Long productId, Integer requiredQuantity);

    // Batch operations
    List<StockEntity> findByBatch(String batchNumber);
    void markBatchAsExpired(String batchNumber, String reason, String performedBy);
    void markBatchAsDamaged(String batchNumber, String reason, String performedBy);
}