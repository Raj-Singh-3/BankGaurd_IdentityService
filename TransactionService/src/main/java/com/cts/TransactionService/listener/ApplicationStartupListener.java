package com.cts.TransactionService.listener;

import com.cts.TransactionService.service.FraudDetectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ApplicationStartupListener {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationStartupListener.class);
    
    @Autowired
    private FraudDetectionService fraudDetectionService;
    
    /**
     * Initializes default fraud detection rules when the application starts
     * This ensures that if no rules exist, the system automatically creates them
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initializeDefaultRules() {
        logger.info("Application started. Initializing default fraud detection rules...");
        try {
            fraudDetectionService.initializeDefaultRules();
            logger.info("Default fraud detection rules initialized successfully");
        } catch (Exception e) {
            logger.error("Error initializing default fraud detection rules: {}", e.getMessage());
        }
    }
}
