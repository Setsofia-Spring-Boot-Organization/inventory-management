package com.backend.inventory_management.inventory.sales;

import com.backend.inventory_management.common.dto.BaseResponse;

import java.util.List;

public interface TransactionService {
    BaseResponse<TransactionEntity> createTransaction(TransactionEntity transaction);
    BaseResponse<TransactionEntity> getTransactionById(Long id);
    BaseResponse<List<TransactionEntity>> getAllTransactions();
    BaseResponse<TransactionEntity> updateTransaction(Long id, TransactionEntity transaction);
    BaseResponse<Void> deleteTransaction(Long id);
    BaseResponse<Boolean> existsByTransactionNumber(String transactionNumber);
}
