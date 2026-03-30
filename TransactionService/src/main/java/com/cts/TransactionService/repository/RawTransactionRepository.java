package com.cts.TransactionService.repository;

import com.cts.TransactionService.entity.RawTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RawTransactionRepository extends JpaRepository<RawTransaction, Integer> {
    List<RawTransaction> findByCustomerProfileCustomerId(Integer customerId);
    List<RawTransaction> findByProcessingStatus(Boolean processingStatus);
    List<RawTransaction> findByPayloadPayloadId(Integer payloadId);
}
