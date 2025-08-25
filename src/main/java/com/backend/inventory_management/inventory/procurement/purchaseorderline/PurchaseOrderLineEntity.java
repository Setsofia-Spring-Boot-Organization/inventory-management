package com.backend.inventory_management.inventory.procurement.purchaseorderline;

import com.backend.inventory_management.common.entity.BaseEntity;
import com.backend.inventory_management.inventory.catalog.product.ProductEntity;
import com.backend.inventory_management.inventory.procurement.purchaseorder.PurchaseOrderEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "purchase_order_lines")
@Data
public class PurchaseOrderLineEntity extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id")
    @NotBlank
    private PurchaseOrderEntity purchaseOrder;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @NotBlank
    private ProductEntity product;
    
    @NotBlank
    private Integer lineNumber;
    
    @NotBlank
    private Integer quantity;
    
    private Integer receivedQuantity = 0;
    private Integer remainingQuantity;
    
    @NotBlank
    private BigDecimal unitPrice;
    
    @NotBlank
    private BigDecimal lineTotal;
    
    private BigDecimal discountPercent = BigDecimal.ZERO;
    private BigDecimal discountAmount = BigDecimal.ZERO;
    
    private String notes;
    private String supplierProductCode;

    // Constructors
    public PurchaseOrderLineEntity() {}

    public PurchaseOrderLineEntity(PurchaseOrderEntity purchaseOrder, ProductEntity product, 
                                  Integer lineNumber, Integer quantity, BigDecimal unitPrice) {
        this.purchaseOrder = purchaseOrder;
        this.product = product;
        this.lineNumber = lineNumber;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        calculateLineTotal();
        calculateRemainingQuantity();
    }

    // Helper methods
    @PrePersist
    @PreUpdate
    private void calculateTotals() {
        calculateLineTotal();
        calculateRemainingQuantity();
    }

    private void calculateLineTotal() {
        BigDecimal subtotal = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
        this.lineTotal = subtotal.subtract(this.discountAmount != null ? this.discountAmount : BigDecimal.ZERO);
    }

    private void calculateRemainingQuantity() {
        this.remainingQuantity = this.quantity - (this.receivedQuantity != null ? this.receivedQuantity : 0);
    }
}