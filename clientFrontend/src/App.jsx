import { useState } from 'react'
import './App.css'
import { API_ENDPOINTS } from './api'

function App() {
  const [step, setStep] = useState(1)
  const [message, setMessage] = useState('')
  const [loading, setLoading] = useState(false)

  // Account Form State
  const [accountForm, setAccountForm] = useState({
    accountNumber: '',
    accountType: '',
    bankName: ''
  })

  // Customer Form State
  const [customerForm, setCustomerForm] = useState({
    email: '',
    name: '',
    riskScore: 0,
    accountNumber: ''
  })

  // Transaction Form State
  const [transactionForm, setTransactionForm] = useState({
    sourceType: 'bank',
    sourceName: '',
    location: '',
    ipAddress: '',
    customerId: null,
    accountNumber: ''
  })

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
        setAccountForm({ accountNumber: '', accountType: '', bankName: '' })
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
        setMessage('✓ Transaction submitted successfully and stored in database!')
        setStep(4)
        setTransactionForm({
          sourceType: 'bank',
          sourceName: '',
          location: '',
          ipAddress: '',
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

  const resetForm = () => {
    setStep(1)
    setAccountForm({ accountNumber: '', accountType: '', bankName: '' })
    setCustomerForm({ email: '', name: '', riskScore: 0, accountNumber: '' })
    setTransactionForm({ sourceType: 'bank', sourceName: '', location: '', ipAddress: '', customerId: null, accountNumber: '' })
    setMessage('')
  }

  return (
    <div className="app-container">
      <div className="form-wrapper">
        <h1>BankGuard - Transaction Portal</h1>

        {/* Step Indicator */}
        <div className="steps">
          <div className={`step ${step >= 1 ? 'active' : ''}`}>1. Account</div>
          <div className={`step ${step >= 2 ? 'active' : ''}`}>2. Profile</div>
          <div className={`step ${step >= 3 ? 'active' : ''}`}>3. Transaction</div>
          {step === 4 && <div className="step active">✓ Complete</div>}
        </div>

        {/* Messages */}
        {message && (
          <div className={`message ${message.includes('successfully') ? 'success' : 'error'}`}>
            {message}
          </div>
        )}

        {/* Step 1: Create Account */}
        {step === 1 && (
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
            <button type="submit" disabled={loading}>{loading ? 'Creating...' : 'Create Account'}</button>
          </form>
        )}

        {/* Step 2: Create Customer Profile */}
        {step === 2 && (
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
          <form onSubmit={handleSubmitTransaction}>
            <h2>Step 3: Submit Transaction</h2>
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
              <button type="button" onClick={() => setStep(2)} className="secondary">Back</button>
              <button type="submit" disabled={loading}>{loading ? 'Submitting...' : 'Submit Transaction'}</button>
            </div>
          </form>
        )}

        {/* Step 4: Success */}
        {step === 4 && (
          <div className="success-message">
            <h2>✓ Transaction Completed!</h2>
            <p>Your transaction has been successfully submitted and stored in the BankGuard database.</p>
            <p style={{ fontSize: '12px', color: '#999', marginTop: '15px' }}>Customer ID: {transactionForm.customerId} | Account: {transactionForm.accountNumber}</p>
            <button onClick={resetForm} className="primary">New Transaction</button>
          </div>
        )}
      </div>
    </div>
  )
}

export default App
