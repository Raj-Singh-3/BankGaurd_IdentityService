package com.cts.TransactionService.controller;

import com.cts.TransactionService.entity.Transaction;
import com.cts.TransactionService.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/evaluated-transactions")
@CrossOrigin(origins = "*")
public class TransactionController {
    
    @Autowired
    private TransactionService transactionService;
    
    /**
     * Creates a new transaction with fraud detection
     * @param transaction The transaction object
     * @return ResponseEntity with the created transaction
     */
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction) {
        try {
            Transaction createdTransaction = transactionService.createTransaction(transaction);
            return new ResponseEntity<>(createdTransaction, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * Retrieves all transactions for a specific customer
     * @param customerId The customer ID
     * @return ResponseEntity with list of transactions
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Transaction>> getCustomerTransactions(@PathVariable Integer customerId) {
        try {
            List<Transaction> transactions = transactionService.getCustomerTransactions(customerId);
            return new ResponseEntity<>(transactions, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * Retrieves a specific transaction by ID
     * @param transactionId The transaction ID
     * @return ResponseEntity with the transaction
     */
    @GetMapping("/{transactionId}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable Integer transactionId) {
        try {
            Transaction transaction = transactionService.getTransactionById(transactionId);
            return new ResponseEntity<>(transaction, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * Updates transaction status
     * @param transactionId The transaction ID
     * @param status The new status
     * @return ResponseEntity with the updated transaction
     */
    @PutMapping("/{transactionId}/status")
    public ResponseEntity<Transaction> updateTransactionStatus(
            @PathVariable Integer transactionId,
            @RequestParam String status) {
        try {
            Transaction updatedTransaction = transactionService.updateTransactionStatus(transactionId, status);
            return new ResponseEntity<>(updatedTransaction, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
