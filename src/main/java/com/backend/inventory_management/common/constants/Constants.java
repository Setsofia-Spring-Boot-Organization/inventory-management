package com.backend.inventory_management.common.constants;

public class Constants {
    public static final String API_PREFIX = "/api/v1";

    public enum Role {
        ADMIN, MANAGER, CASHIER, INVENTORY_CLERK
    }

    public enum StockMovementType {
        PURCHASE, SALE, ADJUSTMENT, TRANSFER, RETURN, EXPIRED, DAMAGED
    }

    public enum AlertType {
        LOW_STOCK, EXPIRY_WARNING, EXPIRED_PRODUCT, OVERSTOCK
    }
}
