# Fraud Detection System Documentation

## Overview
The Fraud Detection System is designed to analyze customer transactions and determine their authenticity by applying a set of predefined rules. The system maintains transaction history and uses risk scoring to flag potentially fraudulent transactions.

## Components

### 1. Transaction Entity
Stores individual transaction records with:
- `transactionId`: Unique identifier
- `customerId`: Reference to the customer
- `amount`: Transaction amount
- `transactionTimestamp`: When the transaction occurred
- `status`: GENUINE, FLAGGED, or SUSPICIOUS
- `riskScore`: Calculated fraud risk score (0-100+)
- `description`: Explanation of risk factors

### 2. RuleBook Entity
Defines fraud detection rules with:
- `ruleId`: Unique identifier
- `ruleName`: Name of the rule
- `description`: What the rule checks
- `threshold`: Trigger condition value
- `riskIncrement`: How much risk score increases
- `isActive`: Whether the rule is enabled

### 3. Fraud Detection Rules

#### Rule 1: High Amount Transaction
- **Condition**: Transaction amount > 50,000
- **Risk Increment**: +15 points
- **Description**: Large transactions are filtered for suspicious activity

#### Rule 2: Simultaneous Transactions
- **Condition**: 3 or more transactions within 10 minutes
- **Risk Increment**: +20 points
- **Description**: Multiple rapid transactions could indicate account compromise

#### Rule 3: Customer Risk Profile
- **Condition**: Customer's existing risk score > 30
- **Risk Increment**: +10 points
- **Description**: Customers with elevated risk history are monitored more closely

### 4. Risk Score Thresholds
- **0-29**: GENUINE - Low risk transaction
- **30-49**: FLAGGED - Medium risk, review recommended
- **50+**: SUSPICIOUS - High risk, requires approval

## Services

### TransactionService
Manages transaction creation and retrieval with integrated fraud detection:
- `createTransaction()`: Creates new transaction with risk assessment
- `getCustomerTransactions()`: Retrieves transaction history
- `getTransactionById()`: Fetches specific transaction
- `updateTransactionStatus()`: Updates transaction status

### FraudDetectionService
Core fraud detection logic:
- `evaluateTransactionRisk()`: Calculates risk score for a transaction
- `checkSimultaneousTransactions()`: Checks for rapid transactions
- `getPreviousTransactions()`: Retrieves customer history
- `updateCustomerRiskScore()`: Updates customer profile risk
- `initializeDefaultRules()`: Sets up standard rules

## REST API Endpoints

### Transaction Endpoints
**POST** `/api/transactions`
- Create new transaction (automatically evaluated for fraud)
- Body: Transaction object with amount and customer ID

**GET** `/api/transactions/customer/{customerId}`
- Retrieve all transactions for a customer

**GET** `/api/transactions/{transactionId}`
- Get specific transaction details

**PUT** `/api/transactions/{transactionId}/status`
- Update transaction status (approve/reject)

### RuleBook Endpoints
**POST** `/api/rulebook`
- Create new fraud detection rule

**GET** `/api/rulebook`
- Retrieve all rules

**GET** `/api/rulebook/active`
- Retrieve only active rules

**GET** `/api/rulebook/{ruleId}`
- Get specific rule

**PUT** `/api/rulebook/{ruleId}`
- Update rule parameters

**DELETE** `/api/rulebook/{ruleId}`
- Remove a rule

**POST** `/api/rulebook/initialize-defaults`
- Initialize default rules in the system

## How It Works

### Transaction Processing Flow
1. Customer initiates a transaction
2. Transaction object created with amount and customer details
3. System retrieves customer profile and transaction history
4. Fraud detection engine evaluates against active rules:
   - Checks if amount exceeds threshold
   - Counts transactions in last 10 minutes
   - Reviews customer's existing risk profile
5. Risk score calculated and status assigned
6. Customer's profile risk is updated if flagged/suspicious
7. Transaction stored in database for future reference

### Example Transaction Creation
```json
{
  "customerProfile": {
    "customerId": 1
  },
  "amount": 75000.00,
  "transactionTimestamp": "2024-04-03T10:30:00"
}
```

### Example Response
```json
{
  "transactionId": 1001,
  "customerId": 1,
  "amount": 75000.00,
  "status": "SUSPICIOUS",
  "riskScore": 35,
  "description": "High transaction amount (> 50,000). Simultaneous transactions detected.",
  "transactionTimestamp": "2024-04-03T10:30:00"
}
```

## Database Schema

### Transaction Table
```sql
CREATE TABLE transaction (
  transaction_id INT PRIMARY KEY AUTO_INCREMENT,
  customer_id INT NOT NULL,
  amount DOUBLE,
  transaction_timestamp DATETIME,
  status VARCHAR(20),
  risk_score INT,
  description VARCHAR(500),
  raw_transaction_id INT,
  FOREIGN KEY (customer_id) REFERENCES customer_profile(customer_id),
  FOREIGN KEY (raw_transaction_id) REFERENCES raw_transaction(transaction_id)
);
```

### RuleBook Table
```sql
CREATE TABLE rulebook (
  rule_id INT PRIMARY KEY AUTO_INCREMENT,
  rule_name VARCHAR(100),
  description VARCHAR(500),
  threshold DOUBLE,
  risk_increment INT,
  is_active BOOLEAN
);
```

## Extensions and Customization

### Adding New Rules
1. Create rule entry in database via `/api/rulebook` endpoint
2. Extend `FraudDetectionService.evaluateTransactionRisk()` to implement logic
3. Set `isActive` to true to enable

### Adjusting Risk Thresholds
- Modify threshold values in `FraudDetectionService` constants
- Update via rulebook API endpoints
- Test with historical transactions

### Integration with Authentication Service
- Transaction service will integrate with BankGuard Identity Service
- Customer verification before processing
- Risk-based authentication for suspicious transactions

## Future Enhancements
- Machine learning model for pattern recognition
- Geolocation-based fraud detection
- Velocity checking across accounts
- Time-of-day analysis
- Device fingerprinting
- Email/SMS alerts for suspicious activity
