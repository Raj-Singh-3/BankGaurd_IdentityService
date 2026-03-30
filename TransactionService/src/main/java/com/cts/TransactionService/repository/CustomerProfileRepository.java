package com.cts.TransactionService.repository;

import com.cts.TransactionService.entity.CustomerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, Integer> {
    Optional<CustomerProfile> findByEmail(String email);
    List<CustomerProfile> findByAccountAccountNumber(Long accountNumber);
    boolean existsByEmail(String email);
}
