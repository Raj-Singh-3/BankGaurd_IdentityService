package com.cts.TransactionService.service;

import com.cts.TransactionService.entity.CustomerProfile;
import com.cts.TransactionService.entity.Transaction;
import com.cts.TransactionService.exception.ResourceNotFoundException;
import com.cts.TransactionService.repository.TransactionRepository;
import com.cts.TransactionService.repository.CustomerProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private CustomerProfileRepository customerProfileRepository;
    
    @Autowired
    private FraudDetectionService fraudDetectionService;
    
    /**
     * Creates a new transaction with fraud detection
     * @param transaction The transaction to create
     * @return The saved transaction with risk assessment
     */
    public Transaction createTransaction(Transaction transaction) {
        if (transaction.getCustomerProfile() == null || transaction.getCustomerProfile().getCustomerId() == null) {
            throw new IllegalArgumentException("Customer profile is required");
        }
        
        // Fetch the customer profile from database
        Optional<CustomerProfile> customerProfileOpt = customerProfileRepository
                .findById(transaction.getCustomerProfile().getCustomerId());
        
        if (!customerProfileOpt.isPresent()) {
            throw new ResourceNotFoundException("Customer profile with ID " + 
                    transaction.getCustomerProfile().getCustomerId() + " not found");
        }
        
        CustomerProfile customerProfile = customerProfileOpt.get();
        
        // Evaluate transaction risk based on rules
        Transaction evaluatedTransaction = fraudDetectionService.evaluateTransactionRisk(transaction, customerProfile);
        
        // Save the transaction
        Transaction savedTransaction = transactionRepository.save(evaluatedTransaction);
        
        // Update customer risk score if transaction is flagged or suspicious
        if ("SUSPICIOUS".equals(savedTransaction.getStatus()) || "FLAGGED".equals(savedTransaction.getStatus())) {
            Integer riskIncrement = savedTransaction.getRiskScore() / 3; // Proportional update
            fraudDetectionService.updateCustomerRiskScore(customerProfile, riskIncrement);
            customerProfileRepository.save(customerProfile);
        }
        
        return savedTransaction;
    }
    
    /**
     * Retrieves all transactions for a customer
     * @param customerId The customer ID
     * @return List of transactions
     */
    public List<Transaction> getCustomerTransactions(Integer customerId) {
        if (!customerProfileRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer profile with ID " + customerId + " not found");
        }
        
        return fraudDetectionService.getPreviousTransactions(customerId);
    }
    
    /**
     * Retrieves a specific transaction by ID
     * @param transactionId The transaction ID
     * @return The transaction
     */
    public Transaction getTransactionById(Integer transactionId) {
        Optional<Transaction> transaction = transactionRepository.findById(transactionId);
        
        if (!transaction.isPresent()) {
            throw new ResourceNotFoundException("Transaction with ID " + transactionId + " not found");
        }
        
        return transaction.get();
    }
    
    /**
     * Updates a transaction status
     * @param transactionId The transaction ID
     * @param newStatus The new status
     * @return The updated transaction
     */
    public Transaction updateTransactionStatus(Integer transactionId, String newStatus) {
        Optional<Transaction> transactionOpt = transactionRepository.findById(transactionId);
        
        if (!transactionOpt.isPresent()) {
            throw new ResourceNotFoundException("Transaction with ID " + transactionId + " not found");
        }
        
        Transaction transaction = transactionOpt.get();
        transaction.setStatus(newStatus);
        return transactionRepository.save(transaction);
    }
}
