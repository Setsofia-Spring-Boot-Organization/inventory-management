package com.backend.inventory_management.inventory.stock;

import com.backend.inventory_management.inventory.catalog.product.ProductEntity;
import com.backend.inventory_management.inventory.warehouse.WarehouseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<StockEntity, Long> {

    // Basic active stock queries
    List<StockEntity> findByIsActiveTrue();

    // Product-based queries
    List<StockEntity> findByProductIdAndIsActiveTrue(Long productId);
    List<StockEntity> findByProductAndIsActiveTrue(ProductEntity product);

    // Warehouse-based queries
    List<StockEntity> findByWarehouseIdAndIsActiveTrue(Long warehouseId);
    List<StockEntity> findByWarehouseAndIsActiveTrue(WarehouseEntity warehouse);

    // Product and Warehouse combined queries
    List<StockEntity> findByProductIdAndWarehouseIdAndIsActiveTrue(Long productId, Long warehouseId);
    List<StockEntity> findByProductAndWarehouseAndIsActiveTrue(ProductEntity product, WarehouseEntity warehouse);

    // Status-based queries
    List<StockEntity> findByStatusAndIsActiveTrue(StockEntity.StockStatus status);

    // Batch-based queries
    List<StockEntity> findByBatchNumberAndIsActiveTrue(String batchNumber);

    // Expiry date queries
    List<StockEntity> findByExpiryDateBetweenAndIsActiveTrue(LocalDate startDate, LocalDate endDate);
    List<StockEntity> findByExpiryDateBeforeAndIsActiveTrue(LocalDate date);

    // Low stock items query
    @Query("SELECT s FROM StockEntity s JOIN s.product p WHERE s.isActive = true AND " +
            "s.currentQuantity <= p.reorderPoint AND s.currentQuantity > 0")
    List<StockEntity> findLowStockItems();

    // Expiring items query (used by findExpiringItems method in service)
    @Query("SELECT s FROM StockEntity s WHERE s.isActive = true AND s.expiryDate IS NOT NULL AND " +
            "s.expiryDate <= :expiryThreshold AND s.currentQuantity > 0")
    List<StockEntity> findExpiringItems(@Param("expiryThreshold") LocalDate expiryThreshold);

    // Available stock query
    @Query("SELECT s FROM StockEntity s WHERE s.isActive = true AND " +
            "(s.currentQuantity - COALESCE(s.reservedQuantity, 0)) > 0 AND s.status = 'GOOD'")
    List<StockEntity> findAvailableStock();

    // Total quantity calculations
    @Query("SELECT SUM(s.currentQuantity) FROM StockEntity s WHERE s.product.id = :productId AND s.isActive = true")
    Optional<Integer> getTotalQuantityByProduct(@Param("productId") Long productId);

    @Query("SELECT COALESCE(SUM(s.currentQuantity), 0) FROM StockEntity s WHERE s.product = :product AND s.isActive = true")
    Integer getTotalStockByProduct(@Param("product") ProductEntity product);

    @Query("SELECT COALESCE(SUM(s.currentQuantity - COALESCE(s.reservedQuantity, 0)), 0) FROM StockEntity s " +
            "WHERE s.product.id = :productId AND s.isActive = true AND " +
            "(s.currentQuantity - COALESCE(s.reservedQuantity, 0)) > 0")
    Integer getTotalAvailableStockByProduct(@Param("productId") Long productId);

    // Stock lookup by product, warehouse, and batch
    @Query("SELECT s FROM StockEntity s WHERE s.product.id = :productId AND s.warehouse.id = :warehouseId " +
            "AND (:batchNumber IS NULL OR s.batchNumber = :batchNumber) AND s.isActive = true")
    Optional<StockEntity> findByProductWarehouseAndBatch(@Param("productId") Long productId,
                                                         @Param("warehouseId") Long warehouseId,
                                                         @Param("batchNumber") String batchNumber);

    // Available stock for sale (FIFO - First Expiry, First Out)
    @Query("SELECT s FROM StockEntity s WHERE s.product.id = :productId AND s.warehouse.id = :warehouseId " +
            "AND (s.currentQuantity - COALESCE(s.reservedQuantity, 0)) >= :quantity AND s.status = 'GOOD' " +
            "AND s.isActive = true ORDER BY s.expiryDate ASC NULLS LAST, s.createdAt ASC")
    List<StockEntity> findAvailableStockForSale(@Param("productId") Long productId,
                                                @Param("warehouseId") Long warehouseId,
                                                @Param("quantity") Integer quantity);

    // Check if stock exists by product, warehouse, and batch
    @Query("SELECT COUNT(s) > 0 FROM StockEntity s WHERE s.product.id = :productId AND " +
            "s.warehouse.id = :warehouseId AND s.batchNumber = :batchNumber AND s.isActive = true")
    boolean existsByProductWarehouseAndBatch(@Param("productId") Long productId,
                                             @Param("warehouseId") Long warehouseId,
                                             @Param("batchNumber") String batchNumber);

    // Additional useful queries for inventory management

    // Find stock items that need attention (low stock, expiring, etc.)
    @Query("SELECT s FROM StockEntity s JOIN s.product p WHERE s.isActive = true AND " +
            "(s.currentQuantity <= p.reorderPoint OR " +
            "(s.expiryDate IS NOT NULL AND s.expiryDate <= :warningDate))")
    List<StockEntity> findStockNeedingAttention(@Param("warningDate") LocalDate warningDate);

    // Find stock by multiple statuses
    @Query("SELECT s FROM StockEntity s WHERE s.isActive = true AND s.status IN :statuses")
    List<StockEntity> findByStatusInAndIsActiveTrue(@Param("statuses") List<String> statuses);

    // Find stock with available quantity greater than specified amount
    @Query("SELECT s FROM StockEntity s WHERE s.isActive = true AND " +
            "(s.currentQuantity - COALESCE(s.reservedQuantity, 0)) >= :minQuantity")
    List<StockEntity> findByAvailableQuantityGreaterThanEqual(@Param("minQuantity") Integer minQuantity);

    // Find all batches for a product across all warehouses
    @Query("SELECT DISTINCT s.batchNumber FROM StockEntity s WHERE s.product.id = :productId " +
            "AND s.isActive = true AND s.batchNumber IS NOT NULL")
    List<String> findDistinctBatchNumbersByProduct(@Param("productId") Long productId);

    // Stock valuation query
    @Query("SELECT SUM(s.currentQuantity * s.unitCost) FROM StockEntity s WHERE s.isActive = true")
    Optional<java.math.BigDecimal> calculateTotalStockValue();

    // Stock valuation by warehouse
    @Query("SELECT SUM(s.currentQuantity * s.unitCost) FROM StockEntity s " +
            "WHERE s.warehouse.id = :warehouseId AND s.isActive = true")
    Optional<java.math.BigDecimal> calculateStockValueByWarehouse(@Param("warehouseId") Long warehouseId);

    // Stock valuation by product
    @Query("SELECT SUM(s.currentQuantity * s.unitCost) FROM StockEntity s " +
            "WHERE s.product.id = :productId AND s.isActive = true")
    Optional<java.math.BigDecimal> calculateStockValueByProduct(@Param("productId") Long productId);
}