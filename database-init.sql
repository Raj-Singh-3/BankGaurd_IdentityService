-- Database Initialization Script for BankGuard Services

-- Create TransactionService Database
CREATE DATABASE IF NOT EXISTS transactionservice;
USE transactionservice;

-- Grant privileges to root user
GRANT ALL PRIVILEGES ON transactionservice.* TO 'root'@'localhost' IDENTIFIED BY 'root';

-- Create IdentityService Database (if needed)
CREATE DATABASE IF NOT EXISTS identityservice;
USE identityservice;

-- Grant privileges to root user
GRANT ALL PRIVILEGES ON identityservice.* TO 'root'@'localhost' IDENTIFIED BY 'root';

-- Flush privileges to apply changes
FLUSH PRIVILEGES;

-- Verify databases were created
SHOW DATABASES;
