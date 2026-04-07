import { useState } from 'react'
import './App.css'
import { API_ENDPOINTS } from './api'

function App() {
  const [step, setStep] = useState(0) // 0: Choice, 1: Account Creation, 2: Customer Registration, 3: Transaction, 4: Success
  const [userChoice, setUserChoice] = useState('') // 'new' or 'existing'
  const [message, setMessage] = useState('')
  const [loading, setLoading] = useState(false)

  // Account Form State
  const [accountForm, setAccountForm] = useState({
    accountNumber: '',
    accountType: '',
    bankName: '',
    balance: ''
  })

  // Customer Form State
  const [customerForm, setCustomerForm] = useState({
    email: '',
    name: '',
    riskScore: 0,
    accountNumber: ''
  })

  // Existing Customer Login State
  const [existingCustomerEmail, setExistingCustomerEmail] = useState('')
  const [existingCustomer, setExistingCustomer] = useState(null)

  // Transaction Form State
  const [transactionForm, setTransactionForm] = useState({
    sourceType: 'bank',
    sourceName: '',
    location: '',
    ipAddress: '',
    amount: '',
    customerId: null,
    accountNumber: ''
  })

  // Money Transfer Form State
  const [moneyTransferForm, setMoneyTransferForm] = useState({
    recipientAccountNumber: '',
    amount: '',
    sourceType: 'bank',
    sourceName: '',
    location: '',
    ipAddress: ''
  })

  // Transaction Type State (fraud-detection or money-transfer)
  const [transactionType, setTransactionType] = useState('fraud-detection')

  // Customer Transactions History
  const [transactionHistory, setTransactionHistory] = useState([])

  const sourceOptions = ['bank', 'upi', 'wallet', 'card']
  const accountTypes = ['savings', 'current', 'business']
  const sourceNames = {
    bank: ['HDFC Bank', 'ICICI Bank', 'SBI', 'Axis Bank'],
    upi: ['Google Pay', 'PhonePe', 'Paytm', 'BHIM'],
    wallet: ['PayPal', 'Skrill', 'Wise', 'Amazon Pay'],
    card: ['Visa', 'Mastercard', 'American Express']
  }

  // Handle Account Creation
  const handleCreateAccount = async (e) => {
    e.preventDefault()
    setLoading(true)
    setMessage('')

    try {
      const response = await fetch(`${API_ENDPOINTS.ACCOUNTS}/add`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(accountForm)
      })

      if (response.ok) {
        const account = await response.json()
        setMessage('✓ Account created successfully!')
        setTransactionForm({ ...transactionForm, accountNumber: accountForm.accountNumber })
        setCustomerForm({ ...customerForm, accountNumber: account.accountNumber })
        setStep(2)
        setAccountForm({ accountNumber: '', accountType: '', bankName: '', balance: '' })
      } else {
        const errorData = await response.json().catch(() => ({}))
        setMessage(`Failed to create account: ${errorData.message || 'Unknown error'}`)
      }
    } catch (error) {
      setMessage('Error: ' + error.message)
    } finally {
      setLoading(false)
    }
  }

  // Handle Existing Customer Login
  const handleExistingCustomerLogin = async (e) => {
    e.preventDefault()
    setLoading(true)
    setMessage('')

    try {
      const response = await fetch(`${API_ENDPOINTS.CUSTOMERS}/email/${existingCustomerEmail}`, {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' }
      })

      if (response.ok) {
        const customer = await response.json()
        setExistingCustomer(customer)
        setTransactionForm({
          ...transactionForm,
          customerId: customer.customerId,
          accountNumber: customer.account?.accountNumber
        })
        setMessage(`✓ Welcome back, ${customer.name}! Your current risk score is: ${customer.riskScore}`)
        
        // Fetch transaction history
        const historyResponse = await fetch(`${API_ENDPOINTS.EVALUATED_TRANSACTIONS}/customer/${customer.customerId}`, {
          method: 'GET',
          headers: { 'Content-Type': 'application/json' }
        })
        
        if (historyResponse.ok) {
          const transactions = await historyResponse.json()
          setTransactionHistory(transactions)
        }
        
        setStep(3)
      } else {
        setMessage('Customer not found. Please check your email and try again.')
      }
    } catch (error) {
      setMessage('Error: ' + error.message)
    } finally {
      setLoading(false)
    }
  }

  // Handle Customer Creation
  const handleCreateCustomer = async (e) => {
    e.preventDefault()
    setLoading(true)
    setMessage('')

    try {
      // Use the endpoint with account number parameter
      const response = await fetch(`${API_ENDPOINTS.CUSTOMERS}/add/${customerForm.accountNumber}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          email: customerForm.email,
          name: customerForm.name,
          riskScore: customerForm.riskScore
        })
      })

      if (response.ok) {
        const customer = await response.json()
        setExistingCustomer(customer)
        setMessage('✓ Customer profile created successfully!')
        setTransactionForm({ 
          ...transactionForm, 
          customerId: customer.customerId,
          accountNumber: customerForm.accountNumber
        })
        setStep(3)
      } else {
        const errorData = await response.json().catch(() => ({}))
        setMessage(`Failed to create customer profile: ${errorData.message || 'Unknown error'}`)
      }
    } catch (error) {
      setMessage('Error: ' + error.message)
    } finally {
      setLoading(false)
    }
  }

  // Handle Transaction Submission
  const handleSubmitTransaction = async (e) => {
    e.preventDefault()
    setLoading(true)
    setMessage('')

    try {
      // Create Payload first
      const payload = {
        sourceType: transactionForm.sourceType,
        sourceName: transactionForm.sourceName,
        location: transactionForm.location,
        ipAddress: transactionForm.ipAddress,
        amount: parseFloat(transactionForm.amount),
        date: new Date().toISOString().split('T')[0],
        time: new Date().toTimeString().split(' ')[0],
        status: 'pending'
      }

      const payloadResponse = await fetch(`${API_ENDPOINTS.PAYLOADS}/add`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      })

      if (!payloadResponse.ok) {
        setMessage('Failed to create payload. Please try again.')
        setLoading(false)
        return
      }

      const savedPayload = await payloadResponse.json()

      // Create RawTransaction
      const transaction = {
        payload: { payloadId: savedPayload.payloadId },
        customerProfile: { customerId: transactionForm.customerId },
        processingStatus: false
      }

      const response = await fetch(`${API_ENDPOINTS.TRANSACTIONS}/add`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(transaction)
      })

      if (response.ok) {
        // Fetch updated customer data (including new risk score)
        const customerResponse = await fetch(`${API_ENDPOINTS.CUSTOMERS}/${transactionForm.customerId}`, {
          method: 'GET',
          headers: { 'Content-Type': 'application/json' }
        })
        
        if (customerResponse.ok) {
          const updatedCustomer = await customerResponse.json()
          setExistingCustomer(updatedCustomer)
        }
        
        // Fetch updated transaction history
        const historyResponse = await fetch(`${API_ENDPOINTS.EVALUATED_TRANSACTIONS}/customer/${transactionForm.customerId}`, {
          method: 'GET',
          headers: { 'Content-Type': 'application/json' }
        })
        
        if (historyResponse.ok) {
          const transactions = await historyResponse.json()
          setTransactionHistory(transactions)
        }
        
        setMessage('✓ Transaction submitted successfully! Risk score updated.')
        // Stay on transaction step to allow multiple transactions
        setTransactionForm({
          sourceType: 'bank',
          sourceName: '',
          location: '',
          ipAddress: '',
          amount: '',
          customerId: transactionForm.customerId,
          accountNumber: transactionForm.accountNumber
        })
      } else {
        const errorData = await response.json().catch(() => ({}))
        setMessage(`Failed to submit transaction: ${errorData.message || 'Unknown error'}`)
      }
    } catch (error) {
      setMessage('Error: ' + error.message)
    } finally {
      setLoading(false)
    }
  }

  // Handle Money Transfer
  const handleMoneyTransfer = async (e) => {
    e.preventDefault()
    setLoading(true)
    setMessage('')

    try {
      // Validation
      if (!moneyTransferForm.recipientAccountNumber) {
        setMessage('Please enter recipient account number')
        setLoading(false)
        return
      }

      if (!moneyTransferForm.amount || parseFloat(moneyTransferForm.amount) <= 0) {
        setMessage('Please enter a valid amount')
        setLoading(false)
        return
      }

      if (!moneyTransferForm.location) {
        setMessage('Please enter location')
        setLoading(false)
        return
      }

      if (!moneyTransferForm.ipAddress) {
        setMessage('Please enter IP address')
        setLoading(false)
        return
      }

      if (!moneyTransferForm.sourceName) {
        setMessage('Please select a source')
        setLoading(false)
        return
      }

      const senderAccountNumber = parseInt(transactionForm.accountNumber)
      const recipientAccountNumber = parseInt(moneyTransferForm.recipientAccountNumber)
      const amount = parseFloat(moneyTransferForm.amount)

      // Step 1: Validate transfer before processing
      const validateResponse = await fetch(`${API_ENDPOINTS.ACCOUNTS}/transfer/validate`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          senderAccountNumber,
          recipientAccountNumber,
          amount
        })
      })

      const validateResult = await validateResponse.json()
      
      if (!validateResponse.ok || !validateResult.success) {
        setMessage(`✗ Transfer validation failed: ${validateResult.message}`)
        setLoading(false)
        return
      }

      // Step 2: Create payload for money transfer with fraud detection
      const payload = {
        sourceType: moneyTransferForm.sourceType,
        sourceName: moneyTransferForm.sourceName,
        location: moneyTransferForm.location,
        ipAddress: moneyTransferForm.ipAddress,
        amount: amount,
        recipientAccountNumber: recipientAccountNumber,
        date: new Date().toISOString().split('T')[0],
        time: new Date().toTimeString().split(' ')[0],
        status: 'pending'
      }

      const payloadResponse = await fetch(`${API_ENDPOINTS.PAYLOADS}/add`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      })

      if (!payloadResponse.ok) {
        setMessage('Failed to create transfer payload. Please try again.')
        setLoading(false)
        return
      }

      const savedPayload = await payloadResponse.json()

      // Step 3: Create RawTransaction - goes through fraud detection
      const rawTransaction = {
        payload: { payloadId: savedPayload.payloadId },
        customerProfile: { customerId: transactionForm.customerId },
        processingStatus: false,
        amount: amount
      }

      const rawTransactionResponse = await fetch(`${API_ENDPOINTS.TRANSACTIONS}/add`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(rawTransaction)
      })

      if (!rawTransactionResponse.ok) {
        setMessage('Failed to create transaction. Please try again.')
        setLoading(false)
        return
      }

      // Step 4: The transaction is now evaluated through fraud detection
      // Step 5: Perform the actual money transfer (debit and credit)
      const transferResponse = await fetch(`${API_ENDPOINTS.ACCOUNTS}/transfer`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          senderAccountNumber,
          recipientAccountNumber,
          amount,
          description: 'Money transfer'
        })
      })

      const transferResult = await transferResponse.json()

      if (transferResponse.ok && transferResult.success) {
        setMessage(`✓ ${transferResult.message} Your balance: $${transferResult.senderNewBalance.toFixed(2)}`)
        setMoneyTransferForm({ recipientAccountNumber: '', amount: '', sourceType: 'bank', sourceName: '', location: '', ipAddress: '' })
        
        // Fetch updated customer data
        const customerResponse = await fetch(`${API_ENDPOINTS.CUSTOMERS}/${transactionForm.customerId}`, {
          method: 'GET',
          headers: { 'Content-Type': 'application/json' }
        })
        
        if (customerResponse.ok) {
          const updatedCustomer = await customerResponse.json()
          setExistingCustomer(updatedCustomer)
        }
        
        // Fetch updated transaction history
        const historyResponse = await fetch(`${API_ENDPOINTS.EVALUATED_TRANSACTIONS}/customer/${transactionForm.customerId}`, {
          method: 'GET',
          headers: { 'Content-Type': 'application/json' }
        })
        
        if (historyResponse.ok) {
          const transactions = await historyResponse.json()
          setTransactionHistory(transactions)
        }
      } else {
        setMessage(`✗ Transfer failed: ${transferResult.message}`)
      }
    } catch (error) {
      setMessage('Error: ' + error.message)
    } finally {
      setLoading(false)
    }
  }

  const resetForm = () => {
    setStep(0)
    setUserChoice('')
    setAccountForm({ accountNumber: '', accountType: '', bankName: '', balance: '' })
    setCustomerForm({ email: '', name: '', riskScore: 0, accountNumber: '' })
    setExistingCustomerEmail('')
    setExistingCustomer(null)
    setTransactionForm({ sourceType: 'bank', sourceName: '', location: '', ipAddress: '', amount: '', customerId: null, accountNumber: '' })
    setMoneyTransferForm({ recipientAccountNumber: '', amount: '' })
    setTransactionHistory([])
    setMessage('')
  }

  return (
    <div className="app-container">
      <div className="form-wrapper">
        <h1>BankGuard - Transaction Portal</h1>

        {/* Step Indicator */}
        <div className="steps">
          <div className={`step ${step >= 1 ? 'active' : ''}`}>1. {userChoice === 'new' ? 'Account' : 'Login'}</div>
          <div className={`step ${step >= 2 ? 'active' : ''}`}>2. {userChoice === 'new' ? 'Profile' : 'Transaction'}</div>
          <div className={`step ${step >= 3 ? 'active' : ''}`}>3. {userChoice === 'new' ? 'Transaction' : 'History'}</div>
        </div>

        {/* Messages */}
        {message && (
          <div className={`message ${message.includes('✓') || message.includes('successfully') ? 'success' : 'error'}`}>
            {message}
          </div>
        )}

        {/* Step 0: User Choice */}
        {step === 0 && (
          <div className="choice-container">
            <h2>Welcome to BankGuard</h2>
            <p>What would you like to do?</p>
            <div className="button-group">
              <button 
                onClick={() => {
                  setUserChoice('new')
                  setStep(1)
                }}
                className="choice-button primary"
              >
                Create New Account
              </button>
              <button 
                onClick={() => {
                  setUserChoice('existing')
                  setStep(1)
                }}
                className="choice-button primary"
              >
                Existing Customer
              </button>
            </div>
          </div>
        )}

        {/* Step 1: Create Account (for new users) or Login (for existing) */}
        {step === 1 && userChoice === 'new' && (
          <form onSubmit={handleCreateAccount}>
            <h2>Step 1: Create Account</h2>
            <div className="form-group">
              <label>Account Number</label>
              <input
                type="number"
                placeholder="Enter 12-digit account number"
                value={accountForm.accountNumber}
                onChange={(e) => setAccountForm({ ...accountForm, accountNumber: e.target.value })}
                required
              />
            </div>
            <div className="form-group">
              <label>Account Type</label>
              <select
                value={accountForm.accountType}
                onChange={(e) => setAccountForm({ ...accountForm, accountType: e.target.value })}
                required
              >
                <option value="">Select Account Type</option>
                {accountTypes.map(type => <option key={type} value={type}>{type}</option>)}
              </select>
            </div>
            <div className="form-group">
              <label>Bank Name</label>
              <input
                type="text"
                placeholder="Enter bank name"
                value={accountForm.bankName}
                onChange={(e) => setAccountForm({ ...accountForm, bankName: e.target.value })}
                required
              />
            </div>
            <div className="form-group">
              <label>Initial Balance ($)</label>
              <input
                type="number"
                step="0.01"
                placeholder="Enter initial account balance"
                value={accountForm.balance}
                onChange={(e) => setAccountForm({ ...accountForm, balance: e.target.value })}
                required
              />
            </div>
            <button type="submit" disabled={loading}>{loading ? 'Creating...' : 'Create Account'}</button>
          </form>
        )}

{/* Step 1: Login for Existing Customers */}
        {step === 1 && userChoice === 'existing' && (
          <form onSubmit={handleExistingCustomerLogin}>
            <h2>Step 1: Welcome Back</h2>
            <div className="form-group">
              <label>Email Address</label>
              <input
                type="email"
                placeholder="Enter your registered email"
                value={existingCustomerEmail}
                onChange={(e) => setExistingCustomerEmail(e.target.value)}
                required
              />
            </div>
            <div className="button-group">
              <button type="button" onClick={() => setStep(0)} className="secondary">Back</button>
              <button type="submit" disabled={loading}>{loading ? 'Loading...' : 'Login'}</button>
            </div>
          </form>
        )}

        {/* Step 2: Create Customer Profile (for new users) */}
        {step === 2 && userChoice === 'new' && (
          <form onSubmit={handleCreateCustomer}>
            <h2>Step 2: Create Customer Profile</h2>
            <div className="form-group">
              <label>Name</label>
              <input
                type="text"
                placeholder="Enter full name"
                value={customerForm.name}
                onChange={(e) => setCustomerForm({ ...customerForm, name: e.target.value })}
                required
              />
            </div>
            <div className="form-group">
              <label>Email</label>
              <input
                type="email"
                placeholder="Enter email address"
                value={customerForm.email}
                onChange={(e) => setCustomerForm({ ...customerForm, email: e.target.value })}
                required
              />
            </div>
            <div className="form-group">
              <label>Risk Score</label>
              <input
                type="number"
                placeholder="Enter risk score (0-100)"
                value={customerForm.riskScore}
                onChange={(e) => setCustomerForm({ ...customerForm, riskScore: parseInt(e.target.value) || 0 })}
                min="0"
                max="100"
                required
              />
            </div>
            <div className="button-group">
              <button type="button" onClick={() => setStep(1)} className="secondary">Back</button>
              <button type="submit" disabled={loading}>{loading ? 'Creating...' : 'Create Profile'}</button>
            </div>
          </form>
        )}

        {/* Step 3: Submit Transaction */}
        {step === 3 && (
          <div>
            <div className="customer-info">
              <h2>Transaction & Money Transfer</h2>
              {existingCustomer && (
                <div className="customer-details">
                  <p><strong>Customer:</strong> {existingCustomer.name}</p>
                  <p><strong>Email:</strong> {existingCustomer.email}</p>
                  <p><strong>Account Number:</strong> {transactionForm.accountNumber}</p>
                  <p><strong>Current Risk Score:</strong> <span style={{color: existingCustomer.riskScore > 50 ? '#ff6b6b' : '#51cf66'}}>{existingCustomer.riskScore}</span></p>
                </div>
              )}
            </div>

            {/* Transaction Type Tabs */}
            <div className="transaction-tabs" style={{display: 'flex', gap: '10px', marginBottom: '20px'}}>
              <button 
                type="button"
                onClick={() => setTransactionType('fraud-detection')}
                style={{
                  padding: '10px 20px',
                  backgroundColor: transactionType === 'fraud-detection' ? '#007bff' : '#e9ecef',
                  color: transactionType === 'fraud-detection' ? 'white' : 'black',
                  border: 'none',
                  borderRadius: '4px',
                  cursor: 'pointer',
                  fontWeight: 'bold'
                }}
              >
                Fraud Detection Transaction
              </button>
              <button 
                type="button"
                onClick={() => setTransactionType('money-transfer')}
                style={{
                  padding: '10px 20px',
                  backgroundColor: transactionType === 'money-transfer' ? '#28a745' : '#e9ecef',
                  color: transactionType === 'money-transfer' ? 'white' : 'black',
                  border: 'none',
                  borderRadius: '4px',
                  cursor: 'pointer',
                  fontWeight: 'bold'
                }}
              >
                Send Money to Another Account
              </button>
            </div>

            {/* Fraud Detection Transaction Form */}
            {transactionType === 'fraud-detection' && (
            <form onSubmit={handleSubmitTransaction}>
              <div className="form-group">
                <label>Amount ($)</label>
                <input
                  type="number"
                  step="0.01"
                  placeholder="Enter transaction amount"
                  value={transactionForm.amount}
                  onChange={(e) => setTransactionForm({ ...transactionForm, amount: e.target.value })}
                  required
                />
              </div>
              <div className="form-group">
                <label>Source Type</label>
                <select
                  value={transactionForm.sourceType}
                  onChange={(e) => {
                    setTransactionForm({
                      ...transactionForm,
                      sourceType: e.target.value,
                      sourceName: ''
                    })
                  }}
                >
                  {sourceOptions.map(type => <option key={type} value={type}>{type}</option>)}
                </select>
              </div>
              <div className="form-group">
                <label>Source Name ({transactionForm.sourceType} Provider)</label>
                <select
                  value={transactionForm.sourceName}
                  onChange={(e) => setTransactionForm({ ...transactionForm, sourceName: e.target.value })}
                  required
                >
                  <option value="">Select a {transactionForm.sourceType} provider</option>
                  {sourceNames[transactionForm.sourceType]?.map(name => 
                    <option key={name} value={name}>{name}</option>
                  )}
                </select>
              </div>
              <div className="form-group">
                <label>Location</label>
                <input
                  type="text"
                  placeholder="e.g., New York, USA"
                  value={transactionForm.location}
                  onChange={(e) => setTransactionForm({ ...transactionForm, location: e.target.value })}
                  required
                />
              </div>
              <div className="form-group">
                <label>IP Address</label>
                <input
                  type="text"
                  placeholder="e.g., 192.168.1.1"
                  value={transactionForm.ipAddress}
                  onChange={(e) => setTransactionForm({ ...transactionForm, ipAddress: e.target.value })}
                  required
                />
              </div>
              <div className="button-group">
                <button type="button" onClick={() => {
                  setStep(userChoice === 'new' ? 2 : 1)
                }} className="secondary">Back</button>
                <button type="submit" disabled={loading}>{loading ? 'Submitting...' : 'Submit Transaction'}</button>
              </div>
            </form>
            )}

            {/* Money Transfer Form */}
            {transactionType === 'money-transfer' && (
            <form onSubmit={handleMoneyTransfer}>
              <div className="form-group">
                <label>Recipient Account Number *</label>
                <input
                  type="number"
                  placeholder="Enter recipient's account number"
                  value={moneyTransferForm.recipientAccountNumber}
                  onChange={(e) => setMoneyTransferForm({ ...moneyTransferForm, recipientAccountNumber: e.target.value })}
                  required
                />
              </div>
              <div className="form-group">
                <label>Amount to Transfer ($) *</label>
                <input
                  type="number"
                  step="0.01"
                  placeholder="Enter amount"
                  value={moneyTransferForm.amount}
                  onChange={(e) => setMoneyTransferForm({ ...moneyTransferForm, amount: e.target.value })}
                  required
                />
              </div>
              <div className="form-group">
                <label>Source Type *</label>
                <select
                  value={moneyTransferForm.sourceType}
                  onChange={(e) => {
                    setMoneyTransferForm({
                      ...moneyTransferForm,
                      sourceType: e.target.value,
                      sourceName: ''
                    })
                  }}
                >
                  {sourceOptions.map(type => <option key={type} value={type}>{type}</option>)}
                </select>
              </div>
              <div className="form-group">
                <label>Source Name ({moneyTransferForm.sourceType} Provider) *</label>
                <select
                  value={moneyTransferForm.sourceName}
                  onChange={(e) => setMoneyTransferForm({ ...moneyTransferForm, sourceName: e.target.value })}
                  required
                >
                  <option value="">Select a {moneyTransferForm.sourceType} provider</option>
                  {sourceNames[moneyTransferForm.sourceType]?.map(name => 
                    <option key={name} value={name}>{name}</option>
                  )}
                </select>
              </div>
              <div className="form-group">
                <label>Location *</label>
                <input
                  type="text"
                  placeholder="e.g., New York, USA"
                  value={moneyTransferForm.location}
                  onChange={(e) => setMoneyTransferForm({ ...moneyTransferForm, location: e.target.value })}
                  required
                />
              </div>
              <div className="form-group">
                <label>IP Address *</label>
                <input
                  type="text"
                  placeholder="e.g., 192.168.1.1"
                  value={moneyTransferForm.ipAddress}
                  onChange={(e) => setMoneyTransferForm({ ...moneyTransferForm, ipAddress: e.target.value })}
                  required
                />
              </div>
              <div style={{backgroundColor: '#f0f8ff', padding: '10px', borderRadius: '4px', marginBottom: '15px'}}>
                <p><strong>⚠️ Validation & Fraud Detection:</strong></p>
                <ul style={{marginLeft: '20px', fontSize: '0.9em'}}>
                  <li>Recipient account must exist in database</li>
                  <li>Recipient account must be different from your account</li>
                  <li>Transfer amount must be ≤ your account balance</li>
                  <li>Transaction is evaluated through fraud detection system</li>
                  <li>Debit & credit applied after fraud evaluation</li>
                </ul>
              </div>
              <div className="button-group">
                <button type="button" onClick={() => {
                  setStep(userChoice === 'new' ? 2 : 1)
                }} className="secondary">Back</button>
                <button type="submit" disabled={loading}>{loading ? 'Processing...' : 'Send Money'}</button>
              </div>
            </form>
            )}

            {/* Transaction History */}
            {transactionHistory.length > 0 && (
              <div className="transaction-history">
                <h3>Recent Transactions</h3>
                <div className="history-table">
                  <div className="history-header">
                    <span>ID</span>
                    <span>Amount</span>
                    <span>Status</span>
                    <span>Risk Score</span>
                    <span>Time</span>
                  </div>
                  {transactionHistory.slice(-5).reverse().map((txn) => (
                    <div key={txn.transactionId} className="history-row">
                      <span>#{txn.transactionId}</span>
                      <span>${txn.amount?.toFixed(2) || 'N/A'}</span>
                      <span className={`status-${txn.status?.toLowerCase()}`}>{txn.status}</span>
                      <span>{txn.riskScore}</span>
                      <span>{new Date(txn.transactionTimestamp).toLocaleString()}</span>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  )
}

export default App
