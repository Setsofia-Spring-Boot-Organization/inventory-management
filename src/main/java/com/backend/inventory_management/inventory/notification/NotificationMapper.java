package com.backend.inventory_management.inventory.notification;

import com.backend.inventory_management.core.store.StoreEntity;
import com.backend.inventory_management.inventory.catalog.product.ProductEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Map;

/**
 * Utility class to facilitate communication between AlertEntity and NotificationEntity
 * Provides mapping, conversion, and synchronization methods
 */
@Component
public class NotificationMapper {

    // Mapping configurations
    private static final Map<AlertEntity.AlertSeverity, NotificationEntity.PriorityLevel> SEVERITY_TO_PRIORITY_MAP = 
        new EnumMap<>(AlertEntity.AlertSeverity.class);
    
    private static final Map<AlertEntity.AlertType, NotificationEntity.AlertType> ALERT_TYPE_MAP = 
        new EnumMap<>(AlertEntity.AlertType.class);

    static {
        // Initialize severity to priority mapping
        SEVERITY_TO_PRIORITY_MAP.put(AlertEntity.AlertSeverity.LOW, NotificationEntity.PriorityLevel.LOW);
        SEVERITY_TO_PRIORITY_MAP.put(AlertEntity.AlertSeverity.MEDIUM, NotificationEntity.PriorityLevel.MEDIUM);
        SEVERITY_TO_PRIORITY_MAP.put(AlertEntity.AlertSeverity.HIGH, NotificationEntity.PriorityLevel.HIGH);
        SEVERITY_TO_PRIORITY_MAP.put(AlertEntity.AlertSeverity.CRITICAL, NotificationEntity.PriorityLevel.CRITICAL);

        // Initialize alert type mapping
        ALERT_TYPE_MAP.put(AlertEntity.AlertType.LOW_STOCK, NotificationEntity.AlertType.LOW_STOCK);
        ALERT_TYPE_MAP.put(AlertEntity.AlertType.OUT_OF_STOCK, NotificationEntity.AlertType.OUT_OF_STOCK);
        ALERT_TYPE_MAP.put(AlertEntity.AlertType.OVERSTOCK, NotificationEntity.AlertType.OVERSTOCK);
        ALERT_TYPE_MAP.put(AlertEntity.AlertType.EXPIRY_WARNING, NotificationEntity.AlertType.EXPIRY_WARNING);
        ALERT_TYPE_MAP.put(AlertEntity.AlertType.EXPIRED_PRODUCT, NotificationEntity.AlertType.EXPIRED_PRODUCT);
        ALERT_TYPE_MAP.put(AlertEntity.AlertType.SYSTEM_ERROR, NotificationEntity.AlertType.SYSTEM_NOTIFICATION);
        ALERT_TYPE_MAP.put(AlertEntity.AlertType.INVENTORY_DISCREPANCY, NotificationEntity.AlertType.INVENTORY_DISCREPANCY);
        ALERT_TYPE_MAP.put(AlertEntity.AlertType.SUPPLIER_DELAY, NotificationEntity.AlertType.SUPPLIER_DELAY);
        ALERT_TYPE_MAP.put(AlertEntity.AlertType.PRICE_CHANGE, NotificationEntity.AlertType.PRICE_CHANGE);
        ALERT_TYPE_MAP.put(AlertEntity.AlertType.CUSTOM, NotificationEntity.AlertType.INVENTORY_UPDATE);
    }

    /**
     * Convert AlertEntity to NotificationEntity for backward compatibility
     */
    public NotificationEntity convertAlertToNotification(AlertEntity alert) {
        if (alert == null) return null;

        NotificationEntity notification = new NotificationEntity();
        
        // Basic mapping
        notification.setAlertType(mapAlertType(alert.getAlertType()));
        notification.setTitle(alert.getTitle());
        notification.setMessage(alert.getMessage());
        notification.setPriorityLevel(mapSeverityToPriority(alert.getSeverity()));
        notification.setProduct(alert.getProduct());
        notification.setStore(alert.getStore());
        notification.setMetadata(alert.getReferenceData());
        
        // Set action required based on priority
        notification.setActionRequired(isActionRequired(alert.getSeverity()));
        
        // Set auto-dismiss based on alert type
        notification.setAutoDismiss(shouldAutoDismiss(alert.getAlertType()));
        
        // Set expiration based on alert type and severity
        notification.setExpiresAt(calculateExpiration(alert.getAlertType(), alert.getSeverity()));
        
        return notification;
    }

    /**
     * Update NotificationEntity from AlertEntity (for synchronization)
     */
    public void syncNotificationWithAlert(NotificationEntity notification, AlertEntity alert) {
        if (notification == null || alert == null) return;

        notification.setTitle(alert.getTitle());
        notification.setMessage(alert.getMessage());
        notification.setPriorityLevel(mapSeverityToPriority(alert.getSeverity()));
        notification.setMetadata(alert.getReferenceData());
        notification.setActionRequired(isActionRequired(alert.getSeverity()));
        
        // Update timestamps if alert is resolved
        if (alert.getStatus() == AlertEntity.AlertStatus.RESOLVED && notification.getAutoDismiss()) {
            notification.setIsRead(true);
            notification.setReadAt(alert.getResolvedAt());
        }
    }

