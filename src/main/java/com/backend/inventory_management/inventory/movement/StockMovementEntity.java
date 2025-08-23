package com.backend.inventory_management.inventory.movement;

import com.backend.inventory_management.common.entity.BaseEntity;
import com.backend.inventory_management.core.user.UserEntity;
import com.backend.inventory_management.inventory.catalog.product.ProductEntity;
import com.backend.inventory_management.inventory.warehouse.WarehouseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@Entity
@Table(name = "stock_movements")
public class StockMovementEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @NotBlank
    private ProductEntity product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    @NotBlank
    private WarehouseEntity warehouse;

    @Enumerated(EnumType.STRING)
    @NotBlank
    private MovementType movementType;

    @NotBlank
    private Integer quantity;

    @NotBlank
    private BigDecimal unitCost = BigDecimal.ZERO;

    private Integer previousQuantity = 0;
    private Integer newQuantity = 0;

    private String batchNumber;
    private String referenceNumber;
    private String reason;
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by")
    private UserEntity performedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_warehouse_id")
    private WarehouseEntity sourceWarehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_warehouse_id")
    private WarehouseEntity destinationWarehouse;

    @Getter
    public enum MovementType {
        PURCHASE_IN("Purchase Receipt"),
        SALE_OUT("Sale"),
        ADJUSTMENT_IN("Stock Adjustment - Increase"),
        ADJUSTMENT_OUT("Stock Adjustment - Decrease"),
        TRANSFER_IN("Transfer In"),
        TRANSFER_OUT("Transfer Out"),
        RETURN_IN("Return to Inventory"),
        RETURN_OUT("Return to Supplier"),
        EXPIRED_OUT("Expired Stock Removal"),
        DAMAGED_OUT("Damaged Stock Removal"),
        PROMOTION_OUT("Promotional Usage"),
        SAMPLE_OUT("Sample Distribution");

        private final String description;

        MovementType(String description) {
            this.description = description;
        }

    }

    // Constructors
    public StockMovementEntity() {}

    public StockMovementEntity(ProductEntity product, WarehouseEntity warehouse, MovementType movementType, Integer quantity, Integer previousQuantity, Integer newQuantity) {
        this.product = product;
        this.warehouse = warehouse;
        this.movementType = movementType;
        this.quantity = quantity;
        this.previousQuantity = previousQuantity;
        this.newQuantity = newQuantity;
    }

}