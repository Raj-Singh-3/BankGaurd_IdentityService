package com.cts.TransactionService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rulebook")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuleBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ruleId;

    private String ruleName;
    private String description;
    private Double threshold;
    private Integer riskIncrement;
    private Boolean isActive;
}
