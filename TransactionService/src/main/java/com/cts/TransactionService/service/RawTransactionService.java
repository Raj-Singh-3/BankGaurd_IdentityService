package com.cts.TransactionService.service;

import com.cts.TransactionService.entity.CustomerProfile;
import com.cts.TransactionService.entity.Payload;
import com.cts.TransactionService.entity.RawTransaction;
import com.cts.TransactionService.exception.ResourceNotFoundException;
import com.cts.TransactionService.repository.CustomerProfileRepository;
import com.cts.TransactionService.repository.PayloadRepository;
import com.cts.TransactionService.repository.RawTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RawTransactionService {
    @Autowired
    private RawTransactionRepository rawTransactionRepository;
    @Autowired
    private PayloadRepository payloadRepository;
    @Autowired
    private CustomerProfileRepository customerProfileRepository;

    public RawTransaction addRawTransaction(RawTransaction rawTransaction) {
        // Fetch the Payload from database if ID is provided
        if (rawTransaction.getPayload() != null && rawTransaction.getPayload().getPayloadId() != null) {
            Optional<Payload> payload = payloadRepository.findById(rawTransaction.getPayload().getPayloadId());
            if (payload.isPresent()) {
                rawTransaction.setPayload(payload.get());
            } else {
                throw new ResourceNotFoundException("Payload with ID " + rawTransaction.getPayload().getPayloadId() + " not found");
            }
        } else {
            throw new IllegalArgumentException("Payload information is required");
        }

        // Fetch the CustomerProfile from database if ID is provided
        if (rawTransaction.getCustomerProfile() != null && rawTransaction.getCustomerProfile().getCustomerId() != null) {
            Optional<CustomerProfile> customerProfile = customerProfileRepository.findById(rawTransaction.getCustomerProfile().getCustomerId());
            if (customerProfile.isPresent()) {
                rawTransaction.setCustomerProfile(customerProfile.get());
            } else {
                throw new ResourceNotFoundException("Customer profile with ID " + rawTransaction.getCustomerProfile().getCustomerId() + " not found");
            }
        } else {
            throw new IllegalArgumentException("Customer profile information is required");
        }

        return rawTransactionRepository.save(rawTransaction);
    }

    public RawTransaction getRawTransaction(Integer transactionID) {
        Optional<RawTransaction> transaction = rawTransactionRepository.findById(transactionID);
        return transaction.orElse(null);
    }

    public List<RawTransaction> getAllRawTransactions() {
        return rawTransactionRepository.findAll();
    }

    public List<RawTransaction> getTransactionsByCustomer(Integer customerId) {
        return rawTransactionRepository.findByCustomerProfileCustomerId(customerId);
    }

    public List<RawTransaction> getTransactionsByStatus(Boolean processingStatus) {
        return rawTransactionRepository.findByProcessingStatus(processingStatus);
    }

    public List<RawTransaction> getTransactionsByPayload(Integer payloadId) {
        return rawTransactionRepository.findByPayloadPayloadId(payloadId);
    }

    public RawTransaction updateRawTransaction(RawTransaction rawTransaction) {
        if (rawTransactionRepository.existsById(rawTransaction.getTransactionID())) {
            return rawTransactionRepository.save(rawTransaction);
        }
        return null;
    }

    public boolean deleteRawTransaction(Integer transactionID) {
        if (rawTransactionRepository.existsById(transactionID)) {
            rawTransactionRepository.deleteById(transactionID);
            return true;
        }
        return false;
    }
}
