package com.cts.TransactionService.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cts.TransactionService.entity.RuleBook;
import com.cts.TransactionService.repository.RuleBookRepository;
import com.cts.TransactionService.service.FraudDetectionService;

@RestController
@RequestMapping("/api/rulebook")
@CrossOrigin(origins = "*")
public class RuleBookController {
    
    @Autowired
    private RuleBookRepository ruleBookRepository;
    
    @Autowired
    private FraudDetectionService fraudDetectionService;
    
    /**
     * Creates a new fraud detection rule
     * @param ruleBook The rule object
     * @return ResponseEntity with the created rule
     */
    @PostMapping
    public ResponseEntity<RuleBook> createRule(@RequestBody RuleBook ruleBook) {
        try {
            RuleBook createdRule = ruleBookRepository.save(ruleBook);
            return new ResponseEntity<>(createdRule, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * Retrieves all fraud detection rules
     * @return ResponseEntity with list of rules
     */
    @GetMapping
    public ResponseEntity<List<RuleBook>> getAllRules() {
        try {
            List<RuleBook> rules = ruleBookRepository.findAll();
            return new ResponseEntity<>(rules, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Retrieves all active fraud detection rules
     * @return ResponseEntity with list of active rules
     */
    @GetMapping("/active")
    public ResponseEntity<List<RuleBook>> getActiveRules() {
        try {
            List<RuleBook> rules = ruleBookRepository.findByIsActive(true);
            return new ResponseEntity<>(rules, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Retrieves a specific rule by ID
     * @param ruleId The rule ID
     * @return ResponseEntity with the rule
     */
    @GetMapping("/{ruleId}")
    public ResponseEntity<RuleBook> getRule(@PathVariable Integer ruleId) {
        try {
            Optional<RuleBook> rule = ruleBookRepository.findById(ruleId);
            if (rule.isPresent()) {
                return new ResponseEntity<>(rule.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Updates a fraud detection rule
     * @param ruleId The rule ID
     * @param ruleBook The updated rule
     * @return ResponseEntity with the updated rule
     */
    @PutMapping("/{ruleId}")
    public ResponseEntity<RuleBook> updateRule(@PathVariable Integer ruleId, @RequestBody RuleBook ruleBook) {
        try {
            Optional<RuleBook> existingRule = ruleBookRepository.findById(ruleId);
            if (existingRule.isPresent()) {
                ruleBook.setRuleId(ruleId);
                RuleBook updatedRule = ruleBookRepository.save(ruleBook);
                return new ResponseEntity<>(updatedRule, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * Deletes a fraud detection rule
     * @param ruleId The rule ID
     * @return ResponseEntity with success status
     */
    @DeleteMapping("/{ruleId}")
    public ResponseEntity<Void> deleteRule(@PathVariable Integer ruleId) {
        try {
            if (ruleBookRepository.existsById(ruleId)) {
                ruleBookRepository.deleteById(ruleId);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Initializes default fraud detection rules
     * @return ResponseEntity with success message
     */
    @PostMapping("/initialize-defaults")
    public ResponseEntity<String> initializeDefaultRules() {
        try {
            fraudDetectionService.initializeDefaultRules();
            return new ResponseEntity<>("Default rules initialized successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error initializing rules: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
