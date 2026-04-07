package com.cts.TransactionService.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cts.TransactionService.dto.MoneyTransferResponse;
import com.cts.TransactionService.entity.Account;
import com.cts.TransactionService.repository.AccountRepository;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    public Account addAccount(Account account) {
        return accountRepository.save(account);
    }

    public Account getAccount(Long accountNumber) {
        Optional<Account> account = accountRepository.findByAccountNumber(accountNumber);
        return account.orElse(null);
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Account updateAccount(Account account) {
        if (accountRepository.existsById(account.getAccountNumber())) {
            return accountRepository.save(account);
        }
        return null;
    }

    public boolean deleteAccount(Long accountNumber) {
        if (accountRepository.existsByAccountNumber(accountNumber)) {
            accountRepository.deleteById(accountNumber);
            return true;
        }
        return false;
    }
    
    /**
     * Validate transfer request
     * @param senderAccountNumber Sender's account number
     * @param recipientAccountNumber Recipient's account number
     * @param amount Amount to transfer
     * @return MoneyTransferResponse with validation result
     */
    public MoneyTransferResponse validateTransfer(Long senderAccountNumber, Long recipientAccountNumber, Double amount) {
        MoneyTransferResponse response = new MoneyTransferResponse();
        
        // Validate: Accounts cannot be the same
        if (senderAccountNumber.equals(recipientAccountNumber)) {
            response.setSuccess(false);
            response.setMessage("Sender and recipient account numbers cannot be the same.");
            return response;
        }
        
        // Get sender account
        Optional<Account> senderOptional = accountRepository.findByAccountNumber(senderAccountNumber);
        if (!senderOptional.isPresent()) {
            response.setSuccess(false);
            response.setMessage("Sender account not found.");
            return response;
        }
        
        // Get recipient account
        Optional<Account> recipientOptional = accountRepository.findByAccountNumber(recipientAccountNumber);
        if (!recipientOptional.isPresent()) {
            response.setSuccess(false);
            response.setMessage("Recipient account not found in database.");
            return response;
        }
        
        Account sender = senderOptional.get();
        
        // Validate: Sender has sufficient balance
        if (sender.getBalance() == null || sender.getBalance() < amount) {
            response.setSuccess(false);
            response.setMessage("Insufficient balance. Available balance: " + (sender.getBalance() != null ? sender.getBalance() : 0));
            return response;
        }
        
        // Validate: Amount must be positive
        if (amount <= 0) {
            response.setSuccess(false);
            response.setMessage("Amount must be greater than zero.");
            return response;
        }
        
        response.setSuccess(true);
        response.setMessage("Transfer validation successful.");
        return response;
    }
    
    /**
     * Transfer money from one account to another with validations
     * @param senderAccountNumber Sender's account number
     * @param recipientAccountNumber Recipient's account number
     * @param amount Amount to transfer
     * @return MoneyTransferResponse with transfer status
     */
    public MoneyTransferResponse transferMoney(Long senderAccountNumber, Long recipientAccountNumber, Double amount) {
        MoneyTransferResponse response = new MoneyTransferResponse();
        
        // First validate the transfer
        MoneyTransferResponse validationResponse = validateTransfer(senderAccountNumber, recipientAccountNumber, amount);
        if (!validationResponse.isSuccess()) {
            return validationResponse;
        }
        
        // Get accounts
        Account sender = accountRepository.findByAccountNumber(senderAccountNumber).get();
        Account recipient = accountRepository.findByAccountNumber(recipientAccountNumber).get();
        
        // Perform the transfer
        try {
            sender.setBalance(sender.getBalance() - amount);
            recipient.setBalance(recipient.getBalance() + amount);
            
            accountRepository.save(sender);
            accountRepository.save(recipient);
            
            response.setSuccess(true);
            response.setMessage("Money transferred successfully!");
            response.setSenderNewBalance(sender.getBalance());
            response.setRecipientNewBalance(recipient.getBalance());
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error processing transfer: " + e.getMessage());
        }
        
        return response;
    }
}

