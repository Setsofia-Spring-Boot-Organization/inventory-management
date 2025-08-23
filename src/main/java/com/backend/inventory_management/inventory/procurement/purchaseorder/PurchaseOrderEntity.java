package com.backend.inventory_management.inventory.procurement.purchaseorder;

import com.backend.inventory_management.common.entity.BaseEntity;
import com.backend.inventory_management.core.store.StoreEntity;
import com.backend.inventory_management.core.user.UserEntity;
import com.backend.inventory_management.inventory.procurement.SupplierEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank
    @Column(unique = true)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    @NotBlank
    private SupplierEntity supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    @NotBlank
    private StoreEntity store;

    // Renamed to avoid conflict with BaseEntity.createdBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private UserEntity createdByUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by_user_id")
    private UserEntity approvedByUser;

    @NotBlank
    private LocalDate orderDate;

    private LocalDate expectedDeliveryDate;
    private LocalDate actualDeliveryDate;

    @Enumerated(EnumType.STRING)
    @NotBlank
    private OrderStatus status = OrderStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.NORMAL;

    @NotBlank
    private BigDecimal subtotal = BigDecimal.ZERO;

    @NotBlank
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @NotBlank
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @NotBlank
    private BigDecimal totalAmount = BigDecimal.ZERO;

    private String currency = "USD";
    private String paymentTerms;
    private String deliveryAddress;
    private String notes;
    private String terms;

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PurchaseOrderLineEntity> orderLines = new ArrayList<>();

    public enum OrderStatus {
        DRAFT, SUBMITTED, APPROVED, SENT_TO_SUPPLIER, PARTIALLY_RECEIVED,
        FULLY_RECEIVED, CANCELLED, CLOSED
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