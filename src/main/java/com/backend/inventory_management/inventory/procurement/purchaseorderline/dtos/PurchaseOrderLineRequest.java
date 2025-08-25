package com.backend.inventory_management.inventory.procurement.purchaseorderline.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseOrderLineRequest {
    @NotNull
    private Long purchaseOrderId;

    @NotNull
    private Long productId;

    @NotNull
    private Integer lineNumber;

    @NotNull
    private Integer quantity;

    @NotNull
    private BigDecimal unitPrice;

    private BigDecimal discountPercent = BigDecimal.ZERO;
    private BigDecimal discountAmount = BigDecimal.ZERO;

    private String notes;
    private String supplierProductCode;
}
