package com.backend.inventory_management.inventory.notification;

import com.backend.inventory_management.common.constants.Constants;
import com.backend.inventory_management.core.store.StoreEntity;
import com.backend.inventory_management.core.user.UserEntity;
import com.backend.inventory_management.core.user.UserRepository;
import com.backend.inventory_management.inventory.catalog.product.ProductEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final AlertRepository alertRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Autowired
    public NotificationServiceImpl(AlertRepository alertRepository,
                                   NotificationRepository notificationRepository,
                                   UserRepository userRepository) {
        this.alertRepository = alertRepository;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    // Alert management methods
    @Override
    @Async
    public void createLowStockAlert(ProductEntity product, StoreEntity store, Integer currentStock) {
        // Check if similar alert already exists to avoid duplicates
        List<AlertEntity> existingAlerts = alertRepository.findExistingAlerts(
                product.getId(), store.getId(), AlertEntity.AlertType.LOW_STOCK);

        if (!existingAlerts.isEmpty()) {
            // Update existing alert instead of creating new one
            AlertEntity existingAlert = existingAlerts.get(0);
            existingAlert.setMessage(String.format("Product '%s' (SKU: %s) is running low. Current stock: %d, Reorder point: %d",
                    product.getProductName(), product.getSku(), currentStock, product.getReorderPoint()));
            existingAlert.setReferenceData(String.format("{\"currentStock\":%d,\"reorderPoint\":%d}",
                    currentStock, product.getReorderPoint()));
            alertRepository.save(existingAlert);
            return;
        }

        AlertEntity alert = getEntity(product, store, currentStock);

        alertRepository.save(alert);

        // Create corresponding legacy notification for backward compatibility
        createNotification(
                NotificationEntity.AlertType.LOW_STOCK.getValue(),
                alert.getTitle(),
                alert.getMessage(),
                mapSeverityToPriority(alert.getSeverity()),
                product,
                store
        );

        // Notify inventory managers
        sendNotificationToRole(Constants.Role.MANAGER, alert.getTitle(), alert.getMessage());
        sendNotificationToRole(Constants.Role.INVENTORY_CLERK, alert.getTitle(), alert.getMessage());
    }

    private static AlertEntity getEntity(ProductEntity product, StoreEntity store, Integer currentStock) {
        AlertEntity alert = new AlertEntity();
        alert.setAlertType(AlertEntity.AlertType.LOW_STOCK);
        alert.setSeverity(AlertEntity.AlertSeverity.HIGH);
        alert.setTitle("Low Stock Alert");
        alert.setMessage(String.format("Product '%s' (SKU: %s) is running low. Current stock: %d, Reorder point: %d",
                product.getProductName(), product.getSku(), currentStock, product.getReorderPoint()));
        alert.setStore(store);
        alert.setProduct(product);
        alert.setReferenceData(String.format("{\"currentStock\":%d,\"reorderPoint\":%d}",
                currentStock, product.getReorderPoint()));
        return alert;
    }

    @Override
    @Async
    public void createExpiryAlert(ProductEntity product, StoreEntity store, int daysToExpiry) {
        AlertEntity.AlertSeverity severity = determineSeverityByDaysToExpiry(daysToExpiry);
        AlertEntity.AlertType alertType = daysToExpiry <= 0 ?
                AlertEntity.AlertType.EXPIRED_PRODUCT : AlertEntity.AlertType.EXPIRY_WARNING;

        // Check for existing alerts
        List<AlertEntity> existingAlerts = alertRepository.findExistingAlerts(
                product.getId(), store.getId(), alertType);

        if (!existingAlerts.isEmpty()) {
            AlertEntity existingAlert = existingAlerts.get(0);
            existingAlert.setSeverity(severity);
            existingAlert.setMessage(buildExpiryMessage(product, daysToExpiry));
            existingAlert.setReferenceData(String.format("{\"daysToExpiry\":%d}", daysToExpiry));
            alertRepository.save(existingAlert);
            return;
        }

        AlertEntity alert = new AlertEntity();
        alert.setAlertType(alertType);
        alert.setSeverity(severity);
        alert.setTitle(daysToExpiry <= 0 ? "Product Expired" : "Product Expiry Warning");
        alert.setMessage(buildExpiryMessage(product, daysToExpiry));
        alert.setStore(store);
        alert.setProduct(product);
        alert.setReferenceData(String.format("{\"daysToExpiry\":%d}", daysToExpiry));

        alertRepository.save(alert);

        // Create corresponding legacy notification
        String legacyAlertType = daysToExpiry <= 0 ?
                NotificationEntity.AlertType.EXPIRED_PRODUCT.getValue() :
                NotificationEntity.AlertType.EXPIRY_WARNING.getValue();

        createNotification(
                legacyAlertType,
                alert.getTitle(),
                alert.getMessage(),
                mapSeverityToPriority(alert.getSeverity()),
                product,
                store
        );

        // Notify relevant staff based on severity
        sendNotificationToRole(Constants.Role.MANAGER, alert.getTitle(), alert.getMessage());
        if (severity == AlertEntity.AlertSeverity.HIGH || severity == AlertEntity.AlertSeverity.CRITICAL) {
            sendNotificationToRole(Constants.Role.INVENTORY_CLERK, alert.getTitle(), alert.getMessage());
        }
    }

    @Override
    @Async
    public void createOverstockAlert(ProductEntity product, StoreEntity store, Integer currentStock) {
        // Check for existing alerts
        List<AlertEntity> existingAlerts = alertRepository.findExistingAlerts(
                product.getId(), store.getId(), AlertEntity.AlertType.OVERSTOCK);

        if (!existingAlerts.isEmpty()) {
            AlertEntity existingAlert = existingAlerts.get(0);
            existingAlert.setMessage(String.format("Product '%s' (SKU: %s) is overstocked. Current stock: %d, Max level: %d",
                    product.getProductName(), product.getSku(), currentStock, product.getMaxStockLevel()));
            existingAlert.setReferenceData(String.format("{\"currentStock\":%d,\"maxLevel\":%d}",
                    currentStock, product.getMaxStockLevel()));
            alertRepository.save(existingAlert);
            return;
        }

        AlertEntity alert = getAlertEntity(product, store, currentStock);

        alertRepository.save(alert);

        // Create corresponding legacy notification
        createNotification(
                NotificationEntity.AlertType.OVERSTOCK.getValue(),
                alert.getTitle(),
                alert.getMessage(),
                NotificationEntity.PriorityLevel.MEDIUM.getValue(),
                product,
                store
        );

        // Notify managers
        sendNotificationToRole(Constants.Role.MANAGER, alert.getTitle(), alert.getMessage());
    }

    private static AlertEntity getAlertEntity(ProductEntity product, StoreEntity store, Integer currentStock) {
        AlertEntity alert = new AlertEntity();
        alert.setAlertType(AlertEntity.AlertType.OVERSTOCK);
        alert.setSeverity(AlertEntity.AlertSeverity.MEDIUM);
        alert.setTitle("Overstock Alert");
        alert.setMessage(String.format("Product '%s' (SKU: %s) is overstocked. Current stock: %d, Max level: %d",
                product.getProductName(), product.getSku(), currentStock, product.getMaxStockLevel()));
        alert.setStore(store);
        alert.setProduct(product);
        alert.setReferenceData(String.format("{\"currentStock\":%d,\"maxLevel\":%d}",
                currentStock, product.getMaxStockLevel()));
        return alert;
    }

    @Override
    @Async
    public void createSystemAlert(String title, String message, AlertEntity.AlertSeverity severity) {
        AlertEntity alert = new AlertEntity();
        alert.setAlertType(AlertEntity.AlertType.SYSTEM_ERROR);
        alert.setSeverity(severity);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setNotificationSent(true); // Mark as sent immediately for system alerts

        alertRepository.save(alert);

        // Create corresponding legacy notification
        createNotification(
                NotificationEntity.AlertType.SYSTEM_NOTIFICATION.getValue(),
                title,
                message,
                mapSeverityToPriority(severity),
                null,
                null
        );

        // Notify admins for system alerts
        sendNotificationToRole(Constants.Role.ADMIN, title, message);
    }

    // Notification sending methods
    @Override
    @Async
    public void sendNotificationToUser(UserEntity user, String subject, String message) {
        // Implementation would integrate with email service, SMS service, or push notifications
        // For now, just log the notification
        System.out.printf("NOTIFICATION TO %s (%s): %s - %s%n",
                user.getUsername(), user.getEmail(), subject, message);
    }

    @Override
    @Async
    public void sendNotificationToRole(Constants.Role role, String subject, String message) {
        List<UserEntity> users = userRepository.findByRole(role);
        if (users.isEmpty()) {
            System.out.printf("WARNING: No users found for role %s%n", role);
            return;
        }
        users.forEach(user -> sendNotificationToUser(user, subject, message));
    }

    // Alert retrieval methods with proper status handling
    @Override
    @Transactional(readOnly = true)
    public List<AlertEntity> getActiveAlertsByStore(Long storeId) {
        return alertRepository.findActiveAlertsByStoreOrderBySeverity(storeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertEntity> getUnreadAlertsByUser(Long userId) {
        return alertRepository.findUnreadAlertsByUser(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertEntity> getAllAlerts() {
        return alertRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertEntity> getAlertsByType(AlertEntity.AlertType alertType) {
        return alertRepository.findActiveAlertsByType(alertType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertEntity> getCriticalAlerts() {
        return alertRepository.findCriticalAlerts();
    }

    // Alert management methods with proper state transitions
    @Override
    public void markAlertAsRead(Long alertId, Long userId) {
        AlertEntity alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found"));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Only update if not already acknowledged or resolved
        if (alert.getStatus() == AlertEntity.AlertStatus.NEW ||
                alert.getStatus() == AlertEntity.AlertStatus.IN_PROGRESS) {
            alert.setStatus(AlertEntity.AlertStatus.ACKNOWLEDGED);
            alert.setAcknowledgedAt(LocalDateTime.now());
            alert.setAcknowledgedBy(user);
            alertRepository.save(alert);
        }
    }

    @Override
    public void resolveAlert(Long alertId, Long userId, String resolutionNotes) {
        AlertEntity alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found"));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        alert.setStatus(AlertEntity.AlertStatus.RESOLVED);
        alert.setResolvedAt(LocalDateTime.now());
        alert.setResolvedBy(user);
        alert.setResolutionNotes(resolutionNotes);

        // If not previously acknowledged, set acknowledgment info as well
        if (alert.getAcknowledgedAt() == null) {
            alert.setAcknowledgedAt(LocalDateTime.now());
            alert.setAcknowledgedBy(user);
        }

        alertRepository.save(alert);
    }

    @Override
    public void dismissAlert(Long alertId, Long userId) {
        AlertEntity alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found"));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        alert.setStatus(AlertEntity.AlertStatus.DISMISSED);
        alert.setAcknowledgedAt(LocalDateTime.now());
        alert.setAcknowledgedBy(user);

        alertRepository.save(alert);
    }

    @Override
    public void assignAlert(Long alertId, Long userId) {
        AlertEntity alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found"));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        alert.setAssignedTo(user);
        // Add missing field - need to add this to AlertEntity if not present
        // alert.setAssignedAt(LocalDateTime.now());

        // Update status appropriately
        if (alert.getStatus() == AlertEntity.AlertStatus.NEW) {
            alert.setStatus(AlertEntity.AlertStatus.IN_PROGRESS);
        }

        alertRepository.save(alert);
    }

    // Legacy notification methods with proper integration
    @Override
    public void createNotification(String alertType, String title, String message,
                                   String priorityLevel, ProductEntity product, StoreEntity store) {
        NotificationEntity notification = new NotificationEntity();
        notification.setAlertType(alertType);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setPriorityLevel(priorityLevel);
        notification.setProduct(product);
        notification.setStore(store);
        notification.setIsRead(false);
        notification.setActionRequired(isPriorityActionRequired(priorityLevel));

        notificationRepository.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationEntity> getUnreadNotifications() {
        return notificationRepository.findByIsReadFalseOrderByCreatedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationEntity> getAllNotifications() {
        return notificationRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationEntity> getNotificationsByStore(Long storeId) {
        return notificationRepository.findUnreadByStoreOrderByPriority(storeId);
    }

    @Override
    public NotificationEntity markAsRead(Long notificationId) {
        NotificationEntity notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setIsRead(true);
        // Note: Need to add readAt field to NotificationEntity or use updatedAt

        return notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead() {
        List<NotificationEntity> unreadNotifications = notificationRepository.findByIsReadFalse();
        LocalDateTime now = LocalDateTime.now();

        unreadNotifications.forEach(notification -> {
            notification.setIsRead(true);
            // notification.setReadAt(now); // Add this field to NotificationEntity
        });

        notificationRepository.saveAll(unreadNotifications);
    }

    @Override
    public void markAllAsReadForStore(Long storeId) {
        List<NotificationEntity> unreadNotifications = notificationRepository.findByStoreIdAndIsReadFalse(storeId);
        LocalDateTime now = LocalDateTime.now();

        unreadNotifications.forEach(notification -> {
            notification.setIsRead(true);
            // notification.setReadAt(now); // Add this field to NotificationEntity
        });

        notificationRepository.saveAll(unreadNotifications);
    }

    // Statistics and reporting with proper counting
    @Override
    @Transactional(readOnly = true)
    public Long getUnreadNotificationCount() {
        return notificationRepository.countUnreadNotifications();
    }

    @Override
    @Transactional(readOnly = true)
    public Long getUnreadNotificationCountByStore(Long storeId) {
        return notificationRepository.countUnreadByStore(storeId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getActiveAlertCount() {
        return alertRepository.countAllActiveAlerts();
    }

    @Override
    @Transactional(readOnly = true)
    public Long getActiveAlertCountByStore(Long storeId) {
        return alertRepository.countActiveAlertsByStore(storeId);
    }

    // Cleanup methods with proper date handling
    @Override
    public void cleanupOldNotifications(LocalDateTime cutoffDate) {
        List<NotificationEntity> oldNotifications = notificationRepository.findOldReadNotifications(cutoffDate);
        notificationRepository.deleteAll(oldNotifications);
    }

    @Override
    public void cleanupOldResolvedAlerts(LocalDateTime cutoffDate) {
        List<AlertEntity> oldResolvedAlerts = alertRepository.findOldResolvedAlerts(cutoffDate);
        alertRepository.deleteAll(oldResolvedAlerts);
    }

    // Helper methods for better class communication
    private String mapSeverityToPriority(AlertEntity.AlertSeverity severity) {
        return switch (severity) {
            case LOW -> NotificationEntity.PriorityLevel.LOW.getValue();
            case MEDIUM -> NotificationEntity.PriorityLevel.MEDIUM.getValue();
            case HIGH -> NotificationEntity.PriorityLevel.HIGH.getValue();
            case CRITICAL -> NotificationEntity.PriorityLevel.CRITICAL.getValue();
        };
    }

    private AlertEntity.AlertSeverity determineSeverityByDaysToExpiry(int daysToExpiry) {
        if (daysToExpiry <= 0) return AlertEntity.AlertSeverity.CRITICAL;
        if (daysToExpiry <= 3) return AlertEntity.AlertSeverity.HIGH;
        if (daysToExpiry <= 7) return AlertEntity.AlertSeverity.MEDIUM;
        return AlertEntity.AlertSeverity.LOW;
    }

    private String buildExpiryMessage(ProductEntity product, int daysToExpiry) {
        return String.format("Product '%s' (SKU: %s) %s",
                product.getProductName(),
                product.getSku(),
                daysToExpiry <= 0 ? "has expired" : String.format("expires in %d days", daysToExpiry));
    }

    private boolean isPriorityActionRequired(String priorityLevel) {
        return NotificationEntity.PriorityLevel.HIGH.getValue().equals(priorityLevel) ||
                NotificationEntity.PriorityLevel.CRITICAL.getValue().equals(priorityLevel);
    }
}