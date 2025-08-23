package com.backend.inventory_management.inventory.notification;

import com.backend.inventory_management.common.entity.BaseEntity;
import com.backend.inventory_management.core.store.StoreEntity;
import com.backend.inventory_management.inventory.catalog.product.ProductEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "idx_notification_read_created", columnList = "is_read, created_at"),
        @Index(name = "idx_notification_store_read", columnList = "store_id, is_read"),
        @Index(name = "idx_notification_alert_type", columnList = "alert_type"),
        @Index(name = "idx_notification_priority", columnList = "priority_level")
})
public class NotificationEntity extends BaseEntity {

    @NotBlank(message = "Alert type is required")
    @Column(name = "alert_type", nullable = false)
    private String alertType; // LOW_STOCK, EXPIRY_WARNING, etc.

    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "priority_level", nullable = false)
    private String priorityLevel = "MEDIUM"; // LOW, MEDIUM, HIGH, CRITICAL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private StoreEntity store;

    @Column(name = "current_quantity")
    private Integer currentQuantity;

    @Column(name = "threshold_quantity")
    private Integer thresholdQuantity;

    @Column(name = "action_required")
    private Boolean actionRequired = false;

    @Column(name = "reference_id")
    private String referenceId; // Reference to related entity/transaction

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata; // JSON data for additional context

    @Column(name = "expires_at")
    private LocalDateTime expiresAt; // When the notification becomes obsolete

    @Column(name = "auto_dismiss")
    private Boolean autoDismiss = false; // Auto dismiss when resolved elsewhere

    @Getter
    public enum AlertType {
        LOW_STOCK("LOW_STOCK"),
        OUT_OF_STOCK("OUT_OF_STOCK"),
        OVERSTOCK("OVERSTOCK"),
        EXPIRY_WARNING("EXPIRY_WARNING"),
        EXPIRED_PRODUCT("EXPIRED_PRODUCT"),
        PRICE_CHANGE("PRICE_CHANGE"),
        INVENTORY_UPDATE("INVENTORY_UPDATE"),
        SYSTEM_NOTIFICATION("SYSTEM_NOTIFICATION"),
        INVENTORY_DISCREPANCY("INVENTORY_DISCREPANCY"),
        SUPPLIER_DELAY("SUPPLIER_DELAY");

        private final String value;

        AlertType(String value) {
            this.value = value;
        }

        public static AlertType fromValue(String value) {
            for (AlertType type : values()) {
                if (type.value.equals(value)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown AlertType: " + value);
        }
    }

    public enum PriorityLevel {
        LOW("LOW", 1),
        MEDIUM("MEDIUM", 2),
        HIGH("HIGH", 3),
        CRITICAL("CRITICAL", 4);

        private final String value;
        private final int priority;

        PriorityLevel(String value, int priority) {
            this.value = value;
            this.priority = priority;
        }

        public String getValue() {
            return value;
        }

        public int getPriority() {
            return priority;
        }

        public static PriorityLevel fromValue(String value) {
            for (PriorityLevel level : values()) {
                if (level.value.equals(value)) {
                    return level;
                }
            }
            throw new IllegalArgumentException("Unknown PriorityLevel: " + value);
        }
    }

    // Constructors
    public NotificationEntity() {}

    public NotificationEntity(String alertType, String title, String message, String priorityLevel) {
        this.alertType = alertType;
        this.title = title;
        this.message = message;
        this.priorityLevel = priorityLevel;
        this.actionRequired = isPriorityActionRequired(priorityLevel);
    }

    public NotificationEntity(AlertType alertType, String title, String message, PriorityLevel priorityLevel) {
        this(alertType.getValue(), title, message, priorityLevel.getValue());
    }

    // Utility methods for better class communication
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isHighPriority() {
        return PriorityLevel.HIGH.getValue().equals(priorityLevel) ||
                PriorityLevel.CRITICAL.getValue().equals(priorityLevel);
    }

    public boolean isCritical() {
        return PriorityLevel.CRITICAL.getValue().equals(priorityLevel);
    }

    public int getPriorityValue() {
        try {
            return PriorityLevel.fromValue(priorityLevel).getPriority();
        } catch (IllegalArgumentException e) {
            return PriorityLevel.MEDIUM.getPriority(); // Default fallback
        }
    }

    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }

    public void setExpiration(LocalDateTime expirationTime) {
        this.expiresAt = expirationTime;
    }

    public void setExpiration(long hours) {
        this.expiresAt = LocalDateTime.now().plusHours(hours);
    }

    private boolean isPriorityActionRequired(String priorityLevel) {
        return PriorityLevel.HIGH.getValue().equals(priorityLevel) ||
                PriorityLevel.CRITICAL.getValue().equals(priorityLevel);
    }

    // Method to sync with AlertEntity (for integration)
    public void syncWithAlert(AlertEntity alert) {
        if (alert != null) {
            this.title = alert.getTitle();
            this.message = alert.getMessage();
            this.metadata = alert.getReferenceData();

            // Map severity to priority
            switch (alert.getSeverity()) {
                case LOW -> this.priorityLevel = PriorityLevel.LOW.getValue();
                case MEDIUM -> this.priorityLevel = PriorityLevel.MEDIUM.getValue();
                case HIGH -> this.priorityLevel = PriorityLevel.HIGH.getValue();
                case CRITICAL -> this.priorityLevel = PriorityLevel.CRITICAL.getValue();
            }

            this.actionRequired = isPriorityActionRequired(this.priorityLevel);
        }
    }

    @Override
    public String toString() {
        return "NotificationEntity{" +
                "id=" + getId() +
                ", alertType='" + alertType + '\'' +
                ", title='" + title + '\'' +
                ", isRead=" + isRead +
                ", priorityLevel='" + priorityLevel + '\'' +
                ", actionRequired=" + actionRequired +
                ", product=" + (product != null ? product.getProductName() : null) +
                ", store=" + (store != null ? store.getAddress() : null) +
                ", createdAt=" + getCreatedAt() +
                ", readAt=" + readAt +
                '}';
    }

    // Lifecycle hooks for better state management
    @PrePersist
    protected void onCreate() {
        super.onCreate();
        if (actionRequired == null) {
            actionRequired = isPriorityActionRequired(priorityLevel);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        super.onUpdate();
        if (isRead && readAt == null) {
            readAt = LocalDateTime.now();
        }
    }
}