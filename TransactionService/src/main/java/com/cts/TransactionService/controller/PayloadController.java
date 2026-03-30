package com.cts.TransactionService.controller;

import com.cts.TransactionService.entity.Payload;
import com.cts.TransactionService.service.PayloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/payloads")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PayloadController {
    
    @Autowired
    private PayloadService payloadService;

    @PostMapping("/add")
    public ResponseEntity<Payload> addPayload(@RequestBody Payload payload) {
        Payload savedPayload = payloadService.addPayload(payload);
        return new ResponseEntity<>(savedPayload, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payload> getPayload(@PathVariable Integer id) {
        Payload payload = payloadService.getPayload(id);
        if (payload != null) {
            return new ResponseEntity<>(payload, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Payload>> getAllPayloads() {
        List<Payload> payloads = payloadService.getAllPayloads();
        return new ResponseEntity<>(payloads, HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Payload>> getPayloadsByStatus(@PathVariable String status) {
        List<Payload> payloads = payloadService.getPayloadsByStatus(status);
        return new ResponseEntity<>(payloads, HttpStatus.OK);
    }

    @GetMapping("/source/{sourceName}")
    public ResponseEntity<List<Payload>> getPayloadsBySourceName(@PathVariable String sourceName) {
        List<Payload> payloads = payloadService.getPayloadsBySourceName(sourceName);
        return new ResponseEntity<>(payloads, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<Payload> updatePayload(@RequestBody Payload payload) {
        Payload updatedPayload = payloadService.updatePayload(payload);
        if (updatedPayload != null) {
            return new ResponseEntity<>(updatedPayload, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePayload(@PathVariable Integer id) {
        boolean isDeleted = payloadService.deletePayload(id);
        if (isDeleted) {
            return new ResponseEntity<>("Payload deleted successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Payload not found", HttpStatus.NOT_FOUND);
    }
}
