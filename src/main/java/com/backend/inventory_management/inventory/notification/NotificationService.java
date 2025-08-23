package com.backend.inventory_management.inventory.notification;

import com.backend.inventory_management.common.constants.Constants;
import com.backend.inventory_management.core.store.StoreEntity;
import com.backend.inventory_management.core.user.UserEntity;
import com.backend.inventory_management.inventory.catalog.product.ProductEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationService {
    
    // Alert management methods
    void createLowStockAlert(ProductEntity product, StoreEntity store, Integer currentStock);
    void createExpiryAlert(ProductEntity product, StoreEntity store, int daysToExpiry);
    void createOverstockAlert(ProductEntity product, StoreEntity store, Integer currentStock);
    void createSystemAlert(String title, String message, AlertEntity.AlertSeverity severity);
    
    // Notification sending methods
    void sendNotificationToUser(UserEntity user, String subject, String message);
    void sendNotificationToRole(Constants.Role role, String subject, String message);
    
    // Alert retrieval methods
    List<AlertEntity> getActiveAlertsByStore(Long storeId);
    List<AlertEntity> getUnreadAlertsByUser(Long userId);
    List<AlertEntity> getAllAlerts();
    List<AlertEntity> getAlertsByType(AlertEntity.AlertType alertType);
    List<AlertEntity> getCriticalAlerts();
    
    // Alert management methods
    void markAlertAsRead(Long alertId, Long userId);
    void resolveAlert(Long alertId, Long userId, String resolutionNotes);
    void dismissAlert(Long alertId, Long userId);
    void assignAlert(Long alertId, Long userId);
    
    // Simple notification methods (for backward compatibility)
    void createNotification(String alertType, String title, String message,
                            String priorityLevel, ProductEntity product, StoreEntity store);
    List<NotificationEntity> getUnreadNotifications();
    List<NotificationEntity> getAllNotifications();
    List<NotificationEntity> getNotificationsByStore(Long storeId);
    NotificationEntity markAsRead(Long notificationId);
    void markAllAsRead();
    void markAllAsReadForStore(Long storeId);
    
    // Statistics and reporting
    Long getUnreadNotificationCount();
    Long getUnreadNotificationCountByStore(Long storeId);
    Long getActiveAlertCount();
    Long getActiveAlertCountByStore(Long storeId);
    
    // Cleanup methods
    void cleanupOldNotifications(LocalDateTime cutoffDate);
    void cleanupOldResolvedAlerts(LocalDateTime cutoffDate);
}