    /**
     * Create a unified notification summary from both Alert and Notification entities
     */
    public NotificationSummary createUnifiedSummary(AlertEntity alert, NotificationEntity notification) {
        NotificationSummary summary = new NotificationSummary();
        
        if (alert != null) {
            summary.setId(alert.getId());
            summary.setTitle(alert.getTitle());
            summary.setMessage(alert.getMessage());
            summary.setType("ALERT");
            summary.setStatus(alert.getStatus().name());
            summary.setSeverity(alert.getSeverity().name());
            summary.setCreatedAt(alert.getCreatedAt());
            summary.setProduct(alert.getProduct());
            summary.setStore(alert.getStore());
            summary.setAssignedTo(alert.getAssignedTo());
        } else if (notification != null) {
            summary.setId(notification.getId());
            summary.setTitle(notification.getTitle());
            summary.setMessage(notification.getMessage());
            summary.setType("NOTIFICATION");
            summary.setStatus(notification.getIsRead() ? "READ" : "UNREAD");
            summary.setSeverity(notification.getPriorityLevel());
            summary.setCreatedAt(notification.getCreatedAt());
            summary.setProduct(notification.getProduct());
            summary.setStore(notification.getStore());
        }
        
        return summary;
    }

    // Mapping helper methods
    private String mapAlertType(AlertEntity.AlertType alertType) {
        NotificationEntity.AlertType mappedType = ALERT_TYPE_MAP.get(alertType);
        return mappedType != null ? mappedType.getValue() : NotificationEntity.AlertType.SYSTEM_NOTIFICATION.getValue();
    }

    private String mapSeverityToPriority(AlertEntity.AlertSeverity severity) {
        NotificationEntity.PriorityLevel priority = SEVERITY_TO_PRIORITY_MAP.get(severity);
        return priority != null ? priority.getValue() : NotificationEntity.PriorityLevel.MEDIUM.getValue();
    }

    private boolean isActionRequired(AlertEntity.AlertSeverity severity) {
        return severity == AlertEntity.AlertSeverity.HIGH || severity == AlertEntity.AlertSeverity.CRITICAL;
    }

    private boolean shouldAutoDismiss(AlertEntity.AlertType alertType) {
        return alertType == AlertEntity.AlertType.LOW_STOCK || 
               alertType == AlertEntity.AlertType.OVERSTOCK ||
               alertType == AlertEntity.AlertType.INVENTORY_DISCREPANCY;
    }

    private LocalDateTime calculateExpiration(AlertEntity.AlertType alertType, AlertEntity.AlertSeverity severity) {
        LocalDateTime now = LocalDateTime.now();
        
        // Different expiration rules based on type and severity
        return switch (alertType) {
            case EXPIRED_PRODUCT -> now.plusDays(30); // Long expiry for expired products
            case EXPIRY_WARNING -> now.plusDays(7);   // Week for expiry warnings
            case SYSTEM_ERROR -> severity == AlertEntity.AlertSeverity.CRITICAL ? 
                               now.plusHours(1) : now.plusHours(24); // Urgent for critical system errors
            case LOW_STOCK -> now.plusDays(3);        // 3 days for stock alerts
            case OVERSTOCK -> now.plusDays(7);        // Week for overstock
            default -> now.plusDays(1);               // Default 1 day
        };
    }

    /**
     * Determine if an alert should create a notification based on business rules
     */
    public boolean shouldCreateNotification(AlertEntity alert) {
        if (alert == null) return false;
        
        // Business rules for notification creation
        return switch (alert.getAlertType()) {
            case SYSTEM_ERROR -> alert.getSeverity() != AlertEntity.AlertSeverity.LOW;
            case EXPIRED_PRODUCT -> true; // Always notify for expired products
            case LOW_STOCK, OUT_OF_STOCK -> true; // Always notify for stock issues
            case EXPIRY_WARNING -> alert.getSeverity() == AlertEntity.AlertSeverity.HIGH ||
                                  alert.getSeverity() == AlertEntity.AlertSeverity.CRITICAL;
            case OVERSTOCK -> alert.getSeverity() != AlertEntity.AlertSeverity.LOW;
            case INVENTORY_DISCREPANCY, SUPPLIER_DELAY -> true;
            case PRICE_CHANGE -> false; // Don't create notifications for price changes
            case CUSTOM -> true; // Always notify for custom alerts
        };
    }

    /**
     * Determine notification channels based on alert properties
     */
    public NotificationChannels determineChannels(AlertEntity alert) {
        NotificationChannels channels = new NotificationChannels();
        
        if (alert.getSeverity() == AlertEntity.AlertSeverity.CRITICAL) {
            channels.setInApp(true);
            channels.setEmail(true);
            channels.setSms(true);
            channels.setPush(true);
        } else if (alert.getSeverity() == AlertEntity.AlertSeverity.HIGH) {
            channels.setInApp(true);
            channels.setEmail(true);
            channels.setPush(true);
        } else {
            channels.setInApp(true);
            if (alert.getAlertType() == AlertEntity.AlertType.LOW_STOCK || 
                alert.getAlertType() == AlertEntity.AlertType.EXPIRED_PRODUCT) {
                channels.setEmail(true);
            }
        }
        
        return channels;
    }

    // Inner classes for communication
    @Setter
    @Getter
    public static class NotificationSummary {
        // Getters and setters
        private Long id;
        private String title;
        private String message;
        private String type; // "ALERT" or "NOTIFICATION"
        private String status;
        private String severity;
        private LocalDateTime createdAt;
        private ProductEntity product;
        private StoreEntity store;
        private Object assignedTo; // Can be UserEntity

    }

    @Setter
    @Getter
    public static class NotificationChannels {
        // Getters and setters
        private boolean inApp = false;
        private boolean email = false;
        private boolean sms = false;
        private boolean push = false;

    }
}