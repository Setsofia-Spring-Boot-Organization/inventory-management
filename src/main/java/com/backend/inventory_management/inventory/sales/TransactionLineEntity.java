package com.backend.inventory_management.inventory.sales;

import com.backend.inventory_management.common.entity.BaseEntity;
import com.backend.inventory_management.inventory.catalog.product.ProductEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "transaction_lines")
@Data
public class TransactionLineEntity extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id")
    @NotBlank
    private TransactionEntity transaction;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @NotBlank
    private ProductEntity product;
    
    @NotBlank
    private Integer lineNumber;
    
    @NotBlank
    private Integer quantity;
    
    @NotBlank
    private BigDecimal unitPrice;
    
    @NotBlank
    private BigDecimal lineTotal;
    
    private BigDecimal discountPercent = BigDecimal.ZERO;
    private BigDecimal discountAmount = BigDecimal.ZERO;
    private BigDecimal taxAmount = BigDecimal.ZERO;
    
    private String batchNumber;
    private String notes;

    // Constructors
    public TransactionLineEntity() {}

    public TransactionLineEntity(TransactionEntity transaction, ProductEntity product, 
                               Integer lineNumber, Integer quantity, BigDecimal unitPrice) {
        this.transaction = transaction;
        this.product = product;
        this.lineNumber = lineNumber;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        calculateLineTotal();
    }

    // Helper method
    @PrePersist
    @PreUpdate
    private void calculateLineTotal() {
        BigDecimal subtotal = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
        this.lineTotal = subtotal
            .subtract(this.discountAmount != null ? this.discountAmount : BigDecimal.ZERO)
            .add(this.taxAmount != null ? this.taxAmount : BigDecimal.ZERO);
    }
}