package com.backend.inventory_management.inventory.stock;

import com.backend.inventory_management.common.entity.BaseEntity;
import com.backend.inventory_management.inventory.catalog.product.ProductEntity;
import com.backend.inventory_management.inventory.warehouse.WarehouseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "stock",
        uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "warehouse_id", "batch_number"}))
public class StockEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @NotNull
    private ProductEntity product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    @NotNull
    private WarehouseEntity warehouse;

    @Column(name = "current_quantity", nullable = false)
    @NotNull
    private Integer currentQuantity = 0;

    @Column(name = "reserved_quantity", nullable = false)
    @NotNull
    private Integer reservedQuantity = 0;

    @Column(name = "available_quantity", nullable = false)
    @NotNull
    private Integer availableQuantity = 0;

    @Column(name = "batch_number")
    private String batchNumber;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "manufacture_date")
    private LocalDate manufactureDate;

    @Column(name = "unit_cost", precision = 10, scale = 4, nullable = false)
    @NotNull
    private BigDecimal unitCost = BigDecimal.ZERO;

    @Column(name = "supplier_batch")
    private String supplierBatch;

    @Column(name = "received_date")
    private LocalDate receivedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StockStatus status = StockStatus.GOOD;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public enum StockStatus {
        GOOD, DAMAGED, EXPIRED, QUARANTINE, RESERVED, SOLD
    }

    // Constructor for creating new stock
    public StockEntity(ProductEntity product, WarehouseEntity warehouse, Integer quantity, BigDecimal unitCost) {
        this.product = product;
        this.warehouse = warehouse;
        this.currentQuantity = quantity;
        this.reservedQuantity = 0;
        this.availableQuantity = quantity;
        this.unitCost = unitCost;
        this.receivedDate = LocalDate.now();
    }

    // Helper method to calculate available quantity
    @PrePersist
    @PreUpdate
    private void calculateAvailableQuantity() {
        if (this.currentQuantity == null) this.currentQuantity = 0;
        if (this.reservedQuantity == null) this.reservedQuantity = 0;

        this.availableQuantity = this.currentQuantity - this.reservedQuantity;
        if (this.availableQuantity < 0) {
            this.availableQuantity = 0;
        }
    }

    // Custom setters to trigger calculation
    public void setCurrentQuantity(Integer currentQuantity) {
        this.currentQuantity = currentQuantity;
        calculateAvailableQuantity();
    }

    public void setReservedQuantity(Integer reservedQuantity) {
        this.reservedQuantity = reservedQuantity;
        calculateAvailableQuantity();
    }

    // Helper method to check if stock is expired
    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now());
    }

    // Helper method to check if stock is expiring soon
    public boolean isExpiringSoon(int days) {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now().plusDays(days));
    }
}