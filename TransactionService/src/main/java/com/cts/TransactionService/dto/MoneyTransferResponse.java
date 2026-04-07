package com.cts.TransactionService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoneyTransferResponse {
    private boolean success;
    private String message;
    private Double senderNewBalance;
    private Double recipientNewBalance;
}
