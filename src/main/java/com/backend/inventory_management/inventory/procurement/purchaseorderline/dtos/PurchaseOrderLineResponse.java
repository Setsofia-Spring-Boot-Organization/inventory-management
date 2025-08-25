package com.backend.inventory_management.inventory.procurement.purchaseorderline.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseOrderLineResponse {
    private Long id;
    private Long purchaseOrderId;
    private Long productId;
    private Integer lineNumber;
    private Integer quantity;
    private Integer receivedQuantity;
    private Integer remainingQuantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;
    private BigDecimal discountPercent;
    private BigDecimal discountAmount;
    private String notes;
    private String supplierProductCode;
}
