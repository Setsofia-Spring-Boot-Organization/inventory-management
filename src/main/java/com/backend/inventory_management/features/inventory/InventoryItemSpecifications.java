package com.backend.inventory_management.features.inventory;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class InventoryItemSpecifications {
    
    public static Specification<InventoryItem> searchByKeyword(String searchKey) {
        return (root, query, criteriaBuilder) -> {
            if (searchKey == null || searchKey.trim().isEmpty()) {
                return criteriaBuilder.conjunction(); // Always true
            }
            
            String likePattern = "%" + searchKey.toLowerCase() + "%";
            
            Predicate namePredicate = criteriaBuilder.like(
                criteriaBuilder.lower(root.get("name")), likePattern);
            
            Predicate categoryPredicate = criteriaBuilder.like(
                criteriaBuilder.lower(root.get("category")), likePattern);
            
            Predicate supplierPredicate = criteriaBuilder.like(
                criteriaBuilder.lower(root.get("supplier")), likePattern);
            
            return criteriaBuilder.or(namePredicate, categoryPredicate, supplierPredicate);
        };
    }
}