package com.backend.inventory_management.inventory.sales;

import com.backend.inventory_management.common.entity.BaseEntity;
import com.backend.inventory_management.core.store.StoreEntity;
import com.backend.inventory_management.core.user.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "transactions")
public class TransactionEntity extends BaseEntity {

    // Getters and setters
    @NotBlank
    @Column(unique = true)
    private String transactionNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    @NotBlank
    private StoreEntity store;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cashier_id")
    @NotBlank
    private UserEntity cashier;
    
    @NotBlank
    private LocalDateTime transactionDateTime;
    
    @Enumerated(EnumType.STRING)
    @NotBlank
    private TransactionType transactionType = TransactionType.SALE;
    
    @Enumerated(EnumType.STRING)
    @NotBlank
    private TransactionStatus status = TransactionStatus.ACTIVE;
    
    @NotBlank
    private BigDecimal subtotal = BigDecimal.ZERO;
    
    @NotBlank
    private BigDecimal taxAmount = BigDecimal.ZERO;
    
    @NotBlank
    private BigDecimal discountAmount = BigDecimal.ZERO;
    
    @NotBlank
    private BigDecimal totalAmount = BigDecimal.ZERO;
    
    @NotBlank
    private BigDecimal paidAmount = BigDecimal.ZERO;
    
    @NotBlank
    private BigDecimal changeAmount = BigDecimal.ZERO;
    
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod = PaymentMethod.CASH;
    
    private String paymentReference;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    
    private String notes;
    private String receiptNumber;
    
    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TransactionLineEntity> transactionLines = new ArrayList<>();
    
    public enum TransactionType {
        SALE, RETURN, EXCHANGE, VOID
    }
    
    public enum TransactionStatus {
        ACTIVE, CANCELLED, VOIDED, RETURNED
    }
    
    public enum PaymentMethod {
        CASH, CREDIT_CARD, DEBIT_CARD, MOBILE_PAYMENT, BANK_TRANSFER, VOUCHER, LOYALTY_POINTS
    }

    // Constructors
    public TransactionEntity() {}

    public TransactionEntity(String transactionNumber, StoreEntity store, UserEntity cashier, LocalDateTime transactionDateTime) {
        this.transactionNumber = transactionNumber;
        this.store = store;
        this.cashier = cashier;
        this.transactionDateTime = transactionDateTime;
        calculateTotals();
    }

    // Helper method to calculate totals
    public void calculateTotals() {
        this.subtotal = transactionLines.stream()
            .map(line -> line.getUnitPrice().multiply(BigDecimal.valueOf(line.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        this.totalAmount = this.subtotal
            .add(this.taxAmount)
            .subtract(this.discountAmount);
            
        this.changeAmount = this.paidAmount.subtract(this.totalAmount);
        if (this.changeAmount.compareTo(BigDecimal.ZERO) < 0) {
            this.changeAmount = BigDecimal.ZERO;
        }
    }

}