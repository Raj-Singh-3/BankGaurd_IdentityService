package com.cts.IdentityService.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/analyst")
public class AnalystController {

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('FRAUD_ANALYST')")
    public String dashboard() {
        return "Fraud Analyst Dashboard Accessed";
    }
}
