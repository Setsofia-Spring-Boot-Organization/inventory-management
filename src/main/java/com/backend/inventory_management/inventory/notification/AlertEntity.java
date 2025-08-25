package com.backend.inventory_management.inventory.notification;

import com.backend.inventory_management.common.entity.BaseEntity;
import com.backend.inventory_management.core.store.StoreEntity;
import com.backend.inventory_management.core.user.UserEntity;
import com.backend.inventory_management.inventory.catalog.product.ProductEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "alerts")
public class AlertEntity extends BaseEntity {

    // Getters and setters
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Alert type is required")
    @Column(name = "alert_type", nullable = false)
    private AlertType alertType;
    
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Alert severity is required")
    @Column(nullable = false)
    private AlertSeverity severity = AlertSeverity.MEDIUM;
    
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private AlertStatus status = AlertStatus.NEW;
    
    @NotBlank(message = "Alert title is required")
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String message;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private StoreEntity store;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductEntity product;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private UserEntity assignedTo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acknowledged_by")
    private UserEntity acknowledgedBy;
    
    @Column(name = "acknowledged_at")
    private LocalDateTime acknowledgedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by")
    private UserEntity resolvedBy;
    
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
    
    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;
    
    @Column(name = "reference_data", columnDefinition = "TEXT")
    private String referenceData; // JSON data for additional context
    
    @Column(name = "notification_sent")
    private Boolean notificationSent = false;
    
    public enum AlertType {
        LOW_STOCK,
        OUT_OF_STOCK,
        OVERSTOCK,
        EXPIRY_WARNING,
        EXPIRED_PRODUCT,
        SYSTEM_ERROR,
        INVENTORY_DISCREPANCY,
        SUPPLIER_DELAY,
        PRICE_CHANGE,
        CUSTOM
    }
    
    public enum AlertSeverity {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }
    
    public enum AlertStatus {
        NEW,
        ACKNOWLEDGED,
        IN_PROGRESS,
        RESOLVED,
        DISMISSED
    }

    // Constructors
    public AlertEntity() {}

    public AlertEntity(AlertType alertType, AlertSeverity severity, String title, String message) {
        this.alertType = alertType;
        this.severity = severity;
        this.title = title;
        this.message = message;
    }

    @Override
    public String toString() {
        return "AlertEntity{" +
                "id=" + getId() +
                ", alertType=" + alertType +
                ", severity=" + severity +
                ", status=" + status +
                ", title='" + title + '\'' +
                ", store=" + (store != null ? store.getAddress() : null) +
                ", product=" + (product != null ? product.getProductName() : null) +
                '}';
    }
}