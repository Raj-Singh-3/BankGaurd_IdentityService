package com.cts.TransactionService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoneyTransferRequest {
    private Long senderAccountNumber;
    private Long recipientAccountNumber;
    private Double amount;
    private String description;
}
