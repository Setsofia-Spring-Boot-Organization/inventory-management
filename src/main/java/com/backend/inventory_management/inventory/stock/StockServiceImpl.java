package com.backend.inventory_management.inventory.stock;

import com.backend.inventory_management.common.constants.Constants;
import com.backend.inventory_management.inventory.catalog.product.ProductEntity;
import com.backend.inventory_management.inventory.catalog.product.ProductRepository;
import com.backend.inventory_management.inventory.movement.StockMovementEntity;
import com.backend.inventory_management.inventory.movement.StockMovementRepository;
import com.backend.inventory_management.inventory.warehouse.WarehouseEntity;
import com.backend.inventory_management.inventory.warehouse.WarehouseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    @Autowired
    public StockServiceImpl(StockRepository stockRepository,
                            StockMovementRepository stockMovementRepository,
                            ProductRepository productRepository,
                            WarehouseRepository warehouseRepository) {
        this.stockRepository = stockRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
    }

    // Basic CRUD operations
    @Override
    public StockEntity createStock(StockEntity stock) {
        validateStock(stock);
        return stockRepository.save(stock);
    }

    @Override
    public StockEntity updateStock(Long id, StockEntity stock) {
        StockEntity existingStock = stockRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Stock not found with id: " + id));

        existingStock.setCurrentQuantity(stock.getCurrentQuantity());
        existingStock.setReservedQuantity(stock.getReservedQuantity());
        existingStock.setUnitCost(stock.getUnitCost());
        existingStock.setStatus(stock.getStatus());
        existingStock.setNotes(stock.getNotes());
        existingStock.setExpiryDate(stock.getExpiryDate());
        existingStock.setManufactureDate(stock.getManufactureDate());

        return stockRepository.save(existingStock);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StockEntity> findById(Long id) {
        return stockRepository.findById(id).filter(StockEntity::getIsActive);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockEntity> findAll() {
        return stockRepository.findByIsActiveTrue();
    }

    @Override
    public void deleteStock(Long id) {
        StockEntity stock = stockRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Stock not found with id: " + id));
        stock.setIsActive(false);
        stockRepository.save(stock);
    }

    // Query operations
    @Override
    @Transactional(readOnly = true)
    public List<StockEntity> findByProduct(Long productId) {
        ProductEntity product = getProductById(productId);
        return stockRepository.findByProductAndIsActiveTrue(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockEntity> findByWarehouse(Long warehouseId) {
        WarehouseEntity warehouse = getWarehouseById(warehouseId);
        return stockRepository.findByWarehouseAndIsActiveTrue(warehouse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockEntity> findByProductAndWarehouse(Long productId, Long warehouseId) {
        ProductEntity product = getProductById(productId);
        WarehouseEntity warehouse = getWarehouseById(warehouseId);
        return stockRepository.findByProductAndWarehouseAndIsActiveTrue(product, warehouse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockEntity> findLowStockItems() {
        return stockRepository.findLowStockItems();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockEntity> findExpiringItems(int days) {
        LocalDate expiryThreshold = LocalDate.now().plusDays(days);
        return stockRepository.findExpiringItems(expiryThreshold);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockEntity> findAvailableStock() {
        return stockRepository.findAvailableStock();
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getTotalStockByProduct(Long productId) {
        ProductEntity product = getProductById(productId);
        return stockRepository.getTotalStockByProduct(product);
    }

    // Stock operations
    @Override
    public StockEntity receiveStock(Long productId, Long warehouseId, Integer quantity,
                                    BigDecimal unitCost, String batchNumber, LocalDate expiryDate,
                                    String performedBy, String notes) {

        ProductEntity product = getProductById(productId);
        WarehouseEntity warehouse = getWarehouseById(warehouseId);

        StockEntity stock = stockRepository
                .findByProductWarehouseAndBatch(productId, warehouseId, batchNumber)
                .orElseGet(() -> new StockEntity(product, warehouse, 0, unitCost));

        int previousQuantity = stock.getCurrentQuantity();
        stock.setCurrentQuantity(previousQuantity + quantity);
        if (unitCost != null && unitCost.compareTo(BigDecimal.ZERO) > 0) {
            stock.setUnitCost(unitCost);
        }
        stock.setBatchNumber(batchNumber);
        stock.setExpiryDate(expiryDate);
        stock.setNotes(notes);

        StockEntity saved = stockRepository.save(stock);

        recordStockMovement(saved, Constants.StockMovementType.PURCHASE.name(), quantity,
                previousQuantity, saved.getCurrentQuantity(), null, notes, performedBy);

        return saved;
    }

    @Override
    public void adjustStock(Long stockId, Integer newQuantity, String reason, String performedBy) {
        StockEntity stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new EntityNotFoundException("Stock not found with id: " + stockId));

        int previousQuantity = stock.getCurrentQuantity();
        stock.setCurrentQuantity(newQuantity);
        stockRepository.save(stock);

        int adjustmentQuantity = newQuantity - previousQuantity;
        recordStockMovement(stock, Constants.StockMovementType.ADJUSTMENT.name(),
                adjustmentQuantity, previousQuantity, newQuantity, null, reason, performedBy);
    }

    @Override
    public void transferStock(Long fromWarehouseId, Long toWarehouseId, Long productId,
                              Integer quantity, String performedBy, String notes) {
        if (fromWarehouseId.equals(toWarehouseId)) {
            throw new IllegalArgumentException("Source and destination warehouses cannot be the same");
        }

        List<StockEntity> availableStocks = stockRepository
                .findAvailableStockForSale(productId, fromWarehouseId, 1);

        int remaining = quantity;
        for (StockEntity sourceStock : availableStocks) {
            if (remaining <= 0) break;

            int transferable = Math.min(remaining, sourceStock.getAvailableQuantity());
            int previousQuantity = sourceStock.getCurrentQuantity();

            sourceStock.setCurrentQuantity(previousQuantity - transferable);
            stockRepository.save(sourceStock);

            StockEntity destinationStock = stockRepository
                    .findByProductWarehouseAndBatch(productId, toWarehouseId, sourceStock.getBatchNumber())
                    .orElseGet(() -> {
                        StockEntity s = new StockEntity(sourceStock.getProduct(),
                                getWarehouseById(toWarehouseId), 0, sourceStock.getUnitCost());
                        s.setBatchNumber(sourceStock.getBatchNumber());
                        s.setExpiryDate(sourceStock.getExpiryDate());
                        s.setManufactureDate(sourceStock.getManufactureDate());
                        return s;
                    });

            destinationStock.setCurrentQuantity(destinationStock.getCurrentQuantity() + transferable);
            stockRepository.save(destinationStock);

            recordStockMovement(sourceStock, Constants.StockMovementType.TRANSFER.name(), -transferable,
                    previousQuantity, sourceStock.getCurrentQuantity(),
                    "Transfer to " + toWarehouseId, notes, performedBy);

            remaining -= transferable;
        }

        if (remaining > 0) {
            throw new IllegalStateException("Insufficient stock for transfer. Short by: " + remaining);
        }
    }

    @Override
    public StockEntity sellStock(Long productId, Long warehouseId, Integer quantity,
                                 String transactionRef, String performedBy) {

        List<StockEntity> availableStocks = stockRepository
                .findAvailableStockForSale(productId, warehouseId, 1);

        if (availableStocks.isEmpty()) {
            throw new IllegalStateException("No stock available for product " + productId);
        }

        int remaining = quantity;
        StockEntity lastUpdated = null;

        for (StockEntity stock : availableStocks) {
            if (remaining <= 0) break;

            int sellQuantity = Math.min(remaining, stock.getAvailableQuantity());
            int previousQuantity = stock.getCurrentQuantity();

            stock.setCurrentQuantity(previousQuantity - sellQuantity);
            lastUpdated = stockRepository.save(stock);

            recordStockMovement(stock, Constants.StockMovementType.SALE.name(), -sellQuantity,
                    previousQuantity, stock.getCurrentQuantity(), transactionRef,
                    "Sale transaction", performedBy);

            remaining -= sellQuantity;
        }

        if (remaining > 0) {
            throw new IllegalStateException("Insufficient stock for sale. Short by: " + remaining);
        }

        return lastUpdated;
    }

    @Override
    public void reserveStock(Long stockId, Integer quantity, String reason, String performedBy) {
        StockEntity stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new EntityNotFoundException("Stock not found with id: " + stockId));

        if (stock.getAvailableQuantity() < quantity) {
            throw new IllegalStateException("Insufficient available stock for reservation. " +
                    "Available: " + stock.getAvailableQuantity() + ", Requested: " + quantity);
        }

        int previousReserved = stock.getReservedQuantity();
        stock.setReservedQuantity(previousReserved + quantity);
        stockRepository.save(stock);

        recordStockMovement(stock, "RESERVATION", quantity,
                stock.getCurrentQuantity(), stock.getCurrentQuantity(), null, reason, performedBy);
    }

    @Override
    public void releaseReservedStock(Long stockId, Integer quantity, String reason, String performedBy) {
        StockEntity stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new EntityNotFoundException("Stock not found with id: " + stockId));

        if (stock.getReservedQuantity() < quantity) {
            throw new IllegalStateException("Cannot release more than reserved quantity. " +
                    "Reserved: " + stock.getReservedQuantity() + ", Requested: " + quantity);
        }

        int previousReserved = stock.getReservedQuantity();
        stock.setReservedQuantity(previousReserved - quantity);
        stockRepository.save(stock);

        recordStockMovement(stock, "RELEASE", -quantity,
                stock.getCurrentQuantity(), stock.getCurrentQuantity(), null, reason, performedBy);
    }

    // Validation methods
    @Override
    @Transactional(readOnly = true)
    public boolean hasAvailableStock(Long productId, Long warehouseId, Integer requiredQuantity) {
        List<StockEntity> availableStocks = stockRepository
                .findAvailableStockForSale(productId, warehouseId, 1);

        int totalAvailable = availableStocks.stream()
                .mapToInt(StockEntity::getAvailableQuantity)
                .sum();

        return totalAvailable >= requiredQuantity;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canFulfillOrder(Long productId, Integer requiredQuantity) {
        Integer totalStock = stockRepository.getTotalAvailableStockByProduct(productId);
        return totalStock != null && totalStock >= requiredQuantity;
    }

    // Batch operations
    @Override
    @Transactional(readOnly = true)
    public List<StockEntity> findByBatch(String batchNumber) {
        return stockRepository.findByBatchNumberAndIsActiveTrue(batchNumber);
    }

    @Override
    public void markBatchAsExpired(String batchNumber, String reason, String performedBy) {
        List<StockEntity> batchStocks = findByBatch(batchNumber);

        for (StockEntity stock : batchStocks) {
            if (stock.getCurrentQuantity() > 0) {
                int previousQuantity = stock.getCurrentQuantity();

                // Set stock to expired status and zero quantity
                stock.setStatus(StockEntity.StockStatus.EXPIRED);
                stock.setCurrentQuantity(0);
                stock.setNotes(reason);
                stockRepository.save(stock);

                recordStockMovement(stock, Constants.StockMovementType.EXPIRED.name(), -previousQuantity,
                        previousQuantity, 0, batchNumber, reason, performedBy);
            }
        }
    }

    @Override
    public void markBatchAsDamaged(String batchNumber, String reason, String performedBy) {
        List<StockEntity> batchStocks = findByBatch(batchNumber);

        for (StockEntity stock : batchStocks) {
            if (stock.getCurrentQuantity() > 0) {
                int previousQuantity = stock.getCurrentQuantity();

                // Set stock to damaged status and zero quantity
                stock.setStatus(StockEntity.StockStatus.DAMAGED);
                stock.setCurrentQuantity(0);
                stock.setNotes(reason);
                stockRepository.save(stock);

                recordStockMovement(stock, Constants.StockMovementType.DAMAGED.name(), -previousQuantity,
                        previousQuantity, 0, batchNumber, reason, performedBy);
            }
        }
    }

    // --- Helper methods ---

    private ProductEntity getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));
    }

    private WarehouseEntity getWarehouseById(Long warehouseId) {
        return warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found: " + warehouseId));
    }

    private void validateStock(StockEntity stock) {
        if (stock.getProduct() == null) throw new IllegalArgumentException("Product is required");
        if (stock.getWarehouse() == null) throw new IllegalArgumentException("Warehouse is required");
        if (stock.getCurrentQuantity() < 0) throw new IllegalArgumentException("Quantity cannot be negative");
        if (stock.getReservedQuantity() < 0) throw new IllegalArgumentException("Reserved quantity cannot be negative");
        if (stock.getUnitCost() != null && stock.getUnitCost().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Unit cost cannot be negative");
        }
    }

    private void recordStockMovement(StockEntity stock, String type, Integer qty,
                                     Integer previousQty, Integer newQty, String ref,
                                     String notes, String performedBy) {
        StockMovementEntity movement = new StockMovementEntity();
        movement.setProduct(stock.getProduct());
        movement.setWarehouse(stock.getWarehouse());
        movement.setMovementType(StockMovementEntity.MovementType.valueOf(type));
        movement.setQuantity(Math.abs(qty));
        movement.setPreviousQuantity(previousQty);
        movement.setNewQuantity(newQty);
        movement.setUnitCost(stock.getUnitCost());
        movement.setReferenceNumber(ref);
        movement.setNotes(notes);
        movement.setBatchNumber(stock.getBatchNumber());
        movement.setCreatedBy(performedBy);

        stockMovementRepository.save(movement);
    }
}