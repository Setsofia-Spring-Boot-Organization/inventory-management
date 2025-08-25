package com.backend.inventory_management.inventory.procurement.purchaseorder;

import com.backend.inventory_management.common.entity.BaseEntity;
import com.backend.inventory_management.core.store.StoreEntity;
import com.backend.inventory_management.core.user.UserEntity;
import com.backend.inventory_management.inventory.procurement.supplier.SupplierEntity;
import com.backend.inventory_management.inventory.procurement.purchaseorderline.PurchaseOrderLineEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "purchase_orders")
public class PurchaseOrderEntity extends BaseEntity {

    @NotBlank(message = "Order number is required")
    @Column(unique = true, name = "order_number")
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    @NotNull(message = "Supplier is required")
    private SupplierEntity supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    @NotNull(message = "Store is required")
    private StoreEntity store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private UserEntity createdByUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by_user_id")
    private UserEntity approvedByUser;

    @NotNull(message = "Order date is required")
    @Column(name = "order_date")
    private LocalDate orderDate;

    @Column(name = "expected_delivery_date")
    private LocalDate expectedDeliveryDate;

    @Column(name = "actual_delivery_date")
    private LocalDate actualDeliveryDate;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Status is required")
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority = Priority.NORMAL;

    @NotNull(message = "Subtotal is required")
    @DecimalMin(value = "0.0", message = "Subtotal must be non-negative")
    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @NotNull(message = "Tax amount is required")
    @DecimalMin(value = "0.0", message = "Tax amount must be non-negative")
    @Column(name = "tax_amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @NotNull(message = "Discount amount is required")
    @DecimalMin(value = "0.0", message = "Discount amount must be non-negative")
    @Column(name = "discount_amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.0", message = "Total amount must be non-negative")
    @Column(name = "total_amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(length = 3)
    private String currency = "USD";

    @Column(name = "payment_terms")
    private String paymentTerms;

    @Column(name = "delivery_address")
    private String deliveryAddress;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(columnDefinition = "TEXT")
    private String terms;

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<PurchaseOrderLineEntity> orderLines = new ArrayList<>();

    public enum OrderStatus {
        DRAFT, SUBMITTED, PENDING, APPROVED, SENT_TO_SUPPLIER,
        PARTIALLY_RECEIVED, FULLY_RECEIVED, CANCELLED, CLOSED
    }

    public enum Priority {
        LOW, NORMAL, HIGH, URGENT
    }

    // Constructors
    public PurchaseOrderEntity() {}

    public PurchaseOrderEntity(String orderNumber, SupplierEntity supplier, StoreEntity store, LocalDate orderDate) {
        this.orderNumber = orderNumber;
        this.supplier = supplier;
        this.store = store;
        this.orderDate = orderDate;
    }

    // Helper method to calculate totals
    public void calculateTotals() {
        this.subtotal = orderLines.stream()
                .map(line -> line.getUnitPrice().multiply(BigDecimal.valueOf(line.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.totalAmount = this.subtotal
                .add(this.taxAmount)
                .subtract(this.discountAmount);
    }
}