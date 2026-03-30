package com.cts.TransactionService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;
import java.time.LocalTime;

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
    private LocalDate date;
    private LocalTime time;
    
    @OneToOne(mappedBy = "payload")
    @JsonIgnore
    private RawTransaction rawTransaction;
}
