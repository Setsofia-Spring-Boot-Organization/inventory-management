package com.backend.inventory_management.inventory.sales;

import com.backend.inventory_management.common.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<BaseResponse<TransactionEntity>> createTransaction(@RequestBody TransactionEntity transaction) {
        return ResponseEntity.status(201).body(transactionService.createTransaction(transaction));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<TransactionEntity>> getTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<TransactionEntity>>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<TransactionEntity>> updateTransaction(@PathVariable Long id, @RequestBody TransactionEntity transaction) {
        return ResponseEntity.ok(transactionService.updateTransaction(id, transaction));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteTransaction(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.deleteTransaction(id));
    }

    @GetMapping("/exists/{transactionNumber}")
    public ResponseEntity<BaseResponse<Boolean>> existsByTransactionNumber(@PathVariable String transactionNumber) {
        return ResponseEntity.ok(transactionService.existsByTransactionNumber(transactionNumber));
    }
}
