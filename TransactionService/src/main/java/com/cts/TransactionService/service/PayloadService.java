package com.cts.TransactionService.service;

import com.cts.TransactionService.entity.Payload;
import com.cts.TransactionService.repository.PayloadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PayloadService {
    @Autowired
    private PayloadRepository payloadRepository;

    public Payload addPayload(Payload payload) {
        return payloadRepository.save(payload);
    }

    public Payload getPayload(Integer id) {
        Optional<Payload> payload = payloadRepository.findById(id);
        return payload.orElse(null);
    }

    public List<Payload> getAllPayloads() {
        return payloadRepository.findAll();
    }

    public List<Payload> getPayloadsByStatus(String status) {
        return payloadRepository.findByStatus(status);
    }

    public List<Payload> getPayloadsBySourceName(String sourceName) {
        return payloadRepository.findBySourceName(sourceName);
    }

    public Payload updatePayload(Payload payload) {
        if (payloadRepository.existsById(payload.getPayloadId())) {
            return payloadRepository.save(payload);
        }
        return null;
    }

    public boolean deletePayload(Integer id) {
        if (payloadRepository.existsById(id)) {
            payloadRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
