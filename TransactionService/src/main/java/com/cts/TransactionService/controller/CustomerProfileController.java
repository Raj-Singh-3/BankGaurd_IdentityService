package com.cts.TransactionService.controller;

import com.cts.TransactionService.entity.CustomerProfile;
import com.cts.TransactionService.service.CustomerProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CustomerProfileController {
    
    @Autowired
    private CustomerProfileService customerProfileService;

    @PostMapping("/add")
    public ResponseEntity<CustomerProfile> addCustomerProfile(@RequestBody CustomerProfile customerProfile) {
        CustomerProfile savedProfile = customerProfileService.addCustomerProfile(customerProfile);
        return new ResponseEntity<>(savedProfile, HttpStatus.CREATED);
    }

    @PostMapping("/add/{accountNumber}")
    public ResponseEntity<CustomerProfile> addCustomerProfileWithAccount(
            @PathVariable Long accountNumber,
            @RequestBody CustomerProfile customerProfile) {
        CustomerProfile savedProfile = customerProfileService.addCustomerProfileWithAccount(accountNumber, customerProfile);
        return new ResponseEntity<>(savedProfile, HttpStatus.CREATED);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerProfile> getCustomerProfile(@PathVariable Integer customerId) {
        CustomerProfile profile = customerProfileService.getCustomerProfile(customerId);
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<CustomerProfile>> getAllCustomerProfiles() {
        List<CustomerProfile> profiles = customerProfileService.getAllCustomerProfiles();
        return new ResponseEntity<>(profiles, HttpStatus.OK);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<CustomerProfile> getCustomerByEmail(@PathVariable String email) {
        Optional<CustomerProfile> profile = customerProfileService.getCustomerByEmail(email);
        if (profile.isPresent()) {
            return new ResponseEntity<>(profile.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<List<CustomerProfile>> getCustomersByAccount(@PathVariable Long accountNumber) {
        List<CustomerProfile> profiles = customerProfileService.getCustomersByAccount(accountNumber);
        return new ResponseEntity<>(profiles, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<CustomerProfile> updateCustomerProfile(@RequestBody CustomerProfile customerProfile) {
        CustomerProfile updatedProfile = customerProfileService.updateCustomerProfile(customerProfile);
        return new ResponseEntity<>(updatedProfile, HttpStatus.OK);
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<String> deleteCustomerProfile(@PathVariable Integer customerId) {
        customerProfileService.deleteCustomerProfile(customerId);
        return new ResponseEntity<>("Customer profile deleted successfully", HttpStatus.OK);
    }
}
