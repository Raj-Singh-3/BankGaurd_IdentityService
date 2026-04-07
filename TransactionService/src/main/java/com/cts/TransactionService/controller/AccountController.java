package com.cts.TransactionService.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cts.TransactionService.dto.MoneyTransferRequest;
import com.cts.TransactionService.dto.MoneyTransferResponse;
import com.cts.TransactionService.entity.Account;
import com.cts.TransactionService.service.AccountService;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AccountController {
    
    @Autowired
    private AccountService accountService;

    @PostMapping("/add")
    public ResponseEntity<Account> addAccount(@RequestBody Account account) {
        Account savedAccount = accountService.addAccount(account);
        return new ResponseEntity<>(savedAccount, HttpStatus.CREATED);
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<Account> getAccount(@PathVariable Long accountNumber) {
        Account account = accountService.getAccount(accountNumber);
        if (account != null) {
            return new ResponseEntity<>(account, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<Account> updateAccount(@RequestBody Account account) {
        Account updatedAccount = accountService.updateAccount(account);
        if (updatedAccount != null) {
            return new ResponseEntity<>(updatedAccount, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{accountNumber}")
    public ResponseEntity<String> deleteAccount(@PathVariable Long accountNumber) {
        boolean isDeleted = accountService.deleteAccount(accountNumber);
        if (isDeleted) {
            return new ResponseEntity<>("Account deleted successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Account not found", HttpStatus.NOT_FOUND);
    }
    
    /**
     * Validate transfer before processing
     * @param transferRequest Contains sender account, recipient account, and amount
     * @return MoneyTransferResponse with validation result
     */
    @PostMapping("/transfer/validate")
    public ResponseEntity<MoneyTransferResponse> validateTransfer(@RequestBody MoneyTransferRequest transferRequest) {
        try {
            MoneyTransferResponse response = accountService.validateTransfer(
                transferRequest.getSenderAccountNumber(),
                transferRequest.getRecipientAccountNumber(),
                transferRequest.getAmount()
            );
            
            if (response.isSuccess()) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            MoneyTransferResponse errorResponse = new MoneyTransferResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Error validating transfer: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Transfer money from one account to another
     * Validates: recipient account exists, recipient != sender, and sufficient balance
     * @param transferRequest Contains sender account, recipient account, and amount
     * @return MoneyTransferResponse with transfer status and new balances
     */
    @PostMapping("/transfer")
    public ResponseEntity<MoneyTransferResponse> transferMoney(@RequestBody MoneyTransferRequest transferRequest) {
        try {
            MoneyTransferResponse response = accountService.transferMoney(
                transferRequest.getSenderAccountNumber(),
                transferRequest.getRecipientAccountNumber(),
                transferRequest.getAmount()
            );
            
            if (response.isSuccess()) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            MoneyTransferResponse errorResponse = new MoneyTransferResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Error processing transfer: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
