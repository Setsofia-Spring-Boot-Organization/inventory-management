package com.backend.inventory_management.inventory.sales;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionLineRepository extends JpaRepository<TransactionLineEntity, Long> {
}