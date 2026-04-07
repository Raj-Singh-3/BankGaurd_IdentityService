package com.cts.TransactionService.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payload {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer payloadId;
    
    private String sourceType;
    private String sourceName;
    private String status = "pending";
    private String location;
    private String ipAddress;
    private Double amount;
    private LocalDate date;
    private LocalTime time;
    private Long recipientAccountNumber; // For money transfers
    
    @OneToOne(mappedBy = "payload")
    @JsonIgnore
    private RawTransaction rawTransaction;
}
