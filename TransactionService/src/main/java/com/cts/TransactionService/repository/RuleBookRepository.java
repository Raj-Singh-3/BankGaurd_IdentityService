package com.cts.TransactionService.repository;

import com.cts.TransactionService.entity.RuleBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RuleBookRepository extends JpaRepository<RuleBook, Integer> {
    RuleBook findByRuleName(String ruleName);
    
    List<RuleBook> findByIsActive(Boolean isActive);
}
