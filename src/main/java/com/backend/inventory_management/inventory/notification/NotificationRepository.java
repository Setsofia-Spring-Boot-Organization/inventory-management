package com.backend.inventory_management.inventory.notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    List<NotificationEntity> findByIsReadFalse();
    List<NotificationEntity> findByIsReadFalseOrderByCreatedAtDesc();
    List<NotificationEntity> findAllByOrderByCreatedAtDesc();
    
    List<NotificationEntity> findByProductId(Long productId);

    List<NotificationEntity> findByStoreIdAndIsReadFalse(Long storeId);
    List<NotificationEntity> findByAlertType(String alertType);
    List<NotificationEntity> findByPriorityLevel(String priorityLevel);
    
    @Query("SELECT n FROM NotificationEntity n WHERE n.product.id = :productId AND n.store.id = :storeId AND n.alertType = :alertType AND n.isRead = false")
    List<NotificationEntity> findByProductIdAndStoreIdAndAlertTypeAndIsReadFalse(
            @Param("productId") Long productId, 
            @Param("storeId") Long storeId, 
            @Param("alertType") String alertType);
    
    @Query("SELECT n FROM NotificationEntity n WHERE n.store.id = :storeId AND n.isRead = false ORDER BY n.priorityLevel DESC, n.createdAt DESC")
    List<NotificationEntity> findUnreadByStoreOrderByPriority(@Param("storeId") Long storeId);
    
    @Query("SELECT COUNT(n) FROM NotificationEntity n WHERE n.isRead = false")
    Long countUnreadNotifications();
    
    @Query("SELECT COUNT(n) FROM NotificationEntity n WHERE n.store.id = :storeId AND n.isRead = false")
    Long countUnreadByStore(@Param("storeId") Long storeId);
    
    @Query("SELECT n FROM NotificationEntity n WHERE n.createdAt BETWEEN :startDate AND :endDate ORDER BY n.createdAt DESC")
    List<NotificationEntity> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT n FROM NotificationEntity n WHERE n.priorityLevel = 'CRITICAL' AND n.isRead = false ORDER BY n.createdAt DESC")
    List<NotificationEntity> findCriticalUnreadNotifications();
    
    // Pagination support
    Page<NotificationEntity> findByIsReadFalseOrderByCreatedAtDesc(Pageable pageable);
    Page<NotificationEntity> findByStoreIdOrderByCreatedAtDesc(Long storeId, Pageable pageable);
    Page<NotificationEntity> findByAlertTypeOrderByCreatedAtDesc(String alertType, Pageable pageable);
    
    // Cleanup old read notifications
    @Query("SELECT n FROM NotificationEntity n WHERE n.isRead = true AND n.updatedAt < :cutoffDate")
    List<NotificationEntity> findOldReadNotifications(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    // Statistics
    @Query("SELECT n.alertType, COUNT(n) FROM NotificationEntity n WHERE n.isRead = false GROUP BY n.alertType")
    List<Object[]> getUnreadNotificationStatsByType();
    
    @Query("SELECT n.priorityLevel, COUNT(n) FROM NotificationEntity n WHERE n.isRead = false GROUP BY n.priorityLevel")
    List<Object[]> getUnreadNotificationStatsByPriority();
}