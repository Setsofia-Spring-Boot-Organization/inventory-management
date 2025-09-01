// InventoryItemRepository.java
package com.backend.inventory_management.features.inventory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;


@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long>, JpaSpecificationExecutor<InventoryItem> {
    
    // Search functionality
    @Query("SELECT i FROM InventoryItem i WHERE " +
            "(:searchKey IS NULL OR " +
            "LOWER(CONCAT(i.name, ' ', CAST(i.category AS string), ' ', CAST(i.supplier AS string))) " +
            "LIKE LOWER(CONCAT('%', :searchKey, '%')))")
    Page<InventoryItem> search(
            @Param("searchKey") String searchKey,
            Pageable pageable
    );

    
    // Find by category
    List<InventoryItem> findByCategory(String category);
    
    // Find by supplier
    List<InventoryItem> findBySupplier(String supplier);
    
    // Find by status
    List<InventoryItem> findByStatus(StockStatus status);
    
    // Find low stock items
    @Query("SELECT i FROM InventoryItem i WHERE i.quantity < i.minStockLevel")
    List<InventoryItem> findLowStockItems();
    
    // Find out of stock items
    @Query("SELECT i FROM InventoryItem i WHERE i.quantity = 0")
    List<InventoryItem> findOutOfStockItems();
    
    // Get total inventory value
    @Query("SELECT SUM(i.quantity * i.price) FROM InventoryItem i")
    BigDecimal getTotalInventoryValue();
    
    // Get count by status
    long countByStatus(StockStatus status);
    
    // Get distinct categories
    @Query("SELECT DISTINCT i.category FROM InventoryItem i ORDER BY i.category")
    List<String> findDistinctCategories();
    
    // Get distinct suppliers
    @Query("SELECT DISTINCT i.supplier FROM InventoryItem i ORDER BY i.supplier")
    List<String> findDistinctSuppliers();
    
    // Check if item name exists (for validation)
    boolean existsByNameIgnoreCase(String name);
    
    // Find items that need reordering
    @Query("SELECT i FROM InventoryItem i WHERE i.quantity <= i.minStockLevel")
    List<InventoryItem> findItemsNeedingReorder();
}
