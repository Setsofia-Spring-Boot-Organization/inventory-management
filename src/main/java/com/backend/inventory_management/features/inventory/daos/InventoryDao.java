package com.backend.inventory_management.features.inventory.daos;

import com.backend.inventory_management.features.inventory.InventoryItem;
import com.backend.inventory_management.features.inventory.StockStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class InventoryDao {
    
    private final EntityManager entityManager;
    
    public Page<InventoryItem> findItemsWithAdvancedFilters(
            String searchTerm, String category, String supplier,
            StockStatus status, Double minPrice, Double maxPrice,
            Pageable pageable) {
        
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<InventoryItem> query = cb.createQuery(InventoryItem.class);
        Root<InventoryItem> root = query.from(InventoryItem.class);
        
        List<Predicate> predicates = new ArrayList<>();
        
        // Search term filter
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            String searchPattern = "%" + searchTerm.toLowerCase() + "%";
            Predicate namePredicate = cb.like(cb.lower(root.get("name")), searchPattern);
            Predicate categoryPredicate = cb.like(cb.lower(root.get("category")), searchPattern);
            Predicate supplierPredicate = cb.like(cb.lower(root.get("supplier")), searchPattern);
            predicates.add(cb.or(namePredicate, categoryPredicate, supplierPredicate));
        }
        
        // Category filter
        if (category != null && !category.trim().isEmpty()) {
            predicates.add(cb.equal(root.get("category"), category));
        }
        
        // Supplier filter
        if (supplier != null && !supplier.trim().isEmpty()) {
            predicates.add(cb.equal(root.get("supplier"), supplier));
        }
        
        // Status filter
        if (status != null) {
            predicates.add(cb.equal(root.get("status"), status));
        }
        
        // Price range filters
        if (minPrice != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
        }
        
        if (maxPrice != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
        }
        
        query.where(predicates.toArray(new Predicate[0]));
        
        // Apply sorting
        if (pageable.getSort().isSorted()) {
            List<Order> orders = new ArrayList<>();
            pageable.getSort().forEach(sortOrder -> {
                if (sortOrder.isAscending()) {
                    orders.add(cb.asc(root.get(sortOrder.getProperty())));
                } else {
                    orders.add(cb.desc(root.get(sortOrder.getProperty())));
                }
            });
            query.orderBy(orders);
        }
        
        TypedQuery<InventoryItem> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());
        
        List<InventoryItem> items = typedQuery.getResultList();
        
        // Count query for pagination
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<InventoryItem> countRoot = countQuery.from(InventoryItem.class);
        countQuery.select(cb.count(countRoot));
        countQuery.where(predicates.toArray(new Predicate[0]));
        
        Long total = entityManager.createQuery(countQuery).getSingleResult();
        
        return new PageImpl<>(items, pageable, total);
    }
}
