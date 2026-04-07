# Automatic Rule Initialization on Application Startup

## Overview
The application now automatically initializes default fraud detection rules when it starts. If no rules exist in the RuleBook table, the system will automatically create the three core fraud detection rules.

## How It Works

### Application Startup Flow
1. Application starts and Spring Boot initializes all beans
2. When the application is ready, `ApplicationStartupListener` is triggered
3. `ApplicationReadyEvent` listener calls `FraudDetectionService.initializeDefaultRules()`
4. System checks if each rule already exists
5. If a rule doesn't exist, it creates it with predefined parameters
6. Rules are now available for fraud detection

### Default Rules Created

#### Rule 1: HIGH_AMOUNT
- **Rule Name**: HIGH_AMOUNT
- **Threshold**: 50,000
- **Risk Increment**: 15 points
- **Description**: Transaction amount exceeds 50,000
- **Status**: Active by default

#### Rule 2: SIMULTANEOUS_TRANSACTIONS
- **Rule Name**: SIMULTANEOUS_TRANSACTIONS
- **Threshold**: 3 (transactions)
- **Risk Increment**: 20 points
- **Description**: 3 or more transactions within 10 minutes
- **Status**: Active by default

#### Rule 3: CUSTOMER_RISK_PROFILE
- **Rule Name**: CUSTOMER_RISK_PROFILE
- **Threshold**: 30 (risk score)
- **Risk Increment**: 10 points
- **Description**: Customer has elevated risk score
- **Status**: Active by default

## Implementation Details

### ApplicationStartupListener.java
Located in: `src/main/java/com/cts/TransactionService/listener/ApplicationStartupListener.java`

```java
@Component
@EventListener(ApplicationReadyEvent.class)
public void initializeDefaultRules() {
    // Automatically called when application starts
    fraudDetectionService.initializeDefaultRules();
}
```

### Safety Features
- **Idempotent**: Running multiple times won't create duplicate rules
- **Check-before-create**: System checks if rule exists before attempting creation
- **Error handling**: Logs any errors during initialization without crashing the app
- **Non-blocking**: Initialization happens asynchronously after app startup

## Database Initialization

The system follows this pattern:

```
First Run:
  ├─ Application starts
  ├─ ApplicationStartupListener triggered
  ├─ Check for HIGH_AMOUNT rule → Not found
  ├─ Create HIGH_AMOUNT rule
  ├─ Check for SIMULTANEOUS_TRANSACTIONS rule → Not found
  ├─ Create SIMULTANEOUS_TRANSACTIONS rule
  ├─ Check for CUSTOMER_RISK_PROFILE rule → Not found
  ├─ Create CUSTOMER_RISK_PROFILE rule
  └─ All rules initialized ✓

Subsequent Runs:
  ├─ Application starts
  ├─ ApplicationStartupListener triggered
  ├─ Check for HIGH_AMOUNT rule → Found (skip)
  ├─ Check for SIMULTANEOUS_TRANSACTIONS rule → Found (skip)
  ├─ Check for CUSTOMER_RISK_PROFILE rule → Found (skip)
  └─ Rules already exist ✓
```

## Rules Auto-Enable Feature

All default rules are automatically set to `isActive = true`, which means:
- Rules are immediately active for fraud detection
- No manual rule activation needed
- All transactions are evaluated against these rules from first startup

## Managing Rules After Startup

After initialization, you can manage rules via REST API:

```bash
# View all rules
GET /api/rulebook

# View only active rules
GET /api/rulebook/active

# Disable a rule (keep data but don't use for detection)
PUT /api/rulebook/{ruleId}
{
  "isActive": false
}

# Add custom rules
POST /api/rulebook
{
  "ruleName": "HIGH_VELOCITY",
  "description": "Multiple transactions in short timeframe",
  "threshold": 5.0,
  "riskIncrement": 25,
  "isActive": true
}

# Delete a rule
DELETE /api/rulebook/{ruleId}
```

## Logging

The system logs rule initialization with SLF4J logs:

```
INFO: Application started. Initializing default fraud detection rules...
INFO: Default fraud detection rules initialized successfully

OR (if rules already exist)

INFO: Application started. Initializing default fraud detection rules...
INFO: Default fraud detection rules initialized successfully
(No log entries for existing rules as they're skipped)
```

## Advantages

✅ **Zero Manual Setup**: No need to manually create rules for new deployments
✅ **Consistency**: Same default rules across all environments
✅ **Safety**: Prevents duplicate rule creation
✅ **Flexibility**: Rules can still be customized after initialization
✅ **Automatic**: Happens on every application startup
✅ **Production Ready**: Works with fresh databases and existing ones

## Testing the Feature

### Scenario 1: Fresh Database
1. Delete all rules from rulebook table
2. Restart application
3. Check logs for initialization messages
4. Query `/api/rulebook` - should return 3 rules

### Scenario 2: Existing Rules
1. Verify rules exist in rulebook table
2. Restart application
3. Check logs - no duplicate creation
4. Rules remain unchanged

### Scenario 3: Partial Rules
1. Keep only HIGH_AMOUNT rule in database
2. Restart application
3. System adds missing rules (SIMULTANEOUS_TRANSACTIONS and CUSTOMER_RISK_PROFILE)
4. Final result: 3 rules total

## Future Enhancements

- Database migration scripts to create default rules
- Configurable rule thresholds via application properties
- Rule templates for common fraud patterns
- Rule versioning/history tracking
- A/B testing different rule configurations
