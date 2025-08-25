package com.backend.inventory_management.inventory.sales;

import com.backend.inventory_management.common.dto.BaseResponse;
import com.backend.inventory_management.common.exception.DuplicateResourceException;
import com.backend.inventory_management.common.exception.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionLineRepository transactionLineRepository;

    @Override
    public BaseResponse<TransactionEntity> createTransaction(TransactionEntity transaction) {
        if (transactionRepository.existsByTransactionNumber(transaction.getTransactionNumber())) {
            throw new DuplicateResourceException("Transaction number already exists");
        }
        transaction.getTransactionLines().forEach(line -> line.setTransaction(transaction));
        transaction.calculateTotals();
        TransactionEntity saved = transactionRepository.save(transaction);
        return BaseResponse.success("Transaction created successfully", saved);
    }

    @Override
    public BaseResponse<TransactionEntity> getTransactionById(Long id) {
        TransactionEntity transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found with id: " + id));
        return BaseResponse.success("Transaction fetched successfully", transaction);
    }

    @Override
    public BaseResponse<List<TransactionEntity>> getAllTransactions() {
        return BaseResponse.success("All transactions fetched successfully", transactionRepository.findAll());
    }

    @Override
    public BaseResponse<TransactionEntity> updateTransaction(Long id, TransactionEntity transaction) {
        TransactionEntity existing = transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found with id: " + id));
        
        existing.setTransactionNumber(transaction.getTransactionNumber());
        existing.setStore(transaction.getStore());
        existing.setCashier(transaction.getCashier());
        existing.setTransactionDateTime(transaction.getTransactionDateTime());
        existing.setTransactionType(transaction.getTransactionType());
        existing.setStatus(transaction.getStatus());
        existing.setSubtotal(transaction.getSubtotal());
        existing.setTaxAmount(transaction.getTaxAmount());
        existing.setDiscountAmount(transaction.getDiscountAmount());
        existing.setTotalAmount(transaction.getTotalAmount());
        existing.setPaidAmount(transaction.getPaidAmount());
        existing.setChangeAmount(transaction.getChangeAmount());
        existing.setPaymentMethod(transaction.getPaymentMethod());
        existing.setPaymentReference(transaction.getPaymentReference());
        existing.setCustomerName(transaction.getCustomerName());
        existing.setCustomerPhone(transaction.getCustomerPhone());
        existing.setCustomerEmail(transaction.getCustomerEmail());
        existing.setNotes(transaction.getNotes());
        existing.setReceiptNumber(transaction.getReceiptNumber());
        
        existing.getTransactionLines().clear();
        transaction.getTransactionLines().forEach(line -> {
            line.setTransaction(existing);
            existing.getTransactionLines().add(line);
        });

        existing.calculateTotals();
        TransactionEntity updated = transactionRepository.save(existing);
        return BaseResponse.success("Transaction updated successfully", updated);
    }

    @Override
    public BaseResponse<Void> deleteTransaction(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Transaction not found with id: " + id);
        }
        transactionRepository.deleteById(id);
        return BaseResponse.success("Transaction deleted successfully", null);
    }

    @Override
    public BaseResponse<Boolean> existsByTransactionNumber(String transactionNumber) {
        return BaseResponse.success("Transaction existence checked", transactionRepository.existsByTransactionNumber(transactionNumber));
    }
}
