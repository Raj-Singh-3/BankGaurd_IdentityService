package com.cts.TransactionService.repository;

import com.cts.TransactionService.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findByCustomerProfileCustomerIdOrderByTransactionTimestampDesc(Integer customerId);
    
    List<Transaction> findByCustomerProfileCustomerIdAndTransactionTimestampBetween(
            Integer customerId, LocalDateTime startTime, LocalDateTime endTime);
}
