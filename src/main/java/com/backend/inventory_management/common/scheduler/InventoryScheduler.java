package com.backend.inventory_management.common.scheduler;

import com.backend.inventory_management.inventory.notification.NotificationService;
import com.backend.inventory_management.inventory.stock.StockEntity;
import com.backend.inventory_management.inventory.stock.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class InventoryScheduler {

    private final StockService stockService;
    private final NotificationService notificationService;

    @Autowired
    public InventoryScheduler(StockService stockService, NotificationService notificationService) {
        this.stockService = stockService;
        this.notificationService = notificationService;
    }

    @Scheduled(fixedRate = 3600000) // Every hour
    public void checkLowStockLevels() {
        List<StockEntity> lowStockItems = stockService.findLowStockItems();

        for (StockEntity stock : lowStockItems) {
            notificationService.createLowStockAlert(
                    stock.getProduct(),
                    stock.getWarehouse().getStore(),
                    stock.getCurrentQuantity()
            );
        }
    }

    @Scheduled(cron = "0 0 9 * * ?") // Daily at 9 AM
    public void checkExpiringItems() {
        List<StockEntity> expiringItems = stockService.findExpiringItems(30); // Next 30 days

        for (StockEntity stock : expiringItems) {
            if (stock.getExpiryDate() != null) {
                long daysToExpiry = ChronoUnit.DAYS.between(LocalDate.now(), stock.getExpiryDate());

                String alertType = daysToExpiry <= 7 ? "EXPIRED_PRODUCT" : "EXPIRY_WARNING";
                String priority = daysToExpiry <= 7 ? "CRITICAL" : "HIGH";

                // Create expiry notification (you can implement this method in NotificationService)
                // notificationService.createExpiryAlert(stock, daysToExpiry);
            }
        }
    }

    @Scheduled(cron = "0 0 2 * * ?") // Daily at 2 AM
    public void cleanupExpiredNotifications() {
        // Clean up old read notifications (older than 30 days)
        // You can implement this in NotificationService if needed
    }
}