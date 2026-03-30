package com.cts.TransactionService.repository;

import com.cts.TransactionService.entity.Payload;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PayloadRepository extends JpaRepository<Payload, Integer> {
    List<Payload> findByStatus(String status);
    List<Payload> findBySourceName(String sourceName);
}
