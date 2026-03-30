package com.cts.TransactionService.controller;

import com.cts.TransactionService.entity.RawTransaction;
import com.cts.TransactionService.service.RawTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RawTransactionController {
    
    @Autowired
    private RawTransactionService rawTransactionService;

    @PostMapping("/add")
    public ResponseEntity<RawTransaction> addRawTransaction(@RequestBody RawTransaction rawTransaction) {
        RawTransaction savedTransaction = rawTransactionService.addRawTransaction(rawTransaction);
        return new ResponseEntity<>(savedTransaction, HttpStatus.CREATED);
    }

    @GetMapping("/{transactionID}")
    public ResponseEntity<RawTransaction> getRawTransaction(@PathVariable Integer transactionID) {
        RawTransaction transaction = rawTransactionService.getRawTransaction(transactionID);
        if (transaction != null) {
            return new ResponseEntity<>(transaction, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/all")
    public ResponseEntity<List<RawTransaction>> getAllRawTransactions() {
        List<RawTransaction> transactions = rawTransactionService.getAllRawTransactions();
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<RawTransaction>> getTransactionsByCustomer(@PathVariable Integer customerId) {
        List<RawTransaction> transactions = rawTransactionService.getTransactionsByCustomer(customerId);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @GetMapping("/status/{processingStatus}")
    public ResponseEntity<List<RawTransaction>> getTransactionsByStatus(@PathVariable Boolean processingStatus) {
        List<RawTransaction> transactions = rawTransactionService.getTransactionsByStatus(processingStatus);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @GetMapping("/payload/{payloadId}")
    public ResponseEntity<List<RawTransaction>> getTransactionsByPayload(@PathVariable Integer payloadId) {
        List<RawTransaction> transactions = rawTransactionService.getTransactionsByPayload(payloadId);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<RawTransaction> updateRawTransaction(@RequestBody RawTransaction rawTransaction) {
        RawTransaction updatedTransaction = rawTransactionService.updateRawTransaction(rawTransaction);
        if (updatedTransaction != null) {
            return new ResponseEntity<>(updatedTransaction, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{transactionID}")
    public ResponseEntity<String> deleteRawTransaction(@PathVariable Integer transactionID) {
        boolean isDeleted = rawTransactionService.deleteRawTransaction(transactionID);
        if (isDeleted) {
            return new ResponseEntity<>("Transaction deleted successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Transaction not found", HttpStatus.NOT_FOUND);
    }
}
