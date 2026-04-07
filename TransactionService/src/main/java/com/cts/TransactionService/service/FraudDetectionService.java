package com.cts.TransactionService.service;

import com.cts.TransactionService.entity.CustomerProfile;
import com.cts.TransactionService.entity.RuleBook;
import com.cts.TransactionService.entity.Transaction;
import com.cts.TransactionService.repository.RuleBookRepository;
import com.cts.TransactionService.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FraudDetectionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private RuleBookRepository ruleBookRepository;
    
    private static final double HIGH_AMOUNT_THRESHOLD = 50000.0;
    private static final int HIGH_AMOUNT_RISK_INCREMENT = 15;
    private static final int SIMULTANEOUS_TRANSACTION_RISK_INCREMENT = 20;
    private static final int SUSPICIOUS_THRESHOLD = 50;
    
    /**
     * Evaluates transaction risk based on defined rules
     * @param transaction The transaction to evaluate
     * @param customerProfile The customer making the transaction
     * @return Updated transaction with risk score and status
     */
    public Transaction evaluateTransactionRisk(Transaction transaction, CustomerProfile customerProfile) {
        Integer riskScore = 0;
        StringBuilder riskDescription = new StringBuilder();
        
        // Rule 1: High Amount Transaction (> 50,000)
        if (transaction.getAmount() != null && transaction.getAmount() > HIGH_AMOUNT_THRESHOLD) {
            riskScore += HIGH_AMOUNT_RISK_INCREMENT;
            riskDescription.append("High transaction amount (> 50,000). ");
        }
        
        // Rule 2: Simultaneous Transactions (3+ transactions within 10 minutes)
        riskScore += checkSimultaneousTransactions(customerProfile.getCustomerId(), transaction.getTransactionTimestamp());
        
        // Rule 3: Customer's existing risk score
        if (customerProfile.getRiskScore() != null && customerProfile.getRiskScore() > 30) {
            riskScore += 10;
            riskDescription.append("Customer has elevated risk profile. ");
        }
        
        // Set transaction status based on risk score
        transaction.setRiskScore(riskScore);
        transaction.setDescription(riskDescription.toString());
        
        if (riskScore >= SUSPICIOUS_THRESHOLD) {
            transaction.setStatus("SUSPICIOUS");
        } else if (riskScore >= 30) {
            transaction.setStatus("FLAGGED");
        } else {
            transaction.setStatus("GENUINE");
        }
        
        return transaction;
    }
    
    /**
     * Checks if customer has made 3 or more transactions within 10 minutes
     * @param customerId The customer ID
     * @param currentTransactionTime The current transaction timestamp
     * @return Risk increment if rule triggered, 0 otherwise
     */
    private Integer checkSimultaneousTransactions(Integer customerId, LocalDateTime currentTransactionTime) {
        LocalDateTime tenMinutesAgo = currentTransactionTime.minusMinutes(10);
        
        List<Transaction> recentTransactions = transactionRepository
                .findByCustomerProfileCustomerIdAndTransactionTimestampBetween(
                        customerId, tenMinutesAgo, currentTransactionTime);
        
        if (recentTransactions.size() >= 3) {
            return SIMULTANEOUS_TRANSACTION_RISK_INCREMENT;
        }
        
        return 0;
    }
    
    /**
     * Gets all previous transactions for a customer
     * @param customerId The customer ID
     * @return List of previous transactions
     */
    public List<Transaction> getPreviousTransactions(Integer customerId) {
        return transactionRepository.findByCustomerProfileCustomerIdOrderByTransactionTimestampDesc(customerId);
    }
    
    /**
     * Initializes default rules in the database
     */
    public void initializeDefaultRules() {
        // Check if rules already exist
        if (ruleBookRepository.findByRuleName("HIGH_AMOUNT") == null) {
            RuleBook highAmountRule = new RuleBook();
            highAmountRule.setRuleName("HIGH_AMOUNT");
            highAmountRule.setDescription("Transaction amount exceeds 50,000");
            highAmountRule.setThreshold(50000.0);
            highAmountRule.setRiskIncrement(15);
            highAmountRule.setIsActive(true);
            ruleBookRepository.save(highAmountRule);
        }
        
        if (ruleBookRepository.findByRuleName("SIMULTANEOUS_TRANSACTIONS") == null) {
            RuleBook simultaneousRule = new RuleBook();
            simultaneousRule.setRuleName("SIMULTANEOUS_TRANSACTIONS");
            simultaneousRule.setDescription("3 or more transactions within 10 minutes");
            simultaneousRule.setThreshold(3.0);
            simultaneousRule.setRiskIncrement(20);
            simultaneousRule.setIsActive(true);
            ruleBookRepository.save(simultaneousRule);
        }
        
        if (ruleBookRepository.findByRuleName("CUSTOMER_RISK_PROFILE") == null) {
            RuleBook riskProfileRule = new RuleBook();
            riskProfileRule.setRuleName("CUSTOMER_RISK_PROFILE");
            riskProfileRule.setDescription("Customer has elevated risk score");
            riskProfileRule.setThreshold(30.0);
            riskProfileRule.setRiskIncrement(10);
            riskProfileRule.setIsActive(true);
            ruleBookRepository.save(riskProfileRule);
        }
    }
    
    /**
     * Updates customer risk score based on transaction assessment
     * @param customerProfile The customer profile
     * @param newRiskIncrement The new risk increment
     */
    public void updateCustomerRiskScore(CustomerProfile customerProfile, Integer newRiskIncrement) {
        Integer currentRisk = customerProfile.getRiskScore();
        Integer incrementValue = newRiskIncrement;
        
        if (currentRisk == null) {
            currentRisk = 0;
        }
        if (incrementValue == null) {
            incrementValue = 0;
        }
        
        customerProfile.setRiskScore(currentRisk + incrementValue);
    }
}
