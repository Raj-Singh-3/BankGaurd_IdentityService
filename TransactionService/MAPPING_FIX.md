# Fix for Ambiguous Mapping Error

## Problem
The application failed to start with the following error:
```
Ambiguous mapping. Cannot map 'transactionController' method 
com.cts.TransactionService.controller.TransactionController#getCustomerTransactions(Integer)
to {GET [/api/transactions/customer/{customerId}]}: There is already 'rawTransactionController' bean method
com.cts.TransactionService.controller.RawTransactionController#getTransactionsByCustomer(Integer) mapped.
```

## Root Cause
Two Spring REST controllers were trying to map to the same endpoint:
- **RawTransactionController**: `@RequestMapping("/api/transactions")` with endpoint `/customer/{customerId}`
- **TransactionController**: `@RequestMapping("/api/transactions")` with endpoint `/customer/{customerId}`

Both resulted in the same URL path: `GET /api/transactions/customer/{customerId}`, causing a mapping conflict.

## Solution
Changed the `TransactionController` base path from `/api/transactions` to `/api/evaluated-transactions` to distinguish it from raw transactions.

### Before
```java
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Transaction>> getCustomerTransactions(@PathVariable Integer customerId)
}
// URL: GET /api/transactions/customer/{customerId}
```

### After
```java
@RestController
@RequestMapping("/api/evaluated-transactions")
public class TransactionController {
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Transaction>> getCustomerTransactions(@PathVariable Integer customerId)
}
// URL: GET /api/evaluated-transactions/customer/{customerId}
```

## Updated API Endpoints

### Raw Transactions (Unchanged - Original data retrieval)
```
POST   /api/transactions/add                              - Create raw transaction
GET    /api/transactions/{transactionID}                  - Get by ID
GET    /api/transactions/all                              - Get all
GET    /api/transactions/customer/{customerId}            - Get by customer
GET    /api/transactions/status/{processingStatus}        - Get by status
GET    /api/transactions/payload/{payloadId}              - Get by payload
PUT    /api/transactions/update                           - Update transaction
DELETE /api/transactions/{transactionID}                  - Delete transaction
```

### Fraud-Evaluated Transactions (New - Fraud Detection Results)
```
POST   /api/evaluated-transactions                        - Create with fraud detection
GET    /api/evaluated-transactions/{transactionId}        - Get transaction details
GET    /api/evaluated-transactions/customer/{customerId}  - Get customer transaction history
PUT    /api/evaluated-transactions/{transactionId}/status - Update transaction status
```

### RuleBook (Fraud Detection Rules)
```
POST   /api/rulebook                                      - Create rule
GET    /api/rulebook                                      - Get all rules
GET    /api/rulebook/active                               - Get active rules
GET    /api/rulebook/{ruleId}                             - Get rule details
PUT    /api/rulebook/{ruleId}                             - Update rule
DELETE /api/rulebook/{ruleId}                             - Delete rule
POST   /api/rulebook/initialize-defaults                  - Initialize default rules
```

## Clarification of Functionality

### RawTransactionController (/api/transactions)
- Manages raw, incoming transaction data
- No fraud evaluation or risk scoring
- Simple CRUD operations on RawTransaction entity

### TransactionController (/api/evaluated-transactions)
- Process transactions with fraud detection
- Performs risk assessment using FraudDetectionService
- Returns risk score, status (GENUINE/FLAGGED/SUSPICIOUS), and fraud indicators
- Updates customer risk profile

## Compilation Result
✅ **BUILD SUCCESS** - 28 source files compiled successfully

## Testing the Fix
The application should now start without the ambiguous mapping error. The two controllers properly segregate concerns:
1. **Raw transactions**: Initial input, data storage
2. **Evaluated transactions**: Fraud detection results and analysis

Clients should use the appropriate endpoint based on their needs:
- For raw transaction submission: Use RawTransactionController endpoints
- For fraud assessment and history: Use TransactionController (evaluated-transactions) endpoints
