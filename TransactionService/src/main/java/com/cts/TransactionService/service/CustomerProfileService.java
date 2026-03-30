package com.cts.TransactionService.service;

import com.cts.TransactionService.entity.Account;
import com.cts.TransactionService.entity.CustomerProfile;
import com.cts.TransactionService.exception.ResourceNotFoundException;
import com.cts.TransactionService.repository.AccountRepository;
import com.cts.TransactionService.repository.CustomerProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerProfileService {
    @Autowired
    private CustomerProfileRepository customerProfileRepository;
    @Autowired
    private AccountRepository accountRepository;

    public CustomerProfile addCustomerProfile(CustomerProfile customerProfile) {
        if (customerProfile.getAccount() != null && customerProfile.getAccount().getAccountNumber() != null) {
            Optional<Account> account = accountRepository.findByAccountNumber(customerProfile.getAccount().getAccountNumber());
            if (account.isPresent()) {
                customerProfile.setAccount(account.get());
                return customerProfileRepository.save(customerProfile);
            } else {
                throw new ResourceNotFoundException("Account with number " + customerProfile.getAccount().getAccountNumber() + " not found");
            }
        }
        throw new IllegalArgumentException("Account information is required to create a customer profile");
    }

    public CustomerProfile addCustomerProfileWithAccount(Long accountNumber, CustomerProfile customerProfile) {
        Optional<Account> account = accountRepository.findByAccountNumber(accountNumber);
        if (account.isPresent()) {
            customerProfile.setAccount(account.get());
            return customerProfileRepository.save(customerProfile);
        }
        throw new ResourceNotFoundException("Account with number " + accountNumber + " not found");
    }

    public CustomerProfile getCustomerProfile(Integer customerId) {
        Optional<CustomerProfile> profile = customerProfileRepository.findById(customerId);
        if (profile.isPresent()) {
            return profile.get();
        }
        throw new ResourceNotFoundException("Customer profile with ID " + customerId + " not found");
    }

    public List<CustomerProfile> getAllCustomerProfiles() {
        return customerProfileRepository.findAll();
    }

    public Optional<CustomerProfile> getCustomerByEmail(String email) {
        return customerProfileRepository.findByEmail(email);
    }

    public List<CustomerProfile> getCustomersByAccount(Long accountNumber) {
        return customerProfileRepository.findByAccountAccountNumber(accountNumber);
    }

    public CustomerProfile updateCustomerProfile(CustomerProfile customerProfile) {
        if (customerProfileRepository.existsById(customerProfile.getCustomerId())) {
            return customerProfileRepository.save(customerProfile);
        }
        throw new ResourceNotFoundException("Customer profile with ID " + customerProfile.getCustomerId() + " not found");
    }

    public boolean deleteCustomerProfile(Integer customerId) {
        if (customerProfileRepository.existsById(customerId)) {
            customerProfileRepository.deleteById(customerId);
            return true;
        }
        throw new ResourceNotFoundException("Customer profile with ID " + customerId + " not found");
    }
}
