package com.backend.inventory_management.inventory.notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<AlertEntity, Long> {

    // Basic queries
    List<AlertEntity> findByAlertType(AlertEntity.AlertType alertType);
    List<AlertEntity> findByStatus(AlertEntity.AlertStatus status);
    List<AlertEntity> findByStoreId(Long storeId);
    List<AlertEntity> findByProductId(Long productId);
    List<AlertEntity> findByStoreIdAndStatus(Long storeId, AlertEntity.AlertStatus status);
    List<AlertEntity> findBySeverity(AlertEntity.AlertSeverity severity);
    List<AlertEntity> findByAssignedToId(Long userId);

    // Enhanced queries for better communication
    @Query("SELECT a FROM AlertEntity a WHERE a.assignedTo.id = :userId AND a.status IN ('NEW', 'ACKNOWLEDGED', 'IN_PROGRESS') ORDER BY a.severity DESC, a.createdAt DESC")
    List<AlertEntity> findUnreadAlertsByUser(@Param("userId") Long userId);

    @Query("SELECT a FROM AlertEntity a WHERE a.store.id = :storeId AND a.status IN ('NEW', 'IN_PROGRESS') ORDER BY a.severity DESC, a.createdAt DESC")
    List<AlertEntity> findActiveAlertsByStoreOrderBySeverity(@Param("storeId") Long storeId);

    @Query("SELECT a FROM AlertEntity a WHERE a.createdAt BETWEEN :startDate AND :endDate ORDER BY a.createdAt DESC")
    List<AlertEntity> findAlertsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Count queries for statistics
    @Query("SELECT COUNT(a) FROM AlertEntity a WHERE a.store.id = :storeId AND a.status IN ('NEW', 'IN_PROGRESS')")
    Long countActiveAlertsByStore(@Param("storeId") Long storeId);

    @Query("SELECT COUNT(a) FROM AlertEntity a WHERE a.status IN ('NEW', 'IN_PROGRESS')")
    Long countAllActiveAlerts();

    @Query("SELECT COUNT(a) FROM AlertEntity a WHERE a.assignedTo.id = :userId AND a.status IN ('NEW', 'ACKNOWLEDGED', 'IN_PROGRESS')")
    Long countUnreadAlertsByUser(@Param("userId") Long userId);

    // Critical alerts
    @Query("SELECT a FROM AlertEntity a WHERE a.status IN ('NEW', 'IN_PROGRESS') AND a.severity IN ('HIGH', 'CRITICAL') ORDER BY a.severity DESC, a.createdAt DESC")
    List<AlertEntity> findCriticalAlerts();

    // Existing alerts check (to avoid duplicates)
    @Query("SELECT a FROM AlertEntity a WHERE a.product.id = :productId AND a.store.id = :storeId AND a.alertType = :alertType AND a.status IN ('NEW', 'ACKNOWLEDGED', 'IN_PROGRESS')")
    List<AlertEntity> findExistingAlerts(@Param("productId") Long productId, @Param("storeId") Long storeId, @Param("alertType") AlertEntity.AlertType alertType);

    // Active alerts by type
    @Query("SELECT a FROM AlertEntity a WHERE a.alertType = :alertType AND a.status IN ('NEW', 'IN_PROGRESS') ORDER BY a.createdAt DESC")
    List<AlertEntity> findActiveAlertsByType(@Param("alertType") AlertEntity.AlertType alertType);

    // Enhanced queries for service communication
    @Query("SELECT a FROM AlertEntity a WHERE a.severity = :severity AND a.status = :status ORDER BY a.createdAt DESC")
    List<AlertEntity> findBySeverityAndStatus(@Param("severity") AlertEntity.AlertSeverity severity, @Param("status") AlertEntity.AlertStatus status);

    @Query("SELECT COUNT(a) FROM AlertEntity a WHERE a.status IN :statuses")
    Long countByStatusIn(@Param("statuses") List<AlertEntity.AlertStatus> statuses);

    @Query("SELECT COUNT(a) FROM AlertEntity a WHERE a.store.id = :storeId AND a.status IN :statuses")
    Long countByStoreIdAndStatusIn(@Param("storeId") Long storeId, @Param("statuses") List<AlertEntity.AlertStatus> statuses);

    @Query("SELECT a FROM AlertEntity a WHERE a.status = :status AND a.resolvedAt < :cutoffDate")
    List<AlertEntity> findByStatusAndResolvedAtBefore(@Param("status") AlertEntity.AlertStatus status, @Param("cutoffDate") LocalDateTime cutoffDate);

    // Pagination support
    Page<AlertEntity> findByStoreIdOrderByCreatedAtDesc(Long storeId, Pageable pageable);
    Page<AlertEntity> findByStatusOrderBySeverityDescCreatedAtDesc(AlertEntity.AlertStatus status, Pageable pageable);
    Page<AlertEntity> findByAssignedToIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // Auto-cleanup old resolved alerts (for maintenance)
    @Query("SELECT a FROM AlertEntity a WHERE a.status = 'RESOLVED' AND a.resolvedAt < :cutoffDate")
    List<AlertEntity> findOldResolvedAlerts(@Param("cutoffDate") LocalDateTime cutoffDate);

    // Performance and statistics queries
    @Query("SELECT a.alertType, COUNT(a) FROM AlertEntity a WHERE a.status IN ('NEW', 'IN_PROGRESS') GROUP BY a.alertType")
    List<Object[]> getAlertTypeStatistics();

    @Query("SELECT a.severity, COUNT(a) FROM AlertEntity a WHERE a.status IN ('NEW', 'IN_PROGRESS') GROUP BY a.severity")
    List<Object[]> getAlertSeverityStatistics();

    // Store-specific statistics
    @Query("SELECT a.alertType, COUNT(a) FROM AlertEntity a WHERE a.store.id = :storeId AND a.status IN ('NEW', 'IN_PROGRESS') GROUP BY a.alertType")
    List<Object[]> getAlertTypeStatisticsByStore(@Param("storeId") Long storeId);

    // Time-based queries for reporting
    @Query("SELECT a FROM AlertEntity a WHERE a.createdAt >= :since ORDER BY a.createdAt DESC")
    List<AlertEntity> findRecentAlerts(@Param("since") LocalDateTime since);

    @Query("SELECT COUNT(a) FROM AlertEntity a WHERE a.createdAt >= :since")
    Long countRecentAlerts(@Param("since") LocalDateTime since);

    // Notification status tracking
    @Query("SELECT a FROM AlertEntity a WHERE a.notificationSent = false AND a.status = 'NEW'")
    List<AlertEntity> findAlertsNeedingNotification();

    @Modifying
    @Query("UPDATE AlertEntity a SET a.notificationSent = true WHERE a.id IN :alertIds")
    void markNotificationsSent(@Param("alertIds") List<Long> alertIds);
